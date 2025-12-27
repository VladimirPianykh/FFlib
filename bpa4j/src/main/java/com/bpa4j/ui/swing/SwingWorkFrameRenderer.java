package com.bpa4j.ui.swing;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.LinearGradientPaint;
import java.awt.RenderingHints;
import java.awt.MultipleGradientPaint.CycleMethod;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import com.bpa4j.core.Root;
import com.bpa4j.core.User;
import com.bpa4j.core.WorkFrame;
import com.bpa4j.core.WorkFrame.FeatureEntry;
import com.bpa4j.core.WorkFrameRenderer;
import com.bpa4j.feature.FeatureRenderingContext;
import com.bpa4j.ui.swing.util.HButton;
import com.bpa4j.ui.swing.util.Message;

public class SwingWorkFrameRenderer extends JFrame implements WorkFrameRenderer{
	public static class SwingPreviewRenderingContext implements FeatureRenderingContext{
		private final BufferedImage target;
		public BufferedImage getPreviewTarget(){
			return target;
		}
		public SwingPreviewRenderingContext(BufferedImage target){
			this.target=target;
		}
	}
	public static class WorkTabButton extends HButton{
		public BufferedImage image;
		public WorkTabButton(FeatureEntry<?>feature,JPanel content,int index,WorkFrame wf){
			super(15,3);
			int h=content.getHeight()/3;
			image=new BufferedImage(h,h,6);
			Graphics2D g2=image.createGraphics();
			g2.setPaint(new GradientPaint(0,0,new Color(13,16,31),h,h,new Color(11,18,31)));
			feature.renderPreview(new SwingPreviewRenderingContext(image));
			g2.dispose();
			// JPanel tab=new JPanel(null){
			// 	float[]fr={0,1};
			// 	C
			
			// Font font=new Font(Font.DIALOG,Font.PLAIN,content.getHeight()/20);
			addComponentListener(new ComponentListener(){
				public void componentMoved(ComponentEvent e){}
				public void componentResized(ComponentEvent e){
					setFont(new Font(Font.DIALOG,Font.PLAIN,getHeight()/8));
				}
				public void componentShown(ComponentEvent e){}
				public void componentHidden(ComponentEvent e){}
			});
			setText(feature.toString());
			addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					content.removeAll();
					// tab.removeAll();
					// tab.setSize(content.getSize());
					wf.selectFeature(feature);
					// content.add(tab);
					content.revalidate();
					content.repaint();
				}
			});
		}
		public void paintComponent(Graphics g){
			Graphics2D g2=(Graphics2D)g;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setPaint(new LinearGradientPaint(0,0,getWidth()/2,getHeight()/2,new float[]{0,1},new Color[]{new Color(50-scale*2,50-scale,50-scale*2),Color.GRAY},CycleMethod.REFLECT));
			g2.fillRect(0,0,getWidth(),getHeight());
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.45f));
			g2.drawImage(image,0,0,this);
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
			FontMetrics fm=g2.getFontMetrics();
			g2.setColor(Color.BLACK);
			String[]t=getText().split(" ");
			for(int i=0;i<t.length;++i)g2.drawString(t[i],(getWidth()-fm.stringWidth(t[i]))/2,getHeight()*9/10-fm.getHeight()*(t.length-(i+1)));
		}
		public static JPanel createTable(int rows,int cols,JPanel tab,boolean dark){
			JPanel p=new JPanel(new GridLayout(rows,2));
			p.setPreferredSize(new Dimension(tab.getWidth(),tab.getHeight()*rows/7));
			p.setSize(p.getPreferredSize());
			if(dark)p.setBackground(Color.DARK_GRAY);
			JScrollPane s=new JScrollPane(p,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			s.setSize(tab.getWidth(),Math.min(p.getHeight(),tab.getHeight()*3/4));
			s.getVerticalScrollBar().setUnitIncrement(tab.getHeight()/50);
			if(dark){
				s.getVerticalScrollBar().setBackground(Color.DARK_GRAY);
				s.getVerticalScrollBar().setForeground(Color.GRAY);
			}
			s.getVerticalScrollBar().setBorder(null);
			tab.add(s);
			return p;
		}
	}
	private WorkFrame wf;
	private JPanel content=new JPanel();
	public WorkFrame getWorkFrame(){
		return wf;
	}
    public SwingWorkFrameRenderer(WorkFrame wf){
		this.wf=wf;
	}
	public void showWorkFrame(){
		setSize(Root.SCREEN_SIZE);
		setExtendedState(3);
		setUndecorated(true);
		setContentPane(new JPanel(null));
		content=new JPanel(new GridLayout(1,1));
		content.setBounds(0,getHeight()/4,getWidth(),getHeight()*3/4);
		content.setOpaque(false);
		add(content);
		JPanel sidebar=new JPanel(new FlowLayout());
		sidebar.setBackground(new Color(10,15,10));
		JScrollPane s=new JScrollPane(sidebar,JScrollPane.VERTICAL_SCROLLBAR_NEVER,JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		s.setBounds(0,0,getWidth(),getHeight()/4);
		s.setOpaque(false);
		s.getHorizontalScrollBar().setBorder(null);
		s.getHorizontalScrollBar().setBackground(Color.DARK_GRAY);
		HButton exit=new HButton(5,5){
			public void paintComponent(Graphics g){
				Graphics2D g2=(Graphics2D)g;
				g2.setColor(new Color(scale*30+15,15,15));
				g2.fillRect(0,0,getWidth(),getHeight());
				g2.setStroke(new BasicStroke(getHeight()/10));
				g2.setColor(Color.WHITE);
				int h=scale*getHeight()/250;
				g2.drawLine(getWidth()/4+h,getHeight()/4+h,getWidth()*3/4-h,getHeight()*3/4-h);
				g2.drawLine(getWidth()*3/4-h,getHeight()/4+h,getWidth()/4+h,getHeight()*3/4-h);
			}
		};
		exit.addActionListener(e->wf.exit());
		exit.setBounds(0,0,getHeight()/4,getHeight()/4);
		exit.setForeground(new Color(54,23,13));
		exit.setBorder(null);
		add(exit);
		if(User.ftrMap.get(wf.getUser().role)==null||User.ftrMap.get(wf.getUser().role).length==0){
			new Message("Ошибка! Возможно, у вас есть устаревшее сохранение и его надо удалить (см. записку).",Color.RED);
			throw new IllegalStateException("ftrMap does not have elements or is null.");
		}
		for(FeatureEntry<?>f:wf.getFeatures()){
			WorkTabButton t=new WorkTabButton(f,content,sidebar.getComponentCount(),wf);
			sidebar.add(t);
			t.setPreferredSize(new Dimension(s.getHeight(),s.getHeight()));
		}
		sidebar.setPreferredSize(new Dimension(s.getHeight()*(sidebar.getComponentCount()+1),s.getHeight()));
		add(s);
		setVisible(true);
	}
	public FeatureRenderingContext getContext(FeatureEntry<?>f){
		return new SwingFeatureRenderingContext(this,content);
	}
}
