package com.futurefactory.editor;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import javax.swing.InputVerifier;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import com.futurefactory.Data;
import com.futurefactory.Data.Editable;
import com.futurefactory.ProgramStarter;

public class FormModule implements IEditorModule{
    public JPanel createTab(JDialog editor,Editable editable,JTextField nameField,JButton ok){
        JPanel tab=new JPanel(null);
		tab.setBackground(Color.BLACK);
		tab.add(nameField);
		tab.add(ok);
		JPanel form=new JPanel(new GridLayout(0,2));
		JScrollPane sForm=new JScrollPane(form);
		Font font=new Font(Font.DIALOG,Font.PLAIN,editor.getHeight()*3/40);
		for(Field f:editable.getClass().getFields())try{
			EditorEntry a=(EditorEntry)f.getAnnotation(EditorEntry.class);
			if(a==null)continue;
			JTextArea name=new JTextArea(a.translation());
			name.setEditable(false);
			name.setLineWrap(true);
			name.setWrapStyleWord(true);
			name.setFont(font);
			name.setBackground(Color.DARK_GRAY);
			name.setForeground(Color.WHITE);
			name.setBorder(null);
			form.add(name);
			if(a.editorBaseSource()==EditorEntryBase.class)
				form.add(wrapEditorComponent(createEditorBase(editable,f),font));
			else form.add(a.editorBaseSource().getDeclaredConstructor().newInstance().createEditorBase(editable,f));
		}catch(Exception ex){throw new RuntimeException(ex);}
		sForm.setBounds(editor.getWidth()/8,editor.getHeight()/8,editor.getWidth()*3/4,Math.min(form.getComponentCount()*editor.getHeight()*3/20,editor.getHeight()*3/4));
		sForm.getVerticalScrollBar().setUnitIncrement(sForm.getHeight()/10);
		form.setPreferredSize(new Dimension(sForm.getWidth(),Math.max(sForm.getHeight(),form.getComponentCount()*sForm.getHeight()/5)));
		tab.add(sForm);
        return tab;
    }
    public static Component wrapEditorComponent(Component a,Font font){
		a.setFont(font);
		a.setBackground(Color.DARK_GRAY);
		a.setForeground(Color.WHITE);
		return a;
	}
	@SuppressWarnings("unchecked")
	private static Component createEditorBase(Editable o,Field f){
		try{
			if(f.getType()==String.class){
				JTextArea a=new JTextArea((String)f.get(o));
				a.addFocusListener(new FocusListener(){
					public void focusGained(FocusEvent e){}
					public void focusLost(FocusEvent e){try{f.set(o,a.getText());}catch(IllegalAccessException ex){}}
				});
				return a;
			}else if(f.getType()==int.class){
				JSpinner a=new JSpinner(new SpinnerNumberModel(1,0,10000,1));
				a.addFocusListener(new FocusListener(){
					public void focusGained(FocusEvent e){}
					public void focusLost(FocusEvent e){try{f.set(o,a.getValue());}catch(IllegalAccessException ex){}}
				});
				return a;
			}else if(f.getType()==LocalDate.class){
				JTextField a=new JTextField();
				a.setInputVerifier(new InputVerifier(){
					public boolean verify(JComponent input){
						try{
							LocalDate.parse(((JTextField)input).getText());
							return true;
						}catch(DateTimeParseException ex){return false;}
					}
				});
				a.setText(((LocalDate)f.get(o)).toString());
				a.addFocusListener(new FocusListener(){
					public void focusGained(FocusEvent e){}
					public void focusLost(FocusEvent e){try{f.set(o,LocalDate.parse(a.getText()));}catch(IllegalAccessException|DateTimeParseException ex){}}
				});
				return a;
			}else if(Editable.class.isAssignableFrom(f.getType())){
				try{
					JComboBox<Editable>a=new JComboBox<>();
					for(Editable e:Data.getInstance().getGroup((Class<? extends Editable>)f.getType()))a.addItem(e);
					a.setSelectedItem(f.get(o));
					a.addFocusListener(new FocusListener(){
						public void focusGained(FocusEvent e){}
						public void focusLost(FocusEvent e){try{f.set(o,a.getSelectedItem());}catch(IllegalAccessException ex){}}
					});
					return a;
				}catch(IllegalArgumentException ex){
					JButton a=new JButton();
					a.addActionListener(e->{
						try{
							ProgramStarter.editor.constructEditor((Editable)f.get(o));
						}catch(IllegalAccessException exception){throw new RuntimeException(ex);}
					});
					return a;
				}
			}else if(f.getType().isEnum()){
				JComboBox<Object>a=new JComboBox<>();
				Object e=f.get(o);
				for(Object obj:(Object[])e.getClass().getMethod("values").invoke(o))a.addItem(obj);
				a.addFocusListener(new FocusListener(){
					public void focusGained(FocusEvent e){}
					public void focusLost(FocusEvent e){try{f.set(o,a.getSelectedItem());}catch(IllegalAccessException ex){}}
				});
				a.setSelectedItem(f.get(o));
				return a;
			}
		}catch(Exception ex){throw new RuntimeException(ex);}
		throw new UnsupportedOperationException("Component for "+f.getType()+" is not defined.");
	}
}
