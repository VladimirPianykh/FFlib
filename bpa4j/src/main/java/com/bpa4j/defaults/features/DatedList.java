package com.bpa4j.defaults.features;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import com.bpa4j.Dater;
import com.bpa4j.Wrapper;
import com.bpa4j.core.Data;
import com.bpa4j.core.Data.Editable;
import com.bpa4j.core.ProgramStarter;
import com.bpa4j.core.User.Feature;
import com.bpa4j.editor.EditorEntry;
import com.bpa4j.editor.EditorEntryBase;
import com.bpa4j.editor.modules.FormModule;
import com.bpa4j.navigation.HelpView.FeatureInstruction;
import com.bpa4j.navigation.ImplementedInfo;
import com.bpa4j.ui.HButton;

@SuppressWarnings("unchecked")
public final class DatedList<T extends Editable>implements Feature{
	static{
		if(!Data.getInstance().ftrInstances.containsKey(DatedList.class.getName()))Data.getInstance().ftrInstances.put(DatedList.class.getName(),new HashMap<>());
	}
	private final Class<T>type;
	private final String name;
	private final HashMap<T,Dater<T>>objects=new HashMap<>();
	private transient Supplier<Dater<T>>dateProvider;
	private DatedList(String name,Class<T>type){this.name=name;this.type=type;}
	public static<T extends Editable>DatedList<T>getList(String name){
		if(((HashMap<String,DatedList<T>>)Data.getInstance().ftrInstances.get(DatedList.class.getName())).containsKey(name))return ((HashMap<String,DatedList<T>>)Data.getInstance().ftrInstances.get(DatedList.class.getName())).get(name);
		else throw new IllegalArgumentException("List \""+name+"\" does not exist.");
	}
	public static<T extends Editable>DatedList<T>registerList(String name,Class<T>type){
		if(((HashMap<String,DatedList<?>>)Data.getInstance().ftrInstances.get(DatedList.class.getName())).containsKey(name)){
			if(((HashMap<String,DatedList<?>>)Data.getInstance().ftrInstances.get(DatedList.class.getName())).get(name).type.equals(type))return(DatedList<T>)((HashMap<String,DatedList<?>>)Data.getInstance().ftrInstances.get(DatedList.class.getName())).get(name);
			else throw new IllegalStateException("\""+name+"\" has already been registered.");
		}
		DatedList<T>b=new DatedList<>(name,type);
		((HashMap<String,DatedList<?>>)Data.getInstance().ftrInstances.get(DatedList.class.getName())).put(name,b);
		return b;
	}
	@SuppressWarnings("null")
	private JComponent createTableEntry(T t,JPanel tab,Font font){
		Wrapper<LocalDate>date=new Wrapper<>(LocalDate.now());
		if(objects.get(t)==null)objects.put(t,dateProvider.get());
		Field[]fields=objects.get(t).getClass().getFields();
		boolean flag=false;
		for(Field f:fields)if(f.isAnnotationPresent(EditorEntry.class)){flag=true;break;}
		ArrayList<Class<? extends EditorEntryBase>>editors=null;
		if(flag){
			editors=new ArrayList<>();
			for(Field f:fields)editors.add(f.getAnnotation(EditorEntry.class).editorBaseSource());
		}
		JPanel p=new JPanel(null);
		p.setBackground(Color.DARK_GRAY);
		HButton b=new HButton(){
			public void paint(Graphics g){
				int c=50-scale*3;
				g.setColor(new Color(c,c,c));
				g.fillRect(0,0,getWidth(),getHeight());
				FontMetrics fm=g.getFontMetrics();
				g.setColor(Color.LIGHT_GRAY);
				g.drawString(t.name,(getWidth()-fm.stringWidth(t.name))/2,(getHeight()+fm.getAscent()+fm.getLeading()-fm.getDescent())/2);
			}
		};
		b.addActionListener(e->{
			ProgramStarter.editor.constructEditor(t,false,()->objects.remove(t));
			tab.revalidate();
		});
		b.setSize(tab.getWidth()/3,tab.getHeight()/10);
		b.setFont(new Font(Font.DIALOG,Font.PLAIN,tab.getHeight()/20));
		p.add(b);
		JPanel dates=new JPanel(new GridLayout(1,7));
		dates.setBounds(tab.getWidth()/3,0,flag?tab.getWidth()/2:tab.getWidth()*2/3,tab.getHeight()/10);
		for(int i=0;i<7;++i)dates.add(objects.get(t).apply(t,date.var.plusDays(i)));
		b.addMouseWheelListener(e->{
			date.var=date.var.plusDays(e.getWheelRotation());
			dates.removeAll();
			for(int i=0;i<7;++i)dates.add(objects.get(t).apply(t,date.var.plusDays(i)));
		});
		p.add(dates);
		if(flag)try{
			JPopupMenu menu=new JPopupMenu("Параметры:");
			ArrayList<Supplier<?>>savers=new ArrayList<>();
			for(int i=0;i<fields.length;++i){
				Wrapper<Supplier<?>>saver=new Wrapper<>(null);
				JPanel c=new JPanel(new GridLayout());
				c.setPreferredSize(new Dimension(tab.getWidth()/6,tab.getHeight()/15));
				c.setBorder(BorderFactory.createTitledBorder(fields[i].getAnnotation(EditorEntry.class).translation()));
				if(editors.get(i)==EditorEntryBase.class)c.add(FormModule.createEditorBase(objects.get(t),fields[i],saver));
				else c.add(editors.get(i).getDeclaredConstructor().newInstance().createEditorBase(objects.get(t),fields[i],saver,new Wrapper<>(null)));
				menu.add(c);
				savers.add(saver.var);
			}
			JButton params=new JButton("Параметры");
			params.addActionListener(e->menu.show(p.getTopLevelAncestor(),params.getLocationOnScreen().x,params.getLocationOnScreen().y));
			params.setBounds(tab.getWidth()*5/6,0,tab.getWidth()/6,tab.getHeight()/10);
			params.setFont(font);
			params.setForeground(Color.BLACK);
			menu.addPopupMenuListener(new PopupMenuListener(){
				public void popupMenuWillBecomeVisible(PopupMenuEvent e){}
				public void popupMenuWillBecomeInvisible(PopupMenuEvent e){
					try{
						for(int i=0;i<fields.length;++i)fields[i].set(t,savers.get(i).get());
					}catch(IllegalAccessException ex){throw new IllegalStateException(ex);}
				}
				public void popupMenuCanceled(PopupMenuEvent e){}
			});
			p.add(params);
		}catch(IllegalAccessException ex){throw new IllegalStateException("The class with editable fields cannot be anonimous. These fields must be public.",ex);}
		catch(ReflectiveOperationException ex){throw new IllegalStateException(ex);}
		return p;
	}
	/**
	 * <p>Sets the date provider - {@link Dater} supplier.</p>
	 * <p>
	 * Dater is a function that takes a {@code T} object and a {@link LocalDate}, 
	 * and returns {@link JComponent} to represent the given day for the given object.
	 * </p>
	 * <p>Date provider can have fields with {@link com.bpa4j.editor.EditorEntry EditorEntry} to let the user configure it</p>
	 * @param provider - provider to use
	 */
	public DatedList<T>setDateProvider(Supplier<Dater<T>>provider){dateProvider=provider;return this;}
	public Set<T>getObjects(){return objects.keySet();}
	public HashMap<T,Dater<T>>getObjectsWithDaters(){return objects;}
	public T createObject(){
		try{
			T object=type.getDeclaredConstructor().newInstance();
			objects.put(object,null);
			return object;
		}catch(ReflectiveOperationException ex){throw new IllegalStateException(ex);}
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
		tab.setLayout(new GridLayout());
		JPanel panel=new JPanel(new GridLayout(Math.max(10,objects.size()+1),1));
		JScrollPane s=new JScrollPane(panel,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		for(T t:objects.keySet()){
			JComponent p=createTableEntry(t,tab,font);
			panel.add(p);
		}
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
			T t=createObject();
			panel.add(createTableEntry(t,tab,font),panel.getComponentCount()-1);
			ProgramStarter.editor.constructEditor(t,true,()->objects.remove(t));
			panel.revalidate();
		});
		add.setFont(font);
		panel.add(add);
		tab.add(s);
	}
	public String toString(){return name;}
	/**
	 * AI-generated.
	 */
	public List<ImplementedInfo>getImplementedInfo(){
		// Получаем информацию от типа объектов, а не от конкретных экземпляров
		try{
			T prototype=type.getDeclaredConstructor().newInstance();
			return prototype.getImplementedInfo().stream()
				.<ImplementedInfo>map(info -> info.appendInstruction(new FeatureInstruction(this)))
				.collect(java.util.stream.Collectors.toList());
		}catch(ReflectiveOperationException ex){
			throw new IllegalStateException(ex);
		}
	}
}