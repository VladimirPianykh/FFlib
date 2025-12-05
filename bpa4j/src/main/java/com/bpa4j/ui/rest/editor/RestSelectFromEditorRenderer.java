package com.bpa4j.ui.rest.editor;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Supplier;
import com.bpa4j.Wrapper;
import com.bpa4j.core.EditableDemo;
import com.bpa4j.defaults.input.SelectFromEditor;
import com.bpa4j.editor.EditorEntryBase;
import com.bpa4j.editor.EditorEntryBaseRenderer;
import com.bpa4j.ui.rest.abstractui.Panel;
import com.bpa4j.ui.rest.abstractui.components.CheckBox;
import com.bpa4j.ui.rest.abstractui.components.ComboBox;

import com.bpa4j.ui.rest.abstractui.layout.GridLayout;
import com.bpa4j.ui.rest.editor.modules.RestFormModuleRenderer;

/**
 * REST renderer for SelectFromEditor.
 * @author AI-generated
 */
public class RestSelectFromEditorRenderer implements EditorEntryBaseRenderer{
	@Override
	@SuppressWarnings("unchecked")
	public void renderEditorBase(Object o,Field f,Wrapper<Supplier<?>> saver,Wrapper<EditableDemo> demo,EditorEntryBase base,EditorEntryBase.EditorEntryRenderingContext context){
		Wrapper<Supplier<?>> w=new Wrapper<Supplier<?>>(()->{
			try{
				return f.get(o);
			}catch(ReflectiveOperationException ex){
				throw new IllegalStateException(ex);
			}
		});
		saver.var=()->w.var.get();

		if(!(context instanceof RestFormModuleRenderer.RestEditorEntryRenderingContext)) return;
		RestFormModuleRenderer.RestEditorEntryRenderingContext ctx=(RestFormModuleRenderer.RestEditorEntryRenderingContext)context;

		try{
			ArrayList<?> elements=SelectFromEditor.getElementSuppliers().get(f).apply(demo.var.get());

			if(Collection.class.isAssignableFrom(f.getType())){
				Panel panel=new Panel();
				panel.setLayout(new GridLayout(0,1,0,0));

				ArrayList<Object> c=new ArrayList<>();
				c.addAll((Collection<Object>)f.get(o));

				for(Object element:elements){
					CheckBox cb=new CheckBox(String.valueOf(element));
					cb.setSelected(c.contains(element));
					cb.setOnChange(box->{
						if(box.isSelected()) c.add(element);
						else c.remove(element);
					});
					panel.add(cb);
				}

				w.var=()->{
					try{
						Collection<Object> l=(Collection<Object>)f.getType().getDeclaredConstructor().newInstance();
						l.addAll(c);
						return l;
					}catch(ReflectiveOperationException ex){
						throw new IllegalStateException(ex);
					}
				};
				ctx.addComponent(panel);
			}else{
				ComboBox cb=new ComboBox();
				ArrayList<String> items=new ArrayList<>();
				for(Object item:elements)
					items.add(String.valueOf(item));
				cb.setItems(items);

				Object currentVal=f.get(o);
				if(currentVal!=null){
					int index=elements.indexOf(currentVal);
					if(index>=0) cb.setSelectedIndex(index);
				}

				w.var=()->{
					int idx=cb.getSelectedIndex();
					if(idx>=0&&idx<elements.size()) return elements.get(idx);
					try{
						return f.getType().isPrimitive()?f.get(o):null;
					}catch(IllegalAccessException e){
						throw new IllegalStateException(e);
					}
				};
				ctx.addComponent(cb);
			}
		}catch(IllegalAccessException ex){
			throw new IllegalStateException(ex);
		}
	}
}
