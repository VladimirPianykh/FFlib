package com.futurefactory;

import java.awt.Component;
import java.awt.Graphics;
import java.io.Serializable;

import javax.swing.Icon;

public class PathIcon implements Icon,Serializable{
	public String path;
	public int w,h;
	public PathIcon(String path,int w,int h){this.path=path;this.w=w;this.h=h;}
	public PathIcon(String path){this.path=path;}
	public void paintIcon(Component c,Graphics g,int x,int y){
		if(w==0){w=c.getWidth();h=c.getHeight();}
		g.drawImage(Root.loadIcon(path,w,h).getImage(),x,y,c);
	}
	public int getIconWidth(){return w;}
	public int getIconHeight(){return h;}
}