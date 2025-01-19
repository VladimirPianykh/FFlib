package com.bpa4j.editor.modules;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JPanel;

import com.bpa4j.core.Root;
import com.bpa4j.core.Data.Editable;
import com.bpa4j.ui.HButton;

public class ImageDisplayModule implements EditorModule{
	private Field f;
	/**
	 * Constructs a new ImageDisplayModule.
	 * @param f - field to display
	 */
	public ImageDisplayModule(Field f){this.f=f;}
	public JPanel createTab(JDialog editor,Editable editable,boolean isNew,Runnable deleter){
		JPanel tab=new JPanel(null);
		tab.setBackground(Color.DARK_GRAY);
		tab.setSize(Root.SCREEN_SIZE);
		HButton c=new HButton(){
			public void paint(Graphics g){
				try{
					Graphics2D g2=(Graphics2D)g;
					g2.setClip(new RoundRectangle2D.Double(0,0,getWidth(),getHeight(),getHeight()/15,getHeight()/10));
					int c=100-scale*4;
					g2.setColor(new Color(c,c,c));
					g2.fillRect(0,0,getWidth(),getHeight());
					g2.setStroke(getIcon()==null?new BasicStroke(getHeight()/80,2,2,2,new float[]{getHeight()/40,getHeight()/40},getHeight()*scale/200):new BasicStroke(getHeight()/80));
					g2.setColor(Color.LIGHT_GRAY);
					g2.draw(g2.getClip());
					int s=getHeight()/10;
					if(getIcon()==null){
						g2.setStroke(new BasicStroke(getHeight()/80,1,1));
						g2.setColor(Color.LIGHT_GRAY);
						g2.drawLine(getWidth()/2-s,getHeight()/2,getWidth()/2+s,getHeight()/2);
						g2.drawLine(getWidth()/2,getHeight()/2-s,getWidth()/2,getHeight()/2+s);
					}else{
						Image image=((ImageIcon)getIcon()).getImage();
						MediaTracker m=new MediaTracker(this);
						image=image.getScaledInstance(getHeight(),getHeight(),0);
						m.addImage(image,0);
						m.waitForAll();
						g2.drawImage(image,0,0,this);
					}
				}catch(InterruptedException ex){throw new RuntimeException(ex);}
			}
		};
		try{
			if(f.get(editable)!=null)c.setIcon(new ImageIcon(((File)f.get(editable)).toURI().toURL()));
		}catch(IllegalAccessException|IOException ex){throw new RuntimeException(ex);}
		JFileChooser fc=new JFileChooser();
		c.addActionListener(e->{
			fc.showOpenDialog(tab);
			try{
				f.set(editable,fc.getSelectedFile());
				if(f.get(editable)!=null)c.setIcon(new ImageIcon(((File)f.get(editable)).toURI().toURL()));
			}catch(IllegalAccessException|IOException ex){throw new RuntimeException(ex);}
		});
		int s=tab.getHeight()*9/10;
		c.setBounds((tab.getWidth()-s)/2,(tab.getHeight()-s)/2,s,s);
		tab.add(c);
		return tab;
	}
}
