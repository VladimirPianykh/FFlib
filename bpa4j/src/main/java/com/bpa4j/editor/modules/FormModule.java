package com.bpa4j.editor.modules;

import java.awt.BasicStroke;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.lang.reflect.Field;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.function.Supplier;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLayer;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.Timer;
import javax.swing.plaf.LayerUI;
import javax.swing.table.AbstractTableModel;

import com.bpa4j.Wrapper;
import com.bpa4j.core.Data;
import com.bpa4j.core.EditableDemo;
import com.bpa4j.core.ProgramStarter;
import com.bpa4j.defaults.input.EmptySaver;
import com.bpa4j.core.Data.Editable;
import com.bpa4j.core.Data.EditableGroup;
import com.bpa4j.editor.EditorEntry;
import com.bpa4j.editor.EditorEntryBase;
import com.bpa4j.editor.InfoProvider;
import com.bpa4j.editor.Input;
import com.bpa4j.editor.NameProvider;
import com.bpa4j.editor.Verifier;
import com.bpa4j.ui.HButton;
import com.bpa4j.ui.Message;
import com.toedter.calendar.JDateChooser;

public class FormModule implements EditorModule{
	public JPanel createTab(JDialog editor,Editable editable,boolean isNew,Runnable deleter){
		JPanel tab=new JPanel(null);
		tab.setBackground(Color.BLACK);
		JButton ok=new JButton(){
			public void paint(Graphics g){
				g.setClip(new RoundRectangle2D.Double(0,0,getWidth(),getHeight(),getHeight(),getHeight()));
				g.setColor(getModel().isPressed()?Color.DARK_GRAY:getBackground());
				g.fillRect(0,0,getWidth(),getHeight());
				g.setColor(Color.BLACK);
				FontMetrics fm=g.getFontMetrics();
				g.drawString(getText(),(getWidth()-fm.stringWidth(getText()))/2,(getHeight()+fm.getAscent()+fm.getLeading()-fm.getDescent())/2);
				if(getModel().isRollover()){
					g.setColor(new Color(255,255,255,200));
					((Graphics2D)g).setStroke(new BasicStroke(getHeight()/10));
					g.drawRoundRect(0,0,getWidth(),getHeight(),getHeight(),getHeight());
				}
			}
		};
		ok.setBounds(editor.getWidth()*2/5,editor.getHeight()*9/10,editor.getWidth()/5,editor.getHeight()/20);
		ok.setOpaque(false);
		ok.setFont(new Font(Font.DIALOG,Font.PLAIN,ok.getHeight()));
		JTextField nameField=new JTextField(editable.name);
		nameField.setBounds(editor.getWidth()/5,editor.getHeight()/100,editor.getWidth()*3/5,editor.getHeight()/10);
		nameField.setFont(new Font(Font.DIALOG,Font.PLAIN,nameField.getHeight()*2/3));
		nameField.setBackground(Color.DARK_GRAY);
		nameField.setForeground(Color.LIGHT_GRAY);
		Wrapper<Verifier>verifier=new Wrapper<Verifier>(null);
		Wrapper<NameProvider>nameProvider=new Wrapper<NameProvider>(null);
		Wrapper<InfoProvider>infoProvider=new Wrapper<InfoProvider>(null);
		Input v=((Input)editable.getClass().getAnnotation(Input.class));
		if(v!=null)try{
			if(v.verifier()!=Verifier.class)verifier.var=v.verifier().getDeclaredConstructor().newInstance();
			if(v.nameProvider()!=NameProvider.class)nameProvider.var=v.nameProvider().getDeclaredConstructor().newInstance();
			if(v.infoProvider()!=InfoProvider.class)infoProvider.var=v.infoProvider().getDeclaredConstructor().newInstance();
		}catch(ReflectiveOperationException ex){throw new IllegalStateException("Verifiers, name providers and info providers must have default constructors.",ex);}
		tab.add(nameField);
		tab.add(ok);
		ArrayList<Field>editableFields=new ArrayList<>();
		for(Field f:editable.getClass().getFields()){
			EditorEntry a=(EditorEntry)f.getAnnotation(EditorEntry.class);
			if(a==null)continue;
			for(String p:a.properties())if(p.equals("hide"))continue;
			editableFields.add(f);
		}
		CardLayout layout=new CardLayout();
		JPanel form=new JPanel(layout);
		Font font=new Font(Font.DIALOG,Font.PLAIN,editor.getHeight()*3/40);
		ArrayList<Supplier<?>>savers=new ArrayList<>();
		Wrapper<Supplier<?>>currentSaver=new Wrapper<Supplier<?>>(null);
		int k=0;
		Wrapper<EditableDemo>demo=new Wrapper<>(null);
		for(Field f:editableFields)try{
			EditorEntry a=(EditorEntry)f.getAnnotation(EditorEntry.class);
			JPanel entry=new JPanel(new GridLayout(1,2));
			JTextArea name=new JTextArea(a.translation());
			name.setEditable(false);
			name.setLineWrap(true);
			name.setWrapStyleWord(true);
			name.setFont(font);
			name.setBackground(Color.DARK_GRAY);
			name.setForeground(Color.WHITE);
			name.setBorder(BorderFactory.createTitledBorder(null,"Параметр",0,0,font.deriveFont(font.getSize2D()/2),Color.LIGHT_GRAY));
			entry.add(name);
			JComponent c;
			if(Supplier.class.isAssignableFrom(f.getType())){
				currentSaver.var=new com.bpa4j.defaults.input.EmptySaver();
				c=wrapEditorComponent(new JTextArea(){
					public String getText(){
						try{
							if(demo.var==null)return "Ошибка!";
							if(f.get(demo.var.get())==null)throw new NullPointerException("Supplier fields must be non-null.");
							return String.valueOf(((Supplier<?>)f.get(demo.var.get())).get());
						}catch(IllegalAccessException ex){throw new IllegalStateException("Supplier fields can only be non-static.",ex);}
					}
					public void paint(Graphics g){
						setText(getText());
						super.paint(g);
					}
				},font);
				((JTextArea)c).setEditable(false);
				((JTextArea)c).setLineWrap(false);
				((JTextArea)c).setWrapStyleWord(false);
			}else if(a.editorBaseSource()==EditorEntryBase.class)c=wrapEditorComponent(createEditorBase(editable,f,currentSaver),font);
			else c=a.editorBaseSource().getDeclaredConstructor().newInstance().createEditorBase(editable,f,currentSaver,demo);
			savers.add(currentSaver.var);
			if(c!=null){
				c.setBorder(BorderFactory.createTitledBorder(null,"Значение",0,0,font.deriveFont(font.getSize2D()/2),Color.LIGHT_GRAY));
				boolean flag=false;
				for(String s:a.properties())if(s.equals("readonly")||(!isNew&&s.equals("initonly"))){flag=true;break;}
				if(flag){
					c.setFocusable(false);
					entry.add(new JLayer<JComponent>(c,new LayerUI<>(){
						protected void processMouseEvent(MouseEvent e,JLayer<? extends JComponent>l){e.consume();}
						protected void processKeyEvent(KeyEvent e,JLayer<? extends JComponent>l){e.consume();}
					}));
				}else entry.add(c);
			}
			form.add(String.valueOf(k),entry);
			++k;
		}catch(ReflectiveOperationException ex){throw new RuntimeException(ex);}
		demo.var=new EditableDemo(editable.getClass(),savers);
		DefaultListModel<String>results=new DefaultListModel<>();
		JList<String>l=new JList<>(results);
		form.add(String.valueOf(k),l);
		ok.setText(savers.isEmpty()?"Готово":"Далее");
		ok.setBackground(savers.isEmpty()?Color.GREEN:Color.GRAY);
		JProgressBar p=new JProgressBar(0,savers.size());
		p.setBounds(editor.getWidth()/8,editor.getHeight()/6,editor.getWidth()*3/4,editor.getHeight()/20);
		p.setBackground(Color.WHITE);
		p.setForeground(Color.GREEN);
		tab.add(p);
		Wrapper<Integer>w=new Wrapper<Integer>(0);
		HButton cancel=null;
		if(deleter!=null){
			HButton delete=new HButton(10,5){
				public void paint(Graphics g){
					g.setColor(new Color(255-scale*8,20,20));
					g.fillRoundRect(0,0,getWidth(),getHeight(),getHeight()*2/3,getHeight()*2/3);
					((Graphics2D)g).setStroke(new BasicStroke(getHeight()/20));
					g.setColor(Color.DARK_GRAY);
					g.drawRoundRect(0,0,getWidth(),getHeight(),getHeight()*2/3,getHeight()*2/3);
					g.setColor(Color.BLACK);
					g.drawLine(getWidth()/3,getHeight()/3,getWidth()*2/3,getHeight()*2/3);
					g.drawLine(getWidth()*2/3,getHeight()/3,getWidth()/3,getHeight()*2/3);
					if(getModel().isPressed()){
						g.setColor(new Color(0,0,0,100));
						g.fillRoundRect(0,0,getWidth(),getHeight(),getHeight()*2/3,getHeight()*2/3);
					}
				}
			};
			delete.addActionListener(e->{deleter.run();editor.dispose();});
			delete.setBounds(editor.getWidth()*3/10,editor.getHeight()*9/10,editor.getHeight()/20,editor.getHeight()/20);
			delete.setOpaque(false);
			tab.add(delete);
		}
		if(!isNew){
			HButton c=new HButton(15,5){
				public void paint(Graphics g){
					g.setClip(new RoundRectangle2D.Double(0,0,getWidth(),getHeight(),getHeight(),getHeight()));
					g.setColor(new Color(71+scale*5,16+scale*2,1+scale));
					g.fillRect(0,0,getWidth(),getHeight());
					g.setColor(Color.WHITE);
					FontMetrics fm=g.getFontMetrics();
					g.drawString(getText(),(getWidth()-fm.stringWidth(getText()))/2,(getHeight()+fm.getAscent()+fm.getLeading()-fm.getDescent())/2);
				}
			};
			c.addActionListener(e->{
				if(w.var==0)editor.dispose();
				else{
					p.setValue(--w.var);
					layout.show(form,String.valueOf(w.var));
					if(w.var==0)c.setText("Отмена");
					ok.setText("Далее");
					ok.setBackground(Color.GRAY);
				}
			});
			c.setBounds(editor.getWidth()*2/5,editor.getHeight()*12/15,editor.getWidth()/5,editor.getHeight()/20);
			c.setText("Отмена");
			c.setOpaque(false);
			c.setFont(ok.getFont());
			tab.add(c);
			cancel=c;
		}
		final HButton fCancel=cancel;
		JComponent info=null;
		if(infoProvider.var!=null){
			info=infoProvider.var.provideInfo(demo.var);
			info.setBounds(editor.getWidth()*5/6,editor.getHeight()/4,editor.getWidth()/6,editor.getHeight()/2);
			tab.add(info);
		}
		final JComponent fInfo=info;
		ok.addActionListener(e->{
			try{
				if(savers.isEmpty()){
					editor.dispose();
					editable.name=nameField.getText();
					return;
				}
				if(w.var==savers.size()){
					if(verifier.var==null||verifier.var.verify(editable,demo.var.get(),isNew).isEmpty()){
						for(int i=0;i<editableFields.size();++i)if(!(savers.get(i)instanceof EmptySaver))editableFields.get(i).set(editable,savers.get(i).get());
						editor.dispose();
						editable.name=nameField.getText();
					}else{
						ok.setBackground(Color.RED);
						Timer t=new Timer(1000,evt->ok.setBackground(Color.GRAY));
						t.setRepeats(false);
						t.start();
						layout.show(form,String.valueOf(w.var=0));
						p.setValue(0);
						new Message(verifier.var.verify(editable,demo.var.get(),isNew),Color.RED);
					}
				}else{
					p.setValue(++w.var);
					layout.show(form,String.valueOf(w.var));
					if(fInfo!=null){
						if(fInfo instanceof JTable)((AbstractTableModel)((JTable)fInfo).getModel()).fireTableDataChanged();
						else if(fInfo instanceof JScrollPane){
							Component c=((JScrollPane)fInfo).getViewport().getView();
							if(c instanceof JTable)((AbstractTableModel)((JTable)c).getModel()).fireTableDataChanged();
						}
						fInfo.repaint();
						fInfo.revalidate();
					}
					if(w.var==savers.size()){
						ok.setText("Готово");
						ok.setBackground(Color.GREEN);
						if(nameProvider.var!=null)nameField.setText(nameProvider.var.provideName(demo.var.get()));
						results.removeAllElements();
						for(int i=0;i<editableFields.size();++i)if(!(savers.get(i)instanceof EmptySaver))results.addElement(editableFields.get(i).getAnnotation(EditorEntry.class).translation()+": "+savers.get(i).get());
					}
					if(fCancel!=null)fCancel.setText("Назад");
				}
			}catch(ReflectiveOperationException ex){throw new RuntimeException(ex);}
		});
		form.setBounds(infoProvider.var==null?editor.getWidth()/8:editor.getWidth()/20,editor.getHeight()/4,editor.getWidth()*3/4,editor.getHeight()/2);
		tab.add(form);
		nameField.requestFocusInWindow();
		nameField.setSelectionStart(0);
		nameField.setSelectionEnd(nameField.getText().length());
		return tab;
	}
	public static JComponent wrapEditorComponent(JComponent a,Font font){
		a.setFont(font);
		a.setBackground(Color.DARK_GRAY);
		a.setForeground(Color.WHITE);
		a.setOpaque(true);
		return a;
	}
	@SuppressWarnings("unchecked")
	public static JComponent createEditorBase(Object o,Field f,Wrapper<Supplier<?>>saver){
		try{
			if(f.getType()==String.class){
				JTextArea a=new JTextArea((String)f.get(o));
				a.setLineWrap(true);
				a.setWrapStyleWord(true);
				saver.var=()->a.getText();
				return a;
			}else if(f.getType()==int.class){
				JSpinner a=new JSpinner(new SpinnerNumberModel(f.getInt(o),0,10000000,1));
				saver.var=()->a.getValue();
				return a;
			}else if(f.getType()==double.class){
				JTextArea a=new JTextArea(f.get(o).toString());
				a.setLineWrap(true);
				a.setWrapStyleWord(true);
				saver.var=()->{
					try{return(double)Double.parseDouble(a.getText());}
					catch(Exception exc){
						a.setText("0");
						new Message("Неправильный формат числа, выбрано значение по умолчанию",Color.RED);
						return(double)0;
					}
				};
				return a;
			}else if(f.getType()==float.class){
				JTextArea a=new JTextArea(f.get(o).toString());
				a.setLineWrap(true);
				a.setWrapStyleWord(true);
				saver.var=()->{
					try{return(float)Float.parseFloat(a.getText());}
					catch(Exception exc){
						a.setText("0");
						new Message("Неправильный формат числа, выбрано значение по умолчанию",Color.RED);
						return(float)0;
					}
				};
				return a;
			}else if(f.getType()==boolean.class){
				JCheckBox a=new JCheckBox();
				a.setSelected(f.getBoolean(o));
				saver.var=()->a.isSelected();
				return a;
			}else if(f.getType()==LocalDate.class){
				JDateChooser a=new JDateChooser();
				a.setDateFormatString("yyyy-MM-dd");
				try{a.setDate(Date.from(((LocalDate)f.get(o)).atStartOfDay(ZoneId.systemDefault()).toInstant()));}catch(NullPointerException ex){throw new NullPointerException("LocalDate fields must be non-null");}
				saver.var=()->Instant.ofEpochMilli(a.getDate().getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
				return a;
			}else if(f.getType()==LocalDateTime.class){
				JDateChooser d=new JDateChooser();
				d.setDateFormatString("yyyy-MM-dd в HH:mm:ss");
				try{d.setDate(Date.from(((LocalDateTime)f.get(o)).atZone(ZoneId.systemDefault()).toInstant()));}catch(NullPointerException ex){throw new NullPointerException("LocalDateTime fields must be non-null");}
				saver.var=()->Instant.ofEpochMilli(d.getDate().getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
				return d;
			}else if(Editable.class.isAssignableFrom(f.getType())){
				try{
					JComboBox<Editable>a=new JComboBox<>();
					for(Editable e:Data.getInstance().getGroup((Class<? extends Editable>)f.getType()))a.addItem(e);
					a.setSelectedItem(f.get(o));
					saver.var=()->a.getSelectedItem();
					return a;
				}catch(IllegalArgumentException exception){
					Wrapper<Editable>value=new Wrapper<>((Editable)f.get(o));
					JButton a=new JButton(value.var==null?"":value.var.name);
					ActionListener edit=e->{
						ProgramStarter.editor.constructEditor((Editable)value.var,false);
					};
					a.addActionListener(value.var==null?new ActionListener(){
						public void actionPerformed(ActionEvent e){
							try{
								f.set(o,f.getType().getDeclaredConstructor().newInstance());
								value.var=(Editable)f.get(o);
								ProgramStarter.editor.constructEditor((Editable)value.var,true,()->{
									try{f.set(o,null);}catch(IllegalAccessException ex){throw new IllegalStateException(ex);}
								});
								a.removeActionListener(this);
								a.addActionListener(edit);
							}catch(ReflectiveOperationException ex){throw new IllegalStateException(ex);}
						}
					}:edit);
					saver.var=()->value.var;
					return a;
				}
			}else if(EditableGroup.class.isAssignableFrom(f.getType())){
				JPanel a=new JPanel(new GridLayout(0,1));
				EditableGroup<?>group=(EditableGroup<?>)f.get(o);
				for(Editable editable:group){
					JButton b=group.createElementButton(editable,null);
					b.addActionListener(e->{
						ProgramStarter.editor.constructEditor(editable,false,()->{
							group.remove(editable);
							a.remove(b);
							a.revalidate();
						});
					});
					a.add(b);
				}
				JButton create=group.createAddButton(null);
				create.addActionListener(e->{
					try{
						Editable nEditable=group.type.getDeclaredConstructor().newInstance();
						group.add(nEditable);
						JButton b=group.createElementButton(nEditable,null);
						Runnable deleter=()->{
							group.remove(nEditable);
							a.remove(b);
							a.revalidate();
						};
						b.addActionListener(evt->ProgramStarter.editor.constructEditor(nEditable,false,deleter));
						a.add(b,a.getComponentCount()-1);
						ProgramStarter.editor.constructEditor(nEditable,true,deleter);
						a.revalidate();
					}catch(ReflectiveOperationException ex){throw new IllegalStateException(ex);}
				});
				a.add(create);
				saver.var=()->group;
				return a;
			}else if(f.getType().isEnum()){
				JComboBox<Object>a=new JComboBox<>();
				Object en=f.get(o);
				if(en==null)throw new NullPointerException("Enum value of field \""+f.getName()+"\" is null.");
				for(Object obj:(Object[])en.getClass().getMethod("values").invoke(o))a.addItem(obj);
				a.setSelectedItem(f.get(o));
				saver.var=()->a.getSelectedItem();
				return a;
			}
		}catch(ReflectiveOperationException ex){throw new IllegalStateException(ex);}
		throw new UnsupportedOperationException("Component for "+f.getType()+" is not defined.");
	}
}