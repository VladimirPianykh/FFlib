package com.futurefactory.core;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.LinearGradientPaint;
import java.awt.RadialGradientPaint;
import java.awt.MultipleGradientPaint.CycleMethod;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;
import java.util.HashMap;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.futurefactory.HButton;
import com.futurefactory.core.User.Feature;
import com.futurefactory.core.User.Role;
import com.futurefactory.defaults.DefaultRole;

public class WorkFrame extends JFrame{
	public static class WorkTabButton extends HButton{
		private BufferedImage image;
		public WorkTabButton(Feature feature,JPanel content,int index){
			super(15,3);
			int h=content.getHeight()/3;
			image=new BufferedImage(h,h,6);
			Graphics2D g2=image.createGraphics();
			g2.setPaint(new GradientPaint(0,0,new Color(13,16,31),h,h,new Color(11,18,31)));
			feature.paint(g2,image,h);
			g2.dispose();
			JPanel tab=new JPanel(null){
				float[]fr={0,1};
				Color[]c1={new Color(20,20,20),Color.BLACK};
				public void paintComponent(Graphics g){
					Graphics2D g2=(Graphics2D)g;
					g2.setPaint(new RadialGradientPaint(0,0,getWidth(),fr,c1));
					g2.fillRect(0,0,getWidth(),getHeight());
				}
			};
			Font font=new Font(Font.DIALOG,Font.PLAIN,content.getHeight()/20);
			addComponentListener(new ComponentListener(){
				public void componentMoved(ComponentEvent e){}
				public void componentResized(ComponentEvent e){
					setFont(new Font(Font.DIALOG,Font.PLAIN,getHeight()/8));
				}
				public void componentShown(ComponentEvent e){}
				public void componentHidden(ComponentEvent e){}
			});
			setText(feature.toString());
			addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					content.removeAll();
					tab.removeAll();
					tab.setSize(content.getSize());
					feature.fillTab(content,tab,font);
					content.add(tab);
					content.revalidate();
					content.repaint();
				}
			});
		}
		public void paintComponent(Graphics g){
			Graphics2D g2=(Graphics2D)g;
			g2.setPaint(new LinearGradientPaint(0,0,getWidth()/2,getHeight()/2,new float[]{0,1},new Color[]{new Color(50-scale*2,50-scale,50-scale*2),Color.GRAY},CycleMethod.REFLECT));
			g2.fillRect(0,0,getWidth(),getHeight());
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.45f));
			g2.drawImage(image,0,0,this);
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
			FontMetrics fm=g2.getFontMetrics();
			g2.setColor(Color.BLACK);
			String[]t=getText().split(" ");
			for(int i=0;i<t.length;++i)g2.drawString(t[i],(getWidth()-fm.stringWidth(t[i]))/2,getHeight()*9/10-fm.getHeight()*(t.length-(i+1)));
		}
		public static JPanel createTable(int rows,int cols,JPanel tab,boolean dark){
			JPanel p=new JPanel(new GridLayout(rows,2));
			p.setPreferredSize(new Dimension(tab.getWidth(),tab.getHeight()*rows/7));
			p.setSize(p.getPreferredSize());
			if(dark)p.setBackground(Color.DARK_GRAY);
			JScrollPane s=new JScrollPane(p,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			s.setSize(tab.getWidth(),Math.min(p.getHeight(),tab.getHeight()*3/4));
			s.getVerticalScrollBar().setUnitIncrement(tab.getHeight()/50);
			if(dark){
				s.getVerticalScrollBar().setBackground(Color.DARK_GRAY);
				s.getVerticalScrollBar().setForeground(Color.GRAY);
			}
			s.getVerticalScrollBar().setBorder(null);
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
		setContentPane(new JPanel(null));
		JPanel content=new JPanel(new GridLayout(1,1));
		content.setBounds(0,getHeight()/4,getWidth(),getHeight()*3/4);
		content.setOpaque(false);
		add(content);
		JPanel sidebar=new JPanel(null);
		sidebar.setBackground(new Color(10,15,10));
		JScrollPane s=new JScrollPane(sidebar,JScrollPane.VERTICAL_SCROLLBAR_NEVER,JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		s.setBounds(0,0,getWidth(),getHeight()/4);
		s.setOpaque(false);
		s.getHorizontalScrollBar().setBorder(null);
		s.getHorizontalScrollBar().setBackground(Color.DARK_GRAY);
		HButton exit=new HButton(5,5){
			public void paintComponent(Graphics g){
				Graphics2D g2=(Graphics2D)g;
				g2.setColor(new Color(scale*30+15,15,15));
				g2.fillRect(0,0,getWidth(),getHeight());
				g2.setStroke(new BasicStroke(getHeight()/10));
				g2.setColor(Color.WHITE);
				int h=scale*getHeight()/250;
				g2.drawLine(getWidth()/4+h,getHeight()/4+h,getWidth()*3/4-h,getHeight()*3/4-h);
				g2.drawLine(getWidth()*3/4-h,getHeight()/4+h,getWidth()/4+h,getHeight()*3/4-h);
			}
		};
		exit.addActionListener(e->{Data.save();System.exit(0);});
		exit.setBounds(0,0,getHeight()/4,getHeight()/4);
		exit.setForeground(new Color(54,23,13));
		exit.setBorder(null);
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