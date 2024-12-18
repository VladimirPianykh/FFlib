package com.futurefactory.defaults.table;

import java.awt.Component;
import java.util.EventObject;

import javax.swing.JTable;
import javax.swing.event.CellEditorListener;
import javax.swing.table.TableCellEditor;

public class EmptyCellEditor implements TableCellEditor{
	public Object getCellEditorValue(){return null;}
	public boolean isCellEditable(EventObject anEvent){return false;}
	public boolean shouldSelectCell(EventObject anEvent){return false;}
	public boolean stopCellEditing(){return true;}
	public void cancelCellEditing(){}
	public void addCellEditorListener(CellEditorListener l){}
	public void removeCellEditorListener(CellEditorListener l){}
	public Component getTableCellEditorComponent(JTable table,Object value,boolean isSelected,int row,int column){return null;}
}
