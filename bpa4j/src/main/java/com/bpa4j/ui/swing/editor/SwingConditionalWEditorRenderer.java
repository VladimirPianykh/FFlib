package com.bpa4j.ui.swing.editor;

import java.lang.reflect.Field;
import java.util.function.Supplier;
import javax.swing.JComponent;
import com.bpa4j.Wrapper;
import com.bpa4j.core.EditableDemo;
import com.bpa4j.defaults.input.ConditionalWEditor;
import com.bpa4j.defaults.input.EmptySaver;
import com.bpa4j.editor.EditorEntryBase;
import com.bpa4j.editor.EditorEntryBaseRenderer;
import com.bpa4j.ui.swing.editor.modules.SwingFormModuleRenderer;
import com.bpa4j.ui.swing.editor.modules.SwingFormModuleRenderer.SwingEditorEntryRenderingContext;

/**
 * Swing renderer for ConditionalWEditor.
 * @author AI-generated
 */
public class SwingConditionalWEditorRenderer implements EditorEntryBaseRenderer{
	@Override
	public void renderEditorBase(Object o,Field f,Wrapper<Supplier<?>> saver,Wrapper<EditableDemo> demo,EditorEntryBase base,EditorEntryBase.EditorEntryRenderingContext context){
		try{
			EditorEntryBase editor=ConditionalWEditor.getTypes().get(f)==null?null:ConditionalWEditor.getTypes().get(f).getDeclaredConstructor().newInstance();
			if(ConditionalWEditor.getConditions().get(f).test(o)){
				if(editor==null){
					JComponent component=SwingFormModuleRenderer.createEditorBase(o,f,saver);
					((SwingEditorEntryRenderingContext)context).addComponent(component);
				}else{
					context.getRenderer(editor).renderEditorBase(o,f,saver,demo,editor,context);
				}
			}else{
				saver.var=new EmptySaver();
				// No component added when condition is false
			}
		}catch(ReflectiveOperationException ex){throw new IllegalStateException(ex);}
	}
}
