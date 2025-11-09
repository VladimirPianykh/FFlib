package com.bpa4j.ui.swing.util;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.LayoutManager;

public class AutoLayout implements LayoutManager{
	public void addLayoutComponent(String name,Component comp){}
	public void removeLayoutComponent(Component comp){}
	public Dimension preferredLayoutSize(Container parent){return parent.getSize();}
	public Dimension minimumLayoutSize(Container parent){return parent.getSize();}
	public void layoutContainer(Container parent){
		int s=parent.getComponentCount();
		if(s==0)return;
		int r=(int)Math.sqrt(s);
		boolean vert=parent.getHeight()>parent.getWidth();
		if(vert)switch(s){
			case 2->{
				parent.getComponent(0).setBounds(0,0,parent.getWidth(),parent.getHeight()/2);
				parent.getComponent(1).setBounds(0,parent.getHeight()/2,parent.getWidth(),parent.getHeight()/2);
			}
			case 3->{
				parent.getComponent(0).setBounds(0,0,parent.getWidth()/2,parent.getHeight()/2);
				parent.getComponent(1).setBounds(0,parent.getHeight()/2,parent.getWidth()/2,parent.getHeight()/2);
				parent.getComponent(2).setBounds(parent.getWidth()/2,0,parent.getWidth()/2,parent.getHeight());
			}
			case 5->{
				parent.getComponent(0).setBounds(0,0,parent.getWidth()/2,parent.getHeight()/3);
				parent.getComponent(1).setBounds(parent.getWidth()/2,0,parent.getWidth()/2,parent.getHeight()/3);
				parent.getComponent(2).setBounds(0,parent.getHeight()/3,parent.getWidth(),parent.getHeight()/3);
				parent.getComponent(3).setBounds(0,parent.getHeight()*2/3,parent.getWidth()/2,parent.getHeight()/3);
				parent.getComponent(4).setBounds(parent.getWidth()/2,parent.getHeight()*2/3,parent.getWidth()/2,parent.getHeight()/3);
			}
			default->new GridLayout(0,r).layoutContainer(parent);
		}else switch(s){
			case 2->{
				parent.getComponent(0).setBounds(0,0,parent.getWidth()/2,parent.getHeight());
				parent.getComponent(1).setBounds(parent.getWidth()/2,0,parent.getWidth()/2,parent.getHeight());
			}
			case 3->{
				parent.getComponent(0).setBounds(0,0,parent.getWidth()/2,parent.getHeight()/2);
				parent.getComponent(1).setBounds(parent.getWidth()/2,0,parent.getWidth()/2,parent.getHeight()/2);
				parent.getComponent(2).setBounds(0,parent.getHeight()/2,parent.getWidth(),parent.getHeight()/2);
			}
			// case 5->{
			// 	parent.getComponent(0).setBounds(0,0,parent.getWidth()/2,parent.getHeight()/3);
			// 	parent.getComponent(1).setBounds(parent.getWidth()/2,0,parent.getWidth()/2,parent.getHeight()/3);
			// 	parent.getComponent(2).setBounds(0,parent.getHeight()/3,parent.getWidth(),parent.getHeight()/3);
			// 	parent.getComponent(3).setBounds(0,parent.getHeight()*2/3,parent.getWidth()/2,parent.getHeight()/3);
			// 	parent.getComponent(4).setBounds(parent.getWidth()/2,parent.getHeight()*2/3,parent.getWidth()/2,parent.getHeight()/3);
			// }
			default->new GridLayout(r,0).layoutContainer(parent);
		}
	}
}
