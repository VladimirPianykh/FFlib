package com.futurefactory;

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.AWTEventListener;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import com.futurefactory.core.Root;

public class Message extends JDialog{
	public Message(String text,Color c){
		super(Window.getWindows()[Window.getWindows().length-1],ModalityType.APPLICATION_MODAL);
		Dimension d=Root.SCREEN_SIZE;
		setBounds(d.width/4,d.height/10,d.width/2,d.width/7);
		setContentPane(new JPanel(new GridLayout()){
			public void paintComponent(Graphics g){
				g.setColor(Color.DARK_GRAY);
				g.fillRect(0,0,getWidth(),getHeight());
				g.setColor(Color.BLACK);
				g.drawRect(0,0,getWidth(),getHeight());
			}
		});
		JTextArea l=new JTextArea(text);
		l.setLineWrap(true);
		l.setWrapStyleWord(true);
		l.setFont(new Font(Font.DIALOG,Font.PLAIN,getHeight()/5));
		l.setBackground(Color.DARK_GRAY);
		l.setForeground(c);
		add(l);
		setVisible(true);
		Wrapper<AWTEventListener>s=new Wrapper<AWTEventListener>(null);
		s.var=e->{
			if(e.getID()==501){
				dispose();
				Toolkit.getDefaultToolkit().removeAWTEventListener(s.var);
			}
		};
		Toolkit.getDefaultToolkit().addAWTEventListener(s.var,AWTEvent.MOUSE_EVENT_MASK);
	}
}