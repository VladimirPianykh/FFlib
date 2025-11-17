package com.bpa4j.ui.swing.features;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;
import com.bpa4j.Wrapper;
import com.bpa4j.core.Editable;
import com.bpa4j.core.ProgramStarter;
import com.bpa4j.defaults.features.transmission_contracts.DisposableDocument;
import com.bpa4j.feature.FeatureRenderingContext;
import com.bpa4j.feature.FeatureRenderer;
import com.bpa4j.ui.swing.SwingFeatureRenderingContext;
import com.bpa4j.ui.swing.SwingWorkFrameRenderer.SwingPreviewRenderingContext;
import com.bpa4j.ui.swing.util.HButton;

public class SwingDisposableDocumentRenderer<T extends Editable> implements FeatureRenderer<DisposableDocument<T>>{
	private DisposableDocument<T> contract;
	public SwingDisposableDocumentRenderer(DisposableDocument<T> contract){
		this.contract=contract;
	}
	public DisposableDocument<T> getTransmissionContract(){
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
		// Empty paint method for DisposableDocument
	}
	public void fillTab(JPanel content,JPanel tab,Font font){
		Wrapper<T> doc=new Wrapper<T>(null);
		HButton b=new HButton(){
			public void paint(Graphics g){
				Graphics2D g2=(Graphics2D)g;
				if(doc.var!=null){
					int c=100-scale*6;
					if(getModel().isPressed()) c-=25;
					g2.setColor(new Color(c,c,c));
				}
				g2.setColor(Color.LIGHT_GRAY);
				g2.setStroke(doc.var==null?new BasicStroke(getHeight()/60,1,1,2,new float[]{getHeight()/50,getHeight()/50},scale*getHeight()/300):new BasicStroke(getHeight()/40,1,1));
				g2.drawRoundRect(getWidth()/4,getHeight()/6,getWidth()/2,getHeight()*2/3,getHeight()/10,getHeight()/10);
				for(int i=0;i<4;++i)
					g2.drawLine(getWidth()/3,getHeight()/5+i*getHeight()/6,getWidth()*2/3,getHeight()/5+i*getHeight()/6);
				g2.drawRoundRect(0,0,getWidth(),getHeight(),getHeight()/10,getHeight()/10);
			}
		};
		b.addActionListener(e->{
			if(doc.var==null) try{
				doc.var=contract.getType().getDeclaredConstructor().newInstance();
				ProgramStarter.editor.constructEditor(doc.var,true,()->doc.var=null,ProgramStarter.getRenderingManager().getDetachedFeatureRenderingContext());
			}catch(ReflectiveOperationException ex){
				throw new RuntimeException(ex);
			}
		});
		int s=tab.getHeight()/2;
		b.setBounds((tab.getWidth()-s)/2,tab.getHeight()/20,s,s);
		tab.add(b);
		HButton confirm=new HButton(){
			public void paint(Graphics g){
				g.setColor(doc.var==null?new Color(50+scale*6,50,50):new Color(50+scale*2,50+scale*6,50));
				g.fillRoundRect(0,0,getWidth(),getHeight(),getHeight(),getHeight());
				FontMetrics fm=g.getFontMetrics();
				g.setColor(doc.var==null?Color.LIGHT_GRAY:Color.GREEN);
				g.drawString(getText(),(getWidth()-fm.stringWidth(getText()))/2,(getHeight()+fm.getAscent()+fm.getLeading()-fm.getDescent())/2);
			}
		};
		confirm.addActionListener(e->{
			if(doc.var!=null){
				contract.processDocument(doc.var);
				doc.var=null;
			}
		});
		confirm.setBounds((tab.getWidth()-s)/2,s+tab.getHeight()/15,s,tab.getHeight()/10);
		confirm.setOpaque(false);
		confirm.setText("Подтвердить");
		tab.add(confirm);
	}
}
