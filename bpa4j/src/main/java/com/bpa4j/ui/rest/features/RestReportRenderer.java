package com.bpa4j.ui.rest.features;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import org.knowm.xchart.CategoryChart;
import org.knowm.xchart.CategoryChartBuilder;
import org.knowm.xchart.PieChart;
import org.knowm.xchart.PieChartBuilder;
import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.internal.chartpart.Chart;
import org.knowm.xchart.style.Styler.ChartTheme;
import com.bpa4j.defaults.features.transmission_contracts.Report;
import com.bpa4j.defaults.ftr_attributes.data_renderers.AnswerDataRenderer;
import com.bpa4j.defaults.ftr_attributes.data_renderers.ChartDataRenderer;
import com.bpa4j.defaults.ftr_attributes.data_renderers.TableDataRenderer;
import com.bpa4j.editor.EditorEntry;
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

	@SuppressWarnings("unchecked")
	public <D extends Report.DataRenderer> DataRendererRenderer<D> getDataRendererRenderer(D dataRenderer){
		if(dataRenderer instanceof TableDataRenderer){
			return (DataRendererRenderer<D>)new RestTableDataRendererRenderer();
		}else if(dataRenderer instanceof AnswerDataRenderer){
			return (DataRendererRenderer<D>)new RestAnswerDataRendererRenderer();
		}else if(dataRenderer instanceof ChartDataRenderer){ return (DataRendererRenderer<D>)new RestChartDataRendererRenderer(); }
		return null;
	}

	public <C extends Report.Configurator> ConfiguratorRenderer<C> getConfiguratorRenderer(C configurator){
		// For now return a dummy renderer since there are no concrete Configurator implementations yet
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

			// Calculate grid size - use most of available space
			int gridWidth=targetWidth;
			int gridHeight=Math.max(400,targetHeight-140); // Leave space for header/config

			// Create grid for data renderers
			Panel grid=new Panel(new GridLayout(rows,cols,10,10));
			grid.setSize(gridWidth,gridHeight);

			// Calculate cell dimensions accounting for gaps
			int cellWidth=(gridWidth-(cols-1)*10)/cols;
			int cellHeight=(gridHeight-(rows-1)*10)/rows;

			// Set renderer source and render data renderers
			for(Report.DataRenderer r:dataRenderers){
				try{
					r.render(this,new RestDataRenderingContext(grid,cellWidth,cellHeight));
				}catch(IllegalStateException ex){
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
		target.update();
	}

	public void renderPreview(FeatureRenderingContext ctx){
		// Preview rendering not needed for REST
	}

	/**
	 * DataRendererRenderer implementation for TableDataRenderer.
	 */
	private static class RestTableDataRendererRenderer implements DataRendererRenderer<TableDataRenderer<?>>{
		public void render(TableDataRenderer<?> dataRenderer,DataRenderingContext ctx){
			RestDataRenderingContext restCtx=(RestDataRenderingContext)ctx;
			Panel panel=restCtx.getPanel();

			ArrayList<?> elements=dataRenderer.getElementSupplier().get();
			String title=dataRenderer.getTitle();

			Panel wrapper=new Panel(new FlowLayout(FlowLayout.LEFT,FlowLayout.TTB,0,5));
			wrapper.setSize(restCtx.getWidth(),restCtx.getHeight());

			if(title!=null){
				wrapper.add(new Label(title));
			}

			if(elements.isEmpty()){
				wrapper.add(new Label("No data"));
			}else{
				Field[] allFields=elements.get(0).getClass().getFields();
				ArrayList<Field> fields=new ArrayList<>();
				for(Field f:allFields)
					if(f.isAnnotationPresent(EditorEntry.class)) fields.add(f);

				int columns=fields.size();
				int rows=elements.size()+1; // +1 for header

				// Calculate table size
				int tableWidth=restCtx.getWidth()-10; // Leave some margin
				int rowHeight=30;
				int tableHeight=Math.min(restCtx.getHeight()-40,rows*rowHeight+(rows-1)*5);

				Panel table=new Panel(new GridLayout(rows,columns,5,5));
				table.setSize(tableWidth,tableHeight);

				// Header
				for(Field f:fields){
					table.add(new Label(f.getAnnotation(EditorEntry.class).translation()));
				}

				// Rows
				for(Object t:elements){
					for(Field f:fields){
						try{
							Object val=f.get(t);
							table.add(new Label(val!=null?String.valueOf(val):""));
						}catch(IllegalAccessException ex){
							table.add(new Label("Error"));
						}
					}
				}
				wrapper.add(table);
			}
			panel.add(wrapper);
		}
	}

	/**
	 * DataRendererRenderer implementation for AnswerDataRenderer.
	 */
	private static class RestAnswerDataRendererRenderer implements DataRendererRenderer<AnswerDataRenderer>{
		public void render(AnswerDataRenderer dataRenderer,DataRenderingContext ctx){
			RestDataRenderingContext restCtx=(RestDataRenderingContext)ctx;
			Panel panel=restCtx.getPanel();

			Panel wrapper=new Panel(new FlowLayout(FlowLayout.LEFT,FlowLayout.TTB,0,5));
			wrapper.setSize(restCtx.getWidth(),restCtx.getHeight());

			Supplier<String>[] answerers=dataRenderer.getAnswerers();
			for(Supplier<String> s:answerers){
				wrapper.add(new Label(s.get()));
			}
			panel.add(wrapper);
		}
	}

	/**
	 * DataRendererRenderer implementation for ChartDataRenderer.
	 * Creates chart using Swing components, converts to image and saves to Downloads.
	 */
	private static class RestChartDataRendererRenderer implements DataRendererRenderer<ChartDataRenderer>{
		public void render(ChartDataRenderer dataRenderer,DataRenderingContext ctx){
			RestDataRenderingContext restCtx=(RestDataRenderingContext)ctx;
			Panel panel=restCtx.getPanel();

			// Access data renderer properties
			ChartDataRenderer.ChartMode mode=dataRenderer.getMode();
			Supplier<? extends List<Object[]>> elementSupplier=dataRenderer.getElementSupplier();
			String title=dataRenderer.getTitle();
			Object[] args=dataRenderer.getArgs();

			// Build the chart component (same as Swing version)
			if(mode==null) throw new NullPointerException("Mode cannot be null.");
			Chart<?,?> chart=switch(mode){
				case LINEAR_EACH -> {
					XYChart c=new XYChartBuilder().theme(ChartTheme.Matlab).title(title==null?"":title).build();
					for(Object[] s:elementSupplier.get()){
						c.addSeries((String)s[0],(int[])s[1],(int[])s[2]);
					}
					yield c;
				}
				case LINEAR_COMPARE -> {
					XYChart c=new XYChartBuilder().theme(ChartTheme.Matlab).title(title==null?"":title).xAxisTitle(args.length==0?"":(String)args[0]).yAxisTitle(args.length<2?"":(String)args[1]).build();
					List<Object[]> a=elementSupplier.get();
					if(a.isEmpty()) yield null;
					HashMap<String,ArrayList<Integer>> x=new HashMap<>(),y=new HashMap<>();
					for(Object[] o:a){
						String k=(String)o[0];
						if(!x.containsKey(k)){
							x.put(k,new ArrayList<>());
							y.put(k,new ArrayList<>());
						}
						x.get(k).add((int)o[1]);
						y.get(k).add((int)o[2]);
					}
					for(String k:x.keySet())
						c.addSeries(k,x.get(k),y.get(k));
					yield c;
				}
				case PIE -> {
					PieChart c=new PieChartBuilder().theme(ChartTheme.Matlab).title(title==null?"":title).build();
					for(Object[] s:elementSupplier.get())
						c.addSeries((String)s[0],(int)s[1]);
					yield c;
				}
				case BAR -> {
					CategoryChart c=new CategoryChartBuilder().theme(ChartTheme.Matlab).title(title==null?"":title).xAxisTitle(args.length==0?"":(String)args[0]).yAxisTitle(args.length<2?"":(String)args[1]).build();
					List<Object[]> a=elementSupplier.get();
					if(a.isEmpty()) yield null;
					HashMap<String,ArrayList<String>> t=new HashMap<>();
					HashMap<String,ArrayList<Integer>> v=new HashMap<>();
					for(int i=0;i<a.size();++i){
						String k=(String)a.get(i)[0];
						if(!t.containsKey(k)){
							t.put(k,new ArrayList<>());
							v.put(k,new ArrayList<>());
						}
						t.get(k).add((String)a.get(i)[1]);
						v.get(k).add((int)a.get(i)[2]);
					}
					ArrayList<String> s=new ArrayList<>(t.keySet());
					s.sort((e1,e2)->t.get(e2).size()-t.get(e1).size());
					for(String k:s)
						c.addSeries(k,t.get(k),v.get(k));
					yield c;
				}
			};

			Panel wrapper=new Panel(new FlowLayout(FlowLayout.CENTER,FlowLayout.CENTER,0,0));
			wrapper.setSize(restCtx.getWidth(),restCtx.getHeight());

			if(chart!=null){
				// Create XChartPanel with chart
				XChartPanel<?> chartPanel=new XChartPanel<>(chart);
				int width=restCtx.getWidth();
				int height=restCtx.getHeight();
				chartPanel.setSize(width,height);
				chartPanel.setPreferredSize(new Dimension(width,height));

				// Create temporary JPanel to properly render the chart
				JPanel tempPanel=new JPanel();
				tempPanel.setSize(width,height);
				tempPanel.setPreferredSize(new Dimension(width,height));
				tempPanel.add(chartPanel);
				tempPanel.validate();

				// Convert chart panel to BufferedImage
				BufferedImage image=new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
				Graphics2D g2=image.createGraphics();
				g2.setColor(Color.WHITE);
				g2.fillRect(0,0,width,height);
				tempPanel.paint(g2);
				g2.dispose();

				// Save image to Downloads folder
				try{
					String home=System.getProperty("user.home");
					String fileName=(title!=null&&!title.isEmpty())?title:"chart";
					String outputPath=home+"\\Downloads\\"+fileName+".png";
					File outputFile=new File(outputPath);
					ImageIO.write(image,"png",outputFile);

					// Add UI feedback
					if(title!=null) wrapper.add(new Label(title));
					wrapper.add(new Label("График сохранён в папку Загрузки: "+fileName+".png"));
				}catch(IOException ex){
					if(title!=null) wrapper.add(new Label(title));
					wrapper.add(new Label("Ошибка при сохранении графика: "+ex.getMessage()));
				}
			}else{
				if(title!=null) wrapper.add(new Label(title));
				wrapper.add(new Label("Нет данных для отображения"));
			}

			panel.add(wrapper);
		}
	}
}
