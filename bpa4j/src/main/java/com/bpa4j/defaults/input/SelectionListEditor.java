package com.bpa4j.defaults.input;

import java.awt.event.ActionEvent;
import java.lang.reflect.Field;
import java.util.function.Supplier;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;

import com.bpa4j.Wrapper;
import com.bpa4j.core.Data;
import com.bpa4j.core.Data.Editable;
import com.bpa4j.core.Data.EditableGroup;
import com.bpa4j.editor.EditorEntryBase;

public class SelectionListEditor implements EditorEntryBase{
    public JComponent createEditorBase(Object o,Field f,Wrapper<Supplier<?>>saver){
        if(f.getType()!=Selectable.class)throw new IllegalArgumentException("The field must have type Selectable<?>, but the actual type is "+f.getType()+".");
        try{
            Selectable<?>s=(Selectable<?>)((Selectable<?>)f.get(o)).clone();
            EditableGroup<?>group=(EditableGroup<?>)Data.getInstance().getGroup(s.type);
            JPopupMenu menu=new JPopupMenu();
            for(Editable e:group){
                JCheckBoxMenuItem item=new JCheckBoxMenuItem(new AbstractAction(){
                    public void actionPerformed(ActionEvent evt){if(s.contains(e))s.remove(e);else s.add(e);}
                });
                item.setText(e.name);
                item.setSelected(s.contains(e));
                menu.add(item);
            }
            JButton b=new JButton("Выбрать...");
            b.addActionListener(e->menu.show(b.getTopLevelAncestor(),b.getLocationOnScreen().x,b.getLocationOnScreen().y));
            saver.var=()->s;
            return b;
        }catch(ReflectiveOperationException ex){throw new RuntimeException(ex);}
    }
}