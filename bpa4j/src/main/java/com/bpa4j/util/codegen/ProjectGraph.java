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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
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
import com.bpa4j.util.codegen.PermissionsNode.FilePermissionsPhysicalNode;
import com.bpa4j.util.codegen.ProjectGraph.NavigatorNode;
import com.bpa4j.util.codegen.ProjectGraph.NavigatorNode.HelpEntry;
import com.bpa4j.util.codegen.ProjectGraph.NavigatorNode.Instruction;
import com.bpa4j.util.codegen.ProjectGraph.NavigatorNode.NavigatorPhysicalNode;
import com.bpa4j.util.codegen.ProjectNode.NodeModel;
import com.bpa4j.util.codegen.ProjectNode.PhysicalNode;
import com.bpa4j.util.codegen.RolesNode.RoleRepresentation;
import com.bpa4j.util.codegen.server.ProjectServer;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;

class GraphModel{
	private final Map<Class<? extends ProjectNode<?>>,List<ProjectNode<?>>> nodesByType=new HashMap<>();
	public <T extends ProjectNode<T>> List<T> getNodes(Class<T> type){
		return nodesByType.getOrDefault(type,List.of()).stream().map(type::cast).toList();
	}
	public <T extends ProjectNode<T>> Optional<T> findNode(Class<T> type,Predicate<T> filter){
		return getNodes(type).stream().filter(filter).findFirst();
	}
	@SuppressWarnings("unchecked")
	public void addNode(ProjectNode<?> node){
		nodesByType.computeIfAbsent((Class<? extends ProjectNode<?>>)node.getClass(),k->new ArrayList<>()).add(node);
	}
	public void removeNode(ProjectNode<?> node){
		nodesByType.get(node.getClass()).remove(node);
	}
	public List<ProjectNode<?>> getAllNodes(){
		return nodesByType.values().stream().flatMap(List::stream).toList();
	}
	/**
	 * FIXME: split model from file system operations
	 * Validates and creates an EditableNode, adding it to the model.
	 * @param projectRoot the project root directory where the node will be created in src/main/java...
	 * @param name the class name (must be a valid Java identifier and unique)
	 * @param objectName the display name for the object
	 * @param basePackage the base package for the node
	 * @param properties the properties to initialize the node with
	 * @return the created EditableNode
	 * @throws IllegalArgumentException if name is invalid or already exists
	 * @throws IOException if node creation fails
	 */
	public EditableNode createEditableNode(File projectRoot,String name,String objectName,String basePackage,EditableNode.Property...properties) throws IOException{
		validateEditableNodeName(name);
		EditableNode.FileEditablePhysicalNode physicalNode=new EditableNode.FileEditablePhysicalNode(name,objectName,basePackage,projectRoot,properties);
		EditableNode node=new EditableNode(physicalNode,name,objectName,basePackage+".editables.registered",properties);
		addNode(node);
		return node;
	}
	/**
	 * Validates that the name is a valid Java identifier and unique among EditableNodes.
	 */
	private void validateEditableNodeName(String name){
		if(name==null||name.isBlank()) throw new IllegalArgumentException("Name '"+name+"' is not a valid identifier.");
		if(!isValidJavaIdentifier(name)) throw new IllegalArgumentException("Name '"+name+"' is not a valid Java identifier.");
		// Check for uniqueness
		for(ProjectNode<?> node:getAllNodes()){
			if(node instanceof EditableNode editableNode&&editableNode.getName().equals(name)){ throw new IllegalArgumentException("EditableNode with name '"+name+"' already exists."); }
		}
	}
	/**
	 * Checks if a string is a valid Java identifier.
	 */
	private static boolean isValidJavaIdentifier(String s){
		if(s.isEmpty()||!Character.isJavaIdentifierStart(s.charAt(0))) return false;
		for(int i=1;i<s.length();++i){
			if(!Character.isJavaIdentifierPart(s.charAt(i))) return false;
		}
		return true;
	}
}

