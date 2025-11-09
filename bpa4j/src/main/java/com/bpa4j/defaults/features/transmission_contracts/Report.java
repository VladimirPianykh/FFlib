package com.bpa4j.defaults.features.transmission_contracts;

import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.swing.JComponent;
import com.bpa4j.feature.FeatureTransmissionContract;

public class Report implements FeatureTransmissionContract{
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
	public void addDataRenderer(Supplier<JComponent> dataRenderer){
		addDataRendererOp.accept(dataRenderer);
	}
	public void addConfigurator(Function<Runnable,JComponent> configurator){
		addConfiguratorOp.accept(configurator);
	}
	public String getFeatureName(){
		return name;
	}
}
