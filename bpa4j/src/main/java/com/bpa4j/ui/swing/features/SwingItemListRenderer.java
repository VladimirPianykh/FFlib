package com.bpa4j.ui.swing.features;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import com.bpa4j.core.Editable;
import com.bpa4j.core.ProgramStarter;
import com.bpa4j.defaults.features.transmission_contracts.ItemList;
import com.bpa4j.editor.EditorEntry;
import com.bpa4j.feature.FeatureRenderer;
import com.bpa4j.feature.FeatureRenderingContext;
import com.bpa4j.ui.swing.SwingFeatureRenderingContext;
import com.bpa4j.ui.swing.SwingWorkFrameRenderer.SwingPreviewRenderingContext;
import com.bpa4j.ui.swing.features.SwingBoardRenderer.SwingConfiguratorRenderingContext;
import com.bpa4j.ui.swing.util.AutoLayout;
import com.bpa4j.ui.swing.util.HButton;

public class SwingItemListRenderer<T extends Serializable> implements FeatureRenderer<ItemList<T>>{
	private ItemList<T> contract;
	public SwingItemListRenderer(ItemList<T> contract){
		this.contract=contract;
	}
	public ItemList<T> getTransmissionContract(){
		return contract;
	}
	public void render(FeatureRenderingContext ctx){
		SwingFeatureRenderingContext swingCtx=(SwingFeatureRenderingContext)ctx;
		JPanel content=swingCtx.getTarget();
		JPanel tab=new JPanel(null);
		tab.setSize(content.getSize());
		Font font=new Font(Font.DIALOG,Font.PLAIN,content.getHeight()/30);
		fillTab(content,tab,font);
		content.add(tab);
		content.revalidate();
	}
	public void renderPreview(FeatureRenderingContext ctx){
		SwingPreviewRenderingContext previewCtx=(SwingPreviewRenderingContext)ctx;
		BufferedImage image=previewCtx.getPreviewTarget();
		int size=image.getHeight();
		Graphics2D g2=image.createGraphics();
		paint(g2,image,size);
		g2.dispose();
	}
	public void paint(Graphics2D g2,BufferedImage image,int s){
		g2.setStroke(new BasicStroke(s/40));
		g2.drawLine(s/6,s/4,s*5/6,s/4);
		g2.drawLine(s/6,s/2,s/2,s/2);
		g2.drawLine(s/6,s*3/4,s*2/3,s*3/4);
	}
	public void fillTab(JPanel content,JPanel tab,Font font){
		ArrayList<T> objects=contract.getObjects();
		tab.setLayout(new BorderLayout());
		tab.setBorder(BorderFactory.createEmptyBorder(tab.getHeight()/300,tab.getWidth()/300,tab.getHeight()/300,tab.getWidth()/300));
		JPanel config=null;
		JComponent filterConfig=null,sorterConfig=null;
		JList<T> t=new JList<T>();
		t.setBackground(Color.DARK_GRAY);
		t.setForeground(Color.WHITE);
		t.setFixedCellHeight(tab.getHeight()/10);
		t.setFont(new Font(Font.DIALOG,Font.ITALIC,tab.getHeight()/30));
		JScrollPane sPane=new JScrollPane(t);
		sPane.setBorder(BorderFactory.createTitledBorder(null,"Задания",0,0,new Font(Font.DIALOG,Font.PLAIN,tab.getHeight()/50),Color.WHITE));
		ArrayList<String> s=new ArrayList<>();
		for(Field f:getType().getFields())
			if(f.isAnnotationPresent(EditorEntry.class)) s.add(f.getAnnotation(EditorEntry.class).translation());
		DefaultListModel<T> m=new DefaultListModel<>();
		if(contract.getAllowCreation()||filterConfig!=null||sorterConfig!=null||!getSingularActions().isEmpty()||!getCollectiveActions().isEmpty()){
			config=new JPanel(new AutoLayout());
			config.setPreferredSize(new Dimension(tab.getWidth(),tab.getHeight()/9));
			SwingConfiguratorRenderingContext ctx=new SwingConfiguratorRenderingContext(config,()->fillTable(m,getTransmissionContract().getObjects()));
			getTransmissionContract().renderSorter(ctx);
			getTransmissionContract().renderFilter(ctx);
			if(filterConfig!=null) config.add(filterConfig);
			if(sorterConfig!=null) config.add(sorterConfig);
			if(!getSingularActions().isEmpty()){
				for(var a:getSingularActions()){
					JButton b=new HButton();
					b.addActionListener(e->a.accept(t.getSelectedValue()));
					config.add(b);
				}
			}
			if(!getTransmissionContract().getCollectiveActions().isEmpty()){
				for(var a:getCollectiveActions()){
					JButton b=new HButton();
					b.addActionListener(e->a.accept(t.getSelectedValuesList()));
					config.add(b);
				}
				t.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			}
			if(contract.getAllowCreation()){
				HButton create=new HButton(){
					public void paint(Graphics g){
						int c=50-scale*4;
						if(getModel().isPressed()) c-=25;
						g.setColor(new Color(c,c,c));
						g.fillRect(0,0,getWidth(),getHeight());
						FontMetrics fm=g.getFontMetrics();
						g.setColor(Color.BLACK);
						g.drawString("Добавить",(getWidth()-fm.stringWidth("Добавить"))/2,(getHeight()+fm.getAscent()+fm.getLeading()-fm.getDescent())/2);
					}
				};
				create.addActionListener(e->{
					try{
						T o=getType().getDeclaredConstructor().newInstance();
						contract.addObject(o);
						if(o instanceof Editable){
							Editable editable=(Editable)o;
							ProgramStarter.editor.constructEditor(editable,true,()->contract.removeObject(o),ProgramStarter.getRenderingManager().getDetachedFeatureRenderingContext());
						}
						m.clear();
						fillTable(m,objects);
						tab.revalidate();
					}catch(ReflectiveOperationException ex){
						throw new IllegalStateException(ex);
					}
				});
				config.add(create);
			}
		}
		fillTable(m,objects);
		sPane.setPreferredSize(new Dimension(tab.getWidth(),config==null?tab.getHeight():tab.getHeight()*8/9));
		t.setModel(m);
		tab.add(sPane,BorderLayout.SOUTH);
		if(config!=null) tab.add(config,BorderLayout.NORTH);
	}
	private List<Consumer<List<T>>> getCollectiveActions(){
		return getTransmissionContract().getCollectiveActions();
	}
	private List<Consumer<T>> getSingularActions(){
		return getTransmissionContract().getSingularActions();
	}
	private Class<T> getType(){
		return getTransmissionContract().getType();
	}
	private void fillTable(DefaultListModel<T> m,ArrayList<T> objects){
		for(T t:objects)
			m.addElement(t);
	}
}
