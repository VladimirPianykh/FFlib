package com.bpa4j.defaults.ftr_attributes.data_renderers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;
import javax.swing.JComponent;
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

/**
 * Data renderer for displaying various types of charts.
 * @author AI-generated
 */
public final class ChartDataRenderer implements Report.DataRenderer{
	// private Function<ChartDataRenderer,DataRendererRenderer<ChartDataRenderer>> rendererSource;
	/*
	 * Type - Formatting - Arguments:
	 * <ul>
	 * <li>LINEAR_EACH - {title, x array, y array} - {}</li>
	 * <li>LINEAR_COMPARE - {group name, x, y} - {x axis name, y axis name}</li>
	 * <li>PIE - {title, value} - {}</li>
	 * <li>BAR - {group name, category name, value} - {x axis name, y axis name}</li>
	 * </ul>
	 */
	public static enum ChartMode{
		LINEAR_EACH,
		LINEAR_COMPARE,
		PIE,
		BAR
	}
	private ChartMode mode;
	private Supplier<? extends List<Object[]>> elementSupplier;
	private String title;
	private Object[] args;

	/**
	 * @param mode - chart type to display
	 * @param elementSupplier <p>- supplier of rendering data.</p>
	 */
	public ChartDataRenderer(ChartMode mode,Supplier<ArrayList<Object[]>> elementSupplier,Object...args){
		this.mode=mode;
		this.elementSupplier=elementSupplier;
		this.args=args.clone();
	}
	public ChartDataRenderer(ChartMode mode,Supplier<ArrayList<Object[]>> elementSupplier,String title,Object...args){
		this(mode,elementSupplier,args);
		this.title=title;
	}

	// @Override
	// @SuppressWarnings("unchecked")
	// public <D extends Report.DataRenderer> void setRendererSource(Function<D,? extends DataRendererRenderer<D>> rendererSource){
	// 	this.rendererSource=(Function<ChartDataRenderer,DataRendererRenderer<ChartDataRenderer>>)(Object)rendererSource;
	// }

	public JComponent getComponent(){
		if(mode==null) throw new NullPointerException("Mode cannot be null.");
		Chart<?,?> chart=switch(mode){
			case LINEAR_EACH -> {
				XYChart c=new XYChartBuilder()
					.theme(ChartTheme.Matlab)
						.title(title==null?"":title)
					.build();
				for(Object[] s:elementSupplier.get()){
					c.addSeries((String)s[0],(int[])s[1],(int[])s[2]);
				}
				yield c;
			}
			case LINEAR_COMPARE -> {
				XYChart c=new XYChartBuilder()
					.theme(ChartTheme.Matlab)
						.title(title==null?"":title).xAxisTitle(args.length==0?"":(String)args[0]).yAxisTitle(args.length<2?"":(String)args[1])
					.build();
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
				PieChart c=new PieChartBuilder()
					.theme(ChartTheme.Matlab)
						.title(title==null?"":title)
					.build();
				for(Object[] s:elementSupplier.get())
					c.addSeries((String)s[0],(int)s[1]);
				yield c;
			}
			case BAR -> {
				CategoryChart c=new CategoryChartBuilder()
					.theme(ChartTheme.Matlab)
						.title(title==null?"":title).xAxisTitle(args.length==0?"":(String)args[0]).yAxisTitle(args.length<2?"":(String)args[1])
					.build();
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
		if(chart==null) return null;
		else return new XChartPanel<>(chart);
	}

	public ChartMode getMode(){
		return mode;
	}

	public Supplier<? extends List<Object[]>> getElementSupplier(){
		return elementSupplier;
	}

	public String getTitle(){
		return title;
	}

	public Object[] getArgs(){
		return args;
	}

	// public Function<ChartDataRenderer,DataRendererRenderer<ChartDataRenderer>> getRendererSource(){
	// 	return rendererSource;
	// }
}
