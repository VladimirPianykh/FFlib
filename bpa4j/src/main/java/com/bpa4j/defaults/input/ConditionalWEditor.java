package com.bpa4j.defaults.input;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.function.Predicate;
import com.bpa4j.editor.EditorEntryBase;

public class ConditionalWEditor implements EditorEntryBase{
	private static HashMap<Field,Class<? extends EditorEntryBase>>types=new HashMap<>();
	private static HashMap<Field,Predicate<Object>>conditions=new HashMap<>();
	public static void configure(Field f,Class<? extends EditorEntryBase>editor,Predicate<Object>condition){
		types.put(f,editor);
		conditions.put(f,condition);
	}
	public static HashMap<Field,Class<? extends EditorEntryBase>> getTypes(){
		return types;
	}
	public static HashMap<Field,Predicate<Object>> getConditions(){
		return conditions;
	}

}

