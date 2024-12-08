package com.futurefactory.defaults.features;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import com.futurefactory.Data;
import com.futurefactory.FieldCellValue;
import com.futurefactory.FormCellEditor;
import com.futurefactory.Data.Task;
import com.futurefactory.User.Feature;
import com.futurefactory.editor.EditorEntry;

/**
 * A board with tasks of the given type.
 */
public class TaskBoard<T extends Task>implements Feature{
	private Class<T>type;
	private String name;
	private ArrayList<T>tasks=new ArrayList<>();
	private transient ArrayList<Consumer<JTable>>tableDecorators;
	private transient Function<T,String>slicer;
	private TaskBoard(String name,Class<T>type){
		this.name=name;
		this.type=type;
	}
	public static TaskBoard<?>getBoard(String name){
		if(Data.getInstance().boards.containsKey(name))return Data.getInstance().boards.get(name);
		else throw new IllegalArgumentException("Board \""+name+"\" does not exist.");
	}
	@SuppressWarnings("unchecked")
	public static<T extends Task>TaskBoard<T>registerBoard(String name,Class<T>type){
		HashMap<String,TaskBoard<?>>boards=Data.getInstance().boards;
		if(boards.containsKey(name)&&boards.get(name).type.equals(type))return(TaskBoard<T>)boards.get(name);
		TaskBoard<T>b=new TaskBoard<T>(name,type);
		boards.put(name,b);
		return b;
	}
	private void fillTable(DefaultTableModel m,String group){
		if(slicer==null)for(T task:tasks){
			Vector<Object>values=new Vector<>();
			for(Field f:type.getFields())if(f.isAnnotationPresent(EditorEntry.class))values.add(new FieldCellValue(f,task));
			m.addRow(values);
		}else{
			for(T task:tasks){
				if(slicer.apply(task).equals(group)){
					Vector<Object>values=new Vector<>();
					for(Field f:type.getFields())if(f.isAnnotationPresent(EditorEntry.class))values.add(new FieldCellValue(f,task));
					m.addRow(values);
				}
			}
		}
	}
	/**
	 * <br>Adds a table decorator to this board.</br>
	 * <br>
	 * Table decorators will process the table additionaly upon creation.
	 * </br>
	 * @param decorator - table decorator to be used
	 */
	public TaskBoard<T>addTableDecorator(Consumer<JTable>decorator){
		if(tableDecorators==null)tableDecorators=new ArrayList<>();
		tableDecorators.add(decorator);
		return this;
	}
	/**
	 * <br>Sets a slicer to this board.</br>
	 * <br>
	 * Slicer names a group for each task given.
	 * Tasks with the same name will be displayed simultaneously.
	 * </br>
	 * @param slicer - function to be used as a slicer
	 */
	public TaskBoard<T>setSlicer(Function<T,String>slicer){
		this.slicer=slicer;
		return this;
	}
	public T createTask(){
		try{
			T task=type.getDeclaredConstructor().newInstance();
			tasks.add(task);
			return task;
		}catch(ReflectiveOperationException ex){throw new RuntimeException(ex);}
	}
	public void fillTab(JPanel content,JPanel tab,Font font){
		tab.setLayout(new BorderLayout());
		tab.setBorder(BorderFactory.createEmptyBorder(tab.getHeight()/300,tab.getWidth()/300,tab.getHeight()/300,tab.getWidth()/300));
		JComboBox<String>groups=null;
		JTable t=new JTable();
		t.setBackground(Color.DARK_GRAY);
		t.setForeground(Color.WHITE);
		t.setRowHeight(tab.getHeight()/10);
		t.getTableHeader().setFont(new Font(Font.DIALOG,Font.ITALIC,tab.getHeight()/30));
		t.setFillsViewportHeight(true);
		t.setDefaultEditor(Object.class,new FormCellEditor());
		JScrollPane sPane=new JScrollPane(t);
		sPane.setBorder(BorderFactory.createTitledBorder(null,"Задания",0,0,new Font(Font.DIALOG,Font.PLAIN,tab.getHeight()/50),Color.WHITE));
		ArrayList<String>s=new ArrayList<>();
		for(Field f:type.getFields())if(f.isAnnotationPresent(EditorEntry.class))s.add(f.getAnnotation(EditorEntry.class).translation());
		DefaultTableModel m=new DefaultTableModel(s.toArray(new String[0]),0);
		if(slicer!=null){
			HashSet<String>set=new HashSet<>();
			for(T task:tasks)set.add(slicer.apply(task));
			groups=new JComboBox<>();
			groups.setPreferredSize(new Dimension(tab.getWidth(),tab.getHeight()/9));
			for(String str:set)groups.addItem(str);
			final JComboBox<String>finalGroups=groups;
			groups.addItemListener(e->{
				m.setRowCount(0);
				fillTable(m,(String)finalGroups.getSelectedItem());
			});
		}
		fillTable(m,groups==null?null:(String)groups.getSelectedItem());
		sPane.setPreferredSize(new Dimension(tab.getWidth(),groups==null?tab.getHeight():tab.getHeight()*8/9));
		t.setModel(m);
		if(tableDecorators!=null)for(Consumer<JTable>c:tableDecorators)c.accept(t);
		tab.add(sPane,BorderLayout.SOUTH);
		if(groups!=null)tab.add(groups,BorderLayout.NORTH);
		//TODO: make tasks editable
	}
	public void paint(Graphics2D g2,BufferedImage image,int s){
		g2.setStroke(new BasicStroke(s/10));
		g2.drawRect(s/10,s/5,s*4/5,s*3/5);
		g2.setStroke(new BasicStroke(s/20));
		g2.drawPolygon(new int[]{s/2,s/4,s/2,s/3,s/2,s/2,s/2,s*2/3,s/2,s*3/4,s/2,s*2/3,s/2,s/2,s/2,s/3,s/2},new int[]{s/2,s/2,s/2,s/3,s/2,s/4,s/2,s/3,s/2,s/2,s/2,s*2/3,s/2,s*3/4,s/2,s*2/3,s/2},16);
        g2.fillOval(s/3,s/3,s/3,s/3);
	}
	public String toString(){return name;}
}