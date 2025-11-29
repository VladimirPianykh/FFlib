package com.bpa4j.ui.rest.editor;

import java.lang.reflect.Field;
import java.util.function.Supplier;
import com.bpa4j.Wrapper;
import com.bpa4j.core.EditableDemo;
import com.bpa4j.editor.EditorEntryBase;
import com.bpa4j.editor.EditorEntryBaseRenderer;

/**
 * REST renderer for SelectionListEditor.
 * @author AI-generated
 */
public class RestSelectionListEditorRenderer implements EditorEntryBaseRenderer{
	public void renderEditorBase(Object o,Field f,Wrapper<Supplier<?>>saver,Wrapper<EditableDemo>demo,EditorEntryBase base,EditorEntryBase.EditorEntryRenderingContext context){
		// REST implementation would use CheckboxGroup component
		// For now, this is a placeholder
		saver.var=()->null;
	}
}
