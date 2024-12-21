package com.futurefactory.defaults.ftr_attributes.data_renderers;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.function.Supplier;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import com.futurefactory.defaults.table.EmptyCellEditor;
import com.futurefactory.editor.EditorEntry;

/**
 * <br>Returns a table, rendering all editable fields of the component given.</br>
 * <br>You can create a separate info class or use an existing editable to fill the table.</br>
 */
public class TableDataRenderer<T>implements Supplier<JComponent>{
	private Supplier<ArrayList<T>>elementSupplier;
	private String title;
	public TableDataRenderer(Supplier<ArrayList<T>>elementSupplier){this.elementSupplier=elementSupplier;}
	public TableDataRenderer(Supplier<ArrayList<T>>elementSupplier,String title){this(elementSupplier);this.title=title;}
	public JComponent get(){
		ArrayList<T>a=elementSupplier.get();
		if(a.isEmpty())return new JTable();
		Field[]allFields=a.get(0).getClass().getFields();
		ArrayList<Field>fields=new ArrayList<>();
		for(Field f:allFields)if(f.isAnnotationPresent(EditorEntry.class))fields.add(f);
		DefaultTableModel m=new DefaultTableModel(fields.stream().map(f->f.getAnnotation(EditorEntry.class).translation()).toArray(),0);
		JTable table=new JTable(m);
		table.setDefaultEditor(Object.class,new EmptyCellEditor());
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		for(T t:a){
			Object[]o=new Object[fields.size()];
			for(int i=0;i<o.length;++i)try{
				o[i]=fields.get(i).get(t);
			}catch(IllegalAccessException ex){throw new RuntimeException(ex);}
			m.addRow(o);
		}
		JScrollPane s=new JScrollPane(table,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);;
		if(title!=null)s.setBorder(BorderFactory.createTitledBorder(title));
		return s;
	}
}
