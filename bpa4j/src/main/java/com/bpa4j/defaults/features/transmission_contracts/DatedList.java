package com.bpa4j.defaults.features.transmission_contracts;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import com.bpa4j.Dater;
import com.bpa4j.core.Editable;
import com.bpa4j.feature.Feature;
import com.bpa4j.feature.FeatureTransmissionContract;

public class DatedList<T extends Editable> implements FeatureTransmissionContract{
	private static final Map<String,Feature<? extends DatedList<?>>> registeredDatedLists=new HashMap<>();

	private Supplier<Set<T>> getObjectsOp;
	private Supplier<HashMap<T,Dater<T>>> getObjectsWithDatersOp;
	private Consumer<T> removeObjectOp;
	private BiConsumer<T,Dater<T>> putObjectOp;
	private Consumer<Supplier<Dater<T>>> setDateProviderOp;
	private Supplier<Supplier<Dater<T>>> getDateProviderOp;
	private Runnable clearObjectsOp;

	private String name;
	private Class<T> type;
	public DatedList(String name,Class<T> type){
		this.name=name;
		this.type=type;
	}
	public void setGetObjectsOp(Supplier<Set<T>> getObjectsOp){
		this.getObjectsOp=getObjectsOp;
	}
	public void setGetObjectsWithDatersOp(Supplier<HashMap<T,Dater<T>>> getObjectsWithDatersOp){
		this.getObjectsWithDatersOp=getObjectsWithDatersOp;
	}
	public void setSetDateProviderOp(Consumer<Supplier<Dater<T>>> setDateProviderOp){
		this.setDateProviderOp=setDateProviderOp;
	}
	public void setRemoveObjectOp(Consumer<T> removeObjectOp){
		this.removeObjectOp=removeObjectOp;
	}
	public void setPutObjectOp(BiConsumer<T,Dater<T>> putObjectOp){
		this.putObjectOp=putObjectOp;
	}
	public void setGetDateProviderOp(Supplier<Supplier<Dater<T>>> getDateProviderOp){
		this.getDateProviderOp=getDateProviderOp;
	}
	public void setClearObjectsOp(Runnable clearObjectsOp){
		this.clearObjectsOp=clearObjectsOp;
	}
	public Set<T> getObjects(){
		return getObjectsOp.get();
	}
	public HashMap<T,Dater<T>> getObjectsWithDaters(){
		return getObjectsWithDatersOp.get();
	}
	public DatedList<T> setDateProvider(Supplier<Dater<T>> provider){
		setDateProviderOp.accept(provider);
		return this;
	}
	public void removeObject(T object){
		removeObjectOp.accept(object);
	}
	public void putObject(T object,Dater<T> dater){
		putObjectOp.accept(object,dater);
	}
	public Supplier<Dater<T>> getDateProvider(){
		return getDateProviderOp.get();
	}
	public void clearObjects(){
		clearObjectsOp.run();
	}

	public String getFeatureName(){
		return name;
	}
	public Class<T> getType(){
		return type;
	}

	/**
	 * Registers a dated list, if it is not registered yet.
	 * @return the feature, registered or already present
	 */
	public static <T extends Editable> Feature<DatedList<T>> registerList(String name,Class<T> clazz){
		DatedList<T> datedList=new DatedList<>(name,clazz);
		Feature<DatedList<T>> feature=new Feature<>(datedList);
		feature.load();
		return registerList(name,feature);
	}
	public static <T extends Editable> Feature<DatedList<T>> registerList(String name,Feature<DatedList<T>> feature){
		registeredDatedLists.put(name,feature);
		return feature;
	}

	@SuppressWarnings("unchecked")
	public static <T extends Editable> DatedList<T> getList(String name){
		Feature<?> feature=registeredDatedLists.get(name);
		if(feature==null){ throw new IllegalArgumentException("DatedList with name '"+name+"' not found. Make sure to register it first."); }
		return (DatedList<T>)feature.getContract();
	}
}
