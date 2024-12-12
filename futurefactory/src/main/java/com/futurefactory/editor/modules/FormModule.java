package com.futurefactory.editor.modules;

import java.awt.BasicStroke;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.geom.RoundRectangle2D;
import java.lang.reflect.Field;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.Timer;

import com.futurefactory.Data;
import com.futurefactory.Data.Editable;
import com.futurefactory.Message;
import com.futurefactory.ProgramStarter;
import com.futurefactory.Wrapper;
import com.futurefactory.editor.EditorEntry;
import com.futurefactory.editor.EditorEntryBase;
import com.futurefactory.editor.VerifiedInput;
import com.futurefactory.editor.Verifier;
import com.toedter.calendar.JDateChooser;

public class FormModule implements IEditorModule{
	public JPanel createTab(JDialog editor,Editable editable,boolean isNew){
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
		VerifiedInput v=((VerifiedInput)editable.getClass().getAnnotation(VerifiedInput.class));
		if(v!=null)try{verifier.var=v.verifier().getDeclaredConstructor().newInstance();}catch(Exception ex){throw new RuntimeException(ex);}
		tab.add(nameField);
		tab.add(ok);
		CardLayout layout=new CardLayout();
		JPanel form=new JPanel(layout);
		Font font=new Font(Font.DIALOG,Font.PLAIN,editor.getHeight()*3/40);
		ArrayList<Runnable>savers=new ArrayList<>();
		Wrapper<Runnable>currentSaver=new Wrapper<Runnable>(null);
		int k=0;
		for(Field f:editable.getClass().getFields())try{
			EditorEntry a=(EditorEntry)f.getAnnotation(EditorEntry.class);
			if(a==null)continue;
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
				entry.add(c);
			}
			form.add(String.valueOf(k),entry);
			++k;
		}catch(ReflectiveOperationException ex){throw new RuntimeException(ex);}
		ok.setText(savers.size()<=1?"Готово":"Далее");
		ok.setBackground(savers.size()<=1?Color.GREEN:Color.GRAY);
		JProgressBar p=new JProgressBar(0,savers.size());
		p.setBounds(editor.getWidth()/8,editor.getHeight()/6,editor.getWidth()*3/4,editor.getHeight()/20);
		p.setBackground(Color.WHITE);
		p.setForeground(Color.GREEN);
		tab.add(p);
		Wrapper<Integer>w=new Wrapper<Integer>(0);
		ok.addActionListener(e->{
			if(savers.size()==0){
				editor.dispose();
				editable.name=nameField.getText();
				return;
			}
			if(w.var==savers.size()-1){
				try{savers.get(w.var).run();}catch(RuntimeException ex){
					ok.setBackground(Color.RED);
					Timer t=new Timer(1000,evt->ok.setBackground(Color.GRAY));
					t.setRepeats(false);
					t.start();
					return;
				}
				if(verifier.var==null||verifier.var.verify(editable,isNew).isEmpty()){
					editor.dispose();
					editable.name=nameField.getText();
				}else{
					ok.setBackground(Color.RED);
					Timer t=new Timer(1000,evt->ok.setBackground(Color.GRAY));
					t.setRepeats(false);
					t.start();
					layout.show(form,String.valueOf(w.var=0));
					p.setValue(0);
					new Message(verifier.var.verify(editable,isNew),Color.RED);
				}
			}else{
				try{savers.get(w.var).run();}catch(RuntimeException ex){
					ok.setBackground(Color.RED);
					Timer t=new Timer(1000,evt->ok.setBackground(Color.GRAY));
					t.setRepeats(false);
					t.start();
					return;
				}
				p.setValue(++w.var);
				layout.show(form,String.valueOf(w.var));
				if(w.var==savers.size()-1){
					ok.setText("Готово");
					ok.setBackground(Color.GREEN);
				}
			}
		});
		form.setBounds(editor.getWidth()/8,editor.getHeight()/4,editor.getWidth()*3/4,editor.getHeight()/2);
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
	public static JComponent createEditorBase(Object o,Field f,Wrapper<Runnable>saver){
		try{
			if(f.getType()==String.class){
				JTextArea a=new JTextArea((String)f.get(o));
				a.setLineWrap(true);
				a.setWrapStyleWord(true);
				saver.var=()->{try{f.set(o,a.getText());}catch(IllegalAccessException ex){throw new RuntimeException(ex);}};
				return a;
			}else if(f.getType()==int.class){
				JSpinner a=new JSpinner(new SpinnerNumberModel((int)f.get(o),0,10000,1));
				saver.var=()->{try{f.set(o,a.getValue());}catch(IllegalAccessException ex){throw new RuntimeException(ex);}};
				return a;
			}else if(f.getType()==LocalDate.class){
				JDateChooser d=new JDateChooser();
				d.setDateFormatString("yyyy-MM-dd");
				d.setDate(Date.from(((LocalDate)f.get(o)).atStartOfDay(ZoneId.systemDefault()).toInstant()));
				saver.var = () -> {
					try {
						f.set(o, Instant.ofEpochMilli(d.getDate().getTime()).atZone(ZoneId.systemDefault()).toLocalDate());
					} catch (IllegalAccessException | DateTimeParseException ex) {throw new RuntimeException(ex);}
				};
				return d;
			}else if(Editable.class.isAssignableFrom(f.getType())){
				try{
					JComboBox<Editable>a=new JComboBox<>();
					for(Editable e:Data.getInstance().getGroup((Class<? extends Editable>)f.getType()))a.addItem(e);
					a.setSelectedItem(f.get(o));
					saver.var=()->{try{f.set(o,a.getSelectedItem());}catch(IllegalAccessException ex){throw new RuntimeException(ex);}};
					return a;
				}catch(IllegalArgumentException ex){
					JButton a=new JButton("Открыть");
					a.addActionListener(e->{try{ProgramStarter.editor.constructEditor((Editable)f.get(o),false);}catch(IllegalAccessException exception){throw new RuntimeException(ex);}});
					saver.var=()->{};
					return a;
				}
			}else if(f.getType().isEnum()){
				JComboBox<Object>a=new JComboBox<>();
				Object en=f.get(o);
				if(en==null)throw new NullPointerException("Enum value of field \""+f.getName()+"\" is null.");
			for(Object obj:(Object[])en.getClass().getMethod("values").invoke(o))a.addItem(obj);
				a.setSelectedItem(f.get(o));
				saver.var=()->{try{f.set(o,a.getSelectedItem());}catch(IllegalAccessException ex){}};
				return a;
			}
		}catch(ReflectiveOperationException ex){throw new RuntimeException(ex);}
		throw new UnsupportedOperationException("Component for "+f.getType()+" is not defined.");
	}
}
