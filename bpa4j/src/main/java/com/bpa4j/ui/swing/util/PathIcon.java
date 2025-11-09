package com.bpa4j.ui.swing.util;

import java.awt.Component;
import java.awt.Graphics;
import java.io.Serializable;

import javax.swing.Icon;

import com.bpa4j.core.Root;

public class PathIcon implements Icon,Serializable{
	public String path;
	public int w,h;
	public PathIcon(String path,int w,int h){this.path=path;this.w=w;this.h=h;}
	public PathIcon(String path){this.path=path;}
	public void paintIcon(Component c,Graphics g,int x,int y){
		try{
			if(w==0){int m=Math.min(c.getWidth(),c.getHeight());w=m;h=m;}
			g.drawImage(Root.loadIcon(path,w,h).getImage(),x,y,c);
		}catch(NullPointerException ex){throw new RuntimeException("Problems with icon \""+path+"\"");}
	}
	public int getIconWidth(){return w;}
	public int getIconHeight(){return h;}
}