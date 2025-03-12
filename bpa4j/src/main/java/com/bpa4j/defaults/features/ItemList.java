package com.bpa4j.defaults.features;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.JList;

import com.bpa4j.core.Data;
import com.bpa4j.core.Data.Editable;
import com.bpa4j.core.ProgramStarter;
import com.bpa4j.core.User.Feature;
import com.bpa4j.editor.EditorEntry;
import com.bpa4j.ui.AutoLayout;
import com.bpa4j.ui.HButton;

/**
 * A ItemList with objects of the given type.
 */
@SuppressWarnings("unchecked")
public final class ItemList<T extends Serializable>implements Feature{
	public static interface Filter<T>extends Predicate<T>{
		public default JComponent getConfigurator(Runnable saver,ArrayList<T>objects){return null;}
	}
	public static interface Sorter<T>extends Comparator<T>{
		public default JComponent getConfigurator(Runnable saver,ArrayList<T>objects){return null;}
	}
	public static interface CollectiveAction<T>extends Consumer<List<T>>{
		public default JButton getActivator(){return new HButton();}
	}
	public static interface SingularAction<T>extends Consumer<T>{
		public default JButton getActivator(){return new HButton();}
	}
	private static class Slicer<T>implements Filter<T>{
		private JComboBox<String>c;
		private final Function<T,String>f;
		public Slicer(Function<T,String>function){f=function;}
		public JComponent getConfigurator(Runnable saver,ArrayList<T>objects){
			HashSet<String>set=new HashSet<>();
			for(T t:objects)set.add(f.apply(t));
			c=new JComboBox<>();
			for(String str:set)c.addItem(str);
			c.addItemListener(e->saver.run());
			return c;
		}
		public boolean test(T t){return f.apply(t).equals(c.getSelectedItem());}
	}
	static{
		if(!Data.getInstance().ftrInstances.containsKey(ItemList.class.getName()))Data.getInstance().ftrInstances.put(ItemList.class.getName(),new HashMap<>());
	}
	private final Class<T>type;
	private final String name;
	private ArrayList<T>objects=new ArrayList<>(){
		private void writeObject(ObjectOutputStream out)throws IOException,ClassNotFoundException{
			if(elementSupplier!=null)clear();
			out.defaultWriteObject();
		}
	};
	private transient ArrayList<Consumer<JList<T>>>listDecorators;
	private transient Sorter<T>sorter;
	private transient Filter<T>filter;
	private transient Supplier<ArrayList<T>>elementSupplier;
	private transient boolean allowCreation;
	private transient ArrayList<CollectiveAction<T>>collectiveActions=new ArrayList<>();
	private transient ArrayList<SingularAction<T>>singularActions=new ArrayList<>();
	private ItemList(String name,Class<T>type){this.name=name;this.type=type;}
	private void readObject(ObjectInputStream is)throws IOException,ClassNotFoundException{
		is.defaultReadObject();
		collectiveActions=new ArrayList<>();
		singularActions=new ArrayList<>();
	}
	public static<T extends Serializable>ItemList<T>getList(String name){
		if(((HashMap<String,ItemList<?>>)Data.getInstance().ftrInstances.get(ItemList.class.getName())).containsKey(name))return(ItemList<T>)((HashMap<String,ItemList<?>>)Data.getInstance().ftrInstances.get(ItemList.class.getName())).get(name);
		else throw new IllegalArgumentException("ItemList \""+name+"\" does not exist.");
	}
	public static<T extends Serializable>ItemList<T>registerList(String name,Class<T>type){
		if(((HashMap<String,ItemList<?>>)Data.getInstance().ftrInstances.get(ItemList.class.getName())).containsKey(name)&&((HashMap<String,ItemList<?>>)Data.getInstance().ftrInstances.get(ItemList.class.getName())).get(name).type.equals(type))return(ItemList<T>)((HashMap<String,ItemList<?>>)Data.getInstance().ftrInstances.get(ItemList.class.getName())).get(name);
		ItemList<T>b=new ItemList<T>(name,type);
		((HashMap<String,ItemList<?>>)Data.getInstance().ftrInstances.get(ItemList.class.getName())).put(name,b);
		return b;
	}
	private void fillTable(DefaultListModel<T>m){
		if(sorter!=null)objects.sort(sorter);
		if(filter==null)for(T t:objects)m.addElement(t);
		else for(T t:objects)if(filter.test(t))m.addElement(t);
	}
	/**
	 * <p>Adds a table decorator to this ItemList.</p>
	 * <p>
	 * Table decorators will process the table additionaly upon creation.
	 * @param decorator - table decorator to be used
	 */
	public ItemList<T>addListDecorator(Consumer<JList<T>>decorator){
		if(listDecorators==null)listDecorators=new ArrayList<>();
		listDecorators.add(decorator);
		return this;
	}
	/**
	 * <p>Sets a filter to this ItemList.</p>
	 * <p>Filter is an objects that provides a {@link JComponent} for configuration
	 * and a {@code test(T)} method to check if an element should be displayed.</p>
	 * @param filter - filter to be used
	 */
	public ItemList<T>setFilter(Filter<T>filter){this.filter=filter;return this;}
	/**
	 * <p>Sets a slicer to this ItemList.</p>
	 * <p>
	 * Slicer names a group for each object given.
	 * Tasks with the same name will be displayed simultaneously.
	 * Slicer will replace the current filter.
	 * @param slicer - function to be used as a slicer
	 */
	public ItemList<T>setSlicer(Function<T,String>slicer){filter=new Slicer<T>(slicer);return this;}
	/**
	 * Sets a sorter to this ItemList.
	 * @param sorter - comparator to be used for sorting
	 */
	public ItemList<T>setSorter(Sorter<T>sorter){this.sorter=sorter;return this;}
	/**
	 * Sets a supplier to get elements from (instead of saving and loading them).
	 * @param supplier - supplier to be used
	 */
	public ItemList<T>setElementSupplier(Supplier<ArrayList<T>>supplier){elementSupplier=supplier;return this;}
	/**
	 * Permits creation of new elements if {@code allow} is true.
	 */
	public ItemList<T>setAllowCreation(boolean allow){allowCreation=allow;return this;}
	public ItemList<T>addCollectiveAction(CollectiveAction<T>action){collectiveActions.add(action);return this;}
	public ItemList<T>addSingularAction(SingularAction<T>action){singularActions.add(action);return this;}
	public ArrayList<T>getObjects(){return objects;}
	public T createObject(){
		try{
			T object=type.getDeclaredConstructor().newInstance();
			objects.add(object);
			return object;
		}catch(ReflectiveOperationException ex){throw new IllegalStateException(ex);}
	}
	public void fillTab(JPanel content,JPanel tab,Font font){
		if(elementSupplier!=null)objects=elementSupplier.get();
		tab.setLayout(new BorderLayout());
		tab.setBorder(BorderFactory.createEmptyBorder(tab.getHeight()/300,tab.getWidth()/300,tab.getHeight()/300,tab.getWidth()/300));
		JPanel config=null;
		JComponent filterConfig=null,sorterConfig=null;
		JList<T>t=new JList<T>();
		t.setBackground(Color.DARK_GRAY);
		t.setForeground(Color.WHITE);
		t.setFixedCellHeight(tab.getHeight()/10);
		t.setFont(new Font(Font.DIALOG,Font.ITALIC,tab.getHeight()/30));
		JScrollPane sPane=new JScrollPane(t);
		sPane.setBorder(BorderFactory.createTitledBorder(null,"Задания",0,0,new Font(Font.DIALOG,Font.PLAIN,tab.getHeight()/50),Color.WHITE));
		ArrayList<String>s=new ArrayList<>();
		for(Field f:type.getFields())if(f.isAnnotationPresent(EditorEntry.class))s.add(f.getAnnotation(EditorEntry.class).translation());
		DefaultListModel<T>m=new DefaultListModel<>();
		if(filter!=null)filterConfig=filter.getConfigurator(()->{m.clear();fillTable(m);},objects);
		if(sorter!=null)sorterConfig=sorter.getConfigurator(()->{m.clear();fillTable(m);},objects);
		if(allowCreation||filterConfig!=null||sorterConfig!=null||!singularActions.isEmpty()||!collectiveActions.isEmpty()){
			config=new JPanel(new AutoLayout());
			config.setPreferredSize(new Dimension(tab.getWidth(),tab.getHeight()/9));
			if(filterConfig!=null)config.add(filterConfig);
			if(sorterConfig!=null)config.add(sorterConfig);
			if(!singularActions.isEmpty())for(SingularAction<T>a:singularActions){
				JButton b=a.getActivator();
				b.addActionListener(e->a.accept(t.getSelectedValue()));
				config.add(b);
			}
			if(!collectiveActions.isEmpty()){
				for(CollectiveAction<T>a:collectiveActions){
					JButton b=a.getActivator();
					b.addActionListener(e->a.accept(t.getSelectedValuesList()));
					config.add(b);
				}
				t.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			}
			if(allowCreation){
				HButton create=new HButton(){
					public void paint(Graphics g){
						g.setColor(Color.DARK_GRAY);
						g.fillRect(0,0,getWidth(),getHeight());
						FontMetrics fm=g.getFontMetrics();
						g.setColor(Color.WHITE);
						g.drawString("Добавить",(getWidth()-fm.stringWidth("Добавить"))/2,(getHeight()+fm.getAscent()+fm.getLeading()-fm.getDescent())/2);
					}
				};
				create.addActionListener(e->{
					try{
						T o=type.getDeclaredConstructor().newInstance();
						objects.add(o);
						if(o instanceof Editable)ProgramStarter.editor.constructEditor((Editable)o,true,()->objects.remove(o));
						m.clear();
						fillTable(m);
						tab.revalidate();
					}catch(ReflectiveOperationException ex){throw new IllegalStateException(ex);}
				});
				config.add(create);
			}
		}
		fillTable(m);
		sPane.setPreferredSize(new Dimension(tab.getWidth(),config==null?tab.getHeight():tab.getHeight()*8/9));
		t.setModel(m);
		if(listDecorators!=null)for(Consumer<JList<T>>c:listDecorators)c.accept(t);
		tab.add(sPane,BorderLayout.SOUTH);
		if(config!=null)tab.add(config,BorderLayout.NORTH);
	}
	public void paint(Graphics2D g2,BufferedImage image,int s){
		g2.setStroke(new BasicStroke(s/40));
		g2.drawLine(s/6,s/4,s*5/6,s/4);
		g2.drawLine(s/6,s/2,s/2,s/2);
		g2.drawLine(s/6,s*3/4,s*2/3,s*3/4);
	}
	public String toString(){return name;}
}