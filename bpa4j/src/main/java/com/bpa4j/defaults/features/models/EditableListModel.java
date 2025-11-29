package com.bpa4j.defaults.features.models;

import java.util.function.BiConsumer;
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
	@SuppressWarnings("null")
	public EditableListModel(EditableList<T> ftc){
		this.ftc=ftc;
		ftc.setGetGroupOp(this::getGroup);
		ftc.setSetComponentProviderOp(this::setComponentProvider);
		ftc.setGetCanCreateOp(this::getCanCreate);
		ftc.setSetCanCreateOp(this::setCanCreate);
		ftc.setGetComponentProviderOp(this::getComponentProvider);
	}
	public EditableList<T> getTransmissionContract(){
		return ftc;
	}
	public EditableGroup<T> getGroup(){
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
	public BiConsumer<T,ItemRenderingContext> getComponentProvider(){
		return componentProvider;
	}
}
