package com.futurefactory.defaults.table;

import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;

import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class FieldCellRenderer extends DefaultTableCellRenderer{
    public Component getTableCellRendererComponent(JTable table,Object value,boolean isSelected,boolean hasFocus,int row,int column){
        FieldCellValue f=(FieldCellValue)value;
        if(f.f.getType()!=ArrayList.class)return super.getTableCellRendererComponent(table,value,isSelected,hasFocus,row,column);
        JComboBox<Object>c=new JComboBox<>();
        try{for(Object o:(ArrayList<?>)f.f.get(f.o))c.addItem(o);}catch(IllegalAccessException ex){throw new RuntimeException(ex);}
        if(c.getItemCount()==0){
            c.setSelectedItem("-----");
            c.setForeground(Color.GRAY);
        }
        c.addItemListener(e->c.setSelectedItem(c.getItemCount()==0?"-----":null));
        return c;
    }
}
