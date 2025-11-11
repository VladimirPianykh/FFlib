package com.bpa4j.defaults.features.transmission_contracts;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.swing.JComponent;
import com.bpa4j.core.Editable;
import com.bpa4j.core.EditableGroup;
import com.bpa4j.feature.FeatureTransmissionContract;

public class EditableList<T extends Editable> implements FeatureTransmissionContract{
	public Supplier<EditableGroup<T>> getGroupOp;
	public Supplier<T> createObjectOp;
	public Consumer<Function<T,JComponent>> setComponentProviderOp;
	public BooleanSupplier getCanCreateOp;
	public Consumer<Boolean> setCanCreateOp;
	public Supplier<Function<T,JComponent>> getComponentProviderOp;
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
	public void setSetComponentProviderOp(Consumer<Function<T,JComponent>> setComponentProviderOp){
		this.setComponentProviderOp=setComponentProviderOp;
	}
	public void setGetCanCreateOp(BooleanSupplier getCanCreateOp){
		this.getCanCreateOp=getCanCreateOp;
	}
	public void setSetCanCreateOp(Consumer<Boolean> setCanCreateOp){
		this.setCanCreateOp=setCanCreateOp;
	}
	public void setGetComponentProviderOp(Supplier<Function<T,JComponent>> getComponentProviderOp){
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
	public void setComponentProvider(Function<T,JComponent> provider){
		setComponentProviderOp.accept(provider);
	}
	public boolean getCanCreate(){
		return getCanCreateOp.getAsBoolean();
	}
	public void setCanCreate(boolean canCreate){
		setCanCreateOp.accept(canCreate);
	}
	public Function<T,JComponent> getComponentProvider(){
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
}
