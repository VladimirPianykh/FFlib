package com.bpa4j.ui.rest.editor;

import java.lang.reflect.Field;
import java.util.function.Supplier;
import com.bpa4j.Wrapper;
import com.bpa4j.core.EditableDemo;
import com.bpa4j.defaults.input.ConditionalWEditor;
import com.bpa4j.defaults.input.EmptySaver;
import com.bpa4j.editor.EditorEntryBase;
import com.bpa4j.editor.EditorEntryBaseRenderer;
import com.bpa4j.ui.rest.abstractui.Component;
import com.bpa4j.ui.rest.editor.modules.RestFormModuleRenderer;

/**
 * REST renderer for ConditionalWEditor.
 * @author AI-generated
 */
public class RestConditionalWEditorRenderer implements EditorEntryBaseRenderer{
	public void renderEditorBase(Object o,Field f,Wrapper<Supplier<?>>saver,Wrapper<EditableDemo>demo,EditorEntryBase base,EditorEntryBase.EditorEntryRenderingContext context){
		try{
			EditorEntryBase editor=ConditionalWEditor.getTypes().get(f)==null?null:ConditionalWEditor.getTypes().get(f).getDeclaredConstructor().newInstance();
			if(ConditionalWEditor.getConditions().get(f).test(o)){
				if(editor==null){
					// Use default REST rendering
					Component c=RestFormModuleRenderer.createEditorComponent(o,f,saver);
					if(context instanceof RestFormModuleRenderer.RestEditorEntryRenderingContext){
						((RestFormModuleRenderer.RestEditorEntryRenderingContext)context).addComponent(c);
					}
				}else{
					editor.renderEditorBase(o,f,saver,demo,context);
				}
			}else{
				saver.var=new EmptySaver();
			}
		}catch(ReflectiveOperationException ex){throw new IllegalStateException(ex);}
	}
}
