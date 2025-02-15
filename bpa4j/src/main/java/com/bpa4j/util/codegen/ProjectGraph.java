package com.bpa4j.util.codegen;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.TransferHandler;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

import com.bpa4j.Wrapper;
import com.bpa4j.core.Root;
import com.bpa4j.ui.Message;
import com.bpa4j.util.ParseUtils;
import com.bpa4j.util.SprintUI;
import com.bpa4j.util.ParseUtils.StandardSkipper;
import com.bpa4j.util.codegen.ProjectGraph.EditableNode.Property;
import com.bpa4j.util.codegen.ProjectGraph.EditableNode.Property.PropertyType;
import com.bpa4j.util.codegen.ProjectGraph.RolesNode.RoleRepresentation;

public class ProjectGraph{
	public static abstract class ProjectNode{
		public String name;
		public File location;
		public ProjectNode(File location){
			name=location.getName().substring(0,location.getName().lastIndexOf('.'));
			this.location=location;
		}
	}
	public static abstract class ClassNode extends ProjectNode{
		public ClassNode(File location){super(location);}
		public synchronized void changeNameIn(ProjectGraph project,String name){
			try{
				if(this.name.equals(name))return;
				File f=new File(location.getParent()+"/"+name+".java");
				if(f.exists())return;
				location.renameTo(f);
				location=f;
				String prevName=this.name;
				Files.walkFileTree(project.projectFolder.toPath(),new SimpleFileVisitor<Path>(){
					public FileVisitResult visitFile(Path file,BasicFileAttributes attrs)throws IOException{
						if(file.toString().endsWith(".java")){
							while(!Files.isWritable(file))Thread.onSpinWait();
							Files.writeString(file,ParseUtils.replaceAll(Files.readString(file),Pattern.compile("(\\W)"+Pattern.quote(prevName)+"(\\W)"),"$1"+Matcher.quoteReplacement(name)+"$2",StandardSkipper.SYNTAX));
						}
						return FileVisitResult.CONTINUE;
					}
				});
				this.name=name;
			}catch(IOException ex){throw new UncheckedIOException(ex);}
		}
	}
	@SuppressWarnings("PMD.ExceptionAsFlowControl")
	public static class EditableNode extends ClassNode{
		public static class Property{
			public static enum PropertyType{
				STRING("String"),
				INT("int"),
				DOUBLE("double"),
				BOOL("boolean"),
				DATE("LocalDate"),
				DATETIME("LocalDateTime");
				private final String typeName;
				private PropertyType(String typeName){this.typeName=typeName;}
				public String toString(){return typeName;}
			}
			public PropertyType type;
			public String name;
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
			public void changeNameFor(String name,EditableNode n){
				try{
					while(!Files.isWritable(n.location.toPath()))Thread.onSpinWait();
					StringBuilder s=new StringBuilder(Files.readString(n.location.toPath()));
					if(type==null){
						Matcher m=Pattern.compile("//\\s*TODO: add property\\s*\"\\s*("+Pattern.quote(this.name)+")\\s*\"").matcher(s);
						if(m.find()){
							s.replace(m.start(1),m.end(1),name);
							Files.writeString(n.location.toPath(),s);
							this.name=name;
							return;
						}
					}
					Matcher m=Pattern.compile("@EditorEntry\\s*\\(\\s*translation\\s*=\\s*\"("+Pattern.quote(this.name)+")\".*?\\.*?(?:\\w+ )*.*?;",Pattern.DOTALL).matcher(s);
					if(m.find()){
						s.replace(m.start(1),m.end(1),name);
						Files.writeString(n.location.toPath(),s);
						this.name=name;
					}
				}catch(IOException ex){throw new UncheckedIOException(ex);}
			}
			public void changeTypeFor(PropertyType type,EditableNode n){
				try{
					while(!Files.isWritable(n.location.toPath()))Thread.onSpinWait();
					StringBuilder s=new StringBuilder(Files.readString(n.location.toPath()));
					if(this.type==null){
						//UNTESTED
						MatchResult r=ParseUtils.find(s,Pattern.compile("//\\s*TODO: add property\\s*\"\\s*"+Pattern.quote(name)+"\\s*\"")).orElse(null);
						if(r==null)return; //property has already been implemented and CANNOT be changed
						s.replace(r.start(),r.end(),String.format("""
							@EditorEntry(translation="%s")
							public %s gvar%d;
						""",name,type.toString(),(int)(Math.random()*9999900+100)));
					}else{
						Matcher m=Pattern.compile("@EditorEntry\\s*\\(\\s*translation\\s*=\\s*\""+Pattern.quote(name)+"\".*?\\).*?(?:\\w+ )*(\\w+) \\w+.*?;",Pattern.DOTALL).matcher(s);
						m.find();
						s.replace(m.start(1),m.end(1),type.toString());
					}
					Files.writeString(n.location.toPath(),s);
					this.type=type;
				}catch(IOException ex){throw new UncheckedIOException(ex);}
			}
			public String toString(){return name+" ("+(type==null?"???":type.toString())+")";}
		}
		public String objectName;
		public ArrayList<Property>properties=new ArrayList<>();
		/**
		 * Resolves an existing node from the file.
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
			}catch(IOException ex){throw new UncheckedIOException(ex);}
		}
		/**
		 * Constructs a new node with the designated file.
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
						super("Нов %s");
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
				while(!Files.isWritable(location.toPath()))Thread.onSpinWait();
				StringBuilder s=new StringBuilder(Files.readString(location.toPath()));
				Matcher m=ParseUtils.createSubClassPattern("Editable").matcher(s);
				m.find();
				s.insert(m.end(),"\n"+property.getCode(varName));
				Files.writeString(location.toPath(),s.toString(),StandardOpenOption.CREATE);
				properties.add(property);
			}catch(IOException ex){throw new UncheckedIOException(ex);}
		}
		public void addProperties(Property...properties){
			try{
				while(!Files.isWritable(location.toPath()))Thread.onSpinWait();
				StringBuilder s=new StringBuilder(Files.readString(location.toPath()));
				Wrapper<Integer>index=new Wrapper<Integer>(0);
				Matcher m=ParseUtils.createSubClassPattern("Editable").matcher(s);
				m.find();
				s.insert(m.end(),Stream.of(properties).map(p->p.getCode("iVar"+(++index.var))).collect(Collectors.joining("\n")));
				Files.writeString(location.toPath(),s.toString(),StandardOpenOption.CREATE,StandardOpenOption.WRITE);
				this.properties.addAll(Arrays.asList(properties));
			}catch(IOException ex){throw new UncheckedIOException(ex);}
		}
		public void removeProperty(Property property){
			try{
				while(!Files.isWritable(location.toPath()))Thread.onSpinWait();
				String s=Files.readString(location.toPath());
				Files.writeString(location.toPath(),Pattern.compile("@EditorEntry\\s*\\(\\s*translation\\s*=\\s*\""+Pattern.quote(property.name)+"\".*?\\.*?(?:\\w+ )*.*?;\\s*",Pattern.DOTALL).matcher(s).replaceFirst(""));
			}catch(IOException ex){throw new UncheckedIOException(ex);}
		}
		public void changeObjectName(String objectName){
			try{
				while(!Files.isWritable(location.toPath()))Thread.onSpinWait();
				Files.writeString(location.toPath(),Pattern.compile("("+name+"\\s*\\(\\)\\s*\\{[\n\\s]*super\\(\"(?:нов[^ ]* )?)+"+this.objectName+"\"",Pattern.CASE_INSENSITIVE+Pattern.UNICODE_CASE).matcher(Files.readString(location.toPath())).replaceAll("$1"+Matcher.quoteReplacement(objectName)+"\""));
				this.objectName=objectName;
			}catch(IOException ex){throw new UncheckedIOException(ex);}
		}
	}
	public static class PermissionsNode extends ProjectNode{
		public ArrayList<String>permissions;
		public PermissionsNode(File file){
			super(file);
			try{
				String s=ParseUtils.findFirstBlock(Pattern.compile("public enum \\w+ implements.*?Permission"),Files.readString(file.toPath()),'{','}',StandardSkipper.SYNTAX);
				String a=s
					.substring(0,s.indexOf(';'))
					.replaceAll("\\s+","");
				permissions=new ArrayList<>(Arrays.asList(a.isBlank()?new String[0]:a.split(",")));
				permissions.sort((a1,a2)->-1);
			}catch(IOException ex){throw new UncheckedIOException(ex);}
		}
		public void addPermission(String permission){
			//UNTESTED
			try{
				if(permissions.contains(permission))throw new IllegalStateException(name+" already has exists.");
				while(!Files.isWritable(location.toPath()))Thread.onSpinWait();
				StringBuilder s=new StringBuilder(Files.readString(location.toPath()));
				MatchResult r=ParseUtils.find(
					s,
					Pattern.compile("public enum \\w+ implements.*?Permission\\s*\\{(\\s*)"),
					StandardSkipper.SYNTAX
				).get();
				s.insert(r.start(1),r.group(1)+permission+",");
				Files.writeString(location.toPath(),s);
				permissions.add(permission);
			}catch(IOException ex){throw new UncheckedIOException(ex);}
		}
		public void removePermission(String permission){
			try{
				if(!permissions.contains(permission))throw new IllegalStateException(name+" does not exist.");
				while(!Files.isWritable(location.toPath()))Thread.onSpinWait();
				StringBuilder s=new StringBuilder(Files.readString(location.toPath()));
				StringBuilder result=new StringBuilder();
				Matcher m=Pattern.compile(Pattern.quote(permission)+",\\s*").matcher(s);
				m.find(ParseUtils.find(
					s,
					Pattern.compile("public enum \\w+ implements.*?Permission\\s*\\{"),
					StandardSkipper.SYNTAX
				).get().end());
				m.appendReplacement(result,"").appendTail(result);
				Files.writeString(location.toPath(),result);
				permissions.remove(permission);
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
					Pattern.compile("public enum \\w+ implements.*?Role"),
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
					int d=ParseUtils.find(entry.getValue(),Pattern.compile(","),StandardSkipper.OUTERSCOPE).get().start();
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
						roles.add(new RoleRepresentation(name,s==null?null:s.isBlank()?new TreeSet<>():new TreeSet<>(Arrays.asList(s.replaceAll("\\s+","").split(","))),null));
					}
				}
			}catch(IOException ex){throw new UncheckedIOException(ex);}
		}
		public void addPermission(String roleName,String permission){
			for(RoleRepresentation r:roles)if(r.name.equals(roleName))try{
				if(r.permissions.contains(permission))throw new IllegalStateException(r.name+" already has permission "+permission+".");
				r.permissions.add(permission);
				while(!Files.isWritable(location.toPath()))Thread.onSpinWait();
				StringBuilder s=new StringBuilder(Files.readString(location.toPath()));
				int index=ParseUtils.find(
					s,
					Pattern.compile(Pattern.quote(roleName)+"\\s*\\("),
					StandardSkipper.SYNTAX,
					ParseUtils.find(
						s,
						ParseUtils.createSubClassPattern("Role"),
						StandardSkipper.SYNTAX
					).get().start()
				).get().start();
				Matcher m=Pattern.compile("\\(\\)\\s*->\\s*new Permission\\s*\\[\\]\\s*\\{(\\s*)(.)").matcher(s);
				m.find(index);
				s.insert(m.end(1),permission+(m.group(2).equals("}")?"":",")+m.group(1));
				Files.writeString(location.toPath(),s);
				return;
			}catch(IOException ex){throw new UncheckedIOException(ex);}
			throw new IllegalArgumentException("There is no role "+roleName+".");
		}
		public void removePermission(String roleName,String permission){
			for(RoleRepresentation r:roles)if(r.name.equals(roleName))try{
				if(r.permissions.contains(permission)){
					r.permissions.remove(permission);
					while(!Files.isWritable(location.toPath()))Thread.onSpinWait();
					String s=Files.readString(location.toPath());
					int index=ParseUtils.find(
						s,
						Pattern.compile(Pattern.quote(roleName)+"\\s*\\("),
						StandardSkipper.SYNTAX,
						ParseUtils.find(
							s,
							ParseUtils.createSubClassPattern("Role"),
							StandardSkipper.SYNTAX
						).get().start()
					).get().start();
					Matcher m=Pattern.compile("(\\s*,?\\s*)"+Pattern.quote(permission)+"(\\s*,?\\s*)").matcher(s);
					m.find(index);
					Files.writeString(location.toPath(),m.replaceFirst(result->{
						if(result.start()<index)return result.group();
						return result.group(1).isBlank()?result.group(1):result.group(2);
					}));
				}else throw new IllegalStateException(r.name+" does not have permission "+permission);
				return;
			}catch(IOException ex){throw new UncheckedIOException(ex);}
			throw new IllegalArgumentException("There is no role "+roleName);
		}
		public RoleRepresentation addRole(String name,String...permissions){
			try{
				RoleRepresentation r=new RoleRepresentation(name,new TreeSet<>(Arrays.asList(permissions)),null);
				while(!Files.isWritable(location.toPath()))Thread.onSpinWait();
				StringBuilder s=new StringBuilder(Files.readString(location.toPath()));
				MatchResult m=ParseUtils.find(s,Pattern.compile("public enum \\w+ implements.*?Role\\s*\\{(\\s*)"),StandardSkipper.SYNTAX).get();
				s.insert(
					m.start(1),
					String.format("""
							%s(
								()->new Permission[]{%s},
								()->new Feature[]{}
							),
					""",m.group(1)+name,Stream.of(permissions).collect(Collectors.joining(",")))
				);
				//TODO: add role
				Files.writeString(location.toPath(),s);
				roles.add(r);
				return r;
			}catch(IOException ex){throw new UncheckedIOException(ex);}
		}
		public void removeRole(String name){
			try{
				while(!Files.isWritable(location.toPath()))Thread.onSpinWait();
				StringBuilder s=new StringBuilder(Files.readString(location.toPath()));
				MatchResult m=ParseUtils.find(
					s,
					Pattern.compile(name+"\\s*\\("),
					StandardSkipper.SYNTAX,
					ParseUtils.find(s,Pattern.compile("public enum \\w+ implements.*?Role\\s*\\{(\\s*)"),StandardSkipper.SYNTAX).get().end()
				).get();
				s=new StringBuilder(ParseUtils.replaceAll(
					s,
					Pattern.compile(".+",Pattern.DOTALL),
					"",
					new ParseUtils.InnerScopeSkipper('(',')'),
					m.end()
				));
				StringBuilder result=new StringBuilder();
				Matcher matcher=Pattern.compile(".*?,\\s*").matcher(s).region(m.start(),s.length());
				matcher.find();
				matcher.appendReplacement(result,"").appendTail(result);
				Files.writeString(location.toPath(),result);
				roles.removeIf(r->r.name.equals(name));
			}catch(IOException ex){throw new UncheckedIOException(ex);}
		}
	}
	public static class GroupsNode extends ProjectNode{
		public GroupsNode(File file){
			super(file);
			try{
				StringBuilder s=new StringBuilder(Files.readString(file.toPath()));
				Matcher m=Pattern.compile("EditableGroup \\w+\\s*=\\s*new EditableGroup\\(.*?(.*?,.*?,.*?)?\\)").matcher(s);
				m.find();
				s.insert(m.end(),"");
			}catch(IOException ex){throw new UncheckedIOException(ex);}
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
		public final Runnable solver;
		public Problem(String message,ProblemType type){
			this.message=message;
			this.type=type;
			solver=null;
		}
		public Problem(String message,ProblemType type,Runnable solver){
			this.message=message;
			this.type=type;
			this.solver=solver;
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
	private void fillObjectsTab(JPanel tab){
		PermissionsNode pn=(PermissionsNode)nodes.parallelStream().filter(n->n instanceof PermissionsNode).findAny().get();
		tab.setLayout(new BorderLayout());
		class B extends JPanel{
			public B(Property p,EditableNode n){
				setLayout(new GridBagLayout());
				GridBagConstraints c=new GridBagConstraints();
				c.fill=GridBagConstraints.BOTH;
				c.weighty=1;
				c.gridheight=1;
				JTextField name=new JTextField(p.name);
				name.addActionListener(e->p.changeNameFor(name.getText(),n));
				name.addFocusListener(new FocusAdapter(){
					public void focusLost(FocusEvent e){p.changeNameFor(name.getText(),n);}
				});
				c.gridwidth=2;
				c.weightx=0.5;
				add(name,c);
				JComboBox<PropertyType>type=new JComboBox<PropertyType>(PropertyType.values());
				type.setSelectedItem(p.type);
				type.addItemListener(e->{if(e.getStateChange()==ItemEvent.SELECTED)p.changeTypeFor((PropertyType)e.getItem(),n);});
				c.gridx=2;
				c.gridwidth=1;
				c.weightx=0.25;
				add(type,c);
				JPanel buttons=new JPanel();
				JButton remove=new JButton();
				remove.addActionListener(e->{
					if(p.type==null&&JOptionPane.showConfirmDialog(tab,"Удалить свойство "+p.name+"?","Удалить?",JOptionPane.OK_CANCEL_OPTION,JOptionPane.WARNING_MESSAGE)!=JOptionPane.OK_OPTION)return;
					n.removeProperty(p);
					Container parent=getParent();
					parent.remove(this);
					((JComponent)parent).getTopLevelAncestor().revalidate();
				});
				remove.setText("удалить св-во");
				remove.setBackground(Color.RED);
				remove.setFont(new Font(Font.DIALOG,Font.PLAIN,Root.SCREEN_SIZE.height/30));
				buttons.add(remove);
				c.gridx=3;
				add(buttons,c);
				setPreferredSize(new Dimension(Root.SCREEN_SIZE.width*2/3,Root.SCREEN_SIZE.height/15));
			}
		}
		class E extends JPanel{
			public E(EditableNode n){
				setBorder(BorderFactory.createTitledBorder(n.name+" ("+n.objectName+")"));
				setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
				JPanel buttons=new JPanel();
				buttons.setPreferredSize(new Dimension(Root.SCREEN_SIZE.width*2/3,Root.SCREEN_SIZE.height/30));
				JPanel addPanel=new JPanel(new GridLayout());
				JComboBox<PropertyType>addType=new JComboBox<>(PropertyType.values());
				addType.setSelectedItem(null);
				addType.setBackground(Color.GREEN);
				addType.addActionListener(e->{
					String name=JOptionPane.showInputDialog("Введите название свойства.");
					if(name==null||name.isBlank())return;
					String varName=JOptionPane.showInputDialog("Введите название переменной.");
					if(varName==null||varName.isBlank())return;
					Property nProperty=new Property(name.trim(),(PropertyType)addType.getSelectedItem());
					addType.setSelectedItem(null);
					n.addProperty(nProperty,varName);
					add(new B(nProperty,n),1);
					getTopLevelAncestor().revalidate();
				});
				addPanel.add(addType);
				buttons.add(addPanel);
				JButton remove=new JButton();
				remove.addActionListener(e->{
					if(n.properties.isEmpty()||JOptionPane.showConfirmDialog(tab,"Действительно удалить "+n.name+" (\""+n.objectName+")\"?","Удалить?",JOptionPane.OK_CANCEL_OPTION,JOptionPane.WARNING_MESSAGE)==JOptionPane.OK_OPTION){
						deleteNode(n);
						Container parent=getParent();
						parent.remove(this);
						((JComponent)parent).getTopLevelAncestor().revalidate();
					}
				});
				remove.setText("удалить");
				remove.setBackground(Color.RED);
				remove.setFont(new Font(Font.DIALOG,Font.PLAIN,Root.SCREEN_SIZE.height/50));
				buttons.add(remove);
				JButton rename=new JButton();
				rename.addActionListener(e->{
					String input=JOptionPane.showInputDialog("Введите новое название класса и отображаемое название (разделить запятой).");
					if(input==null)return;
					String[]s=input.trim().split(", ?");
					if(s.length<2||s[0].isBlank()||s[1].isBlank()){
						new Message("Введите данные правильно!",Color.RED);
						return;
					}
					s[0]=s[0].trim();
					s[1]=s[1].trim();
					setBorder(BorderFactory.createTitledBorder(s[0]+" ("+s[1]+")"));
					if(!n.name.equals(s[0]))n.changeNameIn(ProjectGraph.this,s[0]);
					if(!n.objectName.equals(s[1]))n.changeObjectName(s[1]);
				});
				rename.setText("переименовать");
				remove.setFont(new Font(Font.DIALOG,Font.PLAIN,Root.SCREEN_SIZE.height/50));
				rename.setBackground(Color.CYAN);
				rename.setForeground(Color.BLACK);
				buttons.add(rename);
				String readPermission="READ_"+n.name.toUpperCase(),createPermission="CREATE_"+n.name.toUpperCase();
				if(!(pn.permissions.contains(readPermission)&&pn.permissions.contains(createPermission))){
					JButton addPermissions=new JButton("добавить разрешения");
					addPermissions.addActionListener(e->{
						pn.addPermission(createPermission);
						pn.addPermission(readPermission);
						buttons.remove(addPermissions);
						buttons.revalidate();
						buttons.repaint();
					});
					buttons.add(addPermissions);
				}
				add(buttons);
				for(Property p:n.properties)add(new B(p,n));
			}
		}
		JPanel objList=new JPanel();
		objList.setLayout(new BoxLayout(objList,BoxLayout.Y_AXIS));
		JScrollPane sList=new JScrollPane(objList,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		sList.getVerticalScrollBar().setUnitIncrement(Root.SCREEN_SIZE.height/40);
		sList.setPreferredSize(new Dimension(Root.SCREEN_SIZE.width,Root.SCREEN_SIZE.height*3/5));
		JPanel buttons=new JPanel();
		buttons.setPreferredSize(new Dimension(Root.SCREEN_SIZE.width,Root.SCREEN_SIZE.height/10));
		JButton addButton=new JButton();
		addButton.setText("создать");
		addButton.setBackground(Color.GREEN);
		addButton.addActionListener(e->{
			String name,objectName;
			while(true){
				name=JOptionPane.showInputDialog("Введите название класса.");
				if(name==null||name.isBlank())break;
				objectName=JOptionPane.showInputDialog("Введите название объекта.");
				if(objectName==null||objectName.isBlank())break;
				objList.add(new E(createEditableNode(name,objectName)));
			}
			sList.revalidate();
		});
		buttons.add(addButton);
		tab.add(buttons,BorderLayout.SOUTH);
		tab.add(sList,BorderLayout.NORTH);
		for(ProjectNode node:nodes)if(node instanceof EditableNode)objList.add(new E((EditableNode)node));
	}
	private void fillAccessTab(JPanel tab){
		tab.setLayout(new GridLayout(1,2));
		PermissionsNode pn=(PermissionsNode)nodes.parallelStream().filter(n->n instanceof PermissionsNode).findAny().get();
		RolesNode rn=(RolesNode)nodes.parallelStream().filter(n->n instanceof RolesNode).findAny().get();
		class P extends JPanel{
			public P(String permission){
				setLayout(new GridBagLayout());
				GridBagConstraints c=new GridBagConstraints();
				c.fill=GridBagConstraints.BOTH;
				c.weighty=1;
				c.gridheight=1;
				c.gridwidth=2;
				c.weightx=0.66;
				JLabel name=new JLabel(permission);
				add(name,c);
				name.setTransferHandler(new TransferHandler("text"){
					public boolean canImport(TransferSupport support){return false;}
				});
				name.addMouseListener(new MouseAdapter(){
					public void mousePressed(MouseEvent e){
						name.getTransferHandler().exportAsDrag(name,e,TransferHandler.COPY);
					}
				});
				JButton delete=new JButton();
				delete.addActionListener(e->{
					if(JOptionPane.showConfirmDialog(tab,"Удалить разрешение "+permission+"?","Удалить?",JOptionPane.OK_CANCEL_OPTION,JOptionPane.WARNING_MESSAGE)!=JOptionPane.OK_OPTION)return;
					pn.removePermission(permission);
					Container parent=getParent();
					parent.remove(this);
					((JComponent)parent).getTopLevelAncestor().revalidate();
				});
				delete.setText("удалить");
				delete.setBackground(Color.RED);
				c.gridwidth=1;
				c.weightx=0.33;
				add(delete,c);
			}
		}
		class RP extends JPanel{
			public RP(RoleRepresentation role,String permission){
				setLayout(new GridBagLayout());
				GridBagConstraints c=new GridBagConstraints();
				c.fill=GridBagConstraints.BOTH;
				c.weighty=1;
				c.gridheight=1;
				c.gridwidth=2;
				c.weightx=0.66;
				add(new JLabel(permission),c);
				JButton delete=new JButton();
				delete.addActionListener(e->{
					rn.removePermission(role.name,permission);
					Container parent=getParent();
					parent.remove(this);
					((JComponent)parent).getTopLevelAncestor().revalidate();
				});
				delete.setText("отозвать");
				delete.setBackground(Color.DARK_GRAY);
				delete.setForeground(Color.RED);
				c.gridwidth=1;
				c.weightx=0.33;
				add(delete,c);
			}
		}
		class R extends JPanel{
			public R(RoleRepresentation role){
				setBorder(BorderFactory.createTitledBorder(role.name));
				setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
				JPanel buttons=new JPanel();
				JButton delete=new JButton();
				delete.addActionListener(e->{
					if(JOptionPane.showConfirmDialog(tab,"Удалить "+role.name+"?","Удалить?",JOptionPane.OK_CANCEL_OPTION,JOptionPane.WARNING_MESSAGE)!=JOptionPane.OK_OPTION)return;
					rn.removeRole(role.name);
					Container parent=getParent();
					parent.remove(this);
					((JComponent)parent).getTopLevelAncestor().revalidate();
				});
				delete.setBackground(Color.RED);
				buttons.add(delete);
				add(buttons);
				if(role.permissions!=null)for(String p:role.permissions)add(new RP(role,p));
				setTransferHandler(new TransferHandler(){
					public boolean canImport(TransferSupport support){
						return support.getDataFlavors()[0].getRepresentationClass().equals(String.class);
					}
					public boolean importData(TransferSupport support){
						try{
							String p="AppPermission."+support.getTransferable().getTransferData(support.getDataFlavors()[0]);
							rn.addPermission(role.name,p);
							add(new RP(role,p));
							revalidate();
							return true;
						}catch(IOException|UnsupportedFlavorException ex){throw new IllegalStateException(ex);}
					}
				});
			}
		}
		JPanel pPanel=new JPanel();
		for(String p:pn.permissions)pPanel.add(new P(p));
		tab.add(SprintUI.createList(15,pPanel));
		JPanel rPanel=new JPanel(new BorderLayout());
		JPanel rList=new JPanel();
		rList.setLayout(new BoxLayout(rList,BoxLayout.Y_AXIS));
		JPanel rButtons=new JPanel();
		JButton addRole=new JButton();
		addRole.addActionListener(e->{
			String s=JOptionPane.showInputDialog("Введите системное имя роли.");
			if(s==null)return;
			rList.add(new R(rn.addRole(s)));
			rList.revalidate();
		});
		addRole.setText("добавить роль");
		addRole.setBackground(Color.GREEN);
		rButtons.add(addRole);
		for(RoleRepresentation r:rn.roles)rList.add(new R(r));
		JScrollPane sRPanel=new JScrollPane(rList);
		sRPanel.getVerticalScrollBar().setUnitIncrement(Root.SCREEN_SIZE.height/60);
		rPanel.add(sRPanel,BorderLayout.CENTER);
		rPanel.add(rButtons,BorderLayout.SOUTH);
		tab.add(rPanel);
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
	public void deleteNode(ProjectNode n){
		n.location.delete();
		nodes.remove(n);
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
		JButton analyze=new JButton("Анализировать ТЗ");
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
		objects.addComponentListener(new ComponentAdapter(){
			public void componentShown(ComponentEvent e){
				objects.removeAll();
				fillObjectsTab(objects);
			}
		});	
		p.addTab("Объекты",objects);
		JPanel access=new JPanel();
		access.addComponentListener(new ComponentAdapter(){
			public void componentShown(ComponentEvent e){
				access.removeAll();
				fillAccessTab(access);
			}
		});	
		p.addTab("Доступ",access);
		JPanel problems=new JPanel();
		problems.setBackground(Color.BLACK);
		JScrollPane sProblems=SprintUI.createList(10,problems);
		sProblems.setBounds(f.getWidth()/4,f.getHeight()*4/5,f.getWidth()*3/4,f.getHeight()/5);
		class P extends JButton{
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
