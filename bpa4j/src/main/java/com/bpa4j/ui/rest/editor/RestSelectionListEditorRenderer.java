package com.bpa4j.ui.rest.editor;

import java.lang.reflect.Field;
import java.util.function.Supplier;
import com.bpa4j.Wrapper;
import com.bpa4j.core.Editable;
import com.bpa4j.core.EditableDemo;
import com.bpa4j.core.EditableGroup;
import com.bpa4j.core.ProgramStarter;
import com.bpa4j.defaults.input.Selectable;
import com.bpa4j.editor.EditorEntryBase;
import com.bpa4j.editor.EditorEntryBaseRenderer;
import com.bpa4j.ui.rest.abstractui.Panel;
import com.bpa4j.ui.rest.abstractui.components.CheckBox;
import com.bpa4j.ui.rest.abstractui.layout.GridLayout;
import com.bpa4j.ui.rest.editor.modules.RestFormModuleRenderer;

/**
 * REST renderer for SelectionListEditor.
 * @author AI-generated
 */
public class RestSelectionListEditorRenderer implements EditorEntryBaseRenderer{
	@Override
	public void renderEditorBase(Object o,Field f,Wrapper<Supplier<?>>saver,Wrapper<EditableDemo>demo,EditorEntryBase base,EditorEntryBase.EditorEntryRenderingContext context){
		if(!(context instanceof RestFormModuleRenderer.RestEditorEntryRenderingContext)) return;
		RestFormModuleRenderer.RestEditorEntryRenderingContext ctx=(RestFormModuleRenderer.RestEditorEntryRenderingContext)context;

		if(f.getType()!=Selectable.class) throw new IllegalArgumentException("The field must have type Selectable<?>, but the actual type is "+f.getType()+".");

		try{
			Selectable<?> s=(Selectable<?>)((Selectable<?>)f.get(o)).clone();
			EditableGroup<?> group=(EditableGroup<?>)ProgramStarter.getStorageManager().getStorage().getGroup(s.type);

			Panel panel=new Panel();
			panel.setLayout(new GridLayout(0,1,0,0));

			for(Editable e:group){
				CheckBox cb=new CheckBox(e.name);
				cb.setSelected(s.contains(e));
				cb.setOnChange(box->{
					if(box.isSelected()) s.add(e);
					else s.remove(e);
				});
				panel.add(cb);
			}

			saver.var=()->s;
			ctx.addComponent(panel);
		}catch(ReflectiveOperationException ex){
			throw new IllegalStateException(ex);
		}
	}
}
