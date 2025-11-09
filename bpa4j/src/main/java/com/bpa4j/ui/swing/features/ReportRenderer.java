package com.bpa4j.ui.swing.features;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.swing.JComponent;
import javax.swing.JPanel;
import com.bpa4j.defaults.features.transmission_contracts.Report;
import com.bpa4j.feature.FeatureRenderer;
import com.bpa4j.feature.FeatureRenderingContext;
import com.bpa4j.ui.swing.SwingFeatureRenderingContext;
import com.bpa4j.ui.swing.SwingWorkFrameRenderer.SwingPreviewRenderingContext;
import com.bpa4j.ui.swing.util.AutoLayout;

public class ReportRenderer implements FeatureRenderer<Report>{
	private Report contract;
	public ReportRenderer(Report contract){
		this.contract=contract;
	}
	public Report getTransmissionContract(){
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
		// TODO: paint report icon
	}
	public void fillTab(JPanel content,JPanel tab,Font font){
		tab.setLayout(new BorderLayout());
		JPanel panel=new JPanel(new FlowLayout(FlowLayout.CENTER));
		panel.setPreferredSize(new Dimension(tab.getWidth(),contract.getConfigurators().isEmpty()?tab.getHeight():tab.getHeight()*9/10));
		panel.setSize(panel.getPreferredSize());
		Dimension d=new Dimension(panel.getWidth()*9/(10*(int)Math.ceil(Math.sqrt(contract.getDataRenderers().size()))),panel.getHeight()*9/(10*(int)Math.ceil(Math.sqrt(contract.getDataRenderers().size()))));
		Runnable saver=()->{
			panel.removeAll();
			for(Supplier<JComponent> r:contract.getDataRenderers()){
				JComponent c=r.get();
				if(c==null) continue;
				JPanel p=new JPanel(new GridLayout(1,1));
				p.setPreferredSize(d);
				p.add(c);
				panel.add(p);
			}
			tab.revalidate();
		};
		if(!contract.getConfigurators().isEmpty()){
			JPanel config=new JPanel(new AutoLayout());
			config.setPreferredSize(new Dimension(tab.getWidth(),tab.getHeight()/10));
			for(Function<Runnable,JComponent> c:contract.getConfigurators())
				config.add(c.apply(saver));
			tab.add(config,BorderLayout.NORTH);
		}
		saver.run();
		tab.add(panel,BorderLayout.SOUTH);
	}
}
