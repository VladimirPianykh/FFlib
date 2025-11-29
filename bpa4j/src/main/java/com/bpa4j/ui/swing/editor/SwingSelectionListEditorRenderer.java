package com.bpa4j.ui.swing.editor;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.lang.reflect.Field;
import java.util.function.Supplier;
import javax.swing.AbstractAction;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import com.bpa4j.Wrapper;
import com.bpa4j.core.Editable;
import com.bpa4j.core.EditableDemo;
import com.bpa4j.core.EditableGroup;
import com.bpa4j.core.ProgramStarter;
import com.bpa4j.defaults.input.Selectable;
import com.bpa4j.editor.EditorEntryBase;
import com.bpa4j.editor.EditorEntryBaseRenderer;

/**
 * Swing renderer for SelectionListEditor.
 * @author AI-generated
 */
public class SwingSelectionListEditorRenderer implements EditorEntryBaseRenderer{
	public void renderEditorBase(Object o,Field f,Wrapper<Supplier<?>>saver,Wrapper<EditableDemo>demo,EditorEntryBase base,EditorEntryBase.EditorEntryRenderingContext context){
		// Not used for Swing, see createEditorComponent
	}
	
	@Override
	public Object createEditorComponent(Object o,Field f,Wrapper<Supplier<?>>saver,Wrapper<EditableDemo>demo,EditorEntryBase base,EditorEntryBase.EditorEntryRenderingContext context){
		if(f.getType()!=Selectable.class)throw new IllegalArgumentException("The field must have type Selectable<?>, but the actual type is "+f.getType()+".");
		try{
			Selectable<?>s=(Selectable<?>)((Selectable<?>)f.get(o)).clone();
			EditableGroup<?>group=(EditableGroup<?>)ProgramStarter.getStorageManager().getStorage().getGroup(s.type);
			JMenu menu=new JMenu();
			for(Editable e:group){
				JCheckBoxMenuItem item=new JCheckBoxMenuItem(new AbstractAction(){
					public void actionPerformed(ActionEvent evt){if(s.contains(e))s.remove(e);else s.add(e);}
				});
				item.setText(e.name);
				item.setSelected(s.contains(e));
				menu.add(item);
			}
			JMenuBar b=new JMenuBar(); //UNTESTED
			b.setLayout(new GridLayout());
			b.add(menu);
			saver.var=()->s;
			return b;
		}catch(ReflectiveOperationException ex){throw new IllegalStateException(ex);}
	}
}
