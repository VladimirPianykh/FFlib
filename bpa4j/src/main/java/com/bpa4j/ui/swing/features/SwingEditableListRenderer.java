package com.bpa4j.ui.swing.features;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.function.BiConsumer;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import com.bpa4j.core.Editable;
import com.bpa4j.core.ProgramStarter;
import com.bpa4j.defaults.features.transmission_contracts.EditableList;
import com.bpa4j.defaults.features.transmission_contracts.EditableList.ItemRenderingContext;
import com.bpa4j.feature.FeatureRenderer;
import com.bpa4j.feature.FeatureRenderingContext;
import com.bpa4j.ui.swing.SwingFeatureRenderingContext;
import com.bpa4j.ui.swing.SwingWorkFrameRenderer.SwingPreviewRenderingContext;
import com.bpa4j.ui.swing.util.HButton;

public class SwingEditableListRenderer<T extends Editable> implements FeatureRenderer<EditableList<T>>{
	/**
	 * Context source passes {@code target} and {@code listener},
	 * context user calls {@link #setButton(JButton)}
	 * and this context does the necessary things with them.
	 */
	public static class SwingItemRenderingContext implements ItemRenderingContext{
		private JPanel target;
		private ActionListener l;
		public SwingItemRenderingContext(JPanel target,ActionListener listener){
			this.target=target;
			this.l=listener;
		}
		public void setButton(JButton b){
			b.addActionListener(l);
			target.add(b);
		}
	}
	private EditableList<T> contract;
	public SwingEditableListRenderer(EditableList<T> contract){
		this.contract=contract;
	}
	public EditableList<T> getTransmissionContract(){
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
		var group=contract.getGroup();
		tab.setLayout(new GridLayout());
		JPanel panel=new JPanel(new GridLayout(Math.max(10,group.size()+1),1));
		JScrollPane s=new JScrollPane(panel,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		s.getVerticalScrollBar().setUnitIncrement(tab.getHeight()/50);
		BiConsumer<T,ItemRenderingContext> componentProvider=getTransmissionContract().getComponentProvider();
		if(componentProvider==null){
			componentProvider=(t,ctx)->{
				SwingItemRenderingContext sctx=(SwingItemRenderingContext)ctx;
				HButton b=new HButton(){
					public void paint(Graphics g){
						int c=50-scale*4;
						if(getModel().isPressed()) c-=25;
						g.setColor(new Color(c,c,c));
						g.fillRect(0,0,getWidth(),getHeight());
						g.setColor(Color.WHITE);
						FontMetrics fm=g.getFontMetrics();
						g.drawString(t.name,(getWidth()-fm.stringWidth(t.name))/2,(getHeight()+fm.getAscent()+fm.getLeading()-fm.getDescent())/2);
						if(group.elementIcon!=null) group.elementIcon.paintIcon(this,g,getWidth()*9/10-group.elementIcon.getIconWidth(),(getHeight()-group.elementIcon.getIconHeight())/2);
					}
				};
				sctx.setButton(b);
			};
		}
		final BiConsumer<T,ItemRenderingContext> finalProvider=componentProvider;
		for(T t:group){
			finalProvider.accept(t,new SwingItemRenderingContext(panel,e->ProgramStarter.editor.constructEditor(t,false,()->{
				group.remove(t);
				JComponent b=((JComponent)e.getSource());
				b.getParent().remove(b);
			},null)));
		}
		if(getTransmissionContract().getCanCreate()){
			HButton add=new HButton(){
				public void paint(Graphics g){
					int c=50-scale*4;
					if(getModel().isPressed()) c-=25;
					g.setColor(new Color(c,c,c));
					g.fillRect(0,0,getWidth(),getHeight());
					if(group.addIcon!=null) group.addIcon.paintIcon(this,g,(getWidth()-group.addIcon.getIconWidth())/2,(getHeight()-group.addIcon.getIconHeight())/2);
				}
			};
			add.addActionListener(e->{
				try{
					T t=group.type.getDeclaredConstructor().newInstance();
					group.add(t);
					ProgramStarter.editor.constructEditor(t,true,()->group.remove(t),ProgramStarter.getRenderingManager().getDetachedFeatureRenderingContext());
					if(group.contains(t)){
						Component last=panel.getComponent(panel.getComponentCount()-1); //Create button
						panel.remove(last);
						finalProvider.accept(t,new SwingItemRenderingContext(panel,e2->ProgramStarter.editor.constructEditor(t,false,()->{
							group.remove(t);
							JComponent b=((JComponent)e2.getSource());
							b.getParent().remove(b);
						},null)));
						panel.add(last);
						panel.setPreferredSize(new Dimension(tab.getWidth(),tab.getHeight()*panel.getComponentCount()/10));
						panel.setLayout(new GridLayout(Math.max(10,group.size()+1),1));
						panel.revalidate();
					}
				}catch(ReflectiveOperationException ex){
					throw new RuntimeException(ex);
				}
			});
			panel.add(add);
		}
		panel.setPreferredSize(new Dimension(tab.getWidth(),tab.getHeight()*panel.getComponentCount()/10));
		tab.add(s);
	}
}
