package com.bpa4j.ui.swing.features;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;
import java.util.function.Supplier;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import org.knowm.xchart.CategoryChart;
import org.knowm.xchart.CategoryChartBuilder;
import org.knowm.xchart.PieChart;
import org.knowm.xchart.PieChartBuilder;
import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.internal.chartpart.Chart;
import org.knowm.xchart.style.Styler.ChartTheme;
import com.bpa4j.core.Root;
import com.bpa4j.defaults.features.transmission_contracts.Report;
import com.bpa4j.defaults.ftr_attributes.data_renderers.AnswerDataRenderer;
import com.bpa4j.defaults.ftr_attributes.data_renderers.ChartDataRenderer;
import com.bpa4j.defaults.ftr_attributes.data_renderers.TableDataRenderer;
import com.bpa4j.defaults.table.EmptyCellEditor;
import com.bpa4j.editor.EditorEntry;
import com.bpa4j.feature.ConfiguratorRenderer;
import com.bpa4j.feature.DataRendererRenderer;
import com.bpa4j.feature.FeatureRenderingContext;
import com.bpa4j.feature.ReportRenderer;
import com.bpa4j.ui.swing.SwingFeatureRenderingContext;
import com.bpa4j.ui.swing.SwingWorkFrameRenderer.SwingPreviewRenderingContext;
import com.bpa4j.ui.swing.util.AutoLayout;
import com.bpa4j.ui.swing.util.HButton;
import com.bpa4j.ui.swing.util.Message;
import com.bpa4j.util.excel.ExcelUtils;

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
				DataRendererRenderer<Report.DataRenderer> renderer=getDataRendererRenderer(r);
				SwingDataRenderingContext ctx=new SwingDataRenderingContext(panel,d);
				renderer.render(r,ctx);
			}
			tab.revalidate();
		};
		if(!contract.getConfigurators().isEmpty()){
			JPanel config=new JPanel(new AutoLayout());
			config.setPreferredSize(new Dimension(tab.getWidth(),tab.getHeight()/10));
			for(Report.Configurator c:contract.getConfigurators()){
				SwingConfiguratorRenderingContext ctx=new SwingConfiguratorRenderingContext(config,saver);
				getConfiguratorRenderer(c).render(c,ctx);
			}
			tab.add(config,BorderLayout.NORTH);
		}
		saver.run();
		tab.add(panel,BorderLayout.SOUTH);
	}
	
	/**
	 * DataRendererRenderer implementation for TableDataRenderer.
	 * Renders table components into the Swing panel by accessing the data renderer's properties.
	 */
	private static class SwingTableDataRendererRenderer implements DataRendererRenderer<TableDataRenderer<?>>{
		@SuppressWarnings("PMD.UseArraysAsList")
		public void render(TableDataRenderer<?> dataRenderer,DataRenderingContext ctx){
			SwingDataRenderingContext swingCtx=(SwingDataRenderingContext)ctx;

			// Access data renderer properties
			ArrayList<?> elements=dataRenderer.getElementSupplier().get();
			String title=dataRenderer.getTitle();
			boolean allowExport=dataRenderer.getAllowExport();

			// Build the table component
			JComponent component;
			if(elements.isEmpty()){
				component=new JTable();
			}else{
				Field[] allFields=elements.get(0).getClass().getFields();
				ArrayList<Field> fields=new ArrayList<>();
				for(Field f:allFields)
					if(f.isAnnotationPresent(EditorEntry.class)) fields.add(f);
				DefaultTableModel m=new DefaultTableModel(fields.stream().map(f->f.getAnnotation(EditorEntry.class).translation()).toArray(),0);
				JTable table=new JTable(m);
				table.setDefaultEditor(Object.class,new EmptyCellEditor());
				table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
				for(Object t:elements){
					Object[] o=new Object[fields.size()];
					for(int i=0;i<o.length;++i)
						try{
							o[i]=fields.get(i).get(t);
						}catch(IllegalAccessException ex){
							throw new IllegalStateException(ex);
						}
					m.addRow(o);
				}
				JScrollPane s=new JScrollPane(table,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
				if(title!=null) s.setBorder(BorderFactory.createTitledBorder(title));

				if(allowExport){
					GridBagLayout l=new GridBagLayout();
					l.rowWeights=l.columnWeights=new double[]{0.2,0.2,0.2,0.2,0.2};
					JPanel p=new JPanel(l);
					GridBagConstraints c1=new GridBagConstraints(),c2=new GridBagConstraints();
					HButton export=new HButton(){
						public void paint(Graphics g){
							Graphics2D g2=(Graphics2D)g;
							g2.setColor(new Color(12,54,3));
							g2.fillRect(0,0,getWidth(),getHeight());
							g2.setStroke(new BasicStroke(getHeight()/30));
							g2.setColor(Color.BLACK);
							g2.drawRect(0,0,getWidth(),getHeight());
							g2.setColor(new Color(0,0,0,(getModel().isPressed()?50:10)+scale*4));
							g2.fillRect(0,0,getWidth(),getHeight());
							FontMetrics fm=g2.getFontMetrics();
							g2.setColor(Color.WHITE);
							g2.drawString("Экспорт",(getWidth()-fm.stringWidth("Экспорт"))/2,(getHeight()+fm.getAscent()+fm.getLeading()-fm.getDescent())/2);
						}
					};
					export.addActionListener(e->{
						String home=System.getProperty("user.home");
						String outputPath=home+"\\Downloads\\"+title+".xlsx";
						try{
							ExcelUtils.saveInstances(new File(outputPath),elements);
						}catch(IllegalAccessException ex){
							new Message("Не удалось экспортировать таблицу",Color.RED);
						}
						new Message("Файл экспортирован. Проверьте папку Загрузки",Color.GREEN);
					});
					c1.gridx=c1.gridy=4;
					c1.gridwidth=c1.gridheight=1;
					c1.weightx=c1.weighty=0.2;
					c1.fill=GridBagConstraints.BOTH;
					p.add(export,c1);
					c2.gridx=c2.gridy=0;
					c2.gridwidth=c2.gridheight=GridBagConstraints.REMAINDER;
					c2.weightx=c2.weighty=1;
					c2.fill=GridBagConstraints.BOTH;
					p.add(s,c2);
					component=p;
				}else{
					component=s;
				}
			}

			// Wrap and add to panel
			if(component!=null){
				JPanel wrapper=new JPanel(new GridLayout(1,1));
				wrapper.setPreferredSize(swingCtx.getComponentSize());
				wrapper.add(component);
				swingCtx.getPanel().add(wrapper);
			}
		}
	}
	/**
	 * DataRendererRenderer implementation for AnswerDataRenderer.
	 * Renders answer list components into the Swing panel by accessing the data renderer's properties.
	 */
	private static class SwingAnswerDataRendererRenderer implements DataRendererRenderer<AnswerDataRenderer>{
		public void render(AnswerDataRenderer dataRenderer,DataRenderingContext ctx){
			SwingDataRenderingContext swingCtx=(SwingDataRenderingContext)ctx;

			// Access data renderer properties
			Supplier<String>[] answerers=dataRenderer.getAnswerers();

			// Build the answer list component
			Vector<String> answers=new Vector<>();
			for(Supplier<String> s:answerers)
				answers.add(s.get());
			JList<String> list=new JList<>(answers);
			list.setFont(new Font(Font.DIALOG,Font.PLAIN,Root.SCREEN_SIZE.height/50));

			// Wrap and add to panel
			JPanel wrapper=new JPanel(new GridLayout(1,1));
			wrapper.setPreferredSize(swingCtx.getComponentSize());
			wrapper.add(list);
			swingCtx.getPanel().add(wrapper);
		}
	}
	/**
	 * DataRendererRenderer implementation for ChartDataRenderer.
	 * Renders chart components into the Swing panel by accessing the data renderer's properties.
	 */
	private static class SwingChartDataRendererRenderer implements DataRendererRenderer<ChartDataRenderer>{
		public void render(ChartDataRenderer dataRenderer,DataRenderingContext ctx){
			SwingDataRenderingContext swingCtx=(SwingDataRenderingContext)ctx;

			// Access data renderer properties
			ChartDataRenderer.ChartMode mode=dataRenderer.getMode();
			Supplier<ArrayList<Object[]>> elementSupplier=dataRenderer.getElementSupplier();
			String title=dataRenderer.getTitle();
			Object[] args=dataRenderer.getArgs();

			// Build the chart component
			if(mode==null) throw new NullPointerException("Mode cannot be null.");
			Chart<?,?> chart=switch(mode){
				case LINEAR_EACH->{
					XYChart c=new XYChartBuilder().theme(ChartTheme.Matlab).title(title==null?"":title).build();
					for(Object[] s:elementSupplier.get()){
						c.addSeries((String)s[0],(int[])s[1],(int[])s[2]);
					}
					yield c;
				}
				case LINEAR_COMPARE->{
					XYChart c=new XYChartBuilder().theme(ChartTheme.Matlab).title(title==null?"":title).xAxisTitle(args.length==0?"":(String)args[0]).yAxisTitle(args.length<2?"":(String)args[1]).build();
					ArrayList<Object[]> a=elementSupplier.get();
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
				case PIE->{
					PieChart c=new PieChartBuilder().theme(ChartTheme.Matlab).title(title==null?"":title).build();
					for(Object[] s:elementSupplier.get())
						c.addSeries((String)s[0],(int)s[1]);
					yield c;
				}
				case BAR->{
					CategoryChart c=new CategoryChartBuilder().theme(ChartTheme.Matlab).title(title==null?"":title).xAxisTitle(args.length==0?"":(String)args[0]).yAxisTitle(args.length<2?"":(String)args[1]).build();
					ArrayList<Object[]> a=elementSupplier.get();
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

			// Wrap and add to panel
			if(chart!=null){
				XChartPanel<?> chartPanel=new XChartPanel<>(chart);
				JPanel wrapper=new JPanel(new GridLayout(1,1));
				wrapper.setPreferredSize(swingCtx.getComponentSize());
				wrapper.add(chartPanel);
				swingCtx.getPanel().add(wrapper);
			}
		}
	}
}
