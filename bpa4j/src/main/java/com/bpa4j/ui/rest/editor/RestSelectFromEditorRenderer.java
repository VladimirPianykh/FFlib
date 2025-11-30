package com.bpa4j.ui.rest.editor;

import java.lang.reflect.Field;
import java.util.function.Supplier;
import com.bpa4j.Wrapper;
import com.bpa4j.core.EditableDemo;
import com.bpa4j.editor.EditorEntryBase;
import com.bpa4j.editor.EditorEntryBaseRenderer;
import com.bpa4j.ui.rest.abstractui.components.Label;
import com.bpa4j.ui.rest.editor.modules.RestFormModuleRenderer;

/**
 * REST renderer for SelectFromEditor.
 * @author AI-generated
 */
public class RestSelectFromEditorRenderer implements EditorEntryBaseRenderer{
	public void renderEditorBase(Object o,Field f,Wrapper<Supplier<?>>saver,Wrapper<EditableDemo>demo,EditorEntryBase base,EditorEntryBase.EditorEntryRenderingContext context){
		// REST implementation would use ComboBox or Spinner components
		// depending on whether the field is a Collection or single value
		// For now, this is a placeholder
		//TODO: implement
		saver.var=()->null;
		Label placeholder=new Label("SelectFromEditor - Not yet implemented");
		if(context instanceof RestFormModuleRenderer.RestEditorEntryRenderingContext){
			((RestFormModuleRenderer.RestEditorEntryRenderingContext)context).addComponent(placeholder);
		}
	}
}
