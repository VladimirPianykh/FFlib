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
	private Supplier<EditableGroup<T>> getGroupOp;
	private Supplier<T> createObjectOp;
	private Consumer<BiConsumer<T,ItemRenderingContext>> setComponentProviderOp;
	private BooleanSupplier getAllowCreationOp;
	private Consumer<Boolean> setAllowCreationOp;
	private Supplier<BiConsumer<T,ItemRenderingContext>> getComponentProviderOp;
	private Consumer<Supplier<EditableGroup<T>>> setGroupSupplierOp;
	private BooleanSupplier getAllowDeletionOp;
	private Consumer<Boolean> setAllowDeletionOp;
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
	public void setGetAllowCreationOp(BooleanSupplier getCanCreateOp){
		this.getAllowCreationOp=getCanCreateOp;
	}
	public void setSetAllowCreationOp(Consumer<Boolean> setCanCreateOp){
		this.setAllowCreationOp=setCanCreateOp;
	}
	public void setGetComponentProviderOp(Supplier<BiConsumer<T,ItemRenderingContext>> getComponentProviderOp){
		this.getComponentProviderOp=getComponentProviderOp;
	}
	public void setSetGroupSupplierOp(Consumer<Supplier<EditableGroup<T>>> setGroupSupplierOp){
		this.setGroupSupplierOp=setGroupSupplierOp;
	}
	public void setGetAllowDeletionOp(BooleanSupplier getAllowDeletionOp){
		this.getAllowDeletionOp=getAllowDeletionOp;
	}
	public void setSetAllowDeletionOp(Consumer<Boolean> setAllowDeletionOp){
		this.setAllowDeletionOp=setAllowDeletionOp;
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
	public boolean getAllowCreation(){
		return getAllowCreationOp.getAsBoolean();
	}
	public void setAllowCreation(boolean canCreate){
		setAllowCreationOp.accept(canCreate);
	}
	public boolean getAllowDeletion(){
		return getAllowDeletionOp.getAsBoolean();
	}
	public void setAllowDeletion(boolean allow){
		setAllowDeletionOp.accept(allow);
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
