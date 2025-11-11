package com.bpa4j.defaults.features.transmission_contracts;

import java.util.HashMap;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import com.bpa4j.Dater;
import com.bpa4j.core.Editable;
import com.bpa4j.feature.FeatureTransmissionContract;

public class DatedList<T extends Editable> implements FeatureTransmissionContract{
	public Supplier<Set<T>> getObjectsOp;
	public Supplier<HashMap<T,Dater<T>>> getObjectsWithDatersOp;
	public Supplier<T> createObjectOp;
	public Consumer<T> removeObjectOp;
	public BiConsumer<T,Dater<T>> putObjectOp;
	public Consumer<Supplier<Dater<T>>> setDateProviderOp;
	public Supplier<Supplier<Dater<T>>> getDateProviderOp;
	public Runnable clearObjectsOp;
	private String name;
	public DatedList(String name){
		this.name=name;
	}
	public void setGetObjectsOp(Supplier<Set<T>> getObjectsOp){
		this.getObjectsOp=getObjectsOp;
	}
	public void setGetObjectsWithDatersOp(Supplier<HashMap<T,Dater<T>>> getObjectsWithDatersOp){
		this.getObjectsWithDatersOp=getObjectsWithDatersOp;
	}
	public void setCreateObjectOp(Supplier<T> createObjectOp){
		this.createObjectOp=createObjectOp;
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
	public T createObject(){
		return createObjectOp.get();
	}
	public void setDateProvider(Supplier<Dater<T>> provider){
		setDateProviderOp.accept(provider);
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
}
