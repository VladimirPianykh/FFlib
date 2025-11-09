package com.bpa4j.defaults.features.models;

import java.util.function.Function;
import javax.swing.JComponent;
import com.bpa4j.core.Editable;
import com.bpa4j.core.EditableGroup;
import com.bpa4j.core.ProgramStarter;
import com.bpa4j.defaults.features.transmission_contracts.EditableList;
import com.bpa4j.feature.FeatureModel;

public class EditableListModel<T extends Editable> implements FeatureModel<EditableList<T>>{
	private EditableList<T> ftc;
	private transient Function<T,JComponent> componentProvider;
	private transient boolean canCreate;
	private Class<T> type;
	public EditableListModel(EditableList<T> ftc,Class<T> type){
		this.ftc=ftc;
		this.type=type;
		ftc.setGetGroupOp(()->getGroup());
		ftc.setSetComponentProviderOp((provider)->setComponentProvider(provider));
		ftc.setGetCanCreateOp(()->getCanCreate());
		ftc.setSetCanCreateOp((canCreate)->setCanCreate(canCreate));
		ftc.setGetComponentProviderOp(()->getComponentProvider());
	}
	public EditableList<T> getTransmissionContract(){
		return ftc;
	}
	public EditableGroup<T> getGroup(){
		return ProgramStarter.getStorageManager().getStorage().getGroup(type);
	}
	public void setComponentProvider(Function<T,JComponent> provider){
		this.componentProvider=provider;
	}
	public boolean getCanCreate(){
		return canCreate;
	}
	public void setCanCreate(boolean canCreate){
		this.canCreate=canCreate;
	}
	public Function<T,JComponent> getComponentProvider(){
		return componentProvider;
	}
}