class GraphParser{
	private final JavaParser javaParser=new JavaParser();
	public GraphModel load(Path projectFolder){
		GraphModel model=new GraphModel();
		try{
			Files.walkFileTree(projectFolder,new SimpleFileVisitor<Path>(){
				@Override
				public FileVisitResult visitFile(Path file,BasicFileAttributes attrs) throws IOException{
					ProjectNode<?> node=loadNode(file);
					if(node!=null){
						model.addNode(node);
					}
					return FileVisitResult.CONTINUE;
				}
			});
		}catch(IOException ex){
			throw new UncheckedIOException(ex);
		}
		return model;
	}
	private ProjectNode<?> loadNode(Path file) throws IOException{
		if(file.toString().endsWith(".java")){
			return loadJavaNode(file);
		}else if(file.getFileName().toString().equals("helppath.cfg")){
			NavigatorPhysicalNode pn=new NavigatorNode.FileNavigatorPhysicalNode(file.toFile());
			return new ProjectGraph.NavigatorNode(pn);
		}
		return null;
	}
	private ProjectNode<?> loadJavaNode(Path file) throws IOException{
		CompilationUnit cu=javaParser.parse(file).getResult().orElse(null);
		if(cu==null) return null;
		Optional<ClassOrInterfaceDeclaration> mainClass=cu.findAll(ClassOrInterfaceDeclaration.class).stream().filter(clazz->clazz.getNameAsString().equals("Main")).findFirst();
		if(mainClass.isPresent()){
			FilePermissionsPhysicalNode permPN=new FilePermissionsPhysicalNode(file.toFile());
			PermissionsNode permNode=new PermissionsNode(permPN);
			return permNode;
			//FIXME: also create roles node.
			//FIXME: ensure consistency with the old ProjectGraph.
		}
		Optional<ClassOrInterfaceDeclaration> editableClass=cu.findAll(ClassOrInterfaceDeclaration.class).stream().filter(clazz->clazz.getExtendedTypes().stream().anyMatch(type->type instanceof ClassOrInterfaceType&&((ClassOrInterfaceType)type).getNameAsString().contains("Editable"))).findFirst();
		if(editableClass.isPresent()) return new EditableNode(new EditableNode.FileEditablePhysicalNode(file.toFile()));
		return null;
	}
	public void save(GraphModel model,Path projectFolder){
		for(ProjectNode<?> node:model.getAllNodes()){
			saveNode(node);
		}
	}
	@SuppressWarnings("unchecked")
	private <T extends ProjectNode<T>> void saveNode(ProjectNode<T> node){
		node.getPhysicalRepresentation().persist((NodeModel<T>)node);
	}
}

/*class GraphUI{
	private final GraphModel model;
	private final ProjectGraph graph;
	public GraphUI(GraphModel model,ProjectGraph graph){
		this.model=model;
		this.graph=graph;
	}
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
		JButton parse=new JButton("Создать из разметки");
		parse.addActionListener(e->{
			JFileChooser fc=new JFileChooser(new File(System.getProperty("user.home")+"/Downloads"));
			fc.showOpenDialog(f);
			File file=fc.getSelectedFile();
			if(file!=null){
				int answer=JOptionPane.showConfirmDialog(f,"Включить в проект "+file.getName()+"?","Применить файл разметки?",JOptionPane.OK_CANCEL_OPTION,JOptionPane.QUESTION_MESSAGE);
				if(answer==JOptionPane.OK_OPTION){
					if(file.getName().endsWith(".bpamarkup.used")) answer=JOptionPane.showConfirmDialog(f,"Вы пытаетесь ПОВТОРНО включить в проект "+file.getName()+". Продолжить?","Файл уже использован!",JOptionPane.OK_CANCEL_OPTION,JOptionPane.WARNING_MESSAGE);
					else if(!file.getName().endsWith(".bpamarkup")) answer=JOptionPane.showConfirmDialog(f,"Действительно использовать НЕ помеченный как bpamarkup файл "+file.getName()+".","Файл имеет не то расширение!",JOptionPane.OK_CANCEL_OPTION,JOptionPane.WARNING_MESSAGE);
				}
				if(answer==JOptionPane.OK_OPTION){
					BPAMarkupParser.parse(file,graph);
					if(file.getName().endsWith(".bpamarkup")) file.renameTo(new File(file+".used"));
					else if(!file.getName().endsWith(".bpamarkup.used")) JOptionPane.showMessageDialog(f,"Файл не будет помечен как использованный.","Файл не отмечен",JOptionPane.INFORMATION_MESSAGE);
				}
			}
		});
		parse.setBackground(Color.DARK_GRAY);
		parse.setForeground(Color.WHITE);
		buttons.add(parse);
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
		ArrayList<Problem> problemsCache=graph.findProblems();
		for(Problem p:problemsCache)
			new P(p);
		f.add(sProblems);
		f.add(t);
		f.setVisible(true);
		while(f.isVisible())
			Thread.onSpinWait();
		System.exit(0);
	}
	private void fillObjectsTab(JPanel tab){
		PermissionsNode pn=(PermissionsNode)model.getAllNodes().parallelStream().filter(n->n instanceof PermissionsNode).findAny().get();
		tab.setLayout(new BorderLayout());
		class B extends JPanel{
			public B(Property p,EditableNode n){
				setLayout(new GridBagLayout());
				GridBagConstraints c=new GridBagConstraints();
				c.fill=GridBagConstraints.BOTH;
				c.weighty=1;
				c.gridheight=1;
				JTextField name=new JTextField(p.getName());
				name.addActionListener(e->n.changePropertyName(p,name.getText()));
				name.addFocusListener(new FocusAdapter(){
					public void focusLost(FocusEvent e){
						n.changePropertyName(p,name.getText());
					}
				});
				c.gridwidth=2;
				c.weightx=0.5;
				add(name,c);
				JComboBox<Property.PropertyType> type=new JComboBox<Property.PropertyType>(Property.PropertyType.values());
				type.setSelectedItem(p.getType());
				type.addItemListener(e->{
					if(e.getStateChange()==ItemEvent.SELECTED) n.changePropertyType(p,(Property.PropertyType)e.getItem());
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
				setBorder(BorderFactory.createTitledBorder(n.getName()+" ("+n.getObjectName()+")"));
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
						graph.deleteNode(n);
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
					if(!n.name.equals(s[0])) n.changeNameIn(graph,s[0]);
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
			while(true)
				try{
					name=JOptionPane.showInputDialog("Введите название класса.");
					if(name==null||name.isBlank()) break;
					objectName=JOptionPane.showInputDialog("Введите название объекта.");
					if(objectName==null||objectName.isBlank()) break;
					objList.add(new E(graph.createEditableNode(name,objectName)));
				}catch(IOException ex){
					throw new UncheckedIOException(ex);
				}
			sList.revalidate();
		});
		buttons.add(addButton);
		tab.add(buttons,BorderLayout.SOUTH);
		tab.add(sList,BorderLayout.NORTH);
		for(ProjectNode<?> node:model.getAllNodes())
			if(node instanceof EditableNode) objList.add(new E((EditableNode)node));
	}
	private void fillAccessTab(JPanel tab){
		tab.setLayout(new GridLayout(1,2));
		PermissionsNode pn=(PermissionsNode)model.getAllNodes().parallelStream().filter(n->n instanceof PermissionsNode).findAny().get();
		RolesNode rn=(RolesNode)model.getAllNodes().parallelStream().filter(n->n instanceof RolesNode).findAny().get();
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
		for(String p:pn.getPermissions())
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
		for(RoleRepresentation r:rn.getRoles())
			rList.add(new R(r));
		JScrollPane sRPanel=new JScrollPane(rList);
		sRPanel.getVerticalScrollBar().setUnitIncrement(Root.SCREEN_SIZE.height/60);
		rPanel.add(sRPanel,BorderLayout.CENTER);
		rPanel.add(rButtons,BorderLayout.SOUTH);
		tab.add(rPanel);
	}
	private void fillNavigatorTab(JPanel tab){
		tab.setLayout(new GridLayout());
		Optional<ProjectNode<?>> nodeOptional=model.getAllNodes().stream().filter(n->n instanceof ProjectGraph.NavigatorNode).findAny();
		if(nodeOptional.isEmpty()){
			tab.setLayout(new GridLayout(2,1));
			tab.add(new JLabel("helppath.cfg отсутствует в проекте."));
			JButton add=new JButton();
			add.addActionListener(e->{
				tab.removeAll();
				model.addNode(new ProjectGraph.NavigatorNode(graph.projectFolder));
				fillNavigatorTab(tab);
				tab.revalidate();
			});
			tab.add(add);
			return;
		}
		ProjectGraph.NavigatorNode n=(ProjectGraph.NavigatorNode)nodeOptional.get();
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
}*/

public class ProjectGraph{
	public static class NavigatorNode implements ProjectNode<NavigatorNode>{
		public static class NavigatorModel implements NodeModel<NavigatorNode>{
			private final ArrayList<HelpEntry> entries=new ArrayList<>();
			public ArrayList<HelpEntry> getEntries(){
				return entries;
			}
			public NavigatorModel(Collection<HelpEntry>entries){
				this.entries.addAll(entries);
			}
			public HelpEntry deleteEntry(String text){
				HelpEntry e=entries.stream().filter(entry->entry.text.equals(text)).findAny().get();
				entries.remove(e);
				return e;
			}
		}
		public static interface NavigatorPhysicalNode extends PhysicalNode<NavigatorNode>{
			void deleteEntry(String text);
			void changeText(String oldText,String text);
			void appendInstruction(String entryText,Instruction instruction);
			void replaceInstruction(String entryText,int index,Instruction instruction);
			void deleteLastInstruction(String entryText);
			NavigatorModel load();
		}
		public static class FileNavigatorPhysicalNode implements NavigatorPhysicalNode{
			public File file;
			public FileNavigatorPhysicalNode(File file){
				this.file=file;
			}
			public void changeText(String oldText,String text){
				try{
					StringBuilder b=new StringBuilder();
					for(String l:Files.readString(file.toPath()).split("\n")){
						String[] s=l.split(" ",2);
						if(s[1].equals(oldText)) s[1]=text;
						b.append(s[0]).append(' ').append(s[1]).append('\n');
					}
					Files.writeString(file.toPath(),b);
				}catch(IOException ex){
					throw new UncheckedIOException(ex);
				}
			}
			public void deleteEntry(String text){
				try{
					List<String> lines=Files.readAllLines(file.toPath());
					List<String> updated=new ArrayList<>();
					for(String line:lines){
						if(!line.trim().equals(text.trim())){
							updated.add(line);
						}
					}
					Files.write(file.toPath(),updated);
				}catch(IOException ex){
					throw new UncheckedIOException(ex);
				}
			}
			public void appendInstruction(String entryText,Instruction instruction){
				try{
					StringBuilder b=new StringBuilder();
					for(String l:Files.readString(file.toPath()).split("\n")){
						String[] s=l.split(" ",2);
						b.append(s[0]);
						if(s[1].equals(entryText)) b.append('.').append(instruction.toString());
						b.append(' ').append(s[1]).append('\n');
					}
					Files.writeString(file.toPath(),b);
				}catch(IOException ex){
					throw new UncheckedIOException(ex);
				}
			}
			public void replaceInstruction(String entryText,int index,Instruction instruction){
				try{
					StringBuilder b=new StringBuilder();
					for(String l:Files.readString(file.toPath()).split("\n")){
						String[] s=l.split(" ",2);
						if(entryText.equals(s[1])){
							String[] t=s[0].split("\\.");
							int i=0;
							for(;i<index;++i)
								b.append(t[i]).append('.');
							b.append(instruction.toString()).append('.');
							++i;
							for(;i<t.length;++i)
								b.append(t[i]).append('.');
							b.deleteCharAt(b.length()-1);
						}else b.append(s[0]);
						b.append(' ').append(s[1]).append('\n');
					}
					Files.writeString(file.toPath(),b);
				}catch(IOException ex){
					throw new UncheckedIOException(ex);
				}
			}
			public void deleteLastInstruction(String entryText){
				try{
					StringBuilder b=new StringBuilder();
					for(String l:Files.readString(file.toPath()).split("\n")){
						String[] s=l.split(" ",2);
						if(entryText.equals(s[1])){
							String[] t=s[0].split("\\.");
							for(int j=0;j<t.length-1;++j)
								b.append(t[j]).append('.');
							if(b.length()>0 && b.charAt(b.length()-1)=='.')
								b.deleteCharAt(b.length()-1);
						}else b.append(s[0]);
						b.append(' ').append(s[1]).append('\n');
					}
					Files.writeString(file.toPath(),b);
				}catch(IOException ex){
					throw new UncheckedIOException(ex);
				}
			}
			public void clear(){
				file.delete();
			}
			public void persist(NodeModel<NavigatorNode>node) throws IllegalStateException{
				StringBuilder b=new StringBuilder();
				NavigatorModel n=(NavigatorModel)node;
				for(HelpEntry h:n.getEntries()){
					String inst=h.getInstructions()
						.stream()
						.map(e->e.toString())
						.collect(Collectors.joining(","));
					b.append(inst);
					b.append(' ');
					b.append(h.text);
				}
				try{
					Files.writeString(file.toPath(),b);
				}catch(IOException ex){
					throw new UncheckedIOException(ex);
				}
			}
			public NavigatorModel load(){
				ArrayList<HelpEntry>entries=new ArrayList<>();
				try{
					String str=Files.readString(file.toPath());
					if(str.isBlank()) return new NavigatorModel(entries);
					for(String l:str.split("\n")){
						String[] s=l.split(" ",2);
						HelpEntry e=new HelpEntry(s[1]);
						e.instructions.addAll(Stream.of(s[0].split("\\.")).map(t->new Instruction(t.substring(1),Instruction.Type.toType(t.charAt(0)))).toList());
						entries.add(e);
					}
					return new NavigatorModel(entries);
				}catch(IOException ex){
					throw new UncheckedIOException(ex);
				}
			}
			public boolean exists(){
				return file.exists();
			}
			
		}
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
			private String text;
			private Type type;
			public Instruction(String text,Type type){
				this.text=text;
				this.type=type;
			}
			public String toString(){
				return type.toChar()+text;
			}
		}
		public static class HelpEntry{
			private String text;
			private final ArrayList<Instruction> instructions=new ArrayList<>();
			public HelpEntry(String text){
				this.text=text;
			}
			protected void changeText(String text){
				this.text=text;
			}
			public void replaceInstruction(Instruction instruction,int index){
				instructions.set(index,instruction);
			}
			public void appendInstruction(Instruction instruction){
				instructions.add(instruction);
			}
			public void deleteLastInstruction(){
				instructions.removeLast();
			}
			/**
			 * Returns unmodifiable list of instructions.
			 */
			public List<Instruction>getInstructions(){
				return Collections.unmodifiableList(instructions);
			}
			public String getText(){
				return text;
			}
		}
		private final NavigatorPhysicalNode physicalNode;
		private final NavigatorModel model;
		public NavigatorNode(NavigatorPhysicalNode physicalNode){
			model=physicalNode.load();
			this.physicalNode=physicalNode;
		}
		public NavigatorNode(NavigatorPhysicalNode physicalNode,List<HelpEntry>entries){
			model=new NavigatorModel(entries);
			physicalNode.persist(model);
			this.physicalNode=physicalNode;
		}
		public void deleteEntry(String text){
			HelpEntry deleted=model.deleteEntry(text);
			physicalNode.deleteEntry(text);
		}
		public NavigatorPhysicalNode getPhysicalRepresentation(){
			return physicalNode;
		}
		public NavigatorModel getModel(){
			return model;
		}
		public void changeEntryText(HelpEntry h,String newText){
			String oldText=h.getText();
			h.changeText(newText);
			physicalNode.changeText(oldText,newText);
		}
		public void appendInstruction(HelpEntry entry,Instruction instruction){
			entry.appendInstruction(instruction);
			physicalNode.appendInstruction(entry.getText(),instruction);
		}
		public void replaceInstruction(HelpEntry entry,int index,Instruction instruction){
			entry.replaceInstruction(instruction,index);
			physicalNode.replaceInstruction(entry.getText(),index,instruction);
		}
		public void deleteLastInstruction(HelpEntry entry){
			entry.deleteLastInstruction();
			physicalNode.deleteLastInstruction(entry.getText());
		}
	}
	private static final ArrayList<DiagnosticService> diagnosticServices=new ArrayList<>();
	private final GraphModel model;
	private final GraphParser parser;
	private final GraphUI ui;
	public final File projectFolder;
	public ProjectGraph(File projectFolder){
		projectFolder.mkdirs();
		this.projectFolder=projectFolder;
		this.parser=new GraphParser();
		this.model=parser.load(projectFolder.toPath());
		this.ui=new GraphUI(model,this);
	}
	public GraphModel getModel(){
		return model;
	}
	public ArrayList<Problem> findProblems(){
		ArrayList<Problem> problems=new ArrayList<>();
		for(DiagnosticService service:diagnosticServices){
			problems.addAll(service.findProblems(this));
		}
		return problems;
	}
	public void show(){
		ui.show();
	}
	public void runServer(){
		new ProjectServer(this);
	}
	public void runServer(long port){
		new ProjectServer(this,port);
	}
	public void save(){
		parser.save(model,projectFolder.toPath());
	}
	public EditableNode createEditableNode(String name,String objectName,EditableNode.Property...properties) throws IOException{
		String basePackage=resolveProjectPackage();
		if(basePackage.isBlank()) throw new IllegalStateException("Unable to resolve project package");
		return model.createEditableNode(projectFolder,name,objectName,basePackage,properties);
	}
	public void deleteNode(ProjectNode<?> node){
		node.getPhysicalRepresentation().clear();
		model.removeNode(node);
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
}
