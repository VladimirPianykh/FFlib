package com.bpa4j.util.codegen;

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
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.bpa4j.core.Editable;
import com.bpa4j.editor.EditorEntry;
import com.bpa4j.Wrapper;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.comments.LineComment;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.ExplicitConstructorInvocationStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;

@SuppressWarnings("PMD.ExceptionAsFlowControl")
/**
 * Most methods are AI-generated.
 */
public class EditableNode extends ClassNode{
	public static class Property{
		public static enum PropertyType{
			STRING("String"),INT("int"),DOUBLE("double"),BOOL("boolean"),DATE("LocalDate"),DATETIME("LocalDateTime");

			private final String typeName;

			private PropertyType(String typeName){
				this.typeName=typeName;
			}

			public String toString(){
				return typeName;
			}
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

		public void changeName(String name,EditableNode n){
			try{
				while(!Files.isWritable(n.location.toPath()))
					Thread.onSpinWait();

				CompilationUnit cu=StaticJavaParser.parse(n.location);

				// Найти поле с аннотацией @EditorEntry
				Optional<FieldDeclaration> field=cu.findAll(FieldDeclaration.class).stream().filter(f->f.getAnnotationByName("EditorEntry").isPresent()).filter(f->{
					Optional<StringLiteralExpr> translation=f.getAnnotationByName("EditorEntry").flatMap(ann->ann.asNormalAnnotationExpr().getPairs().stream().filter(pair->pair.getNameAsString().equals("translation")).findFirst().map(pair->pair.getValue().asStringLiteralExpr()));
					return translation.isPresent()&&translation.get().asString().equals(this.name);
				}).findFirst();

				if(field.isPresent()){
					FieldDeclaration fieldDecl=field.get();
					fieldDecl.getAnnotationByName("EditorEntry").flatMap(ann->ann.asNormalAnnotationExpr().getPairs().stream().filter(pair->pair.getNameAsString().equals("translation")).findFirst()).ifPresent(pair->pair.setValue(new StringLiteralExpr(name)));

					Files.writeString(n.location.toPath(),cu.toString());
					this.name=name;
				}
			}catch(IOException ex){
				throw new UncheckedIOException(ex);
			}
		}

		public void changeType(PropertyType type,EditableNode n){
			try{
				while(!Files.isWritable(n.location.toPath()))
					Thread.onSpinWait();

				CompilationUnit cu=StaticJavaParser.parse(n.location);

				if(this.type==null){
					// Найти TO_DO-комментарий и заменить на поле
					Optional<LineComment> todoComment=cu.findAll(LineComment.class).stream().filter(comment->comment.getContent().contains("TODO: add property \""+name+"\"")).findFirst();

					if(todoComment.isPresent()){
						// Удалить комментарий и добавить поле
						todoComment.get().remove();

						// Найти класс и добавить поле
						Optional<ClassOrInterfaceDeclaration> clazz=cu.findAll(ClassOrInterfaceDeclaration.class).stream().filter(c->c.getExtendedTypes().stream().anyMatch(extType->extType instanceof ClassOrInterfaceType&&((ClassOrInterfaceType)extType).getNameAsString().equals("Editable"))).findFirst();

						if(clazz.isPresent()){
							// TODO: Implement field creation with JavaParser
							// FieldDeclaration newField = new FieldDeclaration();
							// newField.setCommonType(StaticJavaParser.parseType(type.toString()));
							// newField.addVariable(new VariableDeclarator(StaticJavaParser.parseType(type.toString()), "var" + (int)(Math.random() * 9999900 + 100)));
							// clazz.get().addMember(newField);
						}
					}
				}else{
					// Изменить тип существующего поля
					Optional<FieldDeclaration> field=cu.findAll(FieldDeclaration.class).stream().filter(f->f.getAnnotationByName("EditorEntry").isPresent()).filter(f->{
						Optional<StringLiteralExpr> translation=f.getAnnotationByName("EditorEntry").flatMap(ann->ann.asNormalAnnotationExpr().getPairs().stream().filter(pair->pair.getNameAsString().equals("translation")).findFirst().map(pair->pair.getValue().asStringLiteralExpr()));
						return translation.isPresent()&&translation.get().asString().equals(name);
					}).findFirst();

					if(field.isPresent()){
						// TODO: Implement field type change with JavaParser
						// field.get().setCommonType(StaticJavaParser.parseType(type.toString()));
					}
				}

				Files.writeString(n.location.toPath(),cu.toString());
				this.type=type;
			}catch(IOException ex){
				throw new UncheckedIOException(ex);
			}
		}

		public String toString(){
			return name+" ("+(type==null?"???":type.toString())+")";
		}
	}

	public String objectName;
	public ArrayList<Property> properties=new ArrayList<>();

