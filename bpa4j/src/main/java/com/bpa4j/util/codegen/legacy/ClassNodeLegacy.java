package com.bpa4j.util.codegen.legacy;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.bpa4j.util.ParseUtils;
import com.bpa4j.util.ParseUtils.StandardSkipper;

public abstract class ClassNodeLegacy extends ProjectNodeLegacy{
	public String name;
	public ClassNodeLegacy(File location){
		super(location);
		name=location.getName().substring(0,location.getName().lastIndexOf('.'));
	}
	public synchronized void changeNameIn(ProjectGraphLegacy project,String name){
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