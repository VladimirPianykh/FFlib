package com.bpa4j.ui.swing.editor;

import java.lang.reflect.Field;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.swing.JLabel;
import com.bpa4j.Wrapper;
import com.bpa4j.core.EditableDemo;
import com.bpa4j.editor.EditorEntryBase;
import com.bpa4j.editor.EditorEntryBaseRenderer;
import com.bpa4j.ui.swing.editor.modules.SwingFormModuleRenderer;
import com.bpa4j.ui.swing.editor.modules.SwingFormModuleRenderer.SwingEditorEntryRenderingContext;

/**
 * Swing renderer for FunctionEditor.
 * @author AI-generated
 */
public class SwingFunctionEditorRenderer implements EditorEntryBaseRenderer{
	@Override
	@SuppressWarnings({"rawtypes","unchecked"})
	public void renderEditorBase(Object o,Field f,Wrapper<Supplier<?>> saver,Wrapper<EditableDemo> demo,EditorEntryBase base,EditorEntryBase.EditorEntryRenderingContext context){
		saver.var=new com.bpa4j.defaults.input.EmptySaver();
		JLabel label=new JLabel(){
			public String getText(){
				try{
					if(demo.var==null)return "Ошибка!";
					return f.get(o)instanceof Function
						?String.valueOf(((Function)f.get(o)).apply(demo.var.get()))
						:String.valueOf(((Supplier)f.get(o)).get());
				}catch(IllegalAccessException ex){throw new IllegalStateException(ex);}
			}
		};
		((SwingEditorEntryRenderingContext)context).addComponent(SwingFormModuleRenderer.wrapEditorComponent(label,null));
	}
}
