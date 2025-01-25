package com.bpa4j.ui;

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.AWTEventListener;
import java.awt.event.WindowAdapter;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import com.bpa4j.core.Root;

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
		l.setEditable(false);
		l.setFocusable(false);
		l.setLineWrap(true);
		l.setWrapStyleWord(true);
		l.setFont(new Font(Font.DIALOG,Font.PLAIN,getHeight()/5));
		l.setBackground(Color.DARK_GRAY);
		l.setForeground(c);
		add(l);
		setVisible(true);
		AWTEventListener s=e->{if(e.getID()==501)dispose();};
		Toolkit.getDefaultToolkit().addAWTEventListener(s,AWTEvent.MOUSE_EVENT_MASK);
		addWindowListener(new WindowAdapter(){
			public void windowClosed(java.awt.event.WindowEvent e){Toolkit.getDefaultToolkit().removeAWTEventListener(s);}
		});
	}
}