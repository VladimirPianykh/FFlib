package com.bpa4j.defaults.input;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import com.bpa4j.Wrapper;
import com.bpa4j.core.EditableDemo;
import com.bpa4j.editor.EditorEntryBase;
import com.bpa4j.util.SprintUI;

/**
 * An editor that suggest the user selection from an {@code ArrayList}, provided by the element supplier.
 * If the field is a {@code Collection}, the choice is multiple, otherwise it is singular.
 */
public class SelectFromEditor implements EditorEntryBase{
	private static HashMap<Field,Supplier<ArrayList<?>>>elementSuppliers=new HashMap<>();
	/**
	 * Configures the editor for the specified field.
	 * @param f - field to configure for
	 * @param elementSupplier - a Supplier to get elements from
	 */
	public void configure(Field f,Supplier<ArrayList<?>>elementSupplier){elementSuppliers.put(f,elementSupplier);}
	@SuppressWarnings("unchecked")
	public JComponent createEditorBase(Object o,Field f,Wrapper<Supplier<?>>saver,Wrapper<EditableDemo>demo){
		//UNTESTED
		ArrayList<?>elements=elementSuppliers.get(f).get();
		try{
			if(Collection.class.isAssignableFrom(f.getType())){
				JMenu m=SprintUI.createMenu();
				Collection<Object>c=new ArrayList<>();
				c.addAll((Collection<Object>)f.get(o));
				for(Object element:elements){
					JCheckBoxMenuItem item=new JCheckBoxMenuItem();
					item.setSelected(c.contains(element));
					item.addActionListener(e->{
						if(item.isSelected())c.add(element);
						else c.remove(element);
					});
				}
				saver.var=()->c;
				return SprintUI.wrap(m);
			}else{
				JComboBox<Object>c=new JComboBox<>(elements.toArray());
				c.setSelectedItem(f.get(o));
				saver.var=()->c.getSelectedItem();
				return c;
			}
		}catch(ReflectiveOperationException ex){throw new IllegalStateException(ex);}
	}

}
