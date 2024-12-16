package com.futurefactory;

import java.awt.Component;
import java.awt.Font;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.Iterator;
import java.util.function.Supplier;

import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.table.TableCellEditor;

import com.futurefactory.editor.modules.FormModule;

public class FormCellEditor implements TableCellEditor{
	private ArrayList<CellEditorListener>a=new ArrayList<>();
	private Component editor;
	private FieldCellValue v;
	private Wrapper<Supplier<?>>saver=new Wrapper<>(null);
	public Object getCellEditorValue(){return v;}
	public boolean isCellEditable(EventObject e){return true;}
	public boolean shouldSelectCell(EventObject e){return true;}
	public boolean stopCellEditing(){
		try{v.f.set(v.o,saver.var.get());}catch(IllegalAccessException ex){return false;}
		ChangeEvent e=new ChangeEvent(this);
		Iterator<CellEditorListener>it=a.iterator();
		while(it.hasNext())it.next().editingStopped(e);
		return true;
	}
	public void cancelCellEditing(){}
	public void addCellEditorListener(CellEditorListener l){SwingUtilities.invokeLater(()->a.add(l));}
	public void removeCellEditorListener(CellEditorListener l){SwingUtilities.invokeLater(()->a.remove(l));}
	public Component getTableCellEditorComponent(JTable table,Object value,boolean isSelected,int row,int column){
		v=(FieldCellValue)value;
		editor=FormModule.wrapEditorComponent(FormModule.createEditorBase(v.o,v.f,saver),new Font(Font.DIALOG,Font.PLAIN,Root.SCREEN_SIZE.height/50));
		return editor;
	}
}
