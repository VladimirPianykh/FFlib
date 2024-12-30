package com.bpa4j;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RadialGradientPaint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;
import java.util.concurrent.locks.ReentrantLock;

public class Switcher extends HButton{
	private Color c1=new Color(41,59,17),c2=new Color(54,82,17),c3=new Color(59,33,17),c4=new Color(82,38,17);
	private static final Color[]c5={Color.DARK_GRAY,new Color(40,40,40)};
	private static final float[]f={0,1};
	private int slide;
	public boolean on;
	private final ActionListener a=new ActionListener(){
		public void actionPerformed(ActionEvent e){
			if(!lock.hasQueuedThreads())new Thread(){
				public void run(){
					lock.lock();
					try{
					if(on){
						on=false;
						while(slide>0){
							slide=Math.max(0,slide-getWidth()*4/140);
							repaint();
							Thread.sleep(2);
						}
					}else{
						on=true;
						int m=getWidth()*4/7;
						while(slide<m){
							slide=Math.min(m,slide+getWidth()*4/140);
							repaint();
							Thread.sleep(2);
						}
					}}catch(InterruptedException ex){}
					lock.unlock();
				}
			}.start();
		}
	};
	private final ReentrantLock lock=new ReentrantLock();
	public Switcher(){
		addActionListener(a);
	}
	public void paint(Graphics g){
		Graphics2D g2=(Graphics2D)g;
		g2.setColor(Color.LIGHT_GRAY);
		g2.fillRoundRect(0,0,getWidth(),getHeight(),getHeight()/3,getHeight()/3);
		g2.setClip(new RoundRectangle2D.Double(slide,0,getWidth()*3/7,getHeight(),getHeight()/3,getHeight()/3));
		g2.setColor(Color.GRAY);
		g2.fillRect(0,0,getWidth(),getHeight());
		g2.setPaint(new GradientPaint(0,getHeight()/3,c4,getWidth()*3/7,getHeight()*2/3,c3));
		g2.fillRect(0,0,getWidth()*3/7,getHeight());
		g2.setPaint(new GradientPaint(getWidth()*4/7,getHeight()*2/3,c2,getWidth(),getHeight()/3,c1));
		g2.fillRect(getWidth()*4/7,0,getWidth()*3/7,getHeight());
		g2.setClip(null);
		g2.setPaint(new RadialGradientPaint(getWidth()/3,0,getWidth()/2,f,c5));
		g2.setStroke(new BasicStroke(getHeight()/30));
		g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,getHeight()/3,getHeight()/3);
	}
}