package com.futurefactory;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;

class Message extends JWindow{
	public Message(String text){
		Dimension d=Root.SCREEN_SIZE;
		setBounds(d.width/3,d.height/10,d.width/3,d.width/9);
		addFocusListener(new FocusAdapter(){
			public void focusLost(FocusEvent e){dispose();}
		});
		setContentPane(new JPanel(){
			public void paintComponent(Graphics g){
				g.setColor(Color.DARK_GRAY);
				g.fillRect(0,0,getWidth(),getHeight());
				g.setColor(Color.BLACK);
				g.drawRect(0,0,getWidth(),getHeight());
			}
		});
		JLabel l=new JLabel(text);
		l.setFont(new Font(Font.DIALOG,Font.PLAIN,getHeight()/5));
		l.setForeground(Color.LIGHT_GRAY);
		add(l);
		setVisible(true);
		requestFocus();
	}
}