package com.bpa4j.defaults.input;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.function.Supplier;
import com.bpa4j.editor.EditorEntryBase;


/**
 * An editor wrapper that displays a flag (checkbox), indicating whether this field actually exists.
 */
public class FlagWEditor implements EditorEntryBase{
	private static final HashMap<Field,Class<? extends EditorEntryBase>>types=new HashMap<>();
	private static final HashMap<Field,Object>defaults=new HashMap<>();
	private static final HashMap<Field,Supplier<?>>initials=new HashMap<>();
	/**
	 * Configures the editor for the specified field.
	 * @param f - field to configure for
	 * @param editor - an editor to wrap
	 * @param defaultValue - a value that used if flag is not set
	 * @param initValue - a value that is set when a flag have just been selected
	 */
	public static void configure(Field f,Class<? extends EditorEntryBase>editor,Object defaultValue,Supplier<?>initValue){
		types.put(f,editor);
		defaults.put(f,defaultValue);
		initials.put(f,initValue);
	}
	public static HashMap<Field,Class<? extends EditorEntryBase>> getTypes(){
		return types;
	}
	public static HashMap<Field,Object> getDefaults(){
		return defaults;
	}
	public static HashMap<Field,Supplier<?>> getInitials(){
		return initials;
	}

}

