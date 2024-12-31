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
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
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

import com.bpa4j.HButton;
import com.bpa4j.Message;
import com.bpa4j.Wrapper;
import com.bpa4j.core.Data;
import com.bpa4j.core.EditableDemo;
import com.bpa4j.core.ProgramStarter;
import com.bpa4j.core.Data.Editable;
import com.bpa4j.editor.EditorEntry;
import com.bpa4j.editor.EditorEntryBase;
import com.bpa4j.editor.InfoProvider;
import com.bpa4j.editor.Input;
import com.bpa4j.editor.NameProvider;
import com.bpa4j.editor.Verifier;
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
		}catch(Exception ex){throw new RuntimeException(ex);}
		tab.add(nameField);
		tab.add(ok);
		ArrayList<Field>editableFields=new ArrayList<>();
		r:for(Field f:editable.getClass().getFields()){
			EditorEntry a=(EditorEntry)f.getAnnotation(EditorEntry.class);
			if(a==null)continue;
			for(String p:a.properties())switch(p){
				case "hide"->{continue r;}
			};
			editableFields.add(f);
		}
		CardLayout layout=new CardLayout();
		JPanel form=new JPanel(layout);
		Font font=new Font(Font.DIALOG,Font.PLAIN,editor.getHeight()*3/40);
		ArrayList<Supplier<?>>savers=new ArrayList<>();
		Wrapper<Supplier<?>>currentSaver=new Wrapper<Supplier<?>>(null);
		int k=0;
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
			JComponent c=null;
			if(a.editorBaseSource()==EditorEntryBase.class)c=wrapEditorComponent(createEditorBase(editable,f,currentSaver),font);
			else c=a.editorBaseSource().getDeclaredConstructor().newInstance().createEditorBase(editable,f,currentSaver);
			savers.add(currentSaver.var);
			if(c!=null){
				c.setBorder(BorderFactory.createTitledBorder(null,"Значение",0,0,font.deriveFont(font.getSize2D()/2),Color.LIGHT_GRAY));
				boolean flag=false;
				for(String s:a.properties())if(s.equals("readonly")){flag=true;break;}
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
		EditableDemo demo=new EditableDemo(editable.getClass(),savers);
		DefaultListModel<String>results=new DefaultListModel<>();
		JList<String>l=new JList<>(results);
		form.add(String.valueOf(k),l);
		ok.setText(savers.size()==0?"Готово":"Далее");
		ok.setBackground(savers.size()==0?Color.GREEN:Color.GRAY);
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
			info=infoProvider.var.provideInfo(demo);
			info.setBounds(editor.getWidth()*5/6,editor.getHeight()/4,editor.getWidth()/6,editor.getHeight()/2);
			tab.add(info);
		}
		final JComponent fInfo=info;
		ok.addActionListener(e->{
			try{
				if(savers.size()==0){
					editor.dispose();
					editable.name=nameField.getText();
					return;
				}
				if(w.var==savers.size()){
					if(verifier.var==null||verifier.var.verify(editable,demo.get(),isNew).isEmpty()){
						for(int i=0;i<editableFields.size();++i)editableFields.get(i).set(editable,savers.get(i).get());
						editor.dispose();
						editable.name=nameField.getText();
					}else{
						ok.setBackground(Color.RED);
						Timer t=new Timer(1000,evt->ok.setBackground(Color.GRAY));
						t.setRepeats(false);
						t.start();
						layout.show(form,String.valueOf(w.var=0));
						p.setValue(0);
						new Message(verifier.var.verify(editable,demo.get(),isNew),Color.RED);
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
						if(nameProvider.var!=null)nameField.setText(nameProvider.var.provideName(demo.get()));
						results.removeAllElements();
						for(int i=0;i<editableFields.size();++i)results.addElement(editableFields.get(i).getAnnotation(EditorEntry.class).translation()+": "+String.valueOf(savers.get(i).get()));
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
				JSpinner a=new JSpinner(new SpinnerNumberModel((int)f.get(o),0,10000000,1));
				saver.var=()->a.getValue();
				return a;
			}else if(f.getType()==double.class){
				JTextArea a=new JTextArea(f.get(o).toString());
				a.setLineWrap(true);
				a.setWrapStyleWord(true);
				saver.var=()->{
					try {return (double) Double.parseDouble(a.getText());}
					catch (Exception exc) {
						a.setText("0");
						new Message("Неправильный формат числа, выбрано значение по умолчанию", Color.RED);
						return (double) 0;
					}
				};
				return a;
			}else if(f.getType()==float.class){
				JTextArea a=new JTextArea(f.get(o).toString());
				a.setLineWrap(true);
				a.setWrapStyleWord(true);
				saver.var=()->{
					try {return (float) Float.parseFloat(a.getText());}
					catch (Exception exc) {
						a.setText("0");
						new Message("Неправильный формат числа, выбрано значение по умолчанию", Color.RED);
						return (float) 0;
					}
				};
				return a;
			}else if(f.getType()==LocalDate.class){
				JDateChooser d=new JDateChooser();
				d.setDateFormatString("yyyy-MM-dd");
				try{
					d.setDate(Date.from(((LocalDate)f.get(o)).atStartOfDay(ZoneId.systemDefault()).toInstant()));
				}catch(NullPointerException ex){throw new NullPointerException("LocalDate fields must be non-null");}
				saver.var=()->Instant.ofEpochMilli(d.getDate().getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
				return d;
			}else if(f.getType()==LocalDateTime.class){
				JDateChooser d=new JDateChooser();
				d.setDateFormatString("yyyy-MM-dd в HH:mm:ss");
				try{
					d.setDate(Date.from(((LocalDateTime)f.get(o)).atZone(ZoneId.systemDefault()).toInstant()));
				}catch(NullPointerException ex){throw new NullPointerException("LocalDateTime fields must be non-null");}
				saver.var=()->Instant.ofEpochMilli(d.getDate().getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
				return d;
			}else if(f.getType()==Supplier.class){
				saver.var=()->{try{return f.get(o);}catch(IllegalAccessException ex){throw new RuntimeException(ex);}};
				JLabel l=new JLabel(){
					public String getText(){try{return String.valueOf(((Supplier<?>)f.get(o)).get());}catch(IllegalAccessException ex){throw new RuntimeException(ex);}}
				};
				l.setOpaque(true);
				return l;
			}else if(Editable.class.isAssignableFrom(f.getType())){
				try{
					JComboBox<Editable>a=new JComboBox<>();
					for(Editable e:Data.getInstance().getGroup((Class<? extends Editable>)f.getType()))a.addItem(e);
					a.setSelectedItem(f.get(o));
					saver.var=()->a.getSelectedItem();
					return a;
				}catch(IllegalArgumentException ex){
					JButton a=new JButton(f.get(o)==null?"":((Editable)f.get(o)).name);
					if(f.get(o)!=null)a.addActionListener(e->{
						try{
							ProgramStarter.editor.constructEditor((Editable)f.get(o),false);
						}catch(IllegalAccessException ex2){throw new RuntimeException(ex2);}
					});
					saver.var=()->{try{return f.get(o);}catch(IllegalAccessException ex2){throw new RuntimeException(ex2);}};
					return a;
				}
			}else if(f.getType().isEnum()){
				JComboBox<Object>a=new JComboBox<>();
				Object en=f.get(o);
				if(en==null)throw new NullPointerException("Enum value of field \""+f.getName()+"\" is null.");
				for(Object obj:(Object[])en.getClass().getMethod("values").invoke(o))a.addItem(obj);
				a.setSelectedItem(f.get(o));
				saver.var=()->a.getSelectedItem();
				return a;
			}
		}catch(ReflectiveOperationException ex){throw new RuntimeException(ex);}
		throw new UnsupportedOperationException("Component for "+f.getType()+" is not defined.");
	}
}
