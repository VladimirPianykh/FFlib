package com.bpa4j.data.file.features;

import com.bpa4j.core.ProgramStarter;
import com.bpa4j.data.file.FileData;
import com.bpa4j.defaults.features.transmission_contracts.EditableList;
import com.bpa4j.feature.FeatureSaver;

/**
 * Saver for EditableList feature.
 * @author AI-generated
 */
@SuppressWarnings("rawtypes")
public class EditableListSaver implements FeatureSaver<EditableList>{
	public void save(EditableList f){
		FileData data = (FileData) ProgramStarter.getStorageManager().getStorage();
		// Do not save group (obtained from Storage) or canCreate (configuration)
		Object[] featureData = new Object[]{};
		data.getFeaturesData().put(f.getFeatureName(), featureData);
	}
	
	public void load(EditableList f){
		FileData data = (FileData) ProgramStarter.getStorageManager().getStorage();
		Object[] featureData = data.getFeaturesData().get(f.getFeatureName());
		if(featureData != null){
			// Nothing to restore - group is from Storage, canCreate is configuration
		}
	}
}
