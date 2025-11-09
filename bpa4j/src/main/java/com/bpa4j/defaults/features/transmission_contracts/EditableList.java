package com.bpa4j.defaults.features.transmission_contracts;

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
	public Supplier<Boolean> getCanCreateOp;
	public Consumer<Boolean> setCanCreateOp;
	public Supplier<Function<T,JComponent>> getComponentProviderOp;
	private String name;
	public EditableList(String name){
		this.name=name;
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
	public void setGetCanCreateOp(Supplier<Boolean> getCanCreateOp){
		this.getCanCreateOp=getCanCreateOp;
	}
	public void setSetCanCreateOp(Consumer<Boolean> setCanCreateOp){
		this.setCanCreateOp=setCanCreateOp;
	}
	public void setGetComponentProviderOp(Supplier<Function<T,JComponent>> getComponentProviderOp){
		this.getComponentProviderOp=getComponentProviderOp;
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
		return getCanCreateOp.get();
	}
	public void setCanCreate(boolean canCreate){
		setCanCreateOp.accept(canCreate);
	}
	public Function<T,JComponent> getComponentProvider(){
		return getComponentProviderOp.get();
	}
	public String getFeatureName(){
		return name;
	}
}
