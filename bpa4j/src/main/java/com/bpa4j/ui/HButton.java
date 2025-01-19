package com.bpa4j.ui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.JButton;
import javax.swing.SwingUtilities;

public class HButton extends JButton{
	public int scale,maxScale,reaction;
	public boolean pressed;
	private final ReentrantLock lock=new ReentrantLock(true);
	private final MouseListener m=new MouseAdapter(){
		public void mousePressed(MouseEvent e){if(SwingUtilities.isLeftMouseButton(e)){pressed=true;repaint();}}
		public void mouseReleased(MouseEvent e){if(SwingUtilities.isLeftMouseButton(e)){pressed=false;repaint();}}
		public void mouseEntered(MouseEvent e){
			if(isEnabled()&&!lock.hasQueuedThreads())
			new Thread(){
				public void run(){
					lock.lock();
					try{
						while(scale<maxScale){
							Thread.sleep(reaction);
							scale++;
							repaint();
						}
					}catch(InterruptedException ex){}
					lock.unlock();
				}
			}.start();
		}
		public void mouseExited(MouseEvent e){resetScale();}
	};
	public HButton(){
		this(5,10);
	}
	public HButton(int maxScale,int reaction){
		this.maxScale=maxScale;
		this.reaction=reaction;
		setOpaque(false);
		setBorder(null);
		addMouseListener(m);
	}
	public void setEnabled(boolean b){super.setEnabled(b);if(!b)resetScale();}
	public void resetScale(){
		new Thread(){
			public void run(){
				lock.lock();
				try{
					while(scale>0){
						Thread.sleep(reaction);
						scale--;
						repaint();
					}
				}catch(InterruptedException ex){}
				lock.unlock();
			}
		}.start();
	}
}