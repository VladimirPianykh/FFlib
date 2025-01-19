package com.bpa4j.util;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.text.PDFTextStripper;

import com.bpa4j.Wrapper;
import com.bpa4j.core.Root;
import com.bpa4j.ui.Message;
import com.bpa4j.util.TaskAnalyzer.Property.PropertyType;

public class TaskAnalyzer{
	public static interface AnalysisResult{
		public boolean checkCompletion(File projectFolder);
		public boolean generate(String prompt,File projectFolder);
	}
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
	public static class NewObjectResult implements AnalysisResult{
		public ArrayList<Property>properties;
		public String name;
		public NewObjectResult(String name,Property...properties){
			this.name=name;
			this.properties=new ArrayList<>(Arrays.asList(properties));
		}
		public boolean generate(String prompt,File projectFolder){
			if(prompt.isBlank())return false;
			Wrapper<Integer>i=new Wrapper<>(0);
			String s="""
			package com.ntoproject.editables.registered;
			
			import com.bpa4j.core.Data.Editable;
			import com.bpa4j.editor.EditorEntry;
			
			public class %s extends Editable{
			"""+
			properties.stream().map(p->p.getCode("var"+(++i.var))).collect(Collectors.joining("\n"))+
			"""
				public %s(){
					super("Новый %s);
				}
			}
			""";
			try{
				File path=new File(projectFolder,"com");
				if(path.isDirectory())path=new File(Stream.of(path.listFiles()).filter(f->f.isDirectory()).findAny().get(),"editables/registered/"+prompt+".java");
				else path=new File(projectFolder,prompt+".java");
				path.createNewFile();
				if(name.isEmpty()){
					String[]p=prompt.split("\s,?*");
					Files.writeString(path.toPath(),String.format(s,p[0],p[0],p[1]));
				}else Files.writeString(path.toPath(),String.format(s,prompt,prompt,name.toLowerCase()));
				return true;
			}catch(IOException ex){return false;}
		}
		public boolean checkCompletion(File projectFolder){
			Wrapper<Boolean>b=new Wrapper<Boolean>(false);
			try{
				Files.walkFileTree(projectFolder.toPath(),new SimpleFileVisitor<Path>() {
					public FileVisitResult visitFile(Path file,BasicFileAttributes attrs)throws IOException{
						if(file.toString().endsWith(".java")){
							String s=Files.readString(file);
							if(s.contains("extends Editable")&&s.contains(name.toLowerCase().trim())){
								b.var=true;
								return FileVisitResult.TERMINATE;
							}
						}
						return FileVisitResult.CONTINUE;
					}
				});
			}catch(IOException ex){throw new UncheckedIOException(ex);}
			return b.var;
		}
		public String toString(){
			return(name.isEmpty()?"Новый объект [имя, отображаемое имя]":"Новый объект \""+name+"\" [имя]")+
				(properties.isEmpty()?" без свойств":" со свойствами:\n"+properties.stream().map(p->p.toString()).collect(Collectors.joining("\n")));
		}
	}
	public static class AddPropertyResult implements AnalysisResult{
		public Property property;
		public String objectName;
		public AddPropertyResult(Property property,String objectName){
			this.property=property;
			this.objectName=objectName;
		}
		private File findFile(File parent)throws IOException{
			Pattern p=Pattern.compile("([a-zA-Z_]+) extends Editable");
			Wrapper<File>w=new Wrapper<File>(null);
			Files.walkFileTree(parent.toPath(),new FileVisitor<Path>(){
				public FileVisitResult preVisitDirectory(Path dir,BasicFileAttributes attrs){return FileVisitResult.CONTINUE;}
				public FileVisitResult visitFile(Path file,BasicFileAttributes attrs) throws IOException {
					if(file.toString().endsWith(".java")){
						String s=Files.readString(file);
						Matcher m=p.matcher(s);
						if(m.find()&&s.indexOf(m.group(1)+"()")<s.lastIndexOf(objectName)){
							w.var=file.toFile();
							return FileVisitResult.TERMINATE;
						}
					}
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult visitFileFailed(Path file, IOException ex) throws IOException {
					// TODO Auto-generated method stub
					throw new UnsupportedOperationException("Unimplemented method 'visitFileFailed'");
				}
				public FileVisitResult postVisitDirectory(Path dir,IOException ex){return FileVisitResult.CONTINUE;}
			});
			if(w.var==null)throw new FileNotFoundException();
			return w.var;
		}
		public boolean generate(String prompt,File projectFolder){
			if(prompt.isBlank())return false;
			try{
				File f=findFile(projectFolder);
				StringBuilder s=new StringBuilder(Files.readString(f.toPath()));
				s.insert(s.indexOf("extends Editable{")+"extends Editable{".length(),"\n\t\t"+property.getCode(prompt));
				Files.writeString(f.toPath(),s.toString(),StandardOpenOption.CREATE,StandardOpenOption.WRITE);
				return true;
			}catch(IOException ex){
				new Message("Cannot find file with editable \""+objectName+" \"",Color.RED);
				return false;
			}
			//TODO: add property into the file
		}
		public boolean checkCompletion(File projectFolder){
			try{
				String s=Files.readString(findFile(projectFolder).toPath());
				return s.contains(property.name)&&(property.type==null||s.contains(property.type.typeName));
			}catch(IOException ex){return false;}
		}
		public String toString(){
			return "Добавить свойство \""+property.name+"\" объекту \""+objectName+"\" [название переменной]";
		}
	}
	public static class UndefinedResult implements AnalysisResult{
		private final String text;
		public UndefinedResult(String text){this.text=text.indent(-100);}
		public String toString(){return text;}
		public boolean checkCompletion(File projectFolder){return false;}
		public boolean generate(String prompt,File projectFolder){
			throw new UnsupportedOperationException("TaskAnalyzer cannot handle such tasks.");
		}
	}
	private String text;
	private File projectFolder=new File(System.getProperty("user.home")+"/Downloads/generated");
	private final ArrayList<AnalysisResult>results=new ArrayList<>();
	public TaskAnalyzer(String text){this.text=text;}
	public TaskAnalyzer(File f){
		try{
			//TODO: extract text from the document
			if(f.getName().endsWith(".txt"))text=Files.readString(f.toPath());
			else if(f.getName().endsWith(".pdf"))text=new PDFTextStripper().getText(Loader.loadPDF(f));
			else throw new UnsupportedOperationException();
		}catch(IOException ex){throw new UncheckedIOException(ex);}
	}
	private Property extractProperty(String s,boolean inQuotes){
		PropertyType type=null;
		String lc=s.toLowerCase();
		if(lc.contains("время"))type=PropertyType.DATETIME;
		else if(lc.contains("дата"))type=PropertyType.DATE;
		else for(String t:new String[]{"целое","количество","кол-во","цена"})if(lc.contains(t))type=PropertyType.INT;
		if(type==null)for(String t:new String[]{"дробное","вещественное","число"})if(lc.contains(t))type=PropertyType.DOUBLE;
		if(type==null)for(String t:new String[]{"текст","строк","строч"})if(lc.contains(t))type=PropertyType.STRING;
		if(type==null)for(String t:new String[]{"булев","логиче","флаг"," ли "})if(lc.contains(t))type=PropertyType.BOOL;
		return inQuotes?new Property(Pattern.compile("\"(.*?)\"").matcher(lc).results().map(e->e.group(1)).findAny().orElse(""),type)
			:new Property(Pattern.compile("(.*)\\(.*\\)").matcher(lc).results().map(e->e.group(1)).findAny().orElse(""),type);
	}
	private AnalysisResult extractUpgradeAction(String s,String objectName){
		Matcher m=Pattern.compile("добавить.*\"(.*?)\"",Pattern.DOTALL+Pattern.CASE_INSENSITIVE+Pattern.UNICODE_CASE).matcher(s);
		if(m.find()){
			Property p=extractProperty(s,true);
			return p.name.isEmpty()?new UndefinedResult(s):new AddPropertyResult(p,objectName);
		}else{
			//TODO: extract other upgrade actions
			return new UndefinedResult(s);
		}
	}
	private void processParagraph(String s){
		s=s+"::sub";
		Matcher name=Pattern.compile("\"(.*?)\"").matcher(s);
		name.find();
		ArrayList<Property>a=new ArrayList<>();
		List<MatchResult>l=Pattern.compile("(?:([a-zA-Z])\\. |::sub)").matcher(s).results().toList();
		Matcher m=Pattern.compile("(?:доработать|дополнить|улучшить|добавить.*?в).*объект[^\"]\"(.*?)\"",Pattern.DOTALL+Pattern.CASE_INSENSITIVE+Pattern.UNICODE_CASE).matcher(s);
		if(m.find()){
			//TODO: add object completion
			for(int i=0;i<l.size()-1;++i)results.add(extractUpgradeAction(s.substring(l.get(i).end(),l.get(i+1).start()),m.group(1)));
		}else if((m=Pattern.compile("(?:объект|справочник)",Pattern.CASE_INSENSITIVE+Pattern.UNICODE_CASE).matcher(s)).find()){
			for(int i=0;i<l.size()-1;++i)a.add(extractProperty(s.substring(l.get(i).end(),l.get(i+1).start()),false));
			results.add(new NewObjectResult(name.hasMatch()?name.group(1):"",a.toArray(new Property[0])));
		}else results.add(new UndefinedResult(s));
	}
	/**
	 * Sets the project folder. It is recommended to set it to <b>src/java</b> path:
	 * <pre>
	 * {@code new File(ApplicationClass.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParent().getParent();}
	 * </pre>
	 * @param f
	 */
	public TaskAnalyzer setProjectFolder(File f){projectFolder=f;return this;}
	@SuppressWarnings("PMD.SystemPrintln")
	public void analyze(){
		String lcText=text.toLowerCase();
		if(!lcText.contains("::paragraph")){
			int t=lcText.indexOf("тестов");
			if(t==-1)text=text+"::paragraph";
			else{
				StringBuilder b=new StringBuilder(text);
				b.insert(t,"::paragraph");
				text=b.toString();
			}
		}
		lcText=text.toLowerCase();
		List<MatchResult>l=Pattern.compile("(?:(\\d+)\\. |::paragraph)").matcher(text).region(lcText.indexOf("техническое задание"),text.length()).results().toList();
		for(int i=0;i<l.size()-1;++i){
			processParagraph(text.substring(l.get(i).end(),l.get(i+1).start()));
			if(l.get(i+1).group(1)==null)break;
		}
		results.sort((r1,r2)->r1 instanceof UndefinedResult?1:-1);
		projectFolder.mkdirs();
		JFrame frame=new JFrame("Task analysis");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setExtendedState(6);
		frame.setLayout(new GridLayout());
		JPanel list=new JPanel();
		for(AnalysisResult r:results){
			JPanel p=new JPanel(new BorderLayout());
			p.setBorder(BorderFactory.createLineBorder(r instanceof UndefinedResult?Color.ORANGE:Color.GREEN));
			JTextArea t=new JTextArea(r.toString());
			t.setFocusable(false);
			t.setEditable(false);
			t.setFont(new Font(Font.DIALOG,Font.ITALIC,Root.SCREEN_SIZE.height/40));
			t.setPreferredSize(new Dimension(r.toString().lines().mapToInt(line->t.getFontMetrics(t.getFont()).stringWidth(line)).max().getAsInt(),(int)r.toString().lines().count()*t.getFontMetrics(t.getFont()).getHeight()));
			JScrollPane s=new JScrollPane(t,JScrollPane.VERTICAL_SCROLLBAR_NEVER,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			s.setPreferredSize(new Dimension(Root.SCREEN_SIZE.width,(int)r.toString().lines().count()*t.getFontMetrics(t.getFont()).getHeight()+s.getHorizontalScrollBar().getHeight()));
			p.add(s,BorderLayout.NORTH);
			if(!(r instanceof UndefinedResult)){
				boolean completed=r.checkCompletion(projectFolder);
				JPanel input=new JPanel(new GridLayout());
				input.setPreferredSize(new Dimension(Root.SCREEN_SIZE.width,Root.SCREEN_SIZE.height/15));
				JTextField prompt=new JTextField();
				prompt.setFocusable(!completed);
				prompt.setEditable(!completed);
				JButton confirm=new JButton("Подтвердить");
				if(completed){
					prompt.setBackground(Color.GREEN);
					confirm.setBackground(Color.GREEN);
					confirm.setFocusable(false);
				}else{
					ActionListener a=new ActionListener(){
						public void actionPerformed(ActionEvent e){
							if(r.generate(prompt.getText(),projectFolder)){
								prompt.setBackground(Color.GREEN);
								prompt.setFocusable(false);
								prompt.removeActionListener(this);
								confirm.setBackground(Color.GREEN);
								confirm.setFocusable(false);
								confirm.removeActionListener(this);
								input.repaint();
							}
						}
					};
					prompt.addActionListener(a);
					confirm.addActionListener(a);
				}
				input.add(prompt);
				input.add(confirm);
				p.add(input,BorderLayout.SOUTH);
			}
			list.add(p);
		}
		list.setPreferredSize(new Dimension(Root.SCREEN_SIZE.width,Stream.of(list.getComponents()).mapToInt(c->c.getPreferredSize().height+Root.SCREEN_SIZE.height/30).sum()));
		JScrollPane s=new JScrollPane(list,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		s.getVerticalScrollBar().setUnitIncrement(Root.SCREEN_SIZE.height/40);
		frame.add(s);
		list.revalidate();
		frame.setVisible(true);
	}
}
