package com.futurefactory.defaults.features;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.futurefactory.Dater;
import com.futurefactory.HButton;
import com.futurefactory.PathIcon;
import com.futurefactory.Wrapper;
import com.futurefactory.core.User.Feature;

public class Calendar<T extends Calendar.Event>implements Feature{
	public static interface Event{}
	public static HashMap<String,Calendar<?>>calendars=new HashMap<>();
	private String name;
	private Class<T>type;
	private HashMap<LocalDate,List<T>>events=new HashMap<>();
	private Dater<List<T>>dater;
	private Consumer<HashMap<LocalDate,List<T>>>eventFiller;
	private Calendar(String name,Class<T>type){this.name=name;this.type=type;}
	@SuppressWarnings("unchecked")
	public static<T extends Event>Calendar<T>getCalendar(String name){
		if(calendars.containsKey(name))return(Calendar<T>)calendars.get(name);
		else throw new IllegalArgumentException("Calendar \""+name+"\" does not exist.");
	}
	@SuppressWarnings("unchecked")
	public static<T extends Event>Calendar<T>registerCalendar(String name,Class<T>type){
		if(calendars.containsKey(name)&&calendars.get(name).type.equals(type))return(Calendar<T>)calendars.get(name);
		Calendar<T>b=new Calendar<>(name,type);
		calendars.put(name,b);
		return b;
	}
	private void fillForMonth(JPanel panel,YearMonth m){
		panel.removeAll();
		for(int i=1;i<m.atDay(1).getDayOfWeek().getValue();++i)panel.add(new JComponent(){
			public void paint(Graphics g){
				g.setColor(Color.DARK_GRAY);
				g.fillRect(0,0,getWidth(),getHeight());
			}
		});
		for(int i=1;i<=m.lengthOfMonth();++i){
			LocalDate d=m.atDay(i);
			panel.add(dater.apply(getEventList(d),d));
		}
		for(int i=0;i<7-m.atEndOfMonth().getDayOfWeek().getValue();++i)panel.add(new JComponent(){
			public void paint(Graphics g){
				g.setColor(Color.DARK_GRAY);
				g.fillRect(0,0,getWidth(),getHeight());
			}
		});
	}
	/**
	 * <br>Sets the event filler for this calendar.</br>
	 * <br>Event filler is a consumer that fills the given {@link HashMap} with events.</br>
	 */
	public Calendar<T>setEventFiller(Consumer<HashMap<LocalDate,List<T>>>eventFiller){this.eventFiller=eventFiller;return this;}
	/**
	 * Sets the dater for this calendar.
	 * @apiNote Unlike {@link DatedList}, you don't have to transfer {@link java.util.function.Supplier Supplier}.
	 * A {@link Dater} is enough.
	 */
	public Calendar<T>setDater(Dater<List<T>>dater){this.dater=dater;return this;}
	public List<T>getEventList(LocalDate date){
		if(!events.containsKey(date))events.put(date,new ArrayList<T>());
		return events.get(date);
	}
	public void paint(Graphics2D g2,BufferedImage image,int h){
		g2.setStroke(new BasicStroke(h/20));
		g2.drawRoundRect(h/20,h/5,h*9/10,h*3/5,h/10,h/10);
		g2.setClip(new RoundRectangle2D.Double(h/20,h/5,h*9/10,h*3/5,h/10,h/10));
		g2.drawLine(0,h/3,h,h/3);
		g2.setStroke(new BasicStroke(h/30));
		for(int i=1;i<=6;++i){
			int x=h/20+i*h*9/70;
			g2.drawLine(x,h/3,x,h);
		}
		for(int i=1;i<=3;++i){
			int y=h/3+i*h*7/60;
			g2.drawLine(0,y,h,y);
		}
	}
	public void fillTab(JPanel content,JPanel tab,Font font){
		if(eventFiller!=null){
			events.clear();
			eventFiller.accept(events);
		}
		JPanel panel=new JPanel(new GridLayout(0,7));
		Wrapper<YearMonth>w=new Wrapper<>(YearMonth.now());
		JPanel weekDays=new JPanel(new GridLayout(1,7));
		for(String s:new String[]{"Пн","Вт","Ср","Чт","Пт","Сб","Вс"}){
			JLabel l=new JLabel(s);
			l.setHorizontalAlignment(JLabel.CENTER);
			weekDays.add(l);
		}
		HButton left=new HButton(){
			private PathIcon icon=new PathIcon("ui/left.png");
			public void paint(Graphics g){
				int c=50-scale*4;
				if(getModel().isPressed())c-=25;
				g.setColor(new Color(c,c,c));
				g.fillRect(0,0,getWidth(),getHeight());
				icon.paintIcon(this,g,0,(getHeight()-icon.getIconHeight())/2);
			}
		},right=new HButton(){
			private PathIcon icon=new PathIcon("ui/right.png");
			public void paint(Graphics g){
				int c=50-scale*4;
				if(getModel().isPressed())c-=25;
				g.setColor(new Color(c,c,c));
				g.fillRect(0,0,getWidth(),getHeight());
				icon.paintIcon(this,g,0,(getHeight()-icon.getIconHeight())/2);
			}
		};
		left.addActionListener(e->{
			w.var=w.var.minus(1,ChronoUnit.MONTHS);
			fillForMonth(panel,w.var);
			panel.revalidate();
		});
		right.addActionListener(e->{
			w.var=w.var.plus(1,ChronoUnit.MONTHS);
			fillForMonth(panel,w.var);
			panel.revalidate();
		});
		weekDays.setBounds(tab.getWidth()/10,0,tab.getWidth()*4/5,tab.getHeight()/10);
		left.setSize(tab.getWidth()/10,tab.getHeight());
		panel.setBounds(tab.getWidth()/10,tab.getHeight()/10,tab.getWidth()*4/5,tab.getHeight()*9/10);
		right.setBounds(tab.getWidth()*9/10,0,tab.getWidth()/10,tab.getHeight());
		tab.add(weekDays);
		tab.add(left);
		tab.add(panel);
		tab.add(right);
		fillForMonth(panel,w.var);
	}
	public String toString(){return name;}
}