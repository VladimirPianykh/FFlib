package com.bpa4j.editor;

import java.lang.reflect.Field;
import java.util.function.Supplier;
import com.bpa4j.Wrapper;
import com.bpa4j.core.EditableDemo;

public interface EditorEntryBaseRenderer {
	void renderEditorBase(Object o,Field f,Wrapper<Supplier<?>>saver,Wrapper<EditableDemo>demo,EditorEntryBase base,EditorEntryBase.EditorEntryRenderingContext context);
	/**
	 * Creates an editor component for Swing UI.
	 * @return the editor component, or null if not applicable
	 */
	default Object createEditorComponent(Object o,Field f,Wrapper<Supplier<?>>saver,Wrapper<EditableDemo>demo,EditorEntryBase base,EditorEntryBase.EditorEntryRenderingContext context){
		renderEditorBase(o,f,saver,demo,base,context);
		return null;
	}
}
