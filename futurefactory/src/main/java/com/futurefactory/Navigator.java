package com.futurefactory;

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.AWTEventListener;
import java.awt.event.KeyEvent;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class Navigator extends JDialog{
	public Navigator(Window w){
		setSize(Root.SCREEN_SIZE);
		setUndecorated(true);
		setLayout(null);
		JPanel p=new JPanel(new GridLayout(0,1));
		p.setPreferredSize(new Dimension(getWidth(),getHeight()*HelpView.paths.size()/10));
		JScrollPane s=new JScrollPane(p,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		s.setSize(getWidth(),Math.min(p.getPreferredSize().height,getHeight()*9/10));
		Font font=new Font(Font.DIALOG,Font.ITALIC,getHeight()/30);
		class L extends HButton{
			public L(String path){
				super(10,5);
				addActionListener(e->HelpView.show(path));
				setFont(font);
				setText(path);
			}
			@Override
			public void paint(Graphics g){
				int c=25+scale*3;
				g.setColor(new Color(c,c,c));
				g.fillRect(0, 0, getWidth(), getHeight());
				g.setColor(Color.WHITE);
				FontMetrics fm=g.getFontMetrics();
				g.drawString(getText(),(getWidth()-fm.stringWidth(getText()))/2,(getHeight()+fm.getAscent()+fm.getLeading()-fm.getDescent())/2);
			}
		}
		for(String path:HelpView.paths.keySet())p.add(new L(path));
		HButton ok=new HButton(){
			public void paint(Graphics g){
				int c=100-scale*5;
				if(getModel().isPressed())c-=50;
				g.setColor(new Color(c,c,c));
				g.fillRoundRect(0,0,getWidth(),getHeight(),getHeight(),getHeight());
				FontMetrics fm=g.getFontMetrics();
				g.setColor(getModel().isPressed()?Color.GREEN:new Color(0,100,0));
				g.drawString("Готово",(getWidth()-fm.stringWidth("Готово"))/2,(getHeight()+fm.getAscent()+fm.getLeading()-fm.getDescent())/2);
			}
		};
		ok.addActionListener(e->dispose());
		ok.setBounds(getWidth()/3,getHeight()*9/10,getWidth()/3,getHeight()/20);
		ok.setOpaque(false);
		ok.setFont(new Font(Font.DIALOG,Font.PLAIN,getHeight()/30));
		add(ok);
		add(s);
		setVisible(true);
	}
	public static void init(){
		Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener(){
			public void eventDispatched(AWTEvent e){
				if(e.getID()==KeyEvent.KEY_RELEASED&&(((KeyEvent)e).getModifiersEx()&KeyEvent.ALT_DOWN_MASK)!=0&&((KeyEvent)e).getKeyCode()==KeyEvent.VK_H)new Navigator(Window.getWindows()[0]);
			}
		},AWTEvent.KEY_EVENT_MASK);
	}
}
