package com.futurefactory;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public class DynamicCellRenderer implements TableCellRenderer{
    public Component getTableCellRendererComponent(JTable t,Object value,boolean isSelected,
    boolean hasFocus,int row,int col){
        value=t.getModel().getValueAt(row,col);
        JLabel l=new JLabel(value.toString());
        if(isSelected)l.setBackground(t.getSelectionBackground());
        else l.setBackground(t.getBackground());
        l.setForeground(t.getForeground());
        l.setOpaque(true);
        return l;
    }
}
