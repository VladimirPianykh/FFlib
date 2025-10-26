package com.bpa4j.util.codegen.legacy;

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
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.bpa4j.Wrapper;
import com.bpa4j.util.ParseUtils;

@SuppressWarnings("PMD.ExceptionAsFlowControl")
public class EditableNodeLegacy extends ClassNodeLegacy{
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
		public void changeName(String name,EditableNodeLegacy n){
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
		public void changeType(PropertyType type,EditableNodeLegacy n){
			try{
				while(!Files.isWritable(n.location.toPath()))Thread.onSpinWait();
				StringBuilder s=new StringBuilder(Files.readString(n.location.toPath()));
				if(this.type==null){
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
	public EditableNodeLegacy(File file){
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
	public EditableNodeLegacy(File file,String objectName,Property...properties){
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