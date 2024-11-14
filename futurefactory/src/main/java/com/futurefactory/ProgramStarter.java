package com.futurefactory;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RadialGradientPaint;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ToolTipManager;

/**
 * Entry point of the program.
 * To start your application, just initialize {@code Editor}, and invoke {@code runProgram()}.
 */
public class ProgramStarter{
	public static WorkFrame frame;
	public static IEditor editor;
	public static void runProgram(){
		new File(Root.folder).mkdirs();
		ToolTipManager.sharedInstance().setInitialDelay(0);
		Dimension d=Root.SCREEN_SIZE;
		Color[]c1={new Color(114,130,46),new Color(79,79,29)},c2={new Color(102,102,77),new Color(69,63,48)};
		Font font=new Font(Font.DIALOG,Font.PLAIN,d.height/35);
		JFrame f=new JFrame();
		f.setContentPane(new JPanel(){
			public void paintComponent(Graphics g){
				Graphics2D g2=(Graphics2D)g;
				g2.setPaint(new RadialGradientPaint(getWidth(),0,getWidth(),new float[]{0,1},c1));
				g2.fillRect(0,0,getWidth(),getHeight());
				Stroke s=g2.getStroke();
				g2.setStroke(new BasicStroke(getHeight()/100));
				g2.setPaint(new RadialGradientPaint(getWidth(),0,getWidth(),new float[]{0,1},c2));
				g2.drawRect(0,0,getWidth(),getHeight());
				g2.setStroke(s);
			}
		});
		f.setUndecorated(true);
		f.setSize(d);
		f.setExtendedState(3);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setLayout(null);
		Switcher reg=new Switcher();
		JLabel regText=new JLabel("Зарегистрировать нового пользователя");
		regText.setFont(font);
		FontMetrics fm=regText.getFontMetrics(font);
		reg.setBounds(d.width/2+d.width/60,d.height*2/3-d.height/60,d.width/25,d.height/30);
		regText.setBounds(d.width/2-(fm.stringWidth("Зарегистрировать нового пользователя")+d.width/30),d.height*2/3-d.height/60,fm.stringWidth("Зарегистрировать нового пользователя")+d.width/60,d.height/30);
		JTextField log=new JTextField(),pass=new JTextField();
		JLabel logLabel=new JLabel("Логин"),passLabel=new JLabel("Пароль");
		logLabel.setFont(font);passLabel.setFont(font);
		log.setBounds(d.width/2+d.width/60,d.height/2-(d.height/25+d.height/60),d.width/5,d.height/30);
		pass.setBounds(d.width/2+d.width/60,d.height/2-d.height/60,d.width/5,d.height/30);
		logLabel.setBounds(d.width/2-(fm.stringWidth("Логин")+d.width/30),d.height/2-(d.height/25+d.height/60),fm.stringWidth("Логин")+d.width/60,d.height/30);
		passLabel.setBounds(d.width/2-(fm.stringWidth("Пароль")+d.width/30),d.height/2-d.height/60,fm.stringWidth("Пароль")+d.width/60,d.height/30);
		AbstractAction action=new AbstractAction(){
			public void actionPerformed(ActionEvent e){
				if(log.getText().isBlank()||pass.getText().isBlank())return;
				if(reg.on){
					if(User.hasUser(log.getText()))new Message("Account already exists.");
					else{
						User.register(log.getText(),pass.getText());
						ProgramStarter.frame=new WorkFrame(User.getActiveUser());
						new Message("User registered.");
						f.dispose();
					}
				}else{
					if(User.hasUser(log.getText())){
						if(User.getUser(log.getText()).login(pass.getText())){
							ProgramStarter.frame=new WorkFrame(User.getActiveUser());
							new Message("Успешно зарегистрирован!");
							f.dispose();
						}else new Message("Неверный пароль.");
					}else new Message("Неизвестный пользователь.");
				}
			}
		};
		log.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){log.transferFocus();}
		});
		pass.setAction(action);
		HButton confirm=new HButton(){
			public void paintComponent(Graphics g){
				Graphics2D g2=(Graphics2D)g;
				g2.setPaint(new GradientPaint(getWidth()*3/4,getHeight()/4,new Color(74+scale,58+scale,21+scale),getWidth()*3/4+1,getHeight()/4+2,new Color(64+scale,50+scale,18+scale)));
				g2.fillRect(0,0,getWidth(),getHeight());
				g2.setColor(Color.BLACK);
				g2.setFont(getFont());
				FontMetrics tfm=g2.getFontMetrics();
				g2.drawString("ПОДТВЕРДИТЬ",(getWidth()-tfm.stringWidth("ПОДТВЕРДИТЬ"))/2,(getHeight()+tfm.getLeading()+tfm.getAscent()-tfm.getDescent())/2);
			}
		};
		confirm.setFont(new Font(Font.DIALOG_INPUT,Font.BOLD,font.getSize()*2));
		confirm.setBounds(d.width*2/5,d.height*9/10,d.width/5,d.height/10);
		confirm.setAction(action);
		f.add(reg);f.add(regText);
		f.add(log);f.add(logLabel);
		f.add(pass);f.add(passLabel);
		f.add(confirm);
		f.setVisible(true);
	}
}