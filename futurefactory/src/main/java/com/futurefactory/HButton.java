package com.futurefactory;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.SwingUtilities;

class HButton extends JButton{
	int scale,maxScale,reaction;
	boolean pressed;
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
	public ImageIcon toolTipIcon;
	public HButton(){
		this(5,10);
	}
	public HButton(int maxScale,int reaction){
		this.maxScale=maxScale;
		this.reaction=reaction;
		setOpaque(false);
		addMouseListener(m);
	}
	private void readObject(ObjectInputStream in)throws IOException,ClassNotFoundException{
		in.defaultReadObject();
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