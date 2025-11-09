package com.bpa4j.core;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JButton;

import com.bpa4j.ui.swing.util.HButton;
import com.bpa4j.ui.swing.util.PathIcon;

/**
 * An array with icons (icons are optional).
 * To change display buttons in the model editing tab, redefine {@code createElementButton} and {@code createAddButton}.
 */
public class EditableGroup<T extends Editable>extends ArrayList<T>{
	public Class<T>type;
	/**
	 * Creates a button for editing element.
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
	/**
	 * Makes this group not display in the list.
	 * @return this group
	 */
	public EditableGroup<T>hide(){
		invisible=true;
		return this;
	}
	@SuppressWarnings("unchecked")
	public boolean add(Editable e){
		if(e.getClass()!=type)throw new RuntimeException(e.getClass()+" cannot be added to the group of type "+type);
		return super.add((T)e);
	}
	@SuppressWarnings("rawtypes")
	public boolean equals(Object o){return o instanceof EditableGroup&&((EditableGroup)o).type.equals(type);}
}