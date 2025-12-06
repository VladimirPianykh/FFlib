package com.bpa4j.defaults.features.models;

import java.util.function.BiConsumer;
import java.util.function.Supplier;
import com.bpa4j.core.Editable;
import com.bpa4j.core.EditableGroup;
import com.bpa4j.core.ProgramStarter;
import com.bpa4j.defaults.features.transmission_contracts.EditableList;
import com.bpa4j.defaults.features.transmission_contracts.EditableList.ItemRenderingContext;
import com.bpa4j.feature.FeatureModel;

public class EditableListModel<T extends Editable> implements FeatureModel<EditableList<T>>{
	private EditableList<T> ftc;
	private transient BiConsumer<T,ItemRenderingContext> componentProvider;
	private transient boolean canCreate;
	private transient boolean allowDeletion;
	private transient Supplier<EditableGroup<T>> groupSupplier;

	@SuppressWarnings("null")
	public EditableListModel(EditableList<T> ftc){
		this.ftc=ftc;
		ftc.setGetGroupOp(this::getGroup);
		ftc.setSetComponentProviderOp(this::setComponentProvider);
		ftc.setGetAllowCreationOp(this::getCanCreate);
		ftc.setSetAllowCreationOp(this::setCanCreate);
		ftc.setGetAllowDeletionOp(this::getAllowDeletion);
		ftc.setSetAllowDeletionOp(this::setAllowDeletion);
		ftc.setGetComponentProviderOp(this::getComponentProvider);
		ftc.setCreateObjectOp(this::createObject);
		ftc.setSetGroupSupplierOp(this::setGroupSupplier);
	}
	public EditableList<T> getTransmissionContract(){
		return ftc;
	}
	public EditableGroup<T> getGroup(){
		if(groupSupplier!=null) return groupSupplier.get();
		return ProgramStarter.getStorageManager().getStorage().getGroup(getTransmissionContract().getType());
	}
	public void setComponentProvider(BiConsumer<T,ItemRenderingContext> provider){
		this.componentProvider=provider;
	}
	public boolean getCanCreate(){
		return canCreate;
	}
	public void setCanCreate(boolean canCreate){
		this.canCreate=canCreate;
	}

	public boolean getAllowDeletion(){
		return allowDeletion;
	}

	public void setAllowDeletion(boolean allow){
		this.allowDeletion=allow;
	}
	public BiConsumer<T,ItemRenderingContext> getComponentProvider(){
		return componentProvider;
	}

	public void setGroupSupplier(Supplier<EditableGroup<T>> supplier){
		this.groupSupplier=supplier;
	}

	public T createObject(){
		try{
			T t=getTransmissionContract().getType().getConstructor().newInstance();
			getGroup().add(t);
			return t;
		}catch(Exception e){
			throw new RuntimeException(e);
		}
	}
}
