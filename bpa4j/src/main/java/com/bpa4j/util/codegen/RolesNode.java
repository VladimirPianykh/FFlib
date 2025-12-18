package com.bpa4j.util.codegen;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.EnumConstantDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.expr.ArrayCreationExpr;
import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;

public class RolesNode extends ProjectNode {
	public static class RoleRepresentation {
		public String name;
		public Set<String> permissions;
		public Set<String> features;
		
		public RoleRepresentation(String name, Set<String> permissions, Set<String> features) {
			this.name = name;
			this.permissions = permissions;
			this.features = features;
		}
	}
	
	public ArrayList<RoleRepresentation> roles = new ArrayList<>();
	
	public RolesNode(File file, PermissionsNode p) {
		super(file);
		try {
			CompilationUnit cu = StaticJavaParser.parse(file);
			
			// Найти enum, который реализует интерфейс Role
			Optional<EnumDeclaration> roleEnum = cu.findAll(EnumDeclaration.class).stream()
				.filter(enumDecl -> enumDecl.getImplementedTypes().stream()
					.anyMatch(type -> type instanceof ClassOrInterfaceType && 
						((ClassOrInterfaceType) type).getNameAsString().contains("Role")))
				.findFirst();
			
			if (roleEnum.isPresent()) {
				EnumDeclaration enumDecl = roleEnum.get();
				
				// Парсить каждую константу enum
				for (EnumConstantDeclaration constant : enumDecl.getEntries()) {
					String name = constant.getNameAsString();
					Set<String> permissions = new TreeSet<>();
					Set<String> features = new TreeSet<>();
					
					// Парсить аргументы константы
					if (constant.getArguments().size() >= 2) {
						// Первый аргумент - лямбда для permissions
						if (constant.getArguments().get(0) instanceof LambdaExpr) {
							LambdaExpr permissionsLambda = (LambdaExpr) constant.getArguments().get(0);
							permissions = parsePermissionsFromLambda(permissionsLambda, p);
						}
						
						// Второй аргумент - лямбда для features (пока не обрабатываем)
						// TODO: #6 Parse features
					}
					
					roles.add(new RoleRepresentation(name, permissions, features));
				}
			}
		} catch (IOException ex) {
			throw new UncheckedIOException(ex);
		}
	}
	
	private Set<String> parsePermissionsFromLambda(LambdaExpr lambda, PermissionsNode p) {
		Set<String> permissions = new TreeSet<>();
		
		// Проверить, является ли это вызовом Permission.values()
		if (lambda.getBody() instanceof ExpressionStmt) {
			ExpressionStmt stmt = (ExpressionStmt) lambda.getBody();
			if (stmt.getExpression() instanceof MethodCallExpr) {
				MethodCallExpr methodCall = (MethodCallExpr) stmt.getExpression();
				if (methodCall.getNameAsString().equals("values") && 
					methodCall.getScope().isPresent() && 
					methodCall.getScope().get() instanceof NameExpr) {
					NameExpr scope = (NameExpr) methodCall.getScope().get();
					if (scope.getNameAsString().contains("Permission")) {
						// Это Permission.values() - все разрешения
						return new TreeSet<>(p.permissions);
					}
				}
			}
		}
		
		// Иначе ищем массив разрешений
		if (lambda.getBody() instanceof ExpressionStmt) {
			ExpressionStmt stmt = (ExpressionStmt) lambda.getBody();
			if (stmt.getExpression() instanceof ArrayCreationExpr) {
				ArrayCreationExpr array = (ArrayCreationExpr) stmt.getExpression();
				array.getInitializer().ifPresent(init -> {
					for (var element : init.getValues()) {
						if (element instanceof NameExpr) {
							NameExpr nameExpr = (NameExpr) element;
							permissions.add(nameExpr.getNameAsString());
						}
					}
				});
			}
		}
		
		return permissions;
	}
	
	public void addPermission(String roleName, String permission) {
		for (RoleRepresentation r : roles) {
			if (r.name.equals(roleName)) {
				try {
					if (r.permissions.contains(permission)) {
						throw new IllegalStateException(r.name + " already has permission " + permission + ".");
					}
					
					while (!Files.isWritable(location.toPath())) Thread.onSpinWait();
					
					CompilationUnit cu = StaticJavaParser.parse(location);
					
					// Найти enum Role
					Optional<EnumDeclaration> roleEnum = cu.findAll(EnumDeclaration.class).stream()
						.filter(enumDecl -> enumDecl.getImplementedTypes().stream()
							.anyMatch(type -> type instanceof ClassOrInterfaceType && 
								((ClassOrInterfaceType) type).getNameAsString().contains("Role")))
						.findFirst();
					
					if (roleEnum.isPresent()) {
						EnumDeclaration enumDecl = roleEnum.get();
						
						// Найти константу роли
						Optional<EnumConstantDeclaration> roleConstant = enumDecl.getEntries().stream()
							.filter(constant -> constant.getNameAsString().equals(roleName))
							.findFirst();
						
						if (roleConstant.isPresent()) {
							EnumConstantDeclaration constant = roleConstant.get();
							
								// Добавить разрешение в массив permissions
								if (constant.getArguments().size() >= 1 && 
									constant.getArguments().get(0) instanceof LambdaExpr) {
									LambdaExpr permissionsLambda = (LambdaExpr) constant.getArguments().get(0);
									
									if (permissionsLambda.getBody() instanceof ExpressionStmt) {
										ExpressionStmt stmt = (ExpressionStmt) permissionsLambda.getBody();
										if (stmt.getExpression() instanceof ArrayCreationExpr) {
											ArrayCreationExpr array = (ArrayCreationExpr) stmt.getExpression();
											array.getInitializer().ifPresent(init -> {
												init.getValues().add(new NameExpr(permission));
											});
										}
									}
								}
							
							Files.writeString(location.toPath(), cu.toString());
							r.permissions.add(permission);
							return;
						}
					}
				} catch (IOException ex) {
					throw new UncheckedIOException(ex);
				}
			}
		}
		throw new IllegalArgumentException("There is no role " + roleName + ".");
	}
	