	/**
	 * Resolves an existing node from the file.
	 */
	public EditableNode(File file){
		super(file);
		try{
			CompilationUnit cu=StaticJavaParser.parse(file);

			// Найти класс, который extends Editable
			Optional<ClassOrInterfaceDeclaration> clazz=cu.findAll(ClassOrInterfaceDeclaration.class).stream().filter(c->c.getExtendedTypes().stream().anyMatch(extType->extType instanceof ClassOrInterfaceType&&((ClassOrInterfaceType)extType).getNameAsString().equals("Editable"))).findFirst();

			if(clazz.isPresent()){
				// Извлечь objectName из конструктора
				Optional<ConstructorDeclaration> constructor=cu.findAll(ConstructorDeclaration.class).stream().filter(c->c.getNameAsString().equals(name)).findFirst();

				if(constructor.isPresent()){
					ConstructorDeclaration constr=constructor.get();
					Optional<ExplicitConstructorInvocationStmt> superCall=constr.getBody().getStatements().stream().filter(stmt->stmt.isExplicitConstructorInvocationStmt()).map(stmt->stmt.asExplicitConstructorInvocationStmt()).findFirst();

					if(superCall.isPresent()&&superCall.get().getArguments().size()>0){
						Expression argExpr=superCall.get().getArguments().get(0);
						// Забираем имя, если есть
						if(argExpr.isStringLiteralExpr()){
							StringLiteralExpr arg=argExpr.asStringLiteralExpr();
							String superArg=arg.asString();
							// Удалить префикс "нов" если есть
							if(superArg.toLowerCase().startsWith("нов")){
								superArg=superArg.substring(superArg.indexOf(' '));
							}else System.err.print(file+" "+superArg);
							// Удалить специальные символы
							objectName=superArg.replaceAll("[!@#$%&*]","").trim();
							if(objectName.isEmpty()) objectName=null;
						}
					}
				}

				// Извлечь поля с аннотацией @EditorEntry
				cu.findAll(FieldDeclaration.class).stream().filter(field->field.getAnnotationByName("EditorEntry").isPresent()).forEach(field->{
					Optional<StringLiteralExpr> translation=field.getAnnotationByName("EditorEntry").flatMap(ann->ann.asNormalAnnotationExpr().getPairs().stream().filter(pair->pair.getNameAsString().equals("translation")).findFirst().map(pair->pair.getValue().asStringLiteralExpr()));

					if(translation.isPresent()){
						String fieldName=translation.get().asString();
						String fieldType=field.getElementType().toString();

						Property.PropertyType propType=switch(fieldType){
							case "LocalDate" -> Property.PropertyType.DATE;
							case "LocalDateTime" -> Property.PropertyType.DATETIME;
							case "String" -> Property.PropertyType.STRING;
							case "int" -> Property.PropertyType.INT;
							case "double" -> Property.PropertyType.DOUBLE;
							case "boolean" -> Property.PropertyType.BOOL;
							default -> null;
						};

						properties.add(new Property(fieldName,propType));
					}
				});

				// Найти TO DO-комментарии для свойств
				cu.findAll(LineComment.class).stream().filter(comment->comment.getContent().contains("TODO: add property")).forEach(comment->{
					Matcher m=Pattern.compile("TODO:\\s*add property\\s*\"(.*?)\"").matcher(comment.getContent());
					if(m.find()){
						properties.add(new Property(m.group(1),null));
					}
				});
			}
		}catch(IOException ex){
			throw new UncheckedIOException(ex);
		}
	}

	/**
	 * Constructs a new node with the designated file.
	 */
	public EditableNode(File file,String objectName,String basePackage,Property...properties){
		super(file);
		this.objectName=objectName.replaceAll("[!@#$%&*]","").trim();
		if(objectName.isEmpty()) objectName=null;
		this.properties.addAll(Arrays.asList(properties));
		try{
			file.createNewFile();
			Wrapper<Integer> index=new Wrapper<>(0);
			String s=String.format("""
					package %s.editables.registered;

					import %s;
					import %s;

					public class %s extends Editable{
					"""+Stream.of(properties).map(p->p.getCode("var"+(++index.var))).collect(Collectors.joining("\n"))+"""
						public %s(){
							super("Нов %s");
						}
					}
					""",basePackage,Editable.class.getName(),EditorEntry.class.getName(),name,name,objectName==null?"":objectName);
			Files.writeString(file.toPath(),s);
		}catch(IOException ex){
			throw new UncheckedIOException(ex);
		}
	}

	protected static File findFile(String name,File parent){
		try{
			Wrapper<File> w=new Wrapper<File>(null);
			Files.walkFileTree(parent.toPath(),new SimpleFileVisitor<Path>(){
				public FileVisitResult visitFile(Path file,BasicFileAttributes attrs) throws IOException{
					if(file.toString().equals(name+".java")){
						w.var=file.toFile();
						return FileVisitResult.TERMINATE;
					}
					return FileVisitResult.CONTINUE;
				}
			});
			if(w.var==null) throw new FileNotFoundException();
			return w.var;
		}catch(IOException ex){
			throw new UncheckedIOException(ex);
		}
	}

