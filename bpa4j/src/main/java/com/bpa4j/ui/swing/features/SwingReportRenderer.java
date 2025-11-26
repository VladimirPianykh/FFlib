package com.bpa4j.ui.swing.features;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import javax.swing.JComponent;
import javax.swing.JPanel;
import com.bpa4j.defaults.features.transmission_contracts.Report;
import com.bpa4j.feature.ConfiguratorRenderer;
import com.bpa4j.feature.DataRendererRenderer;
import com.bpa4j.feature.FeatureRenderingContext;
import com.bpa4j.feature.ReportRenderer;
import com.bpa4j.ui.swing.SwingFeatureRenderingContext;
import com.bpa4j.ui.swing.SwingWorkFrameRenderer.SwingPreviewRenderingContext;
import com.bpa4j.ui.swing.util.AutoLayout;
import com.bpa4j.defaults.ftr_attributes.data_renderers.TableDataRenderer;
import com.bpa4j.defaults.ftr_attributes.data_renderers.AnswerDataRenderer;
import com.bpa4j.defaults.ftr_attributes.data_renderers.ChartDataRenderer;

/**
 * Swing implementation of ReportRenderer.
 * @author AI-generated
 */
public class SwingReportRenderer implements ReportRenderer{
	public static class SwingDataRenderingContext implements DataRenderingContext{
		private final JPanel panel;
		private final Dimension componentSize;
		public SwingDataRenderingContext(JPanel panel,Dimension componentSize){
			this.panel=panel;
			this.componentSize=componentSize;
		}
		public JPanel getPanel(){
			return panel;
		}
		public Dimension getComponentSize(){
			return componentSize;
		}
	}

	public static class SwingConfiguratorRenderingContext implements ConfiguratorRenderingContext{
		private final JPanel panel;
		private final Runnable refreshCallback;
		public SwingConfiguratorRenderingContext(JPanel panel,Runnable refreshCallback){
			this.panel=panel;
			this.refreshCallback=refreshCallback;
		}
		public JPanel getPanel(){
			return panel;
		}
		public Runnable getRefreshCallback(){
			return refreshCallback;
		}
	}

	private Report contract;
	public SwingReportRenderer(Report contract){
		this.contract=contract;
	}
	public Report getTransmissionContract(){
		return contract;
	}

	@SuppressWarnings("unchecked")
	public <D extends Report.DataRenderer> DataRendererRenderer<D> getDataRendererRenderer(D dataRenderer){
		if(dataRenderer instanceof TableDataRenderer){
			return (DataRendererRenderer<D>)new SwingTableDataRendererRenderer();
		}else if(dataRenderer instanceof AnswerDataRenderer){
			return (DataRendererRenderer<D>)new SwingAnswerDataRendererRenderer();
		}else if(dataRenderer instanceof ChartDataRenderer){ return (DataRendererRenderer<D>)new SwingChartDataRendererRenderer(); }
		return null;
	}

	@SuppressWarnings("unchecked")
	public <C extends Report.Configurator> ConfiguratorRenderer<C> getConfiguratorRenderer(C configurator){
		// For now return a dummy renderer since there are no concrete Configurator implementations yet
		return null;
	}

	public DataRenderingContext getDataRenderingContext(FeatureRenderingContext context){
		// This will be set properly during fillTab
		return null;
	}

	public ConfiguratorRenderingContext getConfiguratorRenderingContext(FeatureRenderingContext context){
		// This will be set properly during fillTab
		return null;
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
			for(Report.DataRenderer r:contract.getDataRenderers()){
				r.setRendererSource(dr->getDataRendererRenderer(dr));
				DataRendererRenderer<Report.DataRenderer> renderer=getDataRendererRenderer(r);
				if(renderer==null) continue;
				SwingDataRenderingContext ctx=new SwingDataRenderingContext(panel,d);
				JComponent c=createDataRendererComponent(r,renderer,ctx);
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
			for(Report.Configurator c:contract.getConfigurators()){
				c.setRendererSource(cfg->getConfiguratorRenderer(cfg));
				// TODO: render configurators when we have actual implementations
			}
			tab.add(config,BorderLayout.NORTH);
		}
		saver.run();
		tab.add(panel,BorderLayout.SOUTH);
	}

	@SuppressWarnings("unchecked")
	private <D extends Report.DataRenderer> JComponent createDataRendererComponent(D dataRenderer,DataRendererRenderer<D> renderer,SwingDataRenderingContext ctx){
		if(dataRenderer instanceof TableDataRenderer){
			return ((TableDataRenderer<?>)dataRenderer).getComponent();
		}else if(dataRenderer instanceof AnswerDataRenderer){
			return ((AnswerDataRenderer)dataRenderer).getComponent();
		}else if(dataRenderer instanceof ChartDataRenderer){ return ((ChartDataRenderer)dataRenderer).getComponent(); }
		return null;
	}

	// Dummy renderer implementations for now
	private static class SwingTableDataRendererRenderer implements DataRendererRenderer<TableDataRenderer<?>>{
		public void render(TableDataRenderer<?> dataRenderer,DataRenderingContext ctx){}
	}

	private static class SwingAnswerDataRendererRenderer implements DataRendererRenderer<AnswerDataRenderer>{
		public void render(AnswerDataRenderer dataRenderer,DataRenderingContext ctx){}
	}

	private static class SwingChartDataRendererRenderer implements DataRendererRenderer<ChartDataRenderer>{
		public void render(ChartDataRenderer dataRenderer,DataRenderingContext ctx){}
	}
}
