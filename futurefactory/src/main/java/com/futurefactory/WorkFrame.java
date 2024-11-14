package com.futurefactory;

import java.awt.BasicStroke;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.LinearGradientPaint;
import java.awt.RadialGradientPaint;
import java.awt.MultipleGradientPaint.CycleMethod;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.HashMap;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.futurefactory.User.Feature;
import com.futurefactory.User.Role;
import com.futurefactory.defaults.DefaultRole;

public class WorkFrame extends JFrame{
	public static class WorkTabButton extends HButton{
		private BufferedImage image;
		public WorkTabButton(Feature feature,JPanel content,int index){
			int h=content.getHeight()/3;
			image=new BufferedImage(h,h,6);
			Graphics2D g2=image.createGraphics();
			g2.setPaint(new GradientPaint(0,0,new Color(13,16,31,100),h,h,new Color(11,18,31,100)));
			feature.paint(g2,image,h);
			g2.dispose();
			JPanel tab=new JPanel(null);
			Font font=new Font(Font.DIALOG,Font.PLAIN,content.getHeight()/20);
			addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					if(tab.getComponentCount()>0){
						((CardLayout)content.getLayout()).show(content,"tab"+index);
						content.revalidate();
						return;
					}
					feature.fillTab(content,tab,font);
					((CardLayout)content.getLayout()).show(content,"tab"+index);
					content.revalidate();
				}	
			});
			content.add(tab,"tab"+index);
		}
		public void paintComponent(Graphics g){
			Graphics2D g2=(Graphics2D)g;
			g2.setPaint(new LinearGradientPaint(0,0,getWidth()/2,getHeight()/2,new float[]{0,1},new Color[]{new Color(50-scale,50-scale,50-scale),Color.GRAY},CycleMethod.REFLECT));
			g2.fillRect(0,0,getWidth(),getHeight());
			g2.drawImage(image,0,0,this);
		}
		public static JPanel createTable(int rows,int cols,JPanel tab){
			JPanel p=new JPanel(new GridLayout(rows,2));
			p.setPreferredSize(new Dimension(tab.getWidth(),tab.getHeight()*rows/7));
			p.setSize(p.getPreferredSize());
			JScrollPane s=new JScrollPane(p,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			s.setSize(tab.getWidth(),Math.min(p.getHeight(),tab.getHeight()));
			s.getVerticalScrollBar().setUnitIncrement(tab.getHeight()/50);
			tab.add(s);
			return p;
		}
	}
	public static HashMap<Role,Feature[]>ftrMap=new HashMap<Role,Feature[]>();
	static{
		ftrMap.put(DefaultRole.ADMIN,User.registeredFeatures.toArray(new Feature[0]));
		ftrMap.put(DefaultRole.EMPTY,new Feature[0]);
	}
	public WorkFrame(User user){
		setSize(Root.SCREEN_SIZE);
		setExtendedState(3);
		setUndecorated(true);
		setContentPane(new JPanel(null){
			float[]fr={0,1};
			Color[]c1={Color.GRAY,Color.DARK_GRAY};
			public void paintComponent(Graphics g){
				Graphics2D g2=(Graphics2D)g;
				g2.setPaint(new RadialGradientPaint(getWidth()/2,0,getHeight(),fr,c1));
				g2.fillRect(0,0,getWidth(),getHeight());
			}
		});
		CardLayout l=new CardLayout();
		JPanel content=new JPanel(l);
		content.setOpaque(false);
		content.setBounds(0,getHeight()/4,getWidth(),getHeight()*3/4);
		add(content);
		JPanel sidebar=new JPanel(null){
			public void paintComponent(Graphics g){
				Graphics2D g2=(Graphics2D)g;
				g2.setPaint(new GradientPaint(0,0,new Color(63,82,45),getHeight()/10,getHeight(),new Color(47,61,34)));
				g2.fillRect(0,0,getWidth(),getHeight());
			}
		};
		sidebar.setOpaque(false); 
		JScrollPane s=new JScrollPane(sidebar,JScrollPane.VERTICAL_SCROLLBAR_NEVER,JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		s.setOpaque(false);
		s.setBounds(0,0,getWidth(),getHeight()/4);
		HButton exit=new HButton(5,5){
			public void paintComponent(Graphics g){
				Graphics2D g2=(Graphics2D)g;
				g2.setColor(new Color(scale*30+15,15,15));
				g2.fillRect(0,0,getWidth(),getHeight());
				g2.setStroke(new BasicStroke(getHeight()/10));
				g2.setColor(Color.WHITE);
				int h=scale*getHeight()/300;
				g2.drawLine(getWidth()/4+h,getHeight()/4+h,getWidth()*3/4-h,getHeight()*3/4-h);
				g2.drawLine(getWidth()*3/4-h,getHeight()/4+h,getWidth()/4+h,getHeight()*3/4-h);
			}
		};
		exit.setForeground(new Color(54,23,13));
		exit.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				System.exit(0);
			}
		});
		exit.setBounds(0,0,getHeight()/4,getHeight()/4);
		add(exit);
		for(Feature f:ftrMap.get(user.role)){
			WorkTabButton t=new WorkTabButton(f,content,sidebar.getComponentCount());
			sidebar.add(t);
			t.setBounds(sidebar.getComponentCount()*getHeight()/4,0,getHeight()/4,getHeight()/4);
		}
		add(s);
		setVisible(true);
	}
}