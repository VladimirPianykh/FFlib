package com.bpa4j.defaults.features.transmission_contracts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.swing.JComponent;
import com.bpa4j.core.ProgramStarter;
import com.bpa4j.feature.Feature;
import com.bpa4j.feature.FeatureTransmissionContract;

public class Report implements FeatureTransmissionContract{
	private static final Map<String,Feature<?>> registeredReports;
	static{
		HashMap<String,Feature<?>>reg=new HashMap<>();
		ProgramStarter.getStorageManager().getStorage().putGlobal("BL:Report",reg);
		registeredReports=reg;
	}
	
	public Supplier<ArrayList<Supplier<JComponent>>> getDataRenderersOp;
	public Supplier<ArrayList<Function<Runnable,JComponent>>> getConfiguratorsOp;
	public Consumer<Supplier<JComponent>> addDataRendererOp;
	public Consumer<Function<Runnable,JComponent>> addConfiguratorOp;
	private String name;
	public Report(String name){
		this.name=name;
	}
	public void setGetDataRenderersOp(Supplier<ArrayList<Supplier<JComponent>>> getDataRenderersOp){
		this.getDataRenderersOp=getDataRenderersOp;
	}
	public void setGetConfiguratorsOp(Supplier<ArrayList<Function<Runnable,JComponent>>> getConfiguratorsOp){
		this.getConfiguratorsOp=getConfiguratorsOp;
	}
	public void setAddDataRendererOp(Consumer<Supplier<JComponent>> addDataRendererOp){
		this.addDataRendererOp=addDataRendererOp;
	}
	public void setAddConfiguratorOp(Consumer<Function<Runnable,JComponent>> addConfiguratorOp){
		this.addConfiguratorOp=addConfiguratorOp;
	}
	public ArrayList<Supplier<JComponent>> getDataRenderers(){
		return getDataRenderersOp.get();
	}
	public ArrayList<Function<Runnable,JComponent>> getConfigurators(){
		return getConfiguratorsOp.get();
	}
	public Report addDataRenderer(Supplier<JComponent> dataRenderer){
		addDataRendererOp.accept(dataRenderer);
		return this;
	}
	public Report addConfigurator(Function<Runnable,JComponent> configurator){
		addConfiguratorOp.accept(configurator);
		return this;
	}
	public String getFeatureName(){
		return name;
	}
	
	public static Feature<Report> registerReport(String name) {
		Report report = new Report(name);
		Feature<Report> feature = new Feature<>(report);
		registeredReports.put(name, feature);
		return feature;
	}
	
	public static Report getReport(String name) {
		Feature<?> feature = registeredReports.get(name);
		if (feature == null) {
			throw new IllegalArgumentException("Report with name '" + name + "' not found. Make sure to register it first.");
		}
		return (Report) feature.getContract();
	}
}
