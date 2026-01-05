package com.bpa4j.util.codegen;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
import lombok.Getter;

/**
 * @author AI-generated
 */
public class RolesNode implements ProjectNode<RolesNode>{
	public static class RoleRepresentation{
		public String name;
		public Set<String> permissions;
		public Set<String> features;

		public RoleRepresentation(String name,Set<String> permissions,Set<String> features){
			this.name=name;
			this.permissions=permissions;
			this.features=features;
		}
	}

	public static class RolesPhysicalNode implements PhysicalNode<RolesNode>{
		private final File file;
		private final PermissionsNode permissionsNode; // Dependency for loading

		public RolesPhysicalNode(File file,PermissionsNode permissionsNode){
			this.file=file;
			this.permissionsNode=permissionsNode;
		}

		@Override
		public void clear(){
			file.delete();
		}
		@Override
		public boolean exists(){
			return file.exists();
		}

		@Override
		public NodeModel<RolesNode> load(){
			ArrayList<RoleRepresentation> roles=new ArrayList<>();
			try{
				CompilationUnit cu=StaticJavaParser.parse(file);
				Optional<EnumDeclaration> roleEnum=findRoleEnum(cu);

				if(roleEnum.isPresent()){
					EnumDeclaration enumDecl=roleEnum.get();
					for(EnumConstantDeclaration constant:enumDecl.getEntries()){
						String name=constant.getNameAsString();
						Set<String> permissions=new TreeSet<>();
						Set<String> features=new TreeSet<>();

						if(constant.getArguments().size()>=2){
							if(constant.getArguments().get(0) instanceof LambdaExpr){
								LambdaExpr permissionsLambda=(LambdaExpr)constant.getArguments().get(0);
								permissions=parsePermissionsFromLambda(permissionsLambda,permissionsNode);
							}
							// FIXME Parse features
						}
						roles.add(new RoleRepresentation(name,permissions,features));
					}
				}
				return new RolesModel(roles);
			}catch(IOException ex){
				throw new UncheckedIOException(ex);
			}
		}

		@Override
		public void persist(NodeModel<RolesNode> model){
			// Usually persist takes the whole model and rewrites the file.
			// But for incremental updates (legacy logic), we might have specialized methods.
			// However, the ProjectNode contract implies persist can handle saving.
			// Implementing minimal persist logic or relying on specific methods below.

			// For now, specialized methods handle persistence to preserve comments/structure better.
		}

		public void addPermission(String roleName,String permission){
			try{
				while(!Files.isWritable(file.toPath()))
					Thread.onSpinWait();
				CompilationUnit cu=StaticJavaParser.parse(file);
				Optional<EnumDeclaration> roleEnum=findRoleEnum(cu);

				if(roleEnum.isPresent()){
					Optional<EnumConstantDeclaration> roleConstant=roleEnum.get().getEntries().stream().filter(constant->constant.getNameAsString().equals(roleName)).findFirst();
					if(roleConstant.isPresent()){
						EnumConstantDeclaration constant=roleConstant.get();
						if(constant.getArguments().size()>=1&&constant.getArguments().get(0) instanceof LambdaExpr){
							LambdaExpr permissionsLambda=(LambdaExpr)constant.getArguments().get(0);
							if(permissionsLambda.getBody() instanceof ExpressionStmt){
								ExpressionStmt stmt=(ExpressionStmt)permissionsLambda.getBody();
								if(stmt.getExpression() instanceof ArrayCreationExpr){
									ArrayCreationExpr array=(ArrayCreationExpr)stmt.getExpression();
									array.getInitializer().ifPresent(init->{
										init.getValues().add(new NameExpr(permission));
									});
								}
							}
						}
						Files.writeString(file.toPath(),cu.toString());
					}
				}
			}catch(IOException ex){
				throw new UncheckedIOException(ex);
			}
		}

		public void removePermission(String roleName,String permission){
			try{
				while(!Files.isWritable(file.toPath()))
					Thread.onSpinWait();
				CompilationUnit cu=StaticJavaParser.parse(file);
				Optional<EnumDeclaration> roleEnum=findRoleEnum(cu);

				if(roleEnum.isPresent()){
					Optional<EnumConstantDeclaration> roleConstant=roleEnum.get().getEntries().stream().filter(constant->constant.getNameAsString().equals(roleName)).findFirst();
					if(roleConstant.isPresent()){
						EnumConstantDeclaration constant=roleConstant.get();
						if(constant.getArguments().size()>=1&&constant.getArguments().get(0) instanceof LambdaExpr){
							LambdaExpr permissionsLambda=(LambdaExpr)constant.getArguments().get(0);
							if(permissionsLambda.getBody() instanceof ExpressionStmt){
								ExpressionStmt stmt=(ExpressionStmt)permissionsLambda.getBody();
								if(stmt.getExpression() instanceof ArrayCreationExpr){
									ArrayCreationExpr array=(ArrayCreationExpr)stmt.getExpression();
									array.getInitializer().ifPresent(init->{
										init.getValues().removeIf(element->element instanceof NameExpr&&((NameExpr)element).getNameAsString().equals(permission));
									});
								}
							}
						}
						Files.writeString(file.toPath(),cu.toString());
					}
				}
			}catch(IOException ex){
				throw new UncheckedIOException(ex);
			}
		}