	public void removePermission(String roleName, String permission) {
		for (RoleRepresentation r : roles) {
			if (r.name.equals(roleName)) {
				try {
					if (r.permissions.contains(permission)) {
						while (!Files.isWritable(location.toPath())) Thread.onSpinWait();
						
						CompilationUnit cu = StaticJavaParser.parse(location);
						
						// Найти enum Role
						Optional<EnumDeclaration> roleEnum = cu.findAll(EnumDeclaration.class).stream()
							.filter(enumDecl -> enumDecl.getImplementedTypes().stream()
								.anyMatch(type -> type instanceof ClassOrInterfaceType && 
									((ClassOrInterfaceType) type).getNameAsString().contains("Role")))
							.findFirst();
						
						if (roleEnum.isPresent()) {
							EnumDeclaration enumDecl = roleEnum.get();
							
							// Найти константу роли
							Optional<EnumConstantDeclaration> roleConstant = enumDecl.getEntries().stream()
								.filter(constant -> constant.getNameAsString().equals(roleName))
								.findFirst();
							
							if (roleConstant.isPresent()) {
								EnumConstantDeclaration constant = roleConstant.get();
								
									// Удалить разрешение из массива permissions
									if (constant.getArguments().size() >= 1 && 
										constant.getArguments().get(0) instanceof LambdaExpr) {
										LambdaExpr permissionsLambda = (LambdaExpr) constant.getArguments().get(0);
										
										if (permissionsLambda.getBody() instanceof ExpressionStmt) {
											ExpressionStmt stmt = (ExpressionStmt) permissionsLambda.getBody();
											if (stmt.getExpression() instanceof ArrayCreationExpr) {
												ArrayCreationExpr array = (ArrayCreationExpr) stmt.getExpression();
												array.getInitializer().ifPresent(init -> {
													init.getValues().removeIf(element -> 
														element instanceof NameExpr && 
														((NameExpr) element).getNameAsString().equals(permission));
												});
											}
										}
									}
								
								Files.writeString(location.toPath(), cu.toString());
								r.permissions.remove(permission);
								return;
							}
						}
					} else {
						throw new IllegalStateException(r.name + " does not have permission " + permission);
					}
				} catch (IOException ex) {
					throw new UncheckedIOException(ex);
				}
			}
		}
		throw new IllegalArgumentException("There is no role " + roleName);
	}
	
	public RoleRepresentation addRole(String name, String... permissions) {
		try {
			RoleRepresentation r = new RoleRepresentation(name, new TreeSet<>(Arrays.asList(permissions)), null);
			
			while (!Files.isWritable(location.toPath())) Thread.onSpinWait();
			
			CompilationUnit cu = StaticJavaParser.parse(location);
			
			// Найти enum Role
			Optional<EnumDeclaration> roleEnum = cu.findAll(EnumDeclaration.class).stream()
				.filter(enumDecl -> enumDecl.getImplementedTypes().stream()
					.anyMatch(type -> type instanceof ClassOrInterfaceType && 
						((ClassOrInterfaceType) type).getNameAsString().contains("Role")))
				.findFirst();
			
			if (roleEnum.isPresent()) {
				EnumDeclaration enumDecl = roleEnum.get();
				
					// Создать новую константу enum
					EnumConstantDeclaration newConstant = new EnumConstantDeclaration(name);
				
				enumDecl.addEntry(newConstant);
				
				Files.writeString(location.toPath(), cu.toString());
				roles.add(r);
				return r;
			}
		} catch (IOException ex) {
			throw new UncheckedIOException(ex);
		}
		return null;
	}
	
	public void removeRole(String name) {
		try {
			while (!Files.isWritable(location.toPath())) Thread.onSpinWait();
			
			CompilationUnit cu = StaticJavaParser.parse(location);
			
			// Найти enum Role
			Optional<EnumDeclaration> roleEnum = cu.findAll(EnumDeclaration.class).stream()
				.filter(enumDecl -> enumDecl.getImplementedTypes().stream()
					.anyMatch(type -> type instanceof ClassOrInterfaceType && 
						((ClassOrInterfaceType) type).getNameAsString().contains("Role")))
				.findFirst();
			
			if (roleEnum.isPresent()) {
				EnumDeclaration enumDecl = roleEnum.get();
				
				// Удалить константу роли
				enumDecl.getEntries().removeIf(constant -> constant.getNameAsString().equals(name));
				
				Files.writeString(location.toPath(), cu.toString());
				roles.removeIf(r -> r.name.equals(name));
			}
		} catch (IOException ex) {
			throw new UncheckedIOException(ex);
		}
	}
}