	public void addProperty(Property property,String varName){
		try{
			while(!Files.isWritable(location.toPath()))
				Thread.onSpinWait();

			CompilationUnit cu=StaticJavaParser.parse(location);

			// Найти класс Editable
			Optional<ClassOrInterfaceDeclaration> clazz=cu.findAll(ClassOrInterfaceDeclaration.class).stream().filter(c->c.getExtendedTypes().stream().anyMatch(extType->extType instanceof ClassOrInterfaceType&&((ClassOrInterfaceType)extType).getNameAsString().equals("Editable"))).findFirst();

			if(clazz.isPresent()){
				// TODO: Implement field creation with JavaParser
				// FieldDeclaration newField = new FieldDeclaration();
				// newField.setCommonType(StaticJavaParser.parseType(property.type.toString()));
				// newField.addVariable(new VariableDeclarator(StaticJavaParser.parseType(property.type.toString()), varName));
				// clazz.get().addMember(newField);

				Files.writeString(location.toPath(),cu.toString(),StandardOpenOption.CREATE);
				properties.add(property);
			}
		}catch(IOException ex){
			throw new UncheckedIOException(ex);
		}
	}

	public void addProperties(Property...properties){
		try{
			while(!Files.isWritable(location.toPath()))
				Thread.onSpinWait();

			CompilationUnit cu=StaticJavaParser.parse(location);

			// Найти класс Editable
			Optional<ClassOrInterfaceDeclaration> clazz=cu.findAll(ClassOrInterfaceDeclaration.class).stream().filter(c->c.getExtendedTypes().stream().anyMatch(extType->extType instanceof ClassOrInterfaceType&&((ClassOrInterfaceType)extType).getNameAsString().equals("Editable"))).findFirst();

			if(clazz.isPresent()){
				// TODO: Implement field creation with JavaParser
				// for (Property prop : properties) {
				//     FieldDeclaration newField = new FieldDeclaration();
				//     newField.setCommonType(StaticJavaParser.parseType(prop.type.toString()));
				//     newField.addVariable(new VariableDeclarator(StaticJavaParser.parseType(prop.type.toString()), "iVar" + (++index.var)));
				//     clazz.get().addMember(newField);
				// }

				Files.writeString(location.toPath(),cu.toString(),StandardOpenOption.CREATE,StandardOpenOption.WRITE);
				this.properties.addAll(Arrays.asList(properties));
			}
		}catch(IOException ex){
			throw new UncheckedIOException(ex);
		}
	}

	public void removeProperty(Property property){
		try{
			while(!Files.isWritable(location.toPath()))
				Thread.onSpinWait();

			CompilationUnit cu=StaticJavaParser.parse(location);

			// Найти поле с нужной аннотацией
			Optional<FieldDeclaration> field=cu.findAll(FieldDeclaration.class).stream().filter(f->f.getAnnotationByName("EditorEntry").isPresent()).filter(f->{
				Optional<StringLiteralExpr> translation=f.getAnnotationByName("EditorEntry").flatMap(ann->ann.asNormalAnnotationExpr().getPairs().stream().filter(pair->pair.getNameAsString().equals("translation")).findFirst().map(pair->pair.getValue().asStringLiteralExpr()));
				return translation.isPresent()&&translation.get().asString().equals(property.name);
			}).findFirst();

			if(field.isPresent()){
				field.get().remove();
				Files.writeString(location.toPath(),cu.toString());
			}
		}catch(IOException ex){
			throw new UncheckedIOException(ex);
		}
	}

	public void changeObjectName(String objectName){
		try{
			while(!Files.isWritable(location.toPath()))
				Thread.onSpinWait();

			CompilationUnit cu=StaticJavaParser.parse(location);

			// Найти конструктор и изменить аргумент super()
			Optional<ConstructorDeclaration> constructor=cu.findAll(ConstructorDeclaration.class).stream().filter(c->c.getNameAsString().equals(name)).findFirst();

			if(constructor.isPresent()){
				ConstructorDeclaration constr=constructor.get();
				Optional<MethodCallExpr> superCall=constr.getBody().getStatements().stream().filter(stmt->stmt.isExpressionStmt()&&stmt.asExpressionStmt().getExpression() instanceof MethodCallExpr).map(stmt->stmt.asExpressionStmt().getExpression().asMethodCallExpr()).filter(call->call.getNameAsString().equals("super")).findFirst();

				if(superCall.isPresent()&&superCall.get().getArguments().size()>0){
					superCall.get().getArguments().set(0,new StringLiteralExpr(objectName==null?"":objectName));
				}
			}

			Files.writeString(location.toPath(),cu.toString());
			this.objectName=objectName;
		}catch(IOException ex){
			throw new UncheckedIOException(ex);
		}
	}
}
