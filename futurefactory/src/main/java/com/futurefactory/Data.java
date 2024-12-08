package com.futurefactory;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import javax.swing.JButton;

import com.futurefactory.defaults.features.TaskBoard;

/**
 * A singletone that represents all editable data.
 */
public class Data implements Serializable{
	private static Data instance;
	public static class EditableGroup<T extends Editable> extends ArrayList<T>{
		public Class<?>type;
		/**
		 * Creates a button for editing element
		 * @param e Editable to create button for
		 */
		public JButton createElementButton(Editable e,Font font){
			HButton b=new HButton(){
				public void paintComponent(Graphics g){
					Graphics2D g2=(Graphics2D)g;
					g2.setColor(Color.GRAY);
					g2.fillRect(0,0,getWidth(),getHeight());
					g2.setFont(font);
					FontMetrics fm=g2.getFontMetrics();
					g2.setPaint(new GradientPaint(0,0,getBackground(),0,getHeight(),getForeground()));
					g2.drawString(e.name,getWidth()/100,(getHeight()+fm.getAscent()+fm.getLeading()-fm.getDescent())/2);
					elementIcon.paintIcon(this,g2,getWidth()-elementIcon.getIconWidth(),0);
					g2.setColor(new Color(0,0,0,scale*10));
					g2.fillRect(0,0,getWidth(),getHeight());
					// if(true){
					// 	g2.setPaint(new LinearGradientPaint(getWidth()-getHeight()/2,getHeight()/4,getWidth(),getHeight()*3/4,new float[]{0,0.5f},new Color[]{new Color(89+scale*5,87,63),new Color(122+scale*5,119,80)},CycleMethod.REFLECT));
					// 	g2.fillOval(getWidth()-getHeight()*9/8,getHeight()/8+1,getHeight()*3/4-1,getHeight()*3/4-1);
					// 	g2.setPaint(new LinearGradientPaint(getWidth()-getHeight()/2,getHeight()/4,getWidth(),getHeight()*3/4,new float[]{0,0.5f},new Color[]{new Color(135+scale*5,52,14),new Color(194+scale*5,47,17)},CycleMethod.REFLECT));
					// 	g2.fillOval(getWidth()-getHeight(),getHeight()/4,getHeight()/2,getHeight()/2);
					// 	g2.setPaint(new LinearGradientPaint(getWidth()-getHeight()/2,getHeight()/4,getWidth(),getHeight()*3/4,new float[]{0,0.5f},new Color[]{new Color(54,51,36),new Color(77,74,56)},CycleMethod.REFLECT));
					// 	g2.drawOval(getWidth()-getHeight(),getHeight()/4,getHeight()/2,getHeight()/2);
					// 	g2.drawOval(getWidth()-getHeight()*9/8,getHeight()/8+1,getHeight()*3/4-1,getHeight()*3/4-1);
					// }
				}
			};
			b.setBackground(new Color(42,46,30));
			b.setForeground(new Color(32,36,21));
			return b;
		}
		public JButton createAddButton(Font font){
			return new HButton(){
				public void paintComponent(Graphics g){
					g.setColor(Color.GRAY);
					g.fillRect(0,0,getWidth(),getHeight());
					addIcon.paintIcon(this,g,(getWidth()-addIcon.getIconWidth())/2,0);
					g.setColor(new Color(0,0,0,scale*10));
					g.fillRect(0,0,getWidth(),getHeight());
				}
			};
		}
		public PathIcon elementIcon,addIcon;
		public boolean invisible;
		@SafeVarargs
		public EditableGroup(PathIcon elementIcon,PathIcon addIcon,Class<T>type,T...elements){
			this.elementIcon=elementIcon;
			this.addIcon=addIcon;
			this.type=type;
			addAll(Arrays.asList(elements));
		}
		@SafeVarargs
		public EditableGroup(Class<T>type,T...elements){
			this(null,null,type,elements);
			this.invisible=true;
		}
		@SuppressWarnings("unchecked")
		public boolean add(Editable e){return super.add((T)e);}
		@SuppressWarnings("rawtypes")
		public boolean equals(Object o){return o instanceof EditableGroup&&((EditableGroup)o).type.equals(type);}
	}
	public static abstract class Editable implements Serializable{
		public static class ActionRecord implements Serializable{
			public String text;
			public User source;
			public LocalDateTime time;
			public ActionRecord(String text,User source){
				this.text=text;
				this.source=source;
				time=LocalDateTime.now();
			}
		}
		public String name;
		public ArrayList<ActionRecord>records=new ArrayList<ActionRecord>();
		public Editable(String name){
			this.name=name;
			records.add(new ActionRecord(":CREATED",User.getActiveUser()));
		}
	}
	/**
	 * An interface for creating {@link com.futurefactory.defaults.features.TaskBoard TaskBoards}
	 */
	public static abstract class Task implements Serializable{
		
	}
	public HashSet<EditableGroup<?>>editables=new HashSet<EditableGroup<?>>();
	public HashMap<String,TaskBoard<?>>boards=new HashMap<>();
	public static Data getInstance(){
		if(instance==null)try{
			FileInputStream fIS=new FileInputStream(Root.folder+"Data.ser");
			ObjectInputStream oIS=new ObjectInputStream(fIS);
			instance=(Data)oIS.readObject();
			oIS.close();
		}catch(FileNotFoundException ex){instance=new Data();}
		catch(IOException ex){throw new RuntimeException(ex);}
		catch(ClassNotFoundException ex){throw new RuntimeException("FATAL ERROR: Data corrupted");}
		return instance;
	}
	/**
	 * Saves the Data into a file.
	 */
	public static void save(){
		try{
			FileOutputStream fOS=new FileOutputStream(Root.folder+"Data.ser");
			ObjectOutputStream oOS=new ObjectOutputStream(fOS);
			oOS.writeObject(instance);
			oOS.close();fOS.close();
		}catch(IOException ex){throw new RuntimeException(ex);}
	}
	/**
	 * Tries to aqquire an {@link EditableGroup}.
	 * @return a group with the given type, if any.
	 * @throws IllegalArgumentException if there is no group of such type.
	 */
	public EditableGroup<?>getGroup(Class<? extends Editable>type){
		for(EditableGroup<?>group:editables)if(group.type.equals(type))return group;
		throw new IllegalArgumentException("There is no group of type "+type.getName());
	}
	private Data(){}
}