package com.bpa4j.defaults.features.transmission_contracts;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import com.bpa4j.feature.FeatureTransmissionContract;

public class ItemList<T extends Serializable> implements FeatureTransmissionContract{
	public Supplier<ArrayList<T>> getObjectsOp;
	public Supplier<T> createObjectOp;
	public Consumer<T> addObjectOp;
	public Consumer<T> removeObjectOp;
	public Consumer<Function<T,String>> setSlicerOp;
	public Consumer<Supplier<ArrayList<T>>> setElementSupplierOp;
	public Supplier<Boolean> getAllowCreationOp;
	public Consumer<Boolean> setAllowCreationOp;
	public Consumer<Consumer<List<T>>> addCollectiveActionOp;
	public Consumer<Consumer<T>> addSingularActionOp;
	private String name;
	public ItemList(String name){
		this.name=name;
	}
	public void setGetObjectsOp(Supplier<ArrayList<T>> getObjectsOp){
		this.getObjectsOp=getObjectsOp;
	}
	public void setCreateObjectOp(Supplier<T> createObjectOp){
		this.createObjectOp=createObjectOp;
	}
	public void setAddObjectOp(Consumer<T> addObjectOp){
		this.addObjectOp=addObjectOp;
	}
	public void setRemoveObjectOp(Consumer<T> removeObjectOp){
		this.removeObjectOp=removeObjectOp;
	}
	public void setSetSlicerOp(Consumer<Function<T,String>> setSlicerOp){
		this.setSlicerOp=setSlicerOp;
	}
	public void setSetElementSupplierOp(Consumer<Supplier<ArrayList<T>>> setElementSupplierOp){
		this.setElementSupplierOp=setElementSupplierOp;
	}
	public void setGetAllowCreationOp(Supplier<Boolean> getAllowCreationOp){
		this.getAllowCreationOp=getAllowCreationOp;
	}
	public void setSetAllowCreationOp(Consumer<Boolean> setAllowCreationOp){
		this.setAllowCreationOp=setAllowCreationOp;
	}
	public void setAddCollectiveActionOp(Consumer<Consumer<List<T>>> addCollectiveActionOp){
		this.addCollectiveActionOp=addCollectiveActionOp;
	}
	public void setAddSingularActionOp(Consumer<Consumer<T>> addSingularActionOp){
		this.addSingularActionOp=addSingularActionOp;
	}
	public ArrayList<T> getObjects(){
		return getObjectsOp.get();
	}
	public T createObject(){
		return createObjectOp.get();
	}
	public void addObject(T object){
		addObjectOp.accept(object);
	}
	public void removeObject(T object){
		removeObjectOp.accept(object);
	}
	public void setSlicer(Function<T,String> slicer){
		setSlicerOp.accept(slicer);
	}
	public void setElementSupplier(Supplier<ArrayList<T>> supplier){
		setElementSupplierOp.accept(supplier);
	}
	public boolean getAllowCreation(){
		return getAllowCreationOp.get();
	}
	public void setAllowCreation(boolean allow){
		setAllowCreationOp.accept(allow);
	}
	public void addCollectiveAction(Consumer<List<T>> action){
		addCollectiveActionOp.accept(action);
	}
	public void addSingularAction(Consumer<T> action){
		addSingularActionOp.accept(action);
	}
	public String getFeatureName(){
		return name;
	}
}
