package com.bpa4j.defaults.features.transmission_contracts;

import java.io.Serializable;
import java.util.HashMap;
import java.util.function.Function;
import java.util.function.Supplier;
import com.bpa4j.core.Editable;
import com.bpa4j.feature.FeatureTransmissionContract;

public class MappedList<T extends Editable,V extends Serializable> implements FeatureTransmissionContract{
	public Supplier<HashMap<T,V>> getObjectsOp;
	public Function<T,V> getMappingOp;
	public Supplier<T> createObjectOp;
	private String name;
	public MappedList(String name){
		this.name=name;
	}
	public void setGetObjectsOp(Supplier<HashMap<T,V>> getObjectsOp){
		this.getObjectsOp=getObjectsOp;
	}
	public void setGetMappingOp(Function<T,V> getMappingOp){
		this.getMappingOp=getMappingOp;
	}
	public void setCreateObjectOp(Supplier<T> createObjectOp){
		this.createObjectOp=createObjectOp;
	}
	public HashMap<T,V> getObjects(){
		return getObjectsOp.get();
	}
	public V getMapping(T t){
		return getMappingOp.apply(t);
	}
	public T createObject(){
		return createObjectOp.get();
	}
	public String getFeatureName(){
		return name;
	}
}
