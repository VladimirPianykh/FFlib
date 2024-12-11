package com.futurefactory.defaults.editorbases;

import java.awt.event.ActionEvent;
import java.lang.reflect.Field;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;

import com.futurefactory.Data;
import com.futurefactory.Data.Editable;
import com.futurefactory.Data.EditableGroup;
import com.futurefactory.Selectable;
import com.futurefactory.Wrapper;
import com.futurefactory.editor.EditorEntryBase;

public class SelectionListEditor implements EditorEntryBase{
    public JComponent createEditorBase(Object o,Field f,Wrapper<Runnable>saver){
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
            saver.var=()->{try{f.set(o,s);}catch(IllegalAccessException ex){throw new RuntimeException(ex);}};
            return b;
        }catch(ReflectiveOperationException ex){throw new RuntimeException(ex);}
    }
}