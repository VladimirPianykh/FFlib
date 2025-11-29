package com.bpa4j.ui.swing.editor;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Supplier;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JMenu;
import com.bpa4j.Wrapper;
import com.bpa4j.core.EditableDemo;
import com.bpa4j.defaults.input.SelectFromEditor;
import com.bpa4j.editor.EditorEntryBase;
import com.bpa4j.editor.EditorEntryBaseRenderer;
import com.bpa4j.ui.swing.util.LazyPanel;
import com.bpa4j.util.SprintUI;

/**
 * Swing renderer for SelectFromEditor.
 * @author AI-generated
 */
public class SwingSelectFromEditorRenderer implements EditorEntryBaseRenderer{
	public void renderEditorBase(Object o,Field f,Wrapper<Supplier<?>>saver,Wrapper<EditableDemo>demo,EditorEntryBase base,EditorEntryBase.EditorEntryRenderingContext context){
		// Not used for Swing, see createEditorComponent
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public Object createEditorComponent(Object o,Field f,Wrapper<Supplier<?>>saver,Wrapper<EditableDemo>demo,EditorEntryBase base,EditorEntryBase.EditorEntryRenderingContext context){
		Wrapper<Supplier<?>>w=new Wrapper<Supplier<?>>(()->{
			try{
				return f.get(o);
			}catch(ReflectiveOperationException ex){
				throw new IllegalStateException(ex);
			}
		});
		saver.var=()->w.var.get();
		return new LazyPanel(panel->{
			try{
				ArrayList<?>elements=SelectFromEditor.getElementSuppliers().get(f).apply(demo.var.get());
				if(Collection.class.isAssignableFrom(f.getType())){
					JMenu m=SprintUI.createMenu();
					ArrayList<Object>c=new ArrayList<>();
					c.addAll((Collection<Object>)f.get(o));
					for(Object element:elements){
						JCheckBoxMenuItem item=new JCheckBoxMenuItem(String.valueOf(element));
						item.setSelected(c.contains(element));
						item.addActionListener(e->{
							if(item.isSelected()) c.add(element);
							else c.remove(element);
						});
						m.add(item);
					}
					w.var=()->{
						try{
							Collection<Object>l=(Collection<Object>)f.getType().getDeclaredConstructor().newInstance();
							l.addAll(c);
							return l;
						}catch(ReflectiveOperationException ex){
							throw new IllegalStateException(ex);
						}
					};
					panel.add(SprintUI.wrap(m));
				}else{
					JComboBox<Object>c=new JComboBox<>();
					for(Object item:elements.toArray())
						c.addItem(item);
					c.setSelectedItem(f.get(o));
					w.var=()->{
						try{
							return c.getSelectedItem()==null&&f.getType().isPrimitive()?f.get(o):c.getSelectedItem();
						}catch(IllegalAccessException ex){
							throw new IllegalStateException(ex);
						}
					};
					panel.add(c);
				}
			}catch(IllegalAccessException ex){
				throw new IllegalStateException(ex);
			}
		});
	}
}
