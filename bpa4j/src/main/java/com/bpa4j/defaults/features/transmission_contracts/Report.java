package com.bpa4j.defaults.features.transmission_contracts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;
import com.bpa4j.feature.ConfiguratorRenderer;
import com.bpa4j.feature.DataRendererRenderer;
import com.bpa4j.feature.Feature;
import com.bpa4j.feature.FeatureTransmissionContract;
import com.bpa4j.feature.ReportRenderer;

/**
 * Transmission contract for Report feature.
 */
public class Report implements FeatureTransmissionContract{
	public static interface DataRenderer{
		@SuppressWarnings({"rawtypes","unchecked"})
		default void render(ReportRenderer rr,ReportRenderer.DataRenderingContext ctx){
			DataRendererRenderer r=rr.getDataRendererRenderer(this);
			if(r!=null)r.render(this,ctx);
			else throw new IllegalStateException(getClass().getName()+" does not have renderer.");
		}
	}
	public static interface Configurator{
		@SuppressWarnings({"rawtypes","unchecked"})
		default void render(ReportRenderer rr,ReportRenderer.ConfiguratorRenderingContext ctx){
			ConfiguratorRenderer r=rr.getConfiguratorRenderer(this);
			if(r!=null)r.render(this,ctx);
			else throw new IllegalStateException(getClass().getName()+" does not have renderer.");
		}
	}
	private static final Map<String,Feature<Report>> registeredReports=new HashMap<>();

	private Supplier<ArrayList<DataRenderer>> getDataRenderersOp;
	private Supplier<ArrayList<Configurator>> getConfiguratorsOp;
	private Consumer<DataRenderer> addDataRendererOp;
	private Consumer<Configurator> addConfiguratorOp;
	private String name;
	public Report(String name){
		this.name=name;
	}
	public void setGetDataRenderersOp(Supplier<ArrayList<DataRenderer>> getDataRenderersOp){
		this.getDataRenderersOp=getDataRenderersOp;
	}
	public void setGetConfiguratorsOp(Supplier<ArrayList<Configurator>> getConfiguratorsOp){
		this.getConfiguratorsOp=getConfiguratorsOp;
	}
	public void setAddDataRendererOp(Consumer<DataRenderer> addDataRendererOp){
		this.addDataRendererOp=addDataRendererOp;
	}
	public void setAddConfiguratorOp(Consumer<Configurator> addConfiguratorOp){
		this.addConfiguratorOp=addConfiguratorOp;
	}
	public ArrayList<DataRenderer> getDataRenderers(){
		return getDataRenderersOp.get();
	}
	public ArrayList<Configurator> getConfigurators(){
		return getConfiguratorsOp.get();
	}
	public Report addDataRenderer(DataRenderer dataRenderer){
		addDataRendererOp.accept(dataRenderer);
		return this;
	}
	public Report addConfigurator(Configurator configurator){
		addConfiguratorOp.accept(configurator);
		return this;
	}
	public String getFeatureName(){
		return name;
	}

	/**
	 * Registers a report, if it is not registered yet.
	 * @return the feature, registered or already present
	 */
	public static Feature<Report> registerReport(String name){
		Report report=new Report(name);
		Feature<Report> feature=new Feature<>(report);
		feature.load();
		return registerReport(name,feature);
	}
	public static Feature<Report> registerReport(String name,Feature<Report> feature){
		registeredReports.put(name,feature);
		return feature;
	}

	public static Report getReport(String name){
		Feature<?> feature=registeredReports.get(name);
		if(feature==null){ throw new IllegalArgumentException("Report with name '"+name+"' not found. Make sure to register it first."); }
		return (Report)feature.getContract();
	}
}
