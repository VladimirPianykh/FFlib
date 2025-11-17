package com.bpa4j.defaults.features.transmission_contracts;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import com.bpa4j.Dater;
import com.bpa4j.core.Editable;
import com.bpa4j.core.ProgramStarter;
import com.bpa4j.feature.Feature;
import com.bpa4j.feature.FeatureTransmissionContract;

public class DatedList<T extends Editable> implements FeatureTransmissionContract{
	private static final Map<String,Feature<?>> registeredDatedLists;
	static{
		HashMap<String,Feature<?>> reg=new HashMap<>();
		ProgramStarter.getStorageManager().getStorage().putGlobal("BL:DatedList",reg);
		registeredDatedLists=reg;
	}

	public Supplier<Set<T>> getObjectsOp;
	public Supplier<HashMap<T,Dater<T>>> getObjectsWithDatersOp;
	public Consumer<T> removeObjectOp;
	public BiConsumer<T,Dater<T>> putObjectOp;
	public Consumer<Supplier<Dater<T>>> setDateProviderOp;
	public Supplier<Supplier<Dater<T>>> getDateProviderOp;
	public Runnable clearObjectsOp;

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

	public static <T extends Editable> Feature<DatedList<T>> registerList(String name,Class<T> clazz){
		DatedList<T> datedList=new DatedList<>(name,clazz);
		Feature<DatedList<T>> feature=new Feature<>(datedList);
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
