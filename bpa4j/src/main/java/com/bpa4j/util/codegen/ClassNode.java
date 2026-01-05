package com.bpa4j.util.codegen;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.SimpleName;

import lombok.Getter;
import lombok.Setter;

public abstract class ClassNode<T extends ClassNode<T>> implements ProjectNode<T>{
	public static abstract class ClassPhysicalNode<V extends ClassNode<V>> implements PhysicalNode<V>{
		protected File file;
		public ClassPhysicalNode(File file){
			this.file=file;
		}
		@Override
		public void clear(){
			file.delete();
		}
		@Override
		public boolean exists(){
			return file.exists();
		}
		public void rename(ProjectGraph project,String newName,String oldName){
			try{
				if(oldName.equals(newName)) return;
				File f=new File(file.getParent()+"/"+newName+".java");
				if(f.exists()) return;
				file.renameTo(f);
				file=f;

				Files.walkFileTree(project.projectFolder.toPath(),new SimpleFileVisitor<Path>(){
					public FileVisitResult visitFile(Path file,BasicFileAttributes attrs) throws IOException{
						if(file.toString().endsWith(".java")){
							while(!Files.isWritable(file))
								Thread.onSpinWait();
							CompilationUnit cu=StaticJavaParser.parse(file);

							// Найти все использования имени класса
							cu.findAll(SimpleName.class).forEach(typeExpr->{
								if(typeExpr.getIdentifier().replaceAll(".*\\.","").equals(oldName)){
									typeExpr.setIdentifier(newName);
								}
							});

							Files.writeString(file,cu.toString());
						}
						return FileVisitResult.CONTINUE;
					}
				});
			}catch(IOException ex){
				throw new UncheckedIOException(ex);
			}
		}
	}

	public static class ClassModel<V extends ClassNode<V>> implements NodeModel<V>{
		@Getter
		@Setter
		protected String name;
		public ClassModel(String name){
			this.name=name;
		}
	}

	protected final PhysicalNode<T> physicalNode;
	protected final NodeModel<T> model;

	public ClassNode(PhysicalNode<T> physicalNode){
		this.physicalNode=physicalNode;
		this.model=physicalNode.load();
	}

	@Override
	public PhysicalNode<T> getPhysicalRepresentation(){
		return physicalNode;
	}

	@Override
	public NodeModel<T> getModel(){
		return model;
	}

	public synchronized void changeNameIn(ProjectGraph project,String name){
		String oldName=((ClassModel<T>)model).getName();
		((ClassModel<T>)model).setName(name);
		((ClassPhysicalNode<T>)physicalNode).rename(project,name,oldName);
	}
}
