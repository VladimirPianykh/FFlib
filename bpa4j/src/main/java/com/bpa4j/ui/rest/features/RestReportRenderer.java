package com.bpa4j.ui.rest.features;

import java.util.ArrayList;
import com.bpa4j.defaults.features.transmission_contracts.Report;
import com.bpa4j.feature.ConfiguratorRenderer;
import com.bpa4j.feature.DataRendererRenderer;
import com.bpa4j.feature.FeatureRenderingContext;
import com.bpa4j.feature.ReportRenderer;
import com.bpa4j.ui.rest.RestFeatureRenderingContext;
import com.bpa4j.ui.rest.RestRenderingManager;
import com.bpa4j.ui.rest.abstractui.Panel;
import com.bpa4j.ui.rest.abstractui.components.Label;
import com.bpa4j.ui.rest.abstractui.layout.BorderLayout;
import com.bpa4j.ui.rest.abstractui.layout.FlowLayout;
import com.bpa4j.ui.rest.abstractui.layout.GridLayout;

/**
 * Rest renderer for Report feature - displays configurable tabular reports
 * @author AI-generated
 */
public class RestReportRenderer implements ReportRenderer{
	public static class RestDataRenderingContext implements DataRenderingContext{
		private final Panel panel;
		private final int width;
		private final int height;
		public RestDataRenderingContext(Panel panel,int width,int height){
			this.panel=panel;
			this.width=width;
			this.height=height;
		}
		public Panel getPanel(){
			return panel;
		}
		public int getWidth(){
			return width;
		}
		public int getHeight(){
			return height;
		}
	}

	public static class RestConfiguratorRenderingContext implements ConfiguratorRenderingContext{
		private final Panel panel;
		private final Runnable refreshCallback;
		public RestConfiguratorRenderingContext(Panel panel,Runnable refreshCallback){
			this.panel=panel;
			this.refreshCallback=refreshCallback;
		}
		public Panel getPanel(){
			return panel;
		}
		public Runnable getRefreshCallback(){
			return refreshCallback;
		}
	}

	private final Report contract;

	public RestReportRenderer(Report contract){
		this.contract=contract;
	}

	public Report getTransmissionContract(){
		return contract;
	}

	public <D extends Report.DataRenderer> DataRendererRenderer<D> getDataRendererRenderer(D dataRenderer){
		// Return dummy renderers for now - REST implementation would need its own components
		return null;
	}

	public <C extends Report.Configurator> ConfiguratorRenderer<C> getConfiguratorRenderer(C configurator){
		return null;
	}

	public DataRenderingContext getDataRenderingContext(FeatureRenderingContext context){
		return null;
	}

	public ConfiguratorRenderingContext getConfiguratorRenderingContext(FeatureRenderingContext context){
		return null;
	}

	public void render(FeatureRenderingContext ctx){
		RestFeatureRenderingContext rctx=(RestFeatureRenderingContext)ctx;
		Panel target=rctx.getTarget();
		target.removeAll();

		// Create root panel with border layout
		int targetWidth=target.getWidth();
		int targetHeight=target.getHeight();
		if(targetWidth==0||targetHeight==0){
			targetWidth=RestRenderingManager.DEFAULT_SIZE.width();
			targetHeight=RestRenderingManager.DEFAULT_SIZE.height();
			target.setSize(targetWidth,targetHeight);
		}
		Panel root=new Panel(new BorderLayout());
		root.setSize(targetWidth,targetHeight);

		// Create header
		Panel header=new Panel(new FlowLayout());
		header.setSize(targetWidth,40);
		Label title=new Label(contract.getFeatureName());
		header.add(title);

		// Create configurators panel if there are any
		ArrayList<Report.Configurator> configurators=contract.getConfigurators();
		Panel configPanel=null;
		if(!configurators.isEmpty()){
			configPanel=new Panel(new FlowLayout(FlowLayout.LEFT,FlowLayout.LTR,5,5));
			configPanel.setSize(targetWidth,50);

			// Set renderer source and render configurators
			for(Report.Configurator c:configurators){
				ConfiguratorRenderer<Report.Configurator> cr=getConfiguratorRenderer(c);
				if(cr!=null){
					cr.render(c,new RestConfiguratorRenderingContext(configPanel,()->{
						//No rendering context, because everything is updated automatically right now (can be changed later).
					}));
				}
			}
			Label configLabel=new Label("Configurators: "+configurators.size());
			configPanel.add(configLabel);
		}

		// Create data panel
		ArrayList<Report.DataRenderer> dataRenderers=contract.getDataRenderers();

		Panel dataPanel=new Panel(new BorderLayout());

		// Calculate grid dimensions for data renderers
		int rendererCount=dataRenderers.size();
		if(rendererCount>0){
			int cols=(int)Math.ceil(Math.sqrt(rendererCount));
			int rows=(int)Math.ceil((double)rendererCount/cols);

			// Create grid for data renderers
			Panel grid=new Panel(new GridLayout(rows,cols,10,10));

			// Set renderer source and render data renderers
			for(Report.DataRenderer r:dataRenderers){
				DataRendererRenderer<Report.DataRenderer> drr=getDataRendererRenderer(r);
				if(drr!=null){
					drr.render(r,new RestDataRenderingContext(grid,grid.getWidth(),grid.getHeight()));
				}else{
					grid.add(new Label("No renderer for "+r.getClass().getSimpleName()));
				}
			}

			BorderLayout dataLayout=(BorderLayout)dataPanel.getLayout();
			dataLayout.addLayoutComponent(grid,BorderLayout.CENTER);
			dataPanel.add(grid);
		}else{
			Label noData=new Label("No data renderers configured");
			dataPanel.add(noData);
		}

		// Assemble layout
		BorderLayout layout=(BorderLayout)root.getLayout();
		layout.addLayoutComponent(header,BorderLayout.NORTH);

		if(configPanel!=null){
			Panel combined=new Panel(new BorderLayout());
			combined.setSize(root.getWidth(),configPanel.getHeight()+dataPanel.getHeight());
			BorderLayout combinedLayout=(BorderLayout)combined.getLayout();
			combinedLayout.addLayoutComponent(configPanel,BorderLayout.NORTH);
			combinedLayout.addLayoutComponent(dataPanel,BorderLayout.CENTER);
			combined.add(configPanel);
			combined.add(dataPanel);
			layout.addLayoutComponent(combined,BorderLayout.CENTER);
			root.add(combined);
		}else{
			layout.addLayoutComponent(dataPanel,BorderLayout.CENTER);
			root.add(dataPanel);
		}

		root.add(header);
		target.add(root);
	}

	public void renderPreview(FeatureRenderingContext ctx){
		// Preview rendering not needed for REST
	}
}
