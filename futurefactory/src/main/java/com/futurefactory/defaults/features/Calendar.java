// package com.futurefactory.defaults.features;

// import java.awt.BasicStroke;
// import java.awt.Color;
// import java.awt.Font;
// import java.awt.Graphics;
// import java.awt.Graphics2D;
// import java.awt.GridLayout;
// import java.awt.image.BufferedImage;
// import java.time.YearMonth;
// import java.time.temporal.ChronoUnit;
// import java.util.HashMap;

// import javax.swing.JPanel;

// import com.futurefactory.Data;
// import com.futurefactory.Data.Task;
// import com.futurefactory.HButton;
// import com.futurefactory.PathIcon;
// import com.futurefactory.Wrapper;
// import com.futurefactory.User.Feature;

// public class Calendar implements Feature{
// 	static{
// 		if(!Data.getInstance().ftrInstances.containsKey(Calendar.class.getName()))Data.getInstance().ftrInstances.put(Calendar.class.getName(),new HashMap<>());
// 	}
// 	public static<T extends Task>Calendar<T>getBoard(String name){
// 		if(((HashMap<String,Calendar<?>>)Data.getInstance().ftrInstances.get(Calendar.class.getName())).containsKey(name))return (Calendar<T>)((HashMap<String,Calendar<?>>)Data.getInstance().ftrInstances.get(Calendar.class.getName())).get(name);
// 		else throw new IllegalArgumentException("Board \""+name+"\" does not exist.");
// 	}
// 	public static<T extends Task>Calendar<T>registerBoard(String name,Class<T>type){
// 		if(((HashMap<String,Calendar<?>>)Data.getInstance().ftrInstances.get(Calendar.class.getName())).containsKey(name)&&((HashMap<String,Calendar<?>>)Data.getInstance().ftrInstances.get(Calendar.class.getName())).get(name).type.equals(type))return(Calendar<T>)((HashMap<String,Calendar<?>>)Data.getInstance().ftrInstances.get(Calendar.class.getName())).get(name);
// 		Calendar<T>b=new Calendar<T>(name,type);
// 		((HashMap<String,Calendar<?>>)Data.getInstance().ftrInstances.get(Calendar.class.getName())).put(name,b);
// 		return b;
// 	}
// 	private void fillForMonth(JPanel panel,YearMonth m){
// 		panel.removeAll();
// 		for(int i=1;i<=m.lengthOfMonth();++i){
			
// 		}
// 	}
// 	public void paint(Graphics2D g2,BufferedImage image,int h){g2.setStroke(new BasicStroke(h/20));g2.drawRoundRect(h/20,h/5,h*9/10,h*4/5,h,h);}
// 	public void fillTab(JPanel content,JPanel tab,Font font){
// 		JPanel panel=new JPanel();
// 		panel.setLayout(new GridLayout(0,7));
// 		Wrapper<YearMonth>w=new Wrapper<>(YearMonth.now());
// 		HButton left=new HButton(){
// 			private PathIcon icon=new PathIcon("ui/left.png");
// 			public void paint(Graphics g){
// 				int c=50-scale*4;
// 				if(getModel().isPressed())c-=25;
// 				g.setColor(new Color(c,c,c));
// 				g.fillRect(0,0,getWidth(),getHeight());
// 				icon.paintIcon(this,g,0,(getHeight()-icon.getIconHeight())/2);
// 			}
// 		},right=new HButton(){
// 			private PathIcon icon=new PathIcon("ui/right.png");
// 			public void paint(Graphics g){
// 				int c=50-scale*4;
// 				if(getModel().isPressed())c-=25;
// 				g.setColor(new Color(c,c,c));
// 				g.fillRect(0,0,getWidth(),getHeight());
// 				icon.paintIcon(this,g,0,(getHeight()-icon.getIconHeight())/2);
// 			}
// 		};
// 		left.addActionListener(e->{
// 			w.var=w.var.minus(1,ChronoUnit.MONTHS);
// 			fillForMonth(panel,w.var);
// 		});
// 		right.addActionListener(e->{
// 			w.var=w.var.plus(1,ChronoUnit.MONTHS);
// 			fillForMonth(panel,w.var);
// 		});
// 		left.setSize(tab.getWidth()/10,tab.getHeight());
// 		panel.setBounds(tab.getWidth()/10,0,tab.getWidth()*4/5,tab.getHeight());
// 		right.setBounds(tab.getWidth()*9/10,0,tab.getWidth()/10,tab.getHeight());
// 		tab.add(left);
// 		tab.add(panel);
// 		tab.add(right);
// 		fillForMonth(panel,w.var);
// 	}
// }
