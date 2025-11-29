package com.bpa4j.defaults.features.transmission_contracts;

import java.util.function.BiConsumer;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Supplier;
import com.bpa4j.core.Editable;
import com.bpa4j.core.EditableGroup;
import com.bpa4j.core.RenderingContext;
import com.bpa4j.feature.Feature;
import com.bpa4j.feature.FeatureTransmissionContract;
import java.util.HashMap;
import java.util.Map;

public class EditableList<T extends Editable> implements FeatureTransmissionContract{
	private static final Map<String,Feature<? extends EditableList<?>>> registeredLists=new HashMap<>();
	public static interface ItemRenderingContext extends RenderingContext{}
	public Supplier<EditableGroup<T>> getGroupOp;
	public Supplier<T> createObjectOp;
	public Consumer<BiConsumer<T,ItemRenderingContext>> setComponentProviderOp;
	public BooleanSupplier getCanCreateOp;
	public Consumer<Boolean> setCanCreateOp;
	public Supplier<BiConsumer<T,ItemRenderingContext>> getComponentProviderOp;
	public Consumer<Supplier<EditableGroup<T>>> setGroupSupplierOp;
	private String name;
	private Class<T> type;
	public EditableList(String name,Class<T>type){
		this.name=name;
		this.type=type;
	}
	public void setGetGroupOp(Supplier<EditableGroup<T>> getGroupOp){
		this.getGroupOp=getGroupOp;
	}
	public void setCreateObjectOp(Supplier<T> createObjectOp){
		this.createObjectOp=createObjectOp;
	}
	public void setSetComponentProviderOp(Consumer<BiConsumer<T,ItemRenderingContext>> setComponentProviderOp){
		this.setComponentProviderOp=setComponentProviderOp;
	}
	public void setGetCanCreateOp(BooleanSupplier getCanCreateOp){
		this.getCanCreateOp=getCanCreateOp;
	}
	public void setSetCanCreateOp(Consumer<Boolean> setCanCreateOp){
		this.setCanCreateOp=setCanCreateOp;
	}
	public void setGetComponentProviderOp(Supplier<BiConsumer<T,ItemRenderingContext>> getComponentProviderOp){
		this.getComponentProviderOp=getComponentProviderOp;
	}
	public void setSetGroupSupplierOp(Consumer<Supplier<EditableGroup<T>>> setGroupSupplierOp){
		this.setGroupSupplierOp=setGroupSupplierOp;
	}
	public EditableGroup<T> getGroup(){
		return getGroupOp.get();
	}
	public T createObject(){
		return createObjectOp.get();
	}
	public void setComponentProvider(BiConsumer<T,ItemRenderingContext> provider){
		setComponentProviderOp.accept(provider);
	}
	public boolean getCanCreate(){
		return getCanCreateOp.getAsBoolean();
	}
	public void setCanCreate(boolean canCreate){
		setCanCreateOp.accept(canCreate);
	}
	public BiConsumer<T,ItemRenderingContext> getComponentProvider(){
		return getComponentProviderOp.get();
	}
	public void setGroupSupplier(Supplier<EditableGroup<T>> supplier){
		setGroupSupplierOp.accept(supplier);
	}
	public String getFeatureName(){
		return name;
	}
	public Class<T> getType(){
		return type;
	}

	/**
	 * Registers an editable list, if it is not registered yet.
	 * @return the feature, registered or already present
	 */
	public static <T extends Editable> Feature<EditableList<T>> registerList(String name,Class<T> clazz){
		EditableList<T> list=new EditableList<>(name,clazz);
		Feature<EditableList<T>> feature=new Feature<>(list);
		feature.load();
		return registerList(name,feature);
	}
	public static <T extends Editable> Feature<EditableList<T>> registerList(String name,Feature<EditableList<T>> feature){
		registeredLists.put(name,feature);
		return feature;
	}
	@SuppressWarnings("unchecked")
	public static <T extends Editable> EditableList<T> getList(String name){
		Feature<?> feature=registeredLists.get(name);
		if(feature==null) throw new IllegalArgumentException("EditableList with name '"+name+"' not found. Make sure to register it first.");
		return (EditableList<T>)feature.getContract();
	}
}
