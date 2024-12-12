package com.futurefactory.defaults.features;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.function.Function;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.futurefactory.Data;
import com.futurefactory.HButton;
import com.futurefactory.ProgramStarter;
import com.futurefactory.Registrator;
import com.futurefactory.Data.Editable;
import com.futurefactory.Data.EditableGroup;
import com.futurefactory.User.Feature;

/**
 * A feature that shows a list of {@link Editable}s of the given group.
 */
@SuppressWarnings("unchecked")
public class EditableList<T extends Editable>implements Feature{
	static{
		if(!Data.getInstance().ftrInstances.containsKey(EditableList.class.getName()))Data.getInstance().ftrInstances.put(EditableList.class.getName(),new HashMap<>());
	}
	private Class<T>type;
	private String name;
	private transient Function<T,JComponent>componentProvider;
	private EditableList(String name,EditableGroup<T>group){
		this.name=name;
		Registrator.register(group);
		type=group.type;
	}
	public static<T extends Editable>EditableList<T>getList(String name){
		if(((HashMap<String,EditableList<T>>)Data.getInstance().ftrInstances.get(EditableList.class.getName())).containsKey(name))return ((HashMap<String,EditableList<T>>)Data.getInstance().ftrInstances.get(EditableList.class.getName())).get(name);
		else throw new IllegalArgumentException("Table \""+name+"\" does not exist.");
	}
	public static<T extends Editable>EditableList<T>registerList(String name,EditableGroup<T>group){
		if(((HashMap<String,EditableList<?>>)Data.getInstance().ftrInstances.get(EditableList.class.getName())).containsKey(name)&&((HashMap<String,EditableList<?>>)Data.getInstance().ftrInstances.get(EditableList.class.getName())).get(name).type.equals(group.type))return(EditableList<T>)((HashMap<String,EditableList<?>>)Data.getInstance().ftrInstances.get(EditableList.class.getName())).get(name);
		EditableList<T>b=new EditableList<T>(name,group);
		((HashMap<String,EditableList<?>>)Data.getInstance().ftrInstances.get(EditableList.class.getName())).put(name,b);
		return b;
	}
	/**
	 * <br>Sets the component provider</br>
	 * <br>
	 * Component provider is a function that takes {@link T} and returns a component to represent the given element in the list.
	 * </br>
	 * @param provider - provider to use
	 */
	public EditableList<T>setComponentProvider(Function<T,JComponent>provider){componentProvider=provider;return this;}
	/**
	 * Returns a group of {@link Editable}s associated with this list.
	 */
	public EditableGroup<T>getGroup(){return Data.getInstance().getGroup(type);}
	public T createObject(){
		try{
			EditableGroup<T>group=(EditableGroup<T>)Data.getInstance().getGroup(type);
			T editable=group.type.getDeclaredConstructor().newInstance();
			group.add(editable);
			return editable;
		}catch(ReflectiveOperationException ex){throw new RuntimeException(ex);}
	}
	public void paint(Graphics2D g2,BufferedImage image,int h){
		g2.setStroke(new BasicStroke(h/40));
		g2.drawLine(h/6,h/4,h*5/6,h/4);
		g2.drawLine(h/6,h/2,h/2,h/2);
		g2.drawLine(h/6,h*3/4,h*2/3,h*3/4);
	}
	public void fillTab(JPanel content,JPanel tab,Font font){
		EditableGroup<T>group=(EditableGroup<T>)Data.getInstance().getGroup(type);
		tab.setLayout(new GridLayout());
		JPanel panel=new JPanel(new GridLayout(Math.max(10,group.size()+1),1));
		JScrollPane s=new JScrollPane(panel,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		s.getVerticalScrollBar().setUnitIncrement(tab.getHeight()/50);
		if(componentProvider==null)componentProvider=t->{
			HButton b=new HButton(){
				public void paint(Graphics g){
					int c=50-scale*4;
					if(getModel().isPressed())c-=25;
					g.setColor(new Color(c,c,c));
					g.fillRect(0,0,getWidth(),getHeight());
					g.setColor(Color.WHITE);
					FontMetrics fm=g.getFontMetrics();
					g.drawString(t.name,(getWidth()-fm.stringWidth(t.name))/2,(getHeight()+fm.getAscent()+fm.getLeading()-fm.getDescent())/2);
					group.elementIcon.paintIcon(this,g,getWidth()*9/10-group.elementIcon.getIconWidth(),(getHeight()-group.elementIcon.getIconHeight())/2);
				}
			};
			b.addActionListener(e->ProgramStarter.editor.constructEditor(t,false));
			return b;
		};
		for(T t:group){
			panel.add(componentProvider.apply(t));
		}
		HButton add=new HButton(){
			public void paint(Graphics g){
				int c=50-scale*4;
				if(getModel().isPressed())c-=25;
				g.setColor(new Color(c,c,c));
				g.fillRect(0,0,getWidth(),getHeight());
				group.addIcon.paintIcon(this,g,(getWidth()-group.addIcon.getIconWidth())/2,(getHeight()-group.addIcon.getIconHeight())/2);
			}
		};
		add.addActionListener(e->{
			T t=createObject();
			ProgramStarter.editor.constructEditor(t,true);
			panel.add(componentProvider.apply(t),panel.getComponentCount()-1);
			panel.setPreferredSize(new Dimension(tab.getWidth(),tab.getHeight()*panel.getComponentCount()/10));
			panel.setLayout(new GridLayout(Math.max(10,group.size()+1),1));
			panel.revalidate();
		});
		panel.add(add);
		panel.setPreferredSize(new Dimension(tab.getWidth(),tab.getHeight()*panel.getComponentCount()/10));
		tab.add(s);
	}
	public String toString(){return name;}
}