		public void addRole(String name){
			try{
				while(!Files.isWritable(file.toPath()))
					Thread.onSpinWait();
				CompilationUnit cu=StaticJavaParser.parse(file);
				Optional<EnumDeclaration> roleEnum=findRoleEnum(cu);
				if(roleEnum.isPresent()){
					EnumConstantDeclaration newConstant=new EnumConstantDeclaration(name);
					roleEnum.get().addEntry(newConstant);
					Files.writeString(file.toPath(),cu.toString());
				}
			}catch(IOException ex){
				throw new UncheckedIOException(ex);
			}
		}

		public void removeRole(String name){
			try{
				while(!Files.isWritable(file.toPath()))
					Thread.onSpinWait();
				CompilationUnit cu=StaticJavaParser.parse(file);
				findRoleEnum(cu).ifPresent(roleEnum->{
					roleEnum.getEntries().removeIf(constant->constant.getNameAsString().equals(name));
					try{
						Files.writeString(file.toPath(),cu.toString());
					}catch(IOException e){
						throw new UncheckedIOException(e);
					}
				});
			}catch(IOException ex){
				throw new UncheckedIOException(ex);
			}
		}

		private Optional<EnumDeclaration> findRoleEnum(CompilationUnit cu){
			return cu.findAll(EnumDeclaration.class).stream().filter(enumDecl->enumDecl.getImplementedTypes().stream().anyMatch(type->type instanceof ClassOrInterfaceType&&((ClassOrInterfaceType)type).getNameAsString().contains("Role"))).findFirst();
		}

		private Set<String> parsePermissionsFromLambda(LambdaExpr lambda,PermissionsNode p){
			Set<String> permissions=new TreeSet<>();
			if(lambda.getBody() instanceof ExpressionStmt){
				ExpressionStmt stmt=(ExpressionStmt)lambda.getBody();
				if(stmt.getExpression() instanceof MethodCallExpr){
					MethodCallExpr methodCall=(MethodCallExpr)stmt.getExpression();
					if(methodCall.getNameAsString().equals("values")&&methodCall.getScope().isPresent()&&methodCall.getScope().get() instanceof NameExpr){
						NameExpr scope=(NameExpr)methodCall.getScope().get();
						if(scope.getNameAsString().contains("Permission")){ return new TreeSet<>(p.getPermissions());
						}
					}
				}
			}
			if(lambda.getBody() instanceof ExpressionStmt){
				ExpressionStmt stmt=(ExpressionStmt)lambda.getBody();
				if(stmt.getExpression() instanceof ArrayCreationExpr){
					ArrayCreationExpr array=(ArrayCreationExpr)stmt.getExpression();
					array.getInitializer().ifPresent(init->{
						for(var element:init.getValues()){
							if(element instanceof NameExpr){
								permissions.add(((NameExpr)element).getNameAsString());
							}
						}
					});
				}
			}
			return permissions;
		}
	}

	public static class RolesModel implements NodeModel<RolesNode>{
		@Getter
		private final List<RoleRepresentation> roles;
		public RolesModel(List<RoleRepresentation> roles){
			this.roles=roles;
		}
	}

	private final PhysicalNode<RolesNode> physicalNode;
	private final NodeModel<RolesNode> model;

	public RolesNode(PhysicalNode<RolesNode> physicalNode){
		this.physicalNode=physicalNode;
		this.model=physicalNode.load();
	}

	public RolesNode(File file,PermissionsNode p){
		this(new RolesPhysicalNode(file,p));
	}

	@Override
	public PhysicalNode<RolesNode> getPhysicalRepresentation(){
		return physicalNode;
	}

	@Override
	public NodeModel<RolesNode> getModel(){
		return model;
	}

	public void addPermission(String roleName,String permission){
		for(RoleRepresentation r:((RolesModel)model).getRoles()){
			if(r.name.equals(roleName)){
				if(r.permissions.contains(permission)) throw new IllegalStateException(r.name+" already has permission "+permission+".");
				r.permissions.add(permission); // Model update
				((RolesPhysicalNode)physicalNode).addPermission(roleName,permission); // Physical update
				return;
			}
		}
		throw new IllegalArgumentException("There is no role "+roleName+".");
	}

	public void removePermission(String roleName,String permission){
		for(RoleRepresentation r:((RolesModel)model).getRoles()){
			if(r.name.equals(roleName)){
				if(r.permissions.contains(permission)){
					r.permissions.remove(permission); // Model update
					((RolesPhysicalNode)physicalNode).removePermission(roleName,permission); // Physical update
					return;
				}else{
					throw new IllegalStateException(r.name+" does not have permission "+permission);
				}
			}
		}
		throw new IllegalArgumentException("There is no role "+roleName);
	}

	public RoleRepresentation addRole(String name,String...permissions){
		RoleRepresentation r=new RoleRepresentation(name,new TreeSet<>(Arrays.asList(permissions)),null);
		((RolesModel)model).getRoles().add(r); // Model update
		((RolesPhysicalNode)physicalNode).addRole(name); // Physical update
		// Note: Original addRole didn't add permissions in file creation? 
		// "EnumConstantDeclaration newConstant=new EnumConstantDeclaration(name);" -> acts like simple name.
		// The permissions are passed to RoleRepresentation but not written to file?
		// Replicating original behavior.
		return r;
	}

	public void removeRole(String name){
		((RolesModel)model).getRoles().removeIf(r->r.name.equals(name));
		((RolesPhysicalNode)physicalNode).removeRole(name);
	}
}
