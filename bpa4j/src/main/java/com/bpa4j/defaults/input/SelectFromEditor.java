package com.bpa4j.defaults.input;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Function;
import com.bpa4j.core.Editable;
import com.bpa4j.editor.EditorEntryBase;

/**
 * An editor that suggest the user selection from an {@code ArrayList}, provided by the element supplier.
 * If the field is a {@code Collection}, the choice is multiple, otherwise it is singular.
 * @author AI-generated
 */
public class SelectFromEditor implements EditorEntryBase{
	private static HashMap<Field,Function<Editable,ArrayList<?>>>elementSuppliers=new HashMap<>();
	/**
	 * Configures the editor for the specified field.
	 * @param f - field to configure for
	 * @param elementSupplier - a Function (accepting the editable object) to get elements from
	 */
	public static void configure(Field f,Function<Editable,ArrayList<?>> elementSupplier){
		elementSuppliers.put(f,elementSupplier);
	}
	public static HashMap<Field,Function<Editable,ArrayList<?>>> getElementSuppliers(){
		return elementSuppliers;
	}
}
