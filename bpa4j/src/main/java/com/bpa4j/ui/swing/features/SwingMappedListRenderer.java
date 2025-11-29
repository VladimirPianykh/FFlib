package com.bpa4j.ui.swing.features;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.EventObject;
import java.util.Vector;
import java.util.function.BiConsumer;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.CellEditorListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import com.bpa4j.core.Editable;
import com.bpa4j.core.ProgramStarter;
import com.bpa4j.defaults.features.transmission_contracts.EditableList.ItemRenderingContext;
import com.bpa4j.defaults.features.transmission_contracts.MappedList;
import com.bpa4j.defaults.table.FieldCellRenderer;
import com.bpa4j.defaults.table.FieldCellValue;
import com.bpa4j.defaults.table.FormCellEditor;
import com.bpa4j.editor.EditorEntry;
import com.bpa4j.feature.FeatureRenderer;
import com.bpa4j.feature.FeatureRenderingContext;
import com.bpa4j.ui.swing.SwingFeatureRenderingContext;
import com.bpa4j.ui.swing.SwingWorkFrameRenderer.SwingPreviewRenderingContext;
import com.bpa4j.ui.swing.features.SwingEditableListRenderer.SwingItemRenderingContext;
import com.bpa4j.ui.swing.util.HButton;

@SuppressWarnings({"unchecked","PMD.ReplaceVectorWithList"})
public class SwingMappedListRenderer<T extends Editable,V extends Serializable> implements FeatureRenderer<MappedList<T,V>>{
	private MappedList<T,V> contract;
	public SwingMappedListRenderer(MappedList<T,V> contract){
		this.contract=contract;
	}
	public MappedList<T,V> getTransmissionContract(){
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
	public void paint(Graphics2D g2,BufferedImage image,int h){
		g2.setStroke(new BasicStroke(h/20));
		g2.drawRoundRect(h/10,h/10,h*4/5,h*4/5,h/10,h/10);
		g2.setClip(new RoundRectangle2D.Double(h/10,h/10,h*4/5,h*4/5,h/10,h/10));
		g2.setStroke(new BasicStroke(h/50));
		for(int x=h/10;x<h*9/10;x+=h/10)
			g2.drawLine(x,h/10,x,h*9/10);
		for(int y=h/10;y<h*9/10;y+=h/10)
			g2.drawLine(h/10,y,h*9/10,y);
	}
	public void fillTab(JPanel content,JPanel tab,Font font){
		tab.setLayout(new BorderLayout());
		JTable table=new JTable();
		contract.customizeTable(new SwingBoardRenderer.SwingTableCustomizationRenderingContext(table));
		table.setDefaultEditor(Object.class,new FormCellEditor());
		table.setDefaultRenderer(Object.class,new FieldCellRenderer());
		table.setRowHeight(tab.getHeight()/10);
		Vector<String> v=new Vector<>();
		v.add("Объекты");
		for(Field f:getVType().getFields())
			v.add(f.getAnnotation(EditorEntry.class).translation());
		DefaultTableModel m=new DefaultTableModel(v,0);
		BiConsumer<T,ItemRenderingContext>componentProvider=getTransmissionContract().getComponentProvider();
		if(componentProvider==null){
			componentProvider=(t,ctx)->{
				HButton bb=new HButton(){
					public void paint(Graphics g){
						int c=50-scale;
						if(getModel().isPressed()) c-=25;
						g.setColor(new Color(c,c,c));
						g.fillRect(0,0,getWidth(),getHeight());
						FontMetrics fm=g.getFontMetrics();
						g.setColor(Color.WHITE);
						g.drawString(t.name,(getWidth()-fm.stringWidth(t.name))/2,(getHeight()+fm.getAscent()+fm.getLeading()-fm.getDescent())/2);
					}
				};
				((SwingItemRenderingContext)ctx).setButton(bb);
			};
		}
		final BiConsumer<T,ItemRenderingContext>fComponentProvider=componentProvider;
		table.setModel(m);
		table.getColumnModel().getColumn(0).setCellRenderer((table2,value,isSelected,hasFocus,row,column)->{
			JPanel b=new JPanel();
			SwingItemRenderingContext itemContext=new SwingItemRenderingContext(b,e->ProgramStarter.editor.constructEditor((T)value,false,null,null));
			fComponentProvider.accept((T)value,itemContext);
			return b;
		});
		table.getColumnModel().getColumn(0).setCellEditor(new TableCellEditor(){
			public Object getCellEditorValue(){
				return null;
			}
			public Component getTableCellEditorComponent(JTable table,Object value,boolean isSelected,int row,int column){
				JPanel b=new JPanel();
				SwingItemRenderingContext itemContext=new SwingItemRenderingContext(b,e->ProgramStarter.editor.constructEditor((T)value,false,null,null));
				fComponentProvider.accept((T)value,itemContext);
				return b;
			}
			public boolean isCellEditable(EventObject anEvent){
				return true;
			}
			public boolean shouldSelectCell(EventObject anEvent){
				return true;
			}
			public boolean stopCellEditing(){
				return true;
			}
			public void cancelCellEditing(){}
			public void addCellEditorListener(CellEditorListener l){}
			public void removeCellEditorListener(CellEditorListener l){}
		});
		JScrollPane s=new JScrollPane(table,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		s.setPreferredSize(new Dimension(tab.getWidth(),tab.getHeight()*9/10));
		fillTable(m);
		HButton add=new HButton(){
			public void paint(Graphics g){
				int c=50-scale*4;
				if(getModel().isPressed()) c-=25;
				g.setColor(new Color(c,c,c));
				g.fillRect(0,0,getWidth(),getHeight());
				g.setColor(Color.WHITE);
				FontMetrics fm=g.getFontMetrics();
				g.drawString("Добавить",(getWidth()-fm.stringWidth("Добавить"))/2,(getHeight()+fm.getAscent()+fm.getLeading()-fm.getDescent())/2);
			}
		};
		add.addActionListener(e->{
			T o=contract.createObject();
			ProgramStarter.editor.constructEditor(o,true,null,ProgramStarter.getRenderingManager().getDetachedFeatureRenderingContext());
			Vector<Object> l=new Vector<>();
			l.add(o);
			for(Field f:getVType().getFields())
				l.add(new FieldCellValue(f,contract.getMapping(o)));
			m.addRow(l);
		});
		add.setPreferredSize(new Dimension(tab.getWidth(),tab.getHeight()/10));
		add.setFont(font);
		if(getTransmissionContract().getAllowCreation()) tab.add(add,BorderLayout.SOUTH);
		tab.add(s,BorderLayout.NORTH);
		tab.revalidate();
	}
	private Class<V> getVType(){
		return getTransmissionContract().getVType();
	}
	private void fillTable(DefaultTableModel m){
		for(T t:getTransmissionContract().getObjects().keySet()){
			Vector<Object> l=new Vector<>();
			l.add(t);
			for(Field f:getVType().getFields())
				l.add(new FieldCellValue(f,contract.getMapping(t)));
			m.addRow(l);
		}
	}
}
