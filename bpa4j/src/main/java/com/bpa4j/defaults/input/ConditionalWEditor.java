package com.bpa4j.defaults.input;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.function.Predicate;
import java.util.function.Supplier;

import javax.swing.JComponent;

import com.bpa4j.Wrapper;
import com.bpa4j.core.EditableDemo;
import com.bpa4j.editor.EditorEntryBase;
import com.bpa4j.editor.modules.FormModule;

public class ConditionalWEditor implements EditorEntryBase{
	private static HashMap<Field,Class<? extends EditorEntryBase>>types=new HashMap<>();
	private static HashMap<Field,Predicate<Object>>conditions=new HashMap<>();
	public static void configure(Field f,Class<? extends EditorEntryBase>editor,Predicate<Object>condition){
		types.put(f,editor);
		conditions.put(f,condition);
	}
	public JComponent createEditorBase(Object o,Field f,Wrapper<Supplier<?>>saver,Wrapper<EditableDemo>demo){
		try{
			EditorEntryBase editor=types.get(f)==null?null:types.get(f).getDeclaredConstructor().newInstance();
			if(conditions.get(f).test(o))return editor==null?FormModule.createEditorBase(o,f,saver):editor.createEditorBase(o,f,saver,demo);
			else{
				saver.var=new EmptySaver();
				return null;
			}
		}catch(ReflectiveOperationException ex){throw new IllegalStateException(ex);}
	}
}
