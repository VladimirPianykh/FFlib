package com.bpa4j.ui.swing;

import com.bpa4j.core.Editable;
import com.bpa4j.core.EditableGroup;
import com.bpa4j.core.EditableGroupRenderer;
import com.bpa4j.defaults.features.transmission_contracts.EditableList.ItemRenderingContext;
import com.bpa4j.ui.swing.features.SwingEditableListRenderer;
import com.bpa4j.ui.swing.util.HButton;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.GradientPaint;

public class SwingEditableGroupRenderer implements EditableGroupRenderer<EditableGroup<Editable>>{
	public void renderElementButton(EditableGroup<Editable>group,Editable e,ItemRenderingContext context){
        SwingEditableListRenderer.SwingItemRenderingContext ctx=(SwingEditableListRenderer.SwingItemRenderingContext)context;
		HButton b=new HButton(){
			public void paintComponent(Graphics g){
				Graphics2D g2=(Graphics2D)g;
				g2.setColor(Color.GRAY);
				g2.fillRect(0,0,getWidth(),getHeight());
				// g2.setFont(font);
				FontMetrics fm=g2.getFontMetrics();
				g2.setPaint(new GradientPaint(0,0,getBackground(),0,getHeight(),getForeground()));
				g2.drawString(e.name,getWidth()/100,(getHeight()+fm.getAscent()+fm.getLeading()-fm.getDescent())/2);
				group.elementIcon.paintIcon(this,g2,getWidth()-group.elementIcon.getIconWidth(),0);
				g2.setColor(new Color(0,0,0,scale*10));
				g2.fillRect(0,0,getWidth(),getHeight());
				// if(true){
				// 	g2.setPaint(new LinearGradientPaint(getWidth()-getHeight()/2,getHeight()/4,getWidth(),getHeight()*3/4,new float[]{0,0.5f},new Color[]{new Color(89+scale*5,87,63),new Color(122+scale*5,119,80)},CycleMethod.REFLECT));
				// 	g2.fillOval(getWidth()-getHeight()*9/8,getHeight()/8+1,getHeight()*3/4-1,getHeight()*3/4-1);
				// 	g2.setPaint(new LinearGradientPaint(getWidth()-getHeight()/2,getHeight()/4,getWidth(),getHeight()*3/4,new float[]{0,0.5f},new Color[]{new Color(135+scale*5,52,14),new Color(194+scale*5,47,17)},CycleMethod.REFLECT));
				// 	g2.fillOval(getWidth()-getHeight(),getHeight()/4,getHeight()/2,getHeight()/2);
				// 	g2.setPaint(new LinearGradientPaint(getWidth()-getHeight()/2,getHeight()/4,getWidth(),getHeight()*3/4,new float[]{0,0.5f},new Color[]{new Color(54,51,36),new Color(77,74,56)},CycleMethod.REFLECT));
				// 	g2.drawOval(getWidth()-getHeight(),getHeight()/4,getHeight()/2,getHeight()/2);
				// 	g2.drawOval(getWidth()-getHeight()*9/8,getHeight()/8+1,getHeight()*3/4-1,getHeight()*3/4-1);
				// }
			}
		};
		b.setBackground(new Color(42,46,30));
		b.setForeground(new Color(32,36,21));
		ctx.setButton(b);
	}
	public void renderAddButton(EditableGroup<Editable>group,ItemRenderingContext context){
        SwingEditableListRenderer.SwingItemRenderingContext ctx=(SwingEditableListRenderer.SwingItemRenderingContext)context;
		ctx.setButton(new HButton(){
			public void paintComponent(Graphics g){
				g.setColor(Color.GRAY);
				g.fillRect(0,0,getWidth(),getHeight());
				group.addIcon.paintIcon(this,g,(getWidth()-group.addIcon.getIconWidth())/2,0);
				g.setColor(new Color(0,0,0,scale*10));
				g.fillRect(0,0,getWidth(),getHeight());
			}
		});
	}
}
