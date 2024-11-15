package com.futurefactory;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.MultipleGradientPaint.CycleMethod;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeSet;

import javax.swing.JButton;

/**
 * Represents all the data (except the user-data).
 */
public class Data implements Serializable{
	private static Data instance;
	public static class EditableGroup extends ArrayList<Editable>{
		public JButton createElementButton(Editable e,Font font){
			Color c1=new Color(42,46,30),c2=new Color(32,36,21);
			return new HButton(){
				public void paintComponent(Graphics g){
					Graphics2D g2=(Graphics2D)g;
					g2.setColor(Color.GRAY);
					g2.fillRect(0,0,getWidth(),getHeight());
					g2.setFont(font);
					FontMetrics fm=g2.getFontMetrics();
					g2.setPaint(new GradientPaint(0,0,c1,0,getHeight(),c2));
					g2.drawString(e.name,getWidth()/100,(getHeight()+fm.getAscent()+fm.getLeading()-fm.getDescent())/2);
					elementIcon.paintIcon(this,g2,getWidth()-elementIcon.getIconWidth(),0);
					g2.setColor(new Color(0,0,0,scale*10));
					g2.fillRect(0,0,getWidth(),getHeight());
					if(true){//TODO: understand this code
						g2.setPaint(new LinearGradientPaint(getWidth()-getHeight()/2,getHeight()/4,getWidth(),getHeight()*3/4,new float[]{0,0.5f},new Color[]{new Color(89+scale*5,87,63),new Color(122+scale*5,119,80)},CycleMethod.REFLECT));
						g2.fillOval(getWidth()-getHeight()*9/8,getHeight()/8+1,getHeight()*3/4-1,getHeight()*3/4-1);
						g2.setPaint(new LinearGradientPaint(getWidth()-getHeight()/2,getHeight()/4,getWidth(),getHeight()*3/4,new float[]{0,0.5f},new Color[]{new Color(135+scale*5,52,14),new Color(194+scale*5,47,17)},CycleMethod.REFLECT));
						g2.fillOval(getWidth()-getHeight(),getHeight()/4,getHeight()/2,getHeight()/2);
						g2.setPaint(new LinearGradientPaint(getWidth()-getHeight()/2,getHeight()/4,getWidth(),getHeight()*3/4,new float[]{0,0.5f},new Color[]{new Color(54,51,36),new Color(77,74,56)},CycleMethod.REFLECT));
						g2.drawOval(getWidth()-getHeight(),getHeight()/4,getHeight()/2,getHeight()/2);
						g2.drawOval(getWidth()-getHeight()*9/8,getHeight()/8+1,getHeight()*3/4-1,getHeight()*3/4-1);
					}
				}
			};
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
		public EditableGroup(PathIcon elementIcon,PathIcon addIcon,Editable...elements){
			this.elementIcon=elementIcon;
			this.addIcon=addIcon;
			addAll(Arrays.asList(elements));
		}
	}
	public static abstract class Editable{
		public static class ActionRecord{
			public String text;
			public User source;
			public LocalDateTime time;
			public ActionRecord(String text,User source){
				this.text=text;this.source=source;
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
	public TreeSet<EditableGroup>editables=new TreeSet<EditableGroup>();
	private Data(){}
	public static Data getInstance(){
		if(instance==null)try{
			FileInputStream fIS=new FileInputStream(Root.folder+"Data.ser");
			ObjectInputStream oIS=new ObjectInputStream(fIS);
			instance=(Data)oIS.readObject();
			oIS.close();fIS.close();
		}catch(IOException ex){
			instance=new Data();
		}catch(ClassNotFoundException ex){throw new RuntimeException("FATAL ERROR: Data corrupted");}
		return instance;
	}
	static void save(){
		try{
			FileOutputStream fOS=new FileOutputStream(Root.folder+"Data.ser");
			ObjectOutputStream oOS=new ObjectOutputStream(fOS);
			oOS.writeObject(instance);
			oOS.close();fOS.close();
		}catch(IOException ex){ex.printStackTrace();}
	}
}