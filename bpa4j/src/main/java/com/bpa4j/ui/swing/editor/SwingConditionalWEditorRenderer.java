package com.bpa4j.ui.swing.editor;

import java.lang.reflect.Field;
import java.util.function.Supplier;
import com.bpa4j.Wrapper;
import com.bpa4j.core.EditableDemo;
import com.bpa4j.defaults.input.ConditionalWEditor;
import com.bpa4j.defaults.input.EmptySaver;
import com.bpa4j.editor.EditorEntryBase;
import com.bpa4j.editor.EditorEntryBaseRenderer;
import com.bpa4j.ui.swing.editor.modules.SwingFormModuleRenderer;

/**
 * Swing renderer for ConditionalWEditor.
 * @author AI-generated
 */
public class SwingConditionalWEditorRenderer implements EditorEntryBaseRenderer{
	public void renderEditorBase(Object o,Field f,Wrapper<Supplier<?>>saver,Wrapper<EditableDemo>demo,EditorEntryBase base,EditorEntryBase.EditorEntryRenderingContext context){
		// Not used for Swing, see createEditorComponent
	}
	
	@Override
	public Object createEditorComponent(Object o,Field f,Wrapper<Supplier<?>>saver,Wrapper<EditableDemo>demo,EditorEntryBase base,EditorEntryBase.EditorEntryRenderingContext context){
		try{
			EditorEntryBase editor=ConditionalWEditor.getTypes().get(f)==null?null:ConditionalWEditor.getTypes().get(f).getDeclaredConstructor().newInstance();
			if(ConditionalWEditor.getConditions().get(f).test(o)){
				if(editor==null){
					return SwingFormModuleRenderer.createEditorBase(o,f,saver);
				}else{
					return context.getRenderer(editor).createEditorComponent(o,f,saver,demo,editor,context);
				}
			}else{
				saver.var=new EmptySaver();
				return null;
			}
		}catch(ReflectiveOperationException ex){throw new IllegalStateException(ex);}
	}
}
