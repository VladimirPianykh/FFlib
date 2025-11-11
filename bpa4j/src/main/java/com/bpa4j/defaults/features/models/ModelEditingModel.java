package com.bpa4j.defaults.features.models;

import java.util.List;
import com.bpa4j.core.Editable;
import com.bpa4j.core.EditableGroup;
import com.bpa4j.core.ProgramStarter;
import com.bpa4j.defaults.features.transmission_contracts.ModelEditing;
import com.bpa4j.feature.FeatureModel;

public class ModelEditingModel implements FeatureModel<ModelEditing>{
	private ModelEditing ftc;
	@SuppressWarnings("null")
	public ModelEditingModel(ModelEditing ftc){
		this.ftc=ftc;
		ftc.setCreateOp(this::create);
		ftc.setEditOp(this::edit);
		ftc.setDeleteOp(this::delete);
		ftc.setGetGroupsOp(this::getGroups);
	}
	public ModelEditing getTransmissionContract(){
		return ftc;
	}
	public void create(int groupIndex,Editable e){
		ProgramStarter.getRegisteredEditableGroups().get(groupIndex).add(e);
	}
	public void edit(Editable e){
		//Does not change the model
	}
	public void delete(int groupIndex,Editable e){
		ProgramStarter.getRegisteredEditableGroups().get(groupIndex).remove(e);
	}
	public List<EditableGroup<?>> getGroups(){
		return ProgramStarter.getRegisteredEditableGroups();
	}
}
