package com.bpa4j.util.codegen;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

import com.bpa4j.Wrapper;
import com.bpa4j.core.Root;
import com.bpa4j.ui.HButton;
import com.bpa4j.util.ParseUtils;
import com.bpa4j.util.SprintUI;
import com.bpa4j.util.ParseUtils.StandardSkipper;

public class ProjectGraph{
	public static abstract class ProjectNode{
		public String name;
		public File location;
		public ProjectNode(File location){
			name=location.getName().substring(0,location.getName().lastIndexOf('.'));
			this.location=location;
		}
	}
	@SuppressWarnings("PMD.ExceptionAsFlowControl")
	public static class EditableNode extends ProjectNode{
		public static class Property{
			public static enum PropertyType{
				STRING("String"),
				INT("int"),
				DOUBLE("double"),
				BOOL("boolean"),
				DATE("LocalDate"),
				DATETIME("LocalDateTime");
				String typeName;
				private PropertyType(String typeName){this.typeName=typeName;}
				public String toString(){return typeName;}
			}
			PropertyType type;
			String name;
			public Property(String name,PropertyType type){
				this.name=name.trim();
				this.type=type;
			}
			public String getCode(String prompt){
				return type==null?"\t//TODO: add property \""+name+"\"\n":String.format("""
					@EditorEntry(translation="%s")
					public %s %s;
				""",name,type.toString(),prompt);
			}
			public String toString(){return name+" ("+(type==null?"???":type.toString())+")";}
		}
		public String objectName;
		public ArrayList<Property>properties=new ArrayList<>();
		/**
		 * Resolves an existing node from the file
		 */
		public EditableNode(File file){
			super(file);
			try{
				String s=Files.readString(location.toPath());
				Matcher m=Pattern.compile(name+"\\s*\\(\\)\\s*\\{[\n\\s]*super\\(\"(?:нов[^ ]* )?(.*?)\"",Pattern.CASE_INSENSITIVE+Pattern.UNICODE_CASE).matcher(s);
				if(m.find())objectName=m.group(1).replaceAll("[!@#$%&*]","").trim();
				Pattern.compile("@EditorEntry\\s*\\(.*?translation\\s*=\\s*\"(.*?)\".*?\\).*?(\\w+)\\s*\\w+;",Pattern.DOTALL).matcher(s).results().forEach(r->{
					properties.add(new Property(r.group(1),switch(r.group(2)){
						case "LocalDate"->Property.PropertyType.DATE;
						case "LocalDateTime"->Property.PropertyType.DATETIME;
						case "String"->Property.PropertyType.STRING;
						case "int"->Property.PropertyType.INT;
						case "double"->Property.PropertyType.DOUBLE;
						case "boolean"->Property.PropertyType.BOOL;
						default->null;
					}));
				});
				Pattern.compile("//\\s*TODO:\\s*add property\\s*\"(.*?)\"").matcher(s).results().forEach(r->properties.add(new Property(r.group(1),null)));
				//TODO: parse properties
			}catch(IOException ex){throw new UncheckedIOException(ex);}
		}
		/**
		 * Constructs a new node with the designated file
		 */
		public EditableNode(File file,String objectName,Property...properties){
			super(file);
			this.objectName=objectName;
			this.properties.addAll(Arrays.asList(properties));
			try{
				file.createNewFile();
				Wrapper<Integer>index=new Wrapper<>(0);
				String s=String.format("""
				package com.ntoproject.editables.registered;
				
				import com.bpa4j.core.Data.Editable;
				import com.bpa4j.editor.EditorEntry;
				
				public class %s extends Editable{
				"""+
				Stream.of(properties).map(p->p.getCode("var"+(++index.var))).collect(Collectors.joining("\n"))+
				"""
					public %s(){
						super("Новый %s");
					}
				}
				""",name,name,objectName);
				Files.writeString(file.toPath(),s);
			}catch(IOException ex){throw new UncheckedIOException(ex);}
		}
		protected static File findFile(String name,File parent){
			try{
				Wrapper<File>w=new Wrapper<File>(null);
				Files.walkFileTree(parent.toPath(),new SimpleFileVisitor<Path>(){
					public FileVisitResult visitFile(Path file,BasicFileAttributes attrs)throws IOException{
						if(file.toString().equals(name+".java")){
							w.var=file.toFile();
							return FileVisitResult.TERMINATE;
						}
						return FileVisitResult.CONTINUE;
					}
				});
				if(w.var==null)throw new FileNotFoundException();
				return w.var;
			}catch(IOException ex){throw new UncheckedIOException(ex);}
		}
		public void addProperty(Property property,String varName){
			try{
				StringBuilder s=new StringBuilder(Files.readString(location.toPath()));
				Matcher m=ParseUtils.createSubClassPattern("Editable").matcher(s);
				m.find();
				s.insert(m.end(),"\n"+property.getCode(varName));
				Files.writeString(location.toPath(),s.toString(),StandardOpenOption.CREATE);
			}catch(IOException ex){throw new UncheckedIOException(ex);}
		}
		public void addProperties(Property...properties){
			try{
				StringBuilder s=new StringBuilder(Files.readString(location.toPath()));
				Wrapper<Integer>index=new Wrapper<Integer>(0);
				Matcher m=ParseUtils.createSubClassPattern("Editable").matcher(s);
				m.find();
				s.insert(m.end(),Stream.of(properties).map(p->p.getCode("iVar"+(++index.var))).collect(Collectors.joining("\n")));
				Files.writeString(location.toPath(),s.toString(),StandardOpenOption.CREATE,StandardOpenOption.WRITE);
			}catch(IOException ex){throw new UncheckedIOException(ex);}
		}
	}
	public static class PermissionsNode extends ProjectNode{
		public ArrayList<String>permissions;
		public PermissionsNode(File file){
			super(file);
			try{
				String s=ParseUtils.findFirstBlock(Pattern.compile("public enum (\\w+) implements Permission",Pattern.DOTALL),ParseUtils.rewrite(Files.readString(file.toPath()),StandardSkipper.SYNTAX),'{','}');
				permissions=new ArrayList<>(Arrays.asList(
					s.substring(0,s.indexOf(';'))
					.replaceAll("\\s+","")
					.split(",")
				));
			}catch(IOException ex){throw new UncheckedIOException(ex);}
		}
	}
	public static class RolesNode extends ProjectNode{
		public static class RoleRepresentation{
			public String name;
			public Set<String>permissions;
			public Set<String>features;
			public RoleRepresentation(String name,Set<String>permissions,Set<String>features){
				this.name=name;
				this.permissions=permissions;
				this.features=features;
			}
		}
		public ArrayList<RoleRepresentation>roles=new ArrayList<>();
		public RolesNode(File file,PermissionsNode p){
			super(file);
			try{
				String roleClass=ParseUtils.findFirstBlock(
					Pattern.compile("public enum (\\w+) implements Role"),
					Files.readString(file.toPath()), 
					'{','}',
					StandardSkipper.SYNTAX
				);
				TreeMap<String,String>a=ParseUtils.findAllBlocks(
					Pattern.compile("(\\w+)"),
					roleClass.substring(0,roleClass.indexOf(';')),
					'(',')',
					StandardSkipper.SYNTAX
				);
				Pattern lambdaPattern=Pattern.compile("\\(\\)\\s*->\\s*");
				for(Entry<String,String>entry:a.entrySet()){ //for each role
					String name=entry.getKey();
					int d=ParseUtils.indexOf(entry.getValue(),Pattern.compile(","),StandardSkipper.OUTERSCOPE);
					String permissions=entry.getValue().substring(0,d);
					Matcher m=lambdaPattern.matcher(permissions);
					if(m.find())permissions=permissions.substring(m.end());
					else permissions="";
					String featuresStr=entry.getValue().substring(d+1);
					m=lambdaPattern.matcher(featuresStr);
					if(m.find())featuresStr=featuresStr.substring(m.end());
					else featuresStr="";
					//TODO: parse features
					if(Pattern.compile("\\s*\\w+\\.values\\s*\\(\\).*",Pattern.DOTALL).matcher(permissions).matches()){
						roles.add(new RoleRepresentation(name,new TreeSet<>(p.permissions),null));
					}else{
						String s=ParseUtils.findFirstBlock(Pattern.compile("new Permission\\s*\\[\\]"),permissions,'{','}');
						roles.add(new RoleRepresentation(name,s==null?null:new TreeSet<>(Arrays.asList(s.replaceAll("\\s+","").split(","))),null));
					}
				}
			}catch(IOException ex){throw new UncheckedIOException(ex);}
		}
		public void addPermission(String roleName,String permission){
			for(RoleRepresentation r:roles)if(r.name.equals(roleName))try{
				if(r.permissions.contains(permission))throw new IllegalStateException(r.name+" already has permission "+permission);
				else{
					r.permissions.add(permission);
					StringBuilder s=new StringBuilder(Files.readString(location.toPath()));
					int index=ParseUtils.indexOf(s,Pattern.compile(Pattern.quote(roleName)+"\\s*\\("),StandardSkipper.SYNTAX,ParseUtils.indexOf(s,ParseUtils.createSubClassPattern("Role"),StandardSkipper.SYNTAX));
					Matcher m=Pattern.compile("\\(\\)\\s*->\\s*new Permission\\s*\\[\\]\\s*\\{(\\s*)(.)").matcher(s);
					m.find(index);
					s.insert(m.end(1),permission+(m.group(2).equals("}")?"":",")+m.group(1));
					Files.writeString(location.toPath(),s);
				}
				return;
			}catch(IOException ex){throw new UncheckedIOException(ex);}
			throw new IllegalArgumentException("There is no role "+roleName);
		}
		public void removePermission(String roleName,String permission){
			for(RoleRepresentation r:roles)if(r.name.equals(roleName))try{
				if(r.permissions.contains(permission)){
					r.permissions.remove(permission);
					String s=Files.readString(location.toPath());
					int index=ParseUtils.indexOf(s,Pattern.compile(Pattern.quote(roleName)+"\\s*\\("),StandardSkipper.SYNTAX,ParseUtils.indexOf(s,ParseUtils.createSubClassPattern("Role"),StandardSkipper.SYNTAX));
					Matcher m=Pattern.compile("(\\s*,?\\s*)"+Pattern.quote(permission)+"(\\s*,?\\s*)").matcher(s);
					m.find(index);
					Files.writeString(location.toPath(),m.replaceAll(result->{
						if(result.start()<index)return result.group();
						return result.group(1).isBlank()?result.group(1):result.group(2);
					}));
				}else throw new IllegalStateException(r.name+" does not have permission "+permission);
				return;
			}catch(IOException ex){throw new UncheckedIOException(ex);}
			throw new IllegalArgumentException("There is no role "+roleName);
		}
		public void addRole(String name,String...permissions){
			try{
				RoleRepresentation r=new RoleRepresentation(name,new TreeSet<>(Arrays.asList(permissions)),null);
				StringBuilder s=new StringBuilder(Files.readString(location.toPath()));
				Files.writeString(location.toPath(),s);
				roles.add(r);
			}catch(IOException ex){throw new UncheckedIOException(ex);}
		}
	}
	public static class EditorNode extends ProjectNode{
		public EditorNode(File file){
			super(file);
			//TODO: implement EditorNode
		}
	}
	public static class Problem{
		public static enum ProblemType{
			ERROR,
			WARNING,
			INFO;
		}
		public final String message;
		public final ProblemType type;
		public Problem(String message,ProblemType type){
			this.message=message;
			this.type=type;
		}
	}
	public ArrayList<ProjectNode>nodes=new ArrayList<>();
	public File projectFolder;
	/**
	 * @param projectFolder - the project folder. It is recommended to pass <b>src/main/java</b> path:
	 */
	public ProjectGraph(File projectFolder){
		this.projectFolder=projectFolder;
		projectFolder.mkdirs();
		try{
			Files.walkFileTree(projectFolder.toPath(),new SimpleFileVisitor<Path>(){
				private final Pattern editablePattern=Pattern.compile("\\W?Editable\\W?");
				private final Pattern processablePattern=Pattern.compile("\\WProcessable\\W");
				public FileVisitResult visitFile(Path file,BasicFileAttributes attrs)throws IOException{
					if(file.toString().endsWith(".java")){
						//TODO: scan the java-file
						Matcher m=ParseUtils.classDefPattern.matcher(Files.readString(file));
						if(m.find()){
							if(m.group(1).equals("Main")){
								nodes.add(new PermissionsNode(file.toFile()));
								nodes.add(new RolesNode(file.toFile(),(PermissionsNode)nodes.getLast()));
							// }else if(m.group(2)!=null&&processablePattern.matcher(m.group(2)).find())nodes.add(new EditableNode(file.toFile())); //TODO: add ProcessableNode
							}else if(m.group(2)!=null&&editablePattern.matcher(m.group(2)).find())nodes.add(new EditableNode(file.toFile()));
							System.err.println("Parsed: "+m.group(1));
						}
					}
					return FileVisitResult.CONTINUE;
				}
			});
			System.err.println("Parsing completed!");
		}catch(IOException ex){throw new UncheckedIOException(ex);}
	}
	public EditableNode createEditableNode(String name,String objectName,EditableNode.Property...properties){
		File file=new File(projectFolder,"com");
		if(file.isDirectory())file=new File(Stream.of(file.listFiles()).filter(f->f.isDirectory()).findAny().get(),"editables/registered/"+name+".java");
		else file=new File(projectFolder,name+".java");
		file.getParentFile().mkdirs();
		EditableNode n=new EditableNode(file,objectName,properties);
		nodes.add(n);
		return n;
	}
	public ArrayList<Problem>findProblems(){
		ArrayList<Problem>a=new ArrayList<>();
		//TODO: find problems
		return a;
	}
	public void show(){
		try{
			UIManager.setLookAndFeel(new NimbusLookAndFeel());
		}catch(UnsupportedLookAndFeelException ex){throw new AssertionError("The BPALookAndFeel must be supported.",ex);}
		JFrame f=new JFrame();
		f.setUndecorated(true);
		f.setSize(Root.SCREEN_SIZE);
		f.setLayout(null);
		JButton analyze=new JButton("Анализировать");
		Wrapper<TaskAnalyzer>analyzer=new Wrapper<>(null);
		analyze.addActionListener(e->{
			if(analyzer.var==null){
				JFileChooser fc=new JFileChooser(new File(System.getProperty("user.home")+"/Downloads"));
				fc.showOpenDialog(f);
				analyzer.var=new TaskAnalyzer(this,fc.getSelectedFile());
				analyzer.var.analyze();
			}
			analyzer.var.show();
		});
		analyze.setBounds(0,f.getHeight()*9/10,f.getWidth()/4,f.getHeight()/15);
		analyze.setBackground(Color.DARK_GRAY);
		analyze.setForeground(Color.WHITE);
		f.add(analyze);
		JTabbedPane p=new JTabbedPane();
		p.setSize(f.getWidth(),f.getHeight()*4/5);
		JPanel objects=new JPanel();
		// objects.add();
		p.addTab("Объекты",objects);
		JPanel access=new JPanel();
		p.addTab("Доступ",access);
		JPanel problems=new JPanel();
		problems.setBackground(Color.BLACK);
		JScrollPane sProblems=SprintUI.createList(problems,5);
		sProblems.setBounds(f.getWidth()/4,f.getHeight()*4/5,f.getWidth()*3/4,f.getHeight()/5);
		class P extends HButton{
			public P(Problem p){
				setText(p.message);
				setBackground(Color.BLACK);
				setForeground(switch(p.type){
					case ERROR->Color.RED;
					case WARNING->Color.YELLOW;
					case INFO->Color.GREEN;
				});
				problems.add(this);
			}
		}
		f.add(sProblems);
		f.add(p);
		f.setVisible(true);
		while(f.isVisible())Thread.onSpinWait();
		System.exit(0);
	}
}
