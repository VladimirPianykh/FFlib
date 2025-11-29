package com.bpa4j.editor;

import java.lang.reflect.Field;
import java.util.function.Supplier;
import com.bpa4j.Wrapper;
import com.bpa4j.core.EditableDemo;
import com.bpa4j.core.RenderingContext;

/**
 * Also known as "field editor" or "entry editor".
 */
public interface EditorEntryBase{
	public static interface EditorEntryRenderingContext extends RenderingContext{
		EditorEntryBaseRenderer getRenderer(EditorEntryBase base);
	}
	/**
	 * @param o - owner of the field
	 * @param f - field to create editor for
	 * @param saver - {@code Wrapper} which {@code var} field must be set to supplier that returns the updated field value
	 * @param demo - {@code EditableDemo} link that <b>cannot be used during creation</b>
	 * @param context - {@code EditorEntryRenderingContext} </b>
	 * @returns object representing entry editor ({@code JComponent} for Swing)
	 */
	default void renderEditorBase(Object o,Field f,Wrapper<Supplier<?>>saver,Wrapper<EditableDemo>demo,EditorEntryRenderingContext context){
		context.getRenderer(this).renderEditorBase(o,f,saver,demo,this,context);
	}
}
