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
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Optional;
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
import com.bpa4j.core.Root;
import com.bpa4j.ui.swing.util.Message;
import com.bpa4j.util.SprintUI;
import com.bpa4j.util.codegen.EditableNode.Property;
import com.bpa4j.util.codegen.ProjectGraph.NavigatorNode.HelpEntry;
import com.bpa4j.util.codegen.ProjectGraph.NavigatorNode.Instruction;
import com.bpa4j.util.codegen.RolesNode.RoleRepresentation;
import com.bpa4j.util.codegen.server.ProjectServer;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;

/**
 * A java BPA project representation using JavaParser.
 * Designed to provide low-code access.
 */
public class ProjectGraph{
	/* 	public static class PermissionsNodeV2 extends ProjectNode {
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
						enumDecl.getEntries().forEach(constant -> {
							permissions.add(constant.getNameAsString());
						});
						
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
		
		public static class RolesNodeV2 extends ProjectNode {
			public static class RoleRepresentation {
				public String name;
				public java.util.Set<String> permissions;
				public java.util.Set<String> features;
				
				public RoleRepresentation(String name, java.util.Set<String> permissions, java.util.Set<String> features) {
					this.name = name;
					this.permissions = permissions;
					this.features = features;
				}
			}
			
			public ArrayList<RoleRepresentation> roles = new ArrayList<>();
			
			public RolesNodeV2(File file, PermissionsNodeV2 p) {
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
						enumDecl.getEntries().forEach(constant -> {
							String name = constant.getNameAsString();
							java.util.Set<String> permissions = new java.util.TreeSet<>();
							java.util.Set<String> features = new java.util.TreeSet<>();
							
							// Парсить аргументы константы
							if (constant.getArguments().size() >= 2) {
								// Первый аргумент - лямбда для permissions
								if (constant.getArguments().get(0) instanceof LambdaExpr) {
									LambdaExpr permissionsLambda = (LambdaExpr) constant.getArguments().get(0);
									permissions = parsePermissionsFromLambda(permissionsLambda, p);
								}
								
								// Второй аргумент - лямбда для features (пока не обрабатываем)
								// TO DO: parse features
							}
							
							roles.add(new RoleRepresentation(name, permissions, features));
						});
					}
				} catch (IOException ex) {
					throw new UncheckedIOException(ex);
				}
			}
			
			private java.util.Set<String> parsePermissionsFromLambda(LambdaExpr lambda, PermissionsNodeV2 p) {
				java.util.Set<String> permissions = new java.util.TreeSet<>();
				
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
								return new java.util.TreeSet<>(p.permissions);
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
							init.getValues().forEach(element -> {
								if (element instanceof NameExpr) {
									NameExpr nameExpr = (NameExpr) element;
									permissions.add(nameExpr.getNameAsString());
								}
							});
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
					RoleRepresentation r = new RoleRepresentation(name, new java.util.TreeSet<>(java.util.Arrays.asList(permissions)), null);
					
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
	 */
	public static class NavigatorNode extends ProjectNode{
		public static class Instruction{
			public static enum Type{
				START,FEATURE,TEXT,COMMENT;
				public char toChar(){
					return switch(this){
						case START -> 's';
						case FEATURE -> 'f';
						case TEXT -> 't';
						case COMMENT -> 'c';
						default -> throw new AssertionError("This method does not know other constants.");
					};
				}
				public static Type toType(char c){
					return switch(c){
						case 's' -> Instruction.Type.START;
						case 'f' -> Instruction.Type.FEATURE;
						case 't' -> Instruction.Type.TEXT;
						case 'c' -> Instruction.Type.COMMENT;
						default -> throw new IllegalArgumentException("Char '"+c+"' does not correspond to any constant.");
					};
				}
			}
			public String text;
			public Type type;
			public Instruction(String text,Type type){
				this.text=text;
				this.type=type;
			}
		}
		public static class HelpEntry{
			public String text;
			public ArrayList<Instruction> instructions=new ArrayList<>();
			public HelpEntry(String text){
				this.text=text;
			}
			public void changeText(String text,NavigatorNode n){
				try{
					StringBuilder b=new StringBuilder();
					for(String l:Files.readString(n.location.toPath()).split("\n")){
						String[] s=l.split(" ",2);
						if(s[1].equals(this.text)) s[1]=text;
						b.append(s[0]).append(' ').append(s[1]).append('\n');
					}
					Files.writeString(n.location.toPath(),b);
					this.text=text;
				}catch(IOException ex){
					throw new UncheckedIOException(ex);
				}
			}
			public void replaceInstruction(Instruction c,int index,NavigatorNode n){
				try{
					StringBuilder b=new StringBuilder();
					for(String l:Files.readString(n.location.toPath()).split("\n")){
						String[] s=l.split(" ",2);
						if(text.equals(s[1])){
							String[] t=s[0].split("\\.");
							int i=0;
							for(;i<index;++i)
								b.append(t[i]).append('.');
							b.append(c.type.toChar()).append(c.text).append('.');
							++i;
							for(;i<t.length;++i)
								b.append(t[i]).append('.');
							b.deleteCharAt(b.length()-1);
						}else b.append(s[0]);
						b.append(' ').append(s[1]).append('\n');
					}
					Files.writeString(n.location.toPath(),b);
					instructions.set(index,c);
				}catch(IOException ex){
					throw new UncheckedIOException(ex);
				}
			}
			public void appendInstruction(Instruction c,NavigatorNode n){
				try{
					StringBuilder b=new StringBuilder();
					for(String l:Files.readString(n.location.toPath()).split("\n")){
						String[] s=l.split(" ",2);
						b.append(s[0]);
						if(s[1].equals(text)) b.append('.').append(c.type.toChar()).append(c.text);
						b.append(' ').append(s[1]).append('\n');
					}
					Files.writeString(n.location.toPath(),b);
					instructions.add(c);
				}catch(IOException ex){
					throw new UncheckedIOException(ex);
				}
			}
			public void deleteLastInstruction(NavigatorNode n){
				try{
					StringBuilder b=new StringBuilder();
					for(String l:Files.readString(n.location.toPath()).split("\n")){
						String[] s=l.split(" ",2);
						if(text.equals(s[1])){
							String[] t=s[0].split("\\.");
							for(int j=0;j<t.length-1;++j)
								b.append(t[j]);
						}else b.append(s[0]);
						if(!(b.isEmpty()||Character.isWhitespace(b.charAt(b.length()-1)))) b.append(' ').append(s[1]).append('\n');
					}
					Files.writeString(n.location.toPath(),b);
					instructions.removeLast();
				}catch(IOException ex){
					throw new UncheckedIOException(ex);
				}
			}
		}
		public ArrayList<HelpEntry> entries=new ArrayList<>();
		public NavigatorNode(File file){
			super(file);
			try{
				String str=Files.readString(file.toPath());
				if(str.isBlank()) return;
				for(String l:str.split("\n")){
					String[] s=l.split(" ",2);
					HelpEntry e=new HelpEntry(s[1]);
					e.instructions.addAll(Stream.of(s[0].split("\\.")).map(t->new Instruction(t.substring(1),Instruction.Type.toType(t.charAt(0)))).toList());
					entries.add(e);
				}
			}catch(IOException ex){
				throw new UncheckedIOException(ex);
			}
		}
		/**
		 * Creates a new NavigatorNode.
		 */
		public NavigatorNode(ProjectGraph project){
			super(new File(project.projectFolder,"resources/helppath.cfg"));
			try{
				location.createNewFile();
			}catch(IOException ex){
				throw new UncheckedIOException(ex);
			}
		}
		public void deleteEntry(String text){
			HelpEntry e=entries.stream().filter(entry->entry.text.equals(text)).findAny().get();
			try{
				StringBuilder b=new StringBuilder();
				for(String l:Files.readString(location.toPath()).split("\n")){
					String[] s=l.split(" ",2);
					if(!s[1].equals(e.text)) b.append(s[0]).append(' ').append(s[1]).append('\n');
				}
				Files.writeString(location.toPath(),b);
				entries.remove(e);
			}catch(IOException ex){
				throw new UncheckedIOException(ex);
			}
		}
	}
	public static class Problem{
		public static enum ProblemType{
			ERROR,WARNING,INFO;
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
	public static interface DiagnosticService{
		ArrayList<Problem> findProblems(ProjectGraph graph);
	}
	private static ArrayList<DiagnosticService> diagnosticServices=new ArrayList<>();
	static{
		// diagnosticServices.add();
		//TODO: #2 add diagnostic services
	}
	private JavaParser parser=new JavaParser();
	private ArrayList<Problem> problemsCache;
	public ArrayList<ProjectNode> nodes=new ArrayList<>();
	public File projectFolder;
	/**
	 * @param projectFolder - the project folder. It is recommended to pass <b>src/main/java</b> path:
	 */
	public ProjectGraph(File projectFolder){
		this.projectFolder=projectFolder;
		projectFolder.mkdirs();
		try{
			Files.walkFileTree(projectFolder.toPath(),new SimpleFileVisitor<Path>(){
				public FileVisitResult visitFile(Path file,BasicFileAttributes attrs) throws IOException{
					if(file.toString().endsWith(".java")){
						CompilationUnit cu=parser.parse(file).getResult().get();

						// Найти класс Main
						Optional<ClassOrInterfaceDeclaration> mainClass=cu.findAll(ClassOrInterfaceDeclaration.class).stream().filter(clazz->clazz.getNameAsString().equals("Main")).findFirst();

						if(mainClass.isPresent()){
							nodes.add(new PermissionsNode(file.toFile()));
							nodes.add(new RolesNode(file.toFile(),(PermissionsNode)nodes.getLast()));
						}else{
							// Проверить, является ли класс Editable
							Optional<ClassOrInterfaceDeclaration> editableClass=cu.findAll(ClassOrInterfaceDeclaration.class).stream().filter(clazz->clazz.getExtendedTypes().stream().anyMatch(type->type instanceof ClassOrInterfaceType&&((ClassOrInterfaceType)type).getNameAsString().contains("Editable"))).findFirst();

							if(editableClass.isPresent()){
								nodes.add(new EditableNode(file.toFile()));
							}
						}

						System.err.println("Parsed: "+cu.getPrimaryTypeName().orElse("Unknown"));
					}else if(file.getFileName().toString().equals("helppath.cfg")){
						nodes.add(new NavigatorNode(file.toFile()));
					}
					return FileVisitResult.CONTINUE;
				}
			});
			System.err.println("Parsing completed!");
		}catch(IOException ex){
			throw new UncheckedIOException(ex);
		}
	}	
	public ArrayList<Problem> findProblems(){
		ArrayList<Problem> a=new ArrayList<>();
		for(DiagnosticService d:diagnosticServices)
			a.addAll(d.findProblems(this));
		return a;
	}
	/**
	 * Shows low-code programming UI.
	 */
	public void show(){
		try{
			UIManager.setLookAndFeel(new NimbusLookAndFeel());
		}catch(UnsupportedLookAndFeelException ex){
			throw new AssertionError("The BPALookAndFeel must be supported.",ex);
		}
		JFrame f=new JFrame();
		f.setUndecorated(true);
		f.setSize(Root.SCREEN_SIZE);
		f.setLayout(null);
		JPanel buttons=new JPanel();
		buttons.setBounds(0,f.getHeight()*9/10,f.getWidth()/4,f.getHeight()/15);
		buttons.setLayout(new BoxLayout(buttons,BoxLayout.Y_AXIS));
		// JButton analyze=new JButton("Анализ");
		// Wrapper<TaskAnalyzer>analyzer=new Wrapper<>(null);
		// analyze.addActionListener(e->{
		// 	if(analyzer.var==null){
		// 		JFileChooser fc=new JFileChooser(new File(System.getProperty("user.home")+"/Downloads"));
		// 		fc.showOpenDialog(f);
		// 		analyzer.var=new TaskAnalyzer(this,fc.getSelectedFile());
		// 		analyzer.var.analyze();
		// 	}
		// 	analyzer.var.show();
		// });
		// analyze.setBackground(Color.DARK_GRAY);
		// analyze.setForeground(Color.WHITE);
		// buttons.add(analyze);
		JButton parse=new JButton("Создать из разметки");
		parse.addActionListener(e->{
			JFileChooser fc=new JFileChooser(new File(System.getProperty("user.home")+"/Downloads"));
			fc.showOpenDialog(f);
			File file=fc.getSelectedFile();
			if(file!=null){
				int answer=JOptionPane.showConfirmDialog(f,"Включить в проект "+file.getName()+"?","Применить файл разметки?",JOptionPane.OK_CANCEL_OPTION,JOptionPane.QUESTION_MESSAGE);
				if(answer==JOptionPane.OK_OPTION){
					//Ask to think twice if the file is `.used` or if file has incorrect format
					if(file.getName().endsWith(".bpamarkup.used")) answer=JOptionPane.showConfirmDialog(f,"Вы пытаетесь ПОВТОРНО включить в проект "+file.getName()+". Продолжить?","Файл уже использован!",JOptionPane.OK_CANCEL_OPTION,JOptionPane.WARNING_MESSAGE);
					else if(!file.getName().endsWith(".bpamarkup")) answer=JOptionPane.showConfirmDialog(f,"Действительно использовать НЕ помеченный как bpamarkup файл "+file.getName()+".","Файл имеет не то расширение!",JOptionPane.OK_CANCEL_OPTION,JOptionPane.WARNING_MESSAGE);
				}
				if(answer==JOptionPane.OK_OPTION){
					BPAMarkupParser.parse(file,this);
					if(file.getName().endsWith(".bpamarkup")) file.renameTo(new File(file+".used"));
					else if(!file.getName().endsWith(".bpamarkup.used")) JOptionPane.showMessageDialog(f,"Файл не будет помечен как использованный.","Файл не отмечен",JOptionPane.INFORMATION_MESSAGE);
				}
			}
		});
		parse.setBackground(Color.DARK_GRAY);
		parse.setForeground(Color.WHITE);
		buttons.add(parse);
		// JButton saveState=new JButton("Сохранить состояние");
		// saveState.addActionListener(e->{
		// 	if(new File(Root.folder+"Data.ser"+ProgramStarter.version).exists())try{
		// 		new File(projectFolder+"/resources/initial/").mkdirs();
		// 		Files.copy(Path.of(Root.folder+"Data.ser"+ProgramStarter.version),Path.of(projectFolder+"/resources/initial/Data.ser"),StandardCopyOption.REPLACE_EXISTING);
		// 		Files.copy(Path.of(Root.folder+"Users.ser"+ProgramStarter.version),Path.of(projectFolder+"/resources/initial/Users.ser"),StandardCopyOption.REPLACE_EXISTING);
		// 	}catch(IOException ex){throw new UncheckedIOException(ex);}
		// });
		// buttons.add(saveState);
		f.add(buttons);
		JTabbedPane t=new JTabbedPane();
		t.setSize(f.getWidth(),f.getHeight()*4/5);
		JPanel objects=new JPanel();
		objects.addComponentListener(new ComponentAdapter(){
			public void componentShown(ComponentEvent e){
				objects.removeAll();
				fillObjectsTab(objects);
			}
		});
		t.addTab("Объекты",objects);
		JPanel access=new JPanel();
		access.addComponentListener(new ComponentAdapter(){
			public void componentShown(ComponentEvent e){
				access.removeAll();
				fillAccessTab(access);
			}
		});
		t.addTab("Доступ",access);
		JPanel navigator=new JPanel();
		navigator.addComponentListener(new ComponentAdapter(){
			public void componentShown(ComponentEvent e){
				navigator.removeAll();
				fillNavigatorTab(navigator);
			}
		});
		t.addTab("Навигатор",navigator);
		JPanel problems=new JPanel();
		problems.setBackground(Color.BLACK);
		JScrollPane sProblems=SprintUI.createList(10,problems);
		sProblems.setBounds(f.getWidth()/4,f.getHeight()*4/5,f.getWidth()*3/4,f.getHeight()/5);
		class P extends JButton{
			public P(Problem p){
				setText(p.message);
				setBackground(Color.BLACK);
				setForeground(switch(p.type){
					case ERROR -> Color.RED;
					case WARNING -> Color.YELLOW;
					case INFO -> Color.GREEN;
				});
				problems.add(this);
			}
		}
		problemsCache=findProblems();
		for(Problem p:problemsCache)
			new P(p);
		f.add(sProblems);
		f.add(t);
		f.setVisible(true);
		while(f.isVisible())
			Thread.onSpinWait();
		System.exit(0);
	}
	public void runServer(){
		new ProjectServer(this);
	}
	public void runServer(long port){
		new ProjectServer(this,port);
	}
	private String resolveProjectPackage(){
		try{
			Optional<Path> graphFile=Files.walk(projectFolder.toPath()).filter(p->p.getFileName().toString().equals("ProjectGraph.java")).findFirst();
			if(graphFile.isPresent()){
				Path rel=projectFolder.toPath().relativize(graphFile.get().getParent());
				String pkg=rel.toString().replace(File.separatorChar,'.');
				if(!pkg.isBlank()) return pkg;
			}
		}catch(IOException ex){
			throw new UncheckedIOException(ex);
		}
		Package p=ProjectGraph.class.getPackage();
		return p==null?"":p.getName();
	}
	public EditableNode createEditableNode(String name,String objectName,EditableNode.Property...properties){
		if(name==null||name.isBlank()) throw new IllegalArgumentException("Name "+name+" is not a valid identifier.");
		String basePackage=resolveProjectPackage();
		if(basePackage.isBlank()) throw new IllegalStateException("Unable to resolve project package");
		Path editableDir=projectFolder.toPath().resolve(basePackage.replace('.','/')).resolve("editables/registered");
		File file=editableDir.resolve(name+".java").toFile();
		file.getParentFile().mkdirs();
		EditableNode n=new EditableNode(file,objectName,basePackage,properties);
		nodes.add(n);
		return n;
	}
	public void deleteNode(ProjectNode n){
		n.location.delete();
		nodes.remove(n);
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
				JTextField name=new JTextField(p.getName());
				name.addActionListener(e->p.changeName(name.getText(),n));
				name.addFocusListener(new FocusAdapter(){
					public void focusLost(FocusEvent e){
						p.changeName(name.getText(),n);
					}
				});
				c.gridwidth=2;
				c.weightx=0.5;
				add(name,c);
				JComboBox<Property.PropertyType> type=new JComboBox<Property.PropertyType>(Property.PropertyType.values());
				type.setSelectedItem(p.getType());
				type.addItemListener(e->{
					if(e.getStateChange()==ItemEvent.SELECTED) p.changeType((Property.PropertyType)e.getItem(),n);
				});
				c.gridx=2;
				c.gridwidth=1;
				c.weightx=0.25;
				add(type,c);
				JPanel buttons=new JPanel();
				JButton remove=new JButton();
				if(p.getType()==null&&JOptionPane.showConfirmDialog(tab,"Удалить свойство "+p.getName()+"?","Удалить?",JOptionPane.OK_CANCEL_OPTION,JOptionPane.WARNING_MESSAGE)!=JOptionPane.OK_OPTION) return;
				remove.addActionListener(e->{
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
				setBorder(BorderFactory.createTitledBorder(n.name+" ("+n.getObjectName()+")"));
				setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
				JPanel buttons=new JPanel();
				buttons.setPreferredSize(new Dimension(Root.SCREEN_SIZE.width*2/3,Root.SCREEN_SIZE.height/30));
				JPanel addPanel=new JPanel(new GridLayout());
				JComboBox<Property.PropertyType> addType=new JComboBox<>(Property.PropertyType.values());
				addType.setSelectedItem(null);
				addType.setBackground(Color.GREEN);
				addType.addActionListener(e->{
					String name=JOptionPane.showInputDialog("Введите название свойства.");
					if(name==null||name.isBlank()) return;
					String varName=JOptionPane.showInputDialog("Введите название переменной.");
					if(varName==null||varName.isBlank()) return;
					Property nProperty=new Property(name.trim(),(Property.PropertyType)addType.getSelectedItem());
					addType.setSelectedItem(null);
					n.addProperty(nProperty,varName);
					add(new B(nProperty,n),1);
					getTopLevelAncestor().revalidate();
				});
				addPanel.add(addType);
				buttons.add(addPanel);
				JButton remove=new JButton();
				remove.addActionListener(e->{
					if(n.getProperties().isEmpty()||JOptionPane.showConfirmDialog(tab,"Действительно удалить "+n.name+" (\""+n.getObjectName()+")\"?","Удалить?",JOptionPane.OK_CANCEL_OPTION,JOptionPane.WARNING_MESSAGE)==JOptionPane.OK_OPTION){
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
					if(input==null) return;
					String[] s=input.trim().split(", ?");
					if(s.length<2||s[0].isBlank()||s[1].isBlank()){
						new Message("Введите данные правильно!",Color.RED);
						return;
					}
					s[0]=s[0].trim();
					s[1]=s[1].trim();
					setBorder(BorderFactory.createTitledBorder(s[0]+" ("+s[1]+")"));
					if(!n.name.equals(s[0])) n.changeNameIn(ProjectGraph.this,s[0]);
					if(!n.getObjectName().equals(s[1])) n.changeObjectName(s[1]);
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
				for(Property p:n.getProperties())
					add(new B(p,n));
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
				if(name==null||name.isBlank()) break;
				objectName=JOptionPane.showInputDialog("Введите название объекта.");
				if(objectName==null||objectName.isBlank()) break;
				objList.add(new E(createEditableNode(name,objectName)));
			}
			sList.revalidate();
		});
		buttons.add(addButton);
		tab.add(buttons,BorderLayout.SOUTH);
		tab.add(sList,BorderLayout.NORTH);
		for(ProjectNode node:nodes)
			if(node instanceof EditableNode) objList.add(new E((EditableNode)node));
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
					public boolean canImport(TransferSupport support){
						return false;
					}
				});
				name.addMouseListener(new MouseAdapter(){
					public void mousePressed(MouseEvent e){
						name.getTransferHandler().exportAsDrag(name,e,TransferHandler.COPY);
					}
				});
				JButton delete=new JButton();
				delete.addActionListener(e->{
					if(JOptionPane.showConfirmDialog(tab,"Удалить разрешение "+permission+"?","Удалить?",JOptionPane.OK_CANCEL_OPTION,JOptionPane.WARNING_MESSAGE)!=JOptionPane.OK_OPTION) return;
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
					if(JOptionPane.showConfirmDialog(tab,"Удалить "+role.name+"?","Удалить?",JOptionPane.OK_CANCEL_OPTION,JOptionPane.WARNING_MESSAGE)!=JOptionPane.OK_OPTION) return;
					rn.removeRole(role.name);
					Container parent=getParent();
					parent.remove(this);
					((JComponent)parent).getTopLevelAncestor().revalidate();
				});
				delete.setBackground(Color.RED);
				buttons.add(delete);
				add(buttons);
				if(role.permissions!=null) for(String p:role.permissions)
					add(new RP(role,p));
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
						}catch(IOException|UnsupportedFlavorException ex){
							throw new IllegalStateException(ex);
						}
					}
				});
			}
		}
		JPanel pPanel=new JPanel();
		for(String p:pn.permissions)
			pPanel.add(new P(p));
		tab.add(SprintUI.createList(15,pPanel));
		JPanel rPanel=new JPanel(new BorderLayout());
		JPanel rList=new JPanel();
		rList.setLayout(new BoxLayout(rList,BoxLayout.Y_AXIS));
		JPanel rButtons=new JPanel();
		JButton addRole=new JButton();
		addRole.addActionListener(e->{
			String s=JOptionPane.showInputDialog("Введите системное имя роли.");
			if(s==null) return;
			rList.add(new R(rn.addRole(s)));
			rList.revalidate();
		});
		addRole.setText("добавить роль");
		addRole.setBackground(Color.GREEN);
		rButtons.add(addRole);
		for(RoleRepresentation r:rn.roles)
			rList.add(new R(r));
		JScrollPane sRPanel=new JScrollPane(rList);
		sRPanel.getVerticalScrollBar().setUnitIncrement(Root.SCREEN_SIZE.height/60);
		rPanel.add(sRPanel,BorderLayout.CENTER);
		rPanel.add(rButtons,BorderLayout.SOUTH);
		tab.add(rPanel);
	}
	private void fillNavigatorTab(JPanel tab){
		tab.setLayout(new GridLayout());
		Optional<ProjectNode> nodeOptional=nodes.stream().filter(n->n instanceof NavigatorNode).findAny();
		if(nodeOptional.isEmpty()){
			tab.setLayout(new GridLayout(2,1));
			tab.add(new JLabel("helppath.cfg отсутствует в проекте."));
			JButton add=new JButton();
			add.addActionListener(e->{
				tab.removeAll();
				nodes.add(new NavigatorNode(projectFolder));
				fillNavigatorTab(tab);
				tab.revalidate();
			});
			tab.add(add);
			return;
		}
		NavigatorNode n=(NavigatorNode)nodeOptional.get();
		class I extends JPanel{
			public I(int ind,HelpEntry entry){
				setLayout(new GridLayout());
				JTextField text=new JTextField(entry.instructions.get(ind).text);
				text.addFocusListener(new FocusAdapter(){
					public void focusLost(FocusEvent e){
						Instruction c=entry.instructions.get(ind);
						c.text=text.getText();
						entry.replaceInstruction(c,ind,n);
					}
				});
				add(text);
				// JPanel buttons=new JPanel();
				// add(buttons);
			}
		}
		class E extends JPanel{
			public E(HelpEntry entry){
				setBorder(BorderFactory.createLineBorder(new Color(200,255,200),Root.SCREEN_SIZE.height/100));
				setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
				JPanel buttons=new JPanel();
				JButton delete=new JButton();
				delete.addActionListener(e->{
					if(JOptionPane.showConfirmDialog(tab,"Удалить запись "+entry.text+"?","Удалить?",JOptionPane.OK_CANCEL_OPTION,JOptionPane.WARNING_MESSAGE)!=JOptionPane.OK_OPTION) return;
					n.deleteEntry(entry.text);
					Container parent=getParent();
					parent.remove(parent.getComponent(0));
					((JComponent)parent).getTopLevelAncestor().revalidate();
					System.out.println(((Container)parent.getComponent(0)).getComponentCount());
				});
				delete.setText("удалить");
				delete.setBackground(Color.RED);
				buttons.add(delete);
				JButton delLast=new JButton();
				delLast.addActionListener(e->{
					if(JOptionPane.showConfirmDialog(tab,"Удалить последнюю инструкцию записи "+entry.text+"?","Удалить?",JOptionPane.OK_CANCEL_OPTION,JOptionPane.WARNING_MESSAGE)!=JOptionPane.OK_OPTION) return;
					entry.deleteLastInstruction(n);
					remove(getComponentCount()-1);
					revalidate();
				});
				delLast.setText("удалить последнюю инструкцию");
				delLast.setBackground(new Color(100,0,0));
				delLast.setForeground(Color.WHITE);
				buttons.add(delLast);
				add(buttons);
				JButton add=new JButton();
				add.addActionListener(e->{
					String s=JOptionPane.showInputDialog("Введите текст инструкции");
					int type=JOptionPane.showOptionDialog(tab,"Выберите тип инструкции","Выберите тип",JOptionPane.DEFAULT_OPTION,JOptionPane.QUESTION_MESSAGE,null,Instruction.Type.values(),Instruction.Type.TEXT);
					if(s==null||type==JOptionPane.CLOSED_OPTION) return;
					s=s.replace(' ','_').replace('.',';');
					Instruction c=new Instruction(s,Instruction.Type.values()[type]);
					entry.appendInstruction(c,n);
					add(new I(entry.instructions.size()-1,entry),2);
				});
				add.setText("добавить инструкцию");
				add.setBackground(Color.GREEN);
				buttons.add(add);
				add(buttons);
				JTextField textArea=new JTextField(entry.text);
				textArea.addFocusListener(new FocusAdapter(){
					public void focusLost(FocusEvent e){
						if(!textArea.getText().trim().equals(entry.text)) entry.changeText(textArea.getText().trim(),n);
					}
				});
				add(textArea);
				JPanel instructions=new JPanel();
				instructions.setLayout(new BoxLayout(instructions,BoxLayout.Y_AXIS));
				for(int i=0;i<entry.instructions.size();++i)
					instructions.add(new I(i,entry));
				add(new JScrollPane(instructions));
			}
		}
		JPanel panel=new JPanel();
		for(HelpEntry e:n.entries)
			panel.add(new E(e));
		tab.add(SprintUI.createList(15,panel));
	}

}