package com.bpa4j.util.codegen.legacy;

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
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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

import com.bpa4j.core.ProgramStarter;
import com.bpa4j.core.Root;
import com.bpa4j.ui.Message;
import com.bpa4j.util.ParseUtils;
import com.bpa4j.util.SprintUI;
import com.bpa4j.util.codegen.BPAMarkupParser;
import com.bpa4j.util.codegen.legacy.EditableNodeLegacy.Property;
import com.bpa4j.util.codegen.legacy.NavigatorNodeLegacy.HelpEntry;
import com.bpa4j.util.codegen.legacy.NavigatorNodeLegacy.Instruction;
import com.bpa4j.util.codegen.legacy.RolesNodeLegacy.RoleRepresentation;

/**
 * A java BPA project representation.
 * Designed to provide low-code access.
 */
public class ProjectGraphLegacy{
	public static class Problem{
		public static enum ProblemType{
			ERROR,
			WARNING,
			INFO;
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
	public ArrayList<ProjectNodeLegacy>nodes=new ArrayList<>();
	public File projectFolder;
	/**
	 * @param projectFolder - the project folder. It is recommended to pass <b>src/main/java</b> path:
	 */
	public ProjectGraphLegacy(File projectFolder){
		this.projectFolder=projectFolder;
		projectFolder.mkdirs();
		try{
			Files.walkFileTree(projectFolder.toPath(),new SimpleFileVisitor<Path>(){
				private final Pattern editablePattern=Pattern.compile("\\W?Editable\\W?");
				private final Pattern processablePattern=Pattern.compile("\\WProcessable\\W");
				public FileVisitResult visitFile(Path file,BasicFileAttributes attrs)throws IOException{
					if(file.toString().endsWith(".java")){
						Matcher m=ParseUtils.classDefPattern.matcher(Files.readString(file));
						if(m.find()){
							if(m.group(1).equals("Main")){
								nodes.add(new PermissionsNodeLegacy(file.toFile()));
								nodes.add(new RolesNodeLegacy(file.toFile(),(PermissionsNodeLegacy)nodes.getLast()));
							// }else if(m.group(2)!=null&&processablePattern.matcher(m.group(2)).find())nodes.add(new EditableNode(file.toFile())); //TODO: add ProcessableNode
							}else if(m.group(2)!=null&&editablePattern.matcher(m.group(2)).find())nodes.add(new EditableNodeLegacy(file.toFile()));
							System.err.println("Parsed: "+m.group(1));
						}
					}else if(file.getFileName().toString().equals("helppath.cfg")){
						nodes.add(new NavigatorNodeLegacy(file.toFile()));
					}
					return FileVisitResult.CONTINUE;
				}
			});
			System.err.println("Parsing completed!");
		}catch(IOException ex){throw new UncheckedIOException(ex);}
	}
	private void fillObjectsTab(JPanel tab){
		PermissionsNodeLegacy pn=(PermissionsNodeLegacy)nodes.parallelStream().filter(n->n instanceof PermissionsNodeLegacy).findAny().get();
		tab.setLayout(new BorderLayout());
		class B extends JPanel{
			public B(Property p,EditableNodeLegacy n){
				setLayout(new GridBagLayout());
				GridBagConstraints c=new GridBagConstraints();
				c.fill=GridBagConstraints.BOTH;
				c.weighty=1;
				c.gridheight=1;
				JTextField name=new JTextField(p.name);
				name.addActionListener(e->p.changeName(name.getText(),n));
				name.addFocusListener(new FocusAdapter(){
					public void focusLost(FocusEvent e){p.changeName(name.getText(),n);}
				});
				c.gridwidth=2;
				c.weightx=0.5;
				add(name,c);
				JComboBox<Property.PropertyType>type=new JComboBox<Property.PropertyType>(Property.PropertyType.values());
				type.setSelectedItem(p.type);
				type.addItemListener(e->{if(e.getStateChange()==ItemEvent.SELECTED)p.changeType((Property.PropertyType)e.getItem(),n);});
				c.gridx=2;
				c.gridwidth=1;
				c.weightx=0.25;
				add(type,c);
				JPanel buttons=new JPanel();
				JButton remove=new JButton();
				remove.addActionListener(e->{
					if(p.type==null&&JOptionPane.showConfirmDialog(tab,"Удалить свойство "+p.name+"?","Удалить?",JOptionPane.OK_CANCEL_OPTION,JOptionPane.WARNING_MESSAGE)!=JOptionPane.OK_OPTION)return;
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
			public E(EditableNodeLegacy n){
				setBorder(BorderFactory.createTitledBorder(n.name+" ("+n.objectName+")"));
				setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
				JPanel buttons=new JPanel();
				buttons.setPreferredSize(new Dimension(Root.SCREEN_SIZE.width*2/3,Root.SCREEN_SIZE.height/30));
				JPanel addPanel=new JPanel(new GridLayout());
				JComboBox<Property.PropertyType>addType=new JComboBox<>(Property.PropertyType.values());
				addType.setSelectedItem(null);
				addType.setBackground(Color.GREEN);
				addType.addActionListener(e->{
					String name=JOptionPane.showInputDialog("Введите название свойства.");
					if(name==null||name.isBlank())return;
					String varName=JOptionPane.showInputDialog("Введите название переменной.");
					if(varName==null||varName.isBlank())return;
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
					if(n.properties.isEmpty()||JOptionPane.showConfirmDialog(tab,"Действительно удалить "+n.name+" (\""+n.objectName+")\"?","Удалить?",JOptionPane.OK_CANCEL_OPTION,JOptionPane.WARNING_MESSAGE)==JOptionPane.OK_OPTION){
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
					if(input==null)return;
					String[]s=input.trim().split(", ?");
					if(s.length<2||s[0].isBlank()||s[1].isBlank()){
						new Message("Введите данные правильно!",Color.RED);
						return;
					}
					s[0]=s[0].trim();
					s[1]=s[1].trim();
					setBorder(BorderFactory.createTitledBorder(s[0]+" ("+s[1]+")"));
					if(!n.name.equals(s[0]))n.changeNameIn(ProjectGraphLegacy.this,s[0]);
					if(!n.objectName.equals(s[1]))n.changeObjectName(s[1]);
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
				for(Property p:n.properties)add(new B(p,n));
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
				if(name==null||name.isBlank())break;
				objectName=JOptionPane.showInputDialog("Введите название объекта.");
				if(objectName==null||objectName.isBlank())break;
				objList.add(new E(createEditableNode(name,objectName)));
			}
			sList.revalidate();
		});
		buttons.add(addButton);
		tab.add(buttons,BorderLayout.SOUTH);
		tab.add(sList,BorderLayout.NORTH);
		for(ProjectNodeLegacy node:nodes)if(node instanceof EditableNodeLegacy)objList.add(new E((EditableNodeLegacy)node));
	}
	private void fillAccessTab(JPanel tab){
		tab.setLayout(new GridLayout(1,2));
		PermissionsNodeLegacy pn=(PermissionsNodeLegacy)nodes.parallelStream().filter(n->n instanceof PermissionsNodeLegacy).findAny().get();
		RolesNodeLegacy rn=(RolesNodeLegacy)nodes.parallelStream().filter(n->n instanceof RolesNodeLegacy).findAny().get();
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
					public boolean canImport(TransferSupport support){return false;}
				});
				name.addMouseListener(new MouseAdapter(){
					public void mousePressed(MouseEvent e){
						name.getTransferHandler().exportAsDrag(name,e,TransferHandler.COPY);
					}
				});
				JButton delete=new JButton();
				delete.addActionListener(e->{
					if(JOptionPane.showConfirmDialog(tab,"Удалить разрешение "+permission+"?","Удалить?",JOptionPane.OK_CANCEL_OPTION,JOptionPane.WARNING_MESSAGE)!=JOptionPane.OK_OPTION)return;
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
					if(JOptionPane.showConfirmDialog(tab,"Удалить "+role.name+"?","Удалить?",JOptionPane.OK_CANCEL_OPTION,JOptionPane.WARNING_MESSAGE)!=JOptionPane.OK_OPTION)return;
					rn.removeRole(role.name);
					Container parent=getParent();
					parent.remove(this);
					((JComponent)parent).getTopLevelAncestor().revalidate();
				});
				delete.setBackground(Color.RED);
				buttons.add(delete);
				add(buttons);
				if(role.permissions!=null)for(String p:role.permissions)add(new RP(role,p));
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
						}catch(IOException|UnsupportedFlavorException ex){throw new IllegalStateException(ex);}
					}
				});
			}
		}
		JPanel pPanel=new JPanel();
		for(String p:pn.permissions)pPanel.add(new P(p));
		tab.add(SprintUI.createList(15,pPanel));
		JPanel rPanel=new JPanel(new BorderLayout());
		JPanel rList=new JPanel();
		rList.setLayout(new BoxLayout(rList,BoxLayout.Y_AXIS));
		JPanel rButtons=new JPanel();
		JButton addRole=new JButton();
		addRole.addActionListener(e->{
			String s=JOptionPane.showInputDialog("Введите системное имя роли.");
			if(s==null)return;
			rList.add(new R(rn.addRole(s)));
			rList.revalidate();
		});
		addRole.setText("добавить роль");
		addRole.setBackground(Color.GREEN);
		rButtons.add(addRole);
		for(RoleRepresentation r:rn.roles)rList.add(new R(r));
		JScrollPane sRPanel=new JScrollPane(rList);
		sRPanel.getVerticalScrollBar().setUnitIncrement(Root.SCREEN_SIZE.height/60);
		rPanel.add(sRPanel,BorderLayout.CENTER);
		rPanel.add(rButtons,BorderLayout.SOUTH);
		tab.add(rPanel);
	}
	private void fillNavigatorTab(JPanel tab){
		tab.setLayout(new GridLayout());
		Optional<ProjectNodeLegacy>nodeOptional=nodes.stream().filter(n->n instanceof NavigatorNodeLegacy).findAny();
		if(nodeOptional.isEmpty()){
			//TODO: add "no helppath.cfg in this project" sign and a button to add it
			tab.setLayout(new GridLayout(2,1));
			tab.add(new JLabel("helppath.cfg отсутствует в проекте."));
			JButton add=new JButton();
			add.addActionListener(e->{
				tab.removeAll();
				nodes.add(new NavigatorNodeLegacy(projectFolder));
				fillNavigatorTab(tab);
				tab.revalidate();
			});
			tab.add(add);
			return;
		}
		NavigatorNodeLegacy n=(NavigatorNodeLegacy)nodeOptional.get();
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
					if(JOptionPane.showConfirmDialog(tab,"Удалить запись "+entry.text+"?","Удалить?",JOptionPane.OK_CANCEL_OPTION,JOptionPane.WARNING_MESSAGE)!=JOptionPane.OK_OPTION)return;
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
					if(JOptionPane.showConfirmDialog(tab,"Удалить последнюю инструкцию записи "+entry.text+"?","Удалить?",JOptionPane.OK_CANCEL_OPTION,JOptionPane.WARNING_MESSAGE)!=JOptionPane.OK_OPTION)return;
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
					if(s==null||type==JOptionPane.CLOSED_OPTION)return;
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
					public void focusLost(FocusEvent e){if(!textArea.getText().trim().equals(entry.text))entry.changeText(textArea.getText().trim(),n);}
				});
				add(textArea);
				JPanel instructions=new JPanel();
				instructions.setLayout(new BoxLayout(instructions,BoxLayout.Y_AXIS));
				for(int i=0;i<entry.instructions.size();++i)instructions.add(new I(i,entry));
				add(new JScrollPane(instructions));
			}
		}
		JPanel panel=new JPanel();
		for(HelpEntry e:n.entries)panel.add(new E(e));
		tab.add(SprintUI.createList(15,panel));
	}
	public EditableNodeLegacy createEditableNode(String name,String objectName,EditableNodeLegacy.Property...properties){
		File file=new File(projectFolder,"com");
		if(file.isDirectory())file=new File(Stream.of(file.listFiles()).filter(f->f.isDirectory()).findAny().get(),"editables/registered/"+name+".java");
		else file=new File(projectFolder,name+".java");
		file.getParentFile().mkdirs();
		EditableNodeLegacy n=new EditableNodeLegacy(file,objectName,properties);
		nodes.add(n);
		return n;
	}
	public void deleteNode(ProjectNodeLegacy n){
		n.location.delete();
		nodes.remove(n);
	}
	public ArrayList<Problem>findProblems(){
		ArrayList<Problem>a=new ArrayList<>();
		//TODO: find problems
		return a;
	}
	/**
	 * Shows low-code programming UI.
	 */
	public void show(){
		try{
			UIManager.setLookAndFeel(new NimbusLookAndFeel());
		}catch(UnsupportedLookAndFeelException ex){throw new AssertionError("The BPALookAndFeel must be supported.",ex);}
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
					if(file.getName().endsWith(".bpamarkup.used"))answer=JOptionPane.showConfirmDialog(f,"Вы пытаетесь ПОВТОРНО включить в проект "+file.getName()+". Продолжить?","Файл уже использован!",JOptionPane.OK_CANCEL_OPTION,JOptionPane.WARNING_MESSAGE);
					else if(!file.getName().endsWith(".bpamarkup"))answer=JOptionPane.showConfirmDialog(f,"Действительно использовать НЕ помеченный как bpamarkup файл "+file.getName()+".","Файл имеет не то расширение!",JOptionPane.OK_CANCEL_OPTION,JOptionPane.WARNING_MESSAGE);
				}
				if(answer==JOptionPane.OK_OPTION){
					BPAMarkupParser.parse(file,this);
					if(file.getName().endsWith(".bpamarkup"))file.renameTo(new File(file+".used"));
					else if(!file.getName().endsWith(".bpamarkup.used"))JOptionPane.showMessageDialog(f,"Файл не будет помечен как использованный.","Файл не отмечен",JOptionPane.INFORMATION_MESSAGE);
				}
			}
		});
		parse.setBackground(Color.DARK_GRAY);
		parse.setForeground(Color.WHITE);
		buttons.add(parse);
		JButton saveState=new JButton("Сохранить состояние");
		saveState.addActionListener(e->{
			if(new File(Root.folder+"Data.ser"+ProgramStarter.version).exists())try{
				new File(projectFolder+"/resources/initial/").mkdirs();
				Files.copy(Path.of(Root.folder+"Data.ser"+ProgramStarter.version),Path.of(projectFolder+"/resources/initial/Data.ser"),StandardCopyOption.REPLACE_EXISTING);
				Files.copy(Path.of(Root.folder+"Users.ser"+ProgramStarter.version),Path.of(projectFolder+"/resources/initial/Users.ser"),StandardCopyOption.REPLACE_EXISTING);
			}catch(IOException ex){throw new UncheckedIOException(ex);}
		});
		buttons.add(saveState);
		f.add(buttons);
		JTabbedPane p=new JTabbedPane();
		p.setSize(f.getWidth(),f.getHeight()*4/5);
		JPanel objects=new JPanel();
		objects.addComponentListener(new ComponentAdapter(){
			public void componentShown(ComponentEvent e){
				objects.removeAll();
				fillObjectsTab(objects);
			}
		});	
		p.addTab("Объекты",objects);
		JPanel access=new JPanel();
		access.addComponentListener(new ComponentAdapter(){
			public void componentShown(ComponentEvent e){
				access.removeAll();
				fillAccessTab(access);
			}
		});	
		p.addTab("Доступ",access);
		JPanel navigator=new JPanel();
		navigator.addComponentListener(new ComponentAdapter(){
			public void componentShown(ComponentEvent e){
				navigator.removeAll();
				fillNavigatorTab(navigator);
			}
		});	
		p.addTab("Навигатор",navigator);
		JPanel problems=new JPanel();
		problems.setBackground(Color.BLACK);
		JScrollPane sProblems=SprintUI.createList(10,problems);
		sProblems.setBounds(f.getWidth()/4,f.getHeight()*4/5,f.getWidth()*3/4,f.getHeight()/5);
		class P extends JButton{
			public P(Problem p){
				setText(p.message);
				setBackground(Color.BLACK);
				setForeground(switch(p.type){
					case ERROR->Color.RED;
					case WARNING->Color.YELLOW;
					case INFO->Color.GREEN;
				});
				problems.add(this);
			}
		}
		f.add(sProblems);
		f.add(p);
		f.setVisible(true);
		while(f.isVisible())Thread.onSpinWait();
		System.exit(0);
	}
}
