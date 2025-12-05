package com.bpa4j.ui.rest.editor;

import java.lang.reflect.Field;
import java.util.function.Function;
import java.util.function.Supplier;
import com.bpa4j.Wrapper;
import com.bpa4j.core.EditableDemo;
import com.bpa4j.defaults.input.EmptySaver;
import com.bpa4j.editor.EditorEntryBase;
import com.bpa4j.editor.EditorEntryBaseRenderer;
import com.bpa4j.ui.rest.abstractui.components.Label;
import com.bpa4j.ui.rest.editor.modules.RestFormModuleRenderer;

/**
 * REST renderer for FunctionEditor.
 * @author AI-generated
 */
public class RestFunctionEditorRenderer implements EditorEntryBaseRenderer{
	@SuppressWarnings({"rawtypes","unchecked"})
	public void renderEditorBase(Object o,Field f,Wrapper<Supplier<?>>saver,Wrapper<EditableDemo>demo,EditorEntryBase base,EditorEntryBase.EditorEntryRenderingContext context){
		saver.var=new EmptySaver();
		try{
			String text;
			if(demo.var==null){
				text="Ошибка!";
			}else{
				text=f.get(o)instanceof Function
					?String.valueOf(((Function)f.get(o)).apply(demo.var.get()))
					:String.valueOf(((Supplier)f.get(o)).get());
			}
			Label label=new Label(text);
			// Add the label to the context
			if(context instanceof RestFormModuleRenderer.RestEditorEntryRenderingContext){
				((RestFormModuleRenderer.RestEditorEntryRenderingContext)context).addComponent(label);
			}
		}catch(IllegalAccessException ex){throw new IllegalStateException(ex);}
	}
}
