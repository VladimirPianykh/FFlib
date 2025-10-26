package com.bpa4j.util.codegen;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Optional;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.EnumConstantDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;

public class PermissionsNodeV2 extends ProjectNode {
	public ArrayList<String> permissions;
	
	public PermissionsNodeV2(File file) {
		super(file);
		try {
			CompilationUnit cu = StaticJavaParser.parse(file);
			
			// Найти enum, который реализует интерфейс Permission
			Optional<EnumDeclaration> permissionEnum = cu.findAll(EnumDeclaration.class).stream()
				.filter(enumDecl -> enumDecl.getImplementedTypes().stream()
					.anyMatch(type -> type instanceof ClassOrInterfaceType && 
						((ClassOrInterfaceType) type).getNameAsString().contains("Permission")))
				.findFirst();
			
			if (permissionEnum.isPresent()) {
				EnumDeclaration enumDecl = permissionEnum.get();
				permissions = new ArrayList<>();
				
				// Извлечь константы enum
				for (EnumConstantDeclaration constant : enumDecl.getEntries()) {
					permissions.add(constant.getNameAsString());
				}
				
				permissions.sort((a1, a2) -> -1); // Сохраняем оригинальную сортировку
			} else {
				permissions = new ArrayList<>();
			}
		} catch (IOException ex) {
			throw new UncheckedIOException(ex);
		}
	}
	
	public void addPermission(String permission) {
		try {
			if (permissions.contains(permission)) {
				throw new IllegalStateException(permission + " already exists.");
			}
			
			while (!Files.isWritable(location.toPath())) Thread.onSpinWait();
			
			CompilationUnit cu = StaticJavaParser.parse(location);
			
			// Найти enum Permission
			Optional<EnumDeclaration> permissionEnum = cu.findAll(EnumDeclaration.class).stream()
				.filter(enumDecl -> enumDecl.getImplementedTypes().stream()
					.anyMatch(type -> type instanceof ClassOrInterfaceType && 
						((ClassOrInterfaceType) type).getNameAsString().contains("Permission")))
				.findFirst();
			
			if (permissionEnum.isPresent()) {
				EnumDeclaration enumDecl = permissionEnum.get();
				
				// Добавить новую константу
				EnumConstantDeclaration newConstant = new EnumConstantDeclaration(permission);
				enumDecl.addEntry(newConstant);
				
				Files.writeString(location.toPath(), cu.toString());
				permissions.add(permission);
			}
		} catch (IOException ex) {
			throw new UncheckedIOException(ex);
		}
	}
	
	public void removePermission(String permission) {
		try {
			if (!permissions.contains(permission)) {
				throw new IllegalStateException(permission + " does not exist.");
			}
			
			while (!Files.isWritable(location.toPath())) Thread.onSpinWait();
			
			CompilationUnit cu = StaticJavaParser.parse(location);
			
			// Найти enum Permission
			Optional<EnumDeclaration> permissionEnum = cu.findAll(EnumDeclaration.class).stream()
				.filter(enumDecl -> enumDecl.getImplementedTypes().stream()
					.anyMatch(type -> type instanceof ClassOrInterfaceType && 
						((ClassOrInterfaceType) type).getNameAsString().contains("Permission")))
				.findFirst();
			
			if (permissionEnum.isPresent()) {
				EnumDeclaration enumDecl = permissionEnum.get();
				
				// Удалить константу
				enumDecl.getEntries().removeIf(constant -> constant.getNameAsString().equals(permission));
				
				Files.writeString(location.toPath(), cu.toString());
				permissions.remove(permission);
			}
		} catch (IOException ex) {
			throw new UncheckedIOException(ex);
		}
	}
}
