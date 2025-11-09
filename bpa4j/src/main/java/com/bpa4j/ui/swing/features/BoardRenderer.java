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
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import com.bpa4j.core.Editable;
import com.bpa4j.core.ProgramStarter;
import com.bpa4j.defaults.features.transmission_contracts.Board;
import com.bpa4j.defaults.table.FieldCellValue;
import com.bpa4j.defaults.table.FormCellEditor;
import com.bpa4j.editor.EditorEntry;
import com.bpa4j.feature.FeatureRenderer;
import com.bpa4j.feature.FeatureRenderingContext;
import com.bpa4j.ui.swing.SwingFeatureRenderingContext;
import com.bpa4j.ui.swing.SwingWorkFrameRenderer.SwingPreviewRenderingContext;
import com.bpa4j.ui.swing.util.AutoLayout;
import com.bpa4j.ui.swing.util.HButton;

public class BoardRenderer<T extends Serializable> implements FeatureRenderer<Board<T>>{
	private Board<T> contract;
	private Class<T> type;
	public BoardRenderer(Board<T> contract,Class<T> type){
		this.contract=contract;
		this.type=type;
	}
	public Board<T> getTransmissionContract(){
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
		g2.setStroke(new BasicStroke(s/10));
		g2.drawRect(s/10,s/5,s*4/5,s*3/5);
		g2.setStroke(new BasicStroke(s/20));
		g2.drawPolygon(new int[]{s/2,s/4,s/2,s/3,s/2,s/2,s/2,s*2/3,s/2,s*3/4,s/2,s*2/3,s/2,s/2,s/2,s/3,s/2},new int[]{s/2,s/2,s/2,s/3,s/2,s/4,s/2,s/3,s/2,s/2,s/2,s*2/3,s/2,s*3/4,s/2,s*2/3,s/2},16);
		g2.fillOval(s/3,s/3,s/3,s/3);
	}
	public void fillTab(JPanel content,JPanel tab,Font font){
		ArrayList<T> objects=contract.getObjects();
		tab.setLayout(new BorderLayout());
		tab.setBorder(BorderFactory.createEmptyBorder(tab.getHeight()/300,tab.getWidth()/300,tab.getHeight()/300,tab.getWidth()/300));
		JPanel config=null;
		JComponent filterConfig=null,sorterConfig=null;
		JTable t=new JTable();
		t.setBackground(Color.DARK_GRAY);
		t.setForeground(Color.WHITE);
		t.setRowHeight(tab.getHeight()/10);
		t.getTableHeader().setFont(new Font(Font.DIALOG,Font.ITALIC,tab.getHeight()/30));
		t.setFillsViewportHeight(true);
		t.setDefaultEditor(Object.class,new FormCellEditor());
		JScrollPane sPane=new JScrollPane(t);
		sPane.setBorder(BorderFactory.createTitledBorder(null,"Задания",0,0,new Font(Font.DIALOG,Font.PLAIN,tab.getHeight()/50),Color.WHITE));
		ArrayList<String> s=new ArrayList<>();
		for(Field f:type.getFields())
			if(f.isAnnotationPresent(EditorEntry.class)) s.add(f.getAnnotation(EditorEntry.class).translation());
		DefaultTableModel m=new DefaultTableModel(s.toArray(new String[0]),0);
		if(filterConfig!=null){
			// Filter config would go here
		}
		if(sorterConfig!=null){
			// Sorter config would go here
		}
		if(contract.getAllowCreation()||filterConfig!=null||sorterConfig!=null){
			config=new JPanel(new AutoLayout());
			config.setPreferredSize(new Dimension(tab.getWidth(),tab.getHeight()/9));
			if(filterConfig!=null) config.add(filterConfig);
			if(sorterConfig!=null) config.add(sorterConfig);
			if(contract.getAllowCreation()){
				HButton create=new HButton(){
					public void paint(Graphics g){
						g.setColor(Color.DARK_GRAY);
						g.fillRect(0,0,getWidth(),getHeight());
						FontMetrics fm=g.getFontMetrics();
						g.setColor(Color.WHITE);
						g.drawString("Добавить",(getWidth()-fm.stringWidth("Добавить"))/2,(getHeight()+fm.getAscent()+fm.getLeading()-fm.getDescent())/2);
					}
				};
				create.addActionListener(e->{
					try{
						T o=type.getDeclaredConstructor().newInstance();
						contract.addObject(o);
						if(o instanceof Editable){
							Editable editable=(Editable)o;
							ProgramStarter.editor.constructEditor(editable,true,()->contract.removeObject(o),null);
						}
						m.setRowCount(0);
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
	@SuppressWarnings("PMD.ReplaceVectorWithList")
	private void fillTable(DefaultTableModel m,ArrayList<T> objects){
		for(T t:objects){
			Vector<Object> values=new Vector<>();
			for(Field f:type.getFields())
				if(f.isAnnotationPresent(EditorEntry.class)) values.add(new FieldCellValue(f,t));
			m.addRow(values);
		}
	}
}
