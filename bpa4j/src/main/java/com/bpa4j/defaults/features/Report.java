package com.bpa4j.defaults.features;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.swing.JComponent;
import javax.swing.JPanel;

import com.bpa4j.core.User.Feature;

public class Report implements Feature{
	public static HashMap<String,Report>reports=new HashMap<>();
	private String name;
	private ArrayList<Supplier<JComponent>>dataRenderers=new ArrayList<>();
	private ArrayList<Function<Runnable,JComponent>>configurators=new ArrayList<>();
	private Report(String name){this.name=name;}
	public static Report getReport(String name){
		if(reports.containsKey(name))return(Report)reports.get(name);
		else throw new IllegalArgumentException("Report \""+name+"\" does not exist.");
	}
	public static Report registerReport(String name){
		if(!reports.containsKey(name))reports.put(name,new Report(name));
		return reports.get(name);
	}
	/**
	 * <p>Adds a data renderer to this report.</p>
	 * <p>Data renderer is a {@link JComponent} supplier.</p>
	 */
	public Report addDataRenderer(Supplier<JComponent>dataRenderer){dataRenderers.add(dataRenderer);return this;}
	/**
	 * <p>Adds a configurator to this report.</p>
	 * <p>The configurator is a {@link Function} which takes a saver as input and returns a {@link JComponent}. The component is added to the top of the tab.</p>
	 */
	public Report addConfigurator(Function<Runnable,JComponent>configurator){configurators.add(configurator);return this;}
	public void paint(Graphics2D g2,BufferedImage image,int h){
		//TODO: paint report icon
	}
	public void fillTab(JPanel content,JPanel tab,Font font){
		tab.setLayout(new BorderLayout());
		JPanel panel=new JPanel(new FlowLayout(FlowLayout.CENTER));
		panel.setPreferredSize(new Dimension(tab.getWidth(),configurators.isEmpty()?tab.getHeight():tab.getHeight()*9/10));
		panel.setSize(panel.getPreferredSize());
		Dimension d=new Dimension(panel.getWidth()*9/(10*(int)Math.ceil(Math.sqrt(dataRenderers.size()))),panel.getHeight()*9/(10*(int)Math.ceil(Math.sqrt(dataRenderers.size()))));
		Runnable saver=()->{
			panel.removeAll();
			for(Supplier<JComponent>r:dataRenderers){
				JComponent c=r.get();
				if(c==null)continue;
				JPanel p=new JPanel(new GridLayout(1,1));
				p.setPreferredSize(d);
				p.add(c);
				panel.add(p);
			}
			tab.revalidate();
		};
		if(!configurators.isEmpty()){
			JPanel config=new JPanel(new GridLayout());
			config.setPreferredSize(new Dimension(tab.getWidth(),tab.getHeight()/10));
			for(Function<Runnable,JComponent>c:configurators)config.add(c.apply(saver));
			tab.add(config,BorderLayout.NORTH);
		}
		saver.run();
		tab.add(panel,BorderLayout.SOUTH);
	}
	public String toString(){return name;}
}