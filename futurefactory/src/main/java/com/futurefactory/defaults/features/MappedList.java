package com.futurefactory.defaults.features;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Vector;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.CellEditorListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;

import com.futurefactory.HButton;
import com.futurefactory.core.Data;
import com.futurefactory.core.ProgramStarter;
import com.futurefactory.core.Registrator;
import com.futurefactory.core.Data.Editable;
import com.futurefactory.core.Data.EditableGroup;
import com.futurefactory.core.User.Feature;
import com.futurefactory.defaults.table.FieldCellRenderer;
import com.futurefactory.defaults.table.FieldCellValue;
import com.futurefactory.defaults.table.FormCellEditor;
import com.futurefactory.editor.EditorEntry;

@SuppressWarnings("unchecked")
public class MappedList<T extends Editable,V extends Serializable>implements Feature{
	static{
		if(!Data.getInstance().ftrInstances.containsKey(MappedList.class.getName()))Data.getInstance().ftrInstances.put(MappedList.class.getName(),new HashMap<>());
	}
	private Class<T>type;
	private Class<V>vType;
	private String name;
	private HashMap<T,V>objects=new HashMap<>();
	private MappedList(String name,EditableGroup<T>group,Class<V>vType){this.name=name;this.type=group.type;this.vType=vType;}
	public static<T extends Editable,V extends Serializable>MappedList<T,V>getList(String name){
		if(((HashMap<String,MappedList<T,V>>)Data.getInstance().ftrInstances.get(MappedList.class.getName())).containsKey(name))return ((HashMap<String,MappedList<T,V>>)Data.getInstance().ftrInstances.get(MappedList.class.getName())).get(name);
		else throw new IllegalArgumentException("List \""+name+"\" does not exist.");
	}
	public static<T extends Editable,V extends Serializable>MappedList<T,V>registerList(String name,EditableGroup<T>group,Class<V>vType){
		if(((HashMap<String,MappedList<?,?>>)Data.getInstance().ftrInstances.get(MappedList.class.getName())).containsKey(name)){
			if(((HashMap<String,MappedList<?,?>>)Data.getInstance().ftrInstances.get(MappedList.class.getName())).get(name).type.equals(group.type))return(MappedList<T,V>)((HashMap<String,MappedList<?,?>>)Data.getInstance().ftrInstances.get(MappedList.class.getName())).get(name);
			else throw new IllegalStateException("\""+name+"\" has already been registered with the type"+((HashMap<String,MappedList<?,?>>)Data.getInstance().ftrInstances.get(MappedList.class.getName())).get(name).type+".");
		}
		Registrator.register(group); 
		MappedList<T,V>b=new MappedList<>(name,group,vType);
		((HashMap<String,MappedList<?,?>>)Data.getInstance().ftrInstances.get(MappedList.class.getName())).put(name,b);
		return b;
	}
	private void fillTable(DefaultTableModel m){
		for(T t:Data.getInstance().getGroup(type)){
			Vector<Object>l=new Vector<>();
			l.add(t);
			for(Field f:vType.getFields())l.add(new FieldCellValue(f,getMapping(t)));
			m.addRow(l);
		}
	}
	public V getMapping(T t){
		try{
			if(!objects.containsKey(t))objects.put(t,vType.getDeclaredConstructor(type).newInstance(t));
			return objects.get(t);
		}catch(ReflectiveOperationException ex){throw new RuntimeException(ex);}
	}
	public T createObject(){
		try{
			T t=type.getDeclaredConstructor().newInstance();
			Data.getInstance().getGroup(type).add(t);
			return t;
		}catch(ReflectiveOperationException ex){throw new RuntimeException(ex);}
	}
	public void paint(Graphics2D g2,BufferedImage image,int h){
		g2.setStroke(new BasicStroke(h/20));
		g2.drawRoundRect(h/10,h/10,h*4/5,h*4/5,h/10,h/10);
		g2.setClip(new RoundRectangle2D.Double(h/10,h/10,h*4/5,h*4/5,h/10,h/10));
		g2.setStroke(new BasicStroke(h/50));
		for(int x=h/10;x<h*9/10;x+=h/10)g2.drawLine(x,h/10,x,h*9/10);
		for(int y=h/10;y<h*9/10;y+=h/10)g2.drawLine(h/10,y,h*9/10,y);
	}
	public void fillTab(JPanel content,JPanel tab,Font font){
		tab.setLayout(new BorderLayout());
		JTable t=new JTable();
		t.setDefaultEditor(Object.class,new FormCellEditor());
		t.setDefaultRenderer(Object.class,new FieldCellRenderer());
		t.setRowHeight(tab.getHeight()/10);
		Vector<String>v=new Vector<>();
		v.add("Объекты");
		for(Field f:vType.getFields())v.add(f.getAnnotation(EditorEntry.class).translation());
		DefaultTableModel m=new DefaultTableModel(v,0);
		t.setModel(m);
		t.getColumnModel().getColumn(0).setCellRenderer((table,value,isSelected,hasFocus,row,column)->{
			HButton b=new HButton(){
				public void paint(Graphics g){
					int c=50-scale;
					if(getModel().isPressed())c-=25;
					g.setColor(new Color(c,c,c));
					g.fillRect(0,0,getWidth(),getHeight());
					FontMetrics fm=g.getFontMetrics();
					g.setColor(Color.WHITE);
					g.drawString(((T)value).name,(getWidth()-fm.stringWidth(((T)value).name))/2,(getHeight()+fm.getAscent()+fm.getLeading()-fm.getDescent())/2);
				}
			};
			b.addActionListener(e->ProgramStarter.editor.constructEditor((T)value,false));
			return b;
		});
		t.getColumnModel().getColumn(0).setCellEditor(new TableCellEditor(){
			public Object getCellEditorValue(){return null;}
			public Component getTableCellEditorComponent(JTable table,Object value,boolean isSelected,int row,int column){
				HButton b=new HButton(){
					public void paint(Graphics g){
						int c=50-scale;
						if(getModel().isPressed())c-=25;
						g.setColor(new Color(c,c,c));
						g.fillRect(0,0,getWidth(),getHeight());
						FontMetrics fm=g.getFontMetrics();
						g.setColor(Color.WHITE);
						g.drawString(((T)value).name,(getWidth()-fm.stringWidth(((T)value).name))/2,(getHeight()+fm.getAscent()+fm.getLeading()-fm.getDescent())/2);
					}
				};
				b.addActionListener(e->ProgramStarter.editor.constructEditor((T)value,false));
				return b;
			}
			public boolean isCellEditable(EventObject anEvent){return true;}
			public boolean shouldSelectCell(EventObject anEvent){return true;}
			public boolean stopCellEditing(){return true;}
			public void cancelCellEditing(){}
			public void addCellEditorListener(CellEditorListener l){}
			public void removeCellEditorListener(CellEditorListener l){}
		});
		JScrollPane s=new JScrollPane(t,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		s.setPreferredSize(new Dimension(tab.getWidth(),tab.getHeight()*9/10));
		fillTable(m);
		HButton add=new HButton(){
			public void paint(Graphics g){
				int c=50-scale*4;
				if(getModel().isPressed())c-=25;
				g.setColor(new Color(c,c,c));
				g.fillRect(0,0,getWidth(),getHeight());
				g.setColor(Color.WHITE);
				FontMetrics fm=g.getFontMetrics();
				g.drawString("Добавить",(getWidth()-fm.stringWidth("Добавить"))/2,(getHeight()+fm.getAscent()+fm.getLeading()-fm.getDescent())/2);
			}
		};
		add.addActionListener(e->{
			T o=createObject();
			ProgramStarter.editor.constructEditor(o,true);
			Vector<Object>l=new Vector<>();
			l.add(o);
			for(Field f:vType.getFields())l.add(new FieldCellValue(f,getMapping(o)));
			m.addRow(l);
		});
		add.setPreferredSize(new Dimension(tab.getWidth(),tab.getHeight()/10));
		add.setFont(font);
		tab.add(add,BorderLayout.SOUTH);
		tab.add(s,BorderLayout.NORTH);
		tab.revalidate();
	}
	public String toString(){return name;}
}
