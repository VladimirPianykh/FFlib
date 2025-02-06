package com.bpa4j.defaults.input;

import java.lang.reflect.Field;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.swing.JComponent;
import javax.swing.JLabel;

import com.bpa4j.Wrapper;
import com.bpa4j.core.EditableDemo;
import com.bpa4j.editor.EditorEntryBase;
import com.bpa4j.editor.modules.FormModule;

public class FunctionEditor implements EditorEntryBase{
	@SuppressWarnings({"rawtypes","unchecked"})
	public JComponent createEditorBase(Object o,Field f,Wrapper<Supplier<?>>saver,Wrapper<EditableDemo>demo){
		saver.var=new EmptySaver();
		return FormModule.wrapEditorComponent(new JLabel(){
			public String getText(){
				try{
					if(demo.var==null)return "Ошибка!";
					return String.valueOf(((Function)f.get(o)).apply(demo.var.get()));
				}catch(IllegalAccessException ex){throw new IllegalStateException(ex);}
			}
		},null);
	}
}
