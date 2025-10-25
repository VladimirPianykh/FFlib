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
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.expr.TypeExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;

public abstract class ClassNodeV2 extends ProjectNode {
	public String name;
	
	public ClassNodeV2(File location) {
		super(location);
		name = location.getName().substring(0, location.getName().lastIndexOf('.'));
	}
	
	public synchronized void changeNameIn(ProjectGraphV2 project, String name) {
		try {
			if (this.name.equals(name)) return;
			File f = new File(location.getParent() + "/" + name + ".java");
			if (f.exists()) return;
			location.renameTo(f);
			location = f;
			String prevName = this.name;
			
			Files.walkFileTree(project.projectFolder.toPath(), new SimpleFileVisitor<Path>() {
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					if (file.toString().endsWith(".java")) {
						while (!Files.isWritable(file)) Thread.onSpinWait();
						CompilationUnit cu = StaticJavaParser.parse(file);
						
						// Найти все использования имени класса
						cu.findAll(SimpleName.class).forEach(typeExpr -> {
							if (typeExpr.getIdentifier().replaceAll(".*\\.","").equals(prevName)) {
								typeExpr.setIdentifier(name); //TODO: check whether this is correct (it can be wrong, because type is sometimes a fully-qualified class name)
							}
						});
						
						Files.writeString(file, cu.toString());
					}
					return FileVisitResult.CONTINUE;
				}
			});
			this.name = name;
		} catch (IOException ex) {
			throw new UncheckedIOException(ex);
		}
	}
}
