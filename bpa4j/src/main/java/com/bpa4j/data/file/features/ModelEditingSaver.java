package com.bpa4j.data.file.features;

import com.bpa4j.core.ProgramStarter;
import com.bpa4j.data.file.FileData;
import com.bpa4j.defaults.features.transmission_contracts.ModelEditing;
import com.bpa4j.feature.FeatureSaver;

/**
 * Saver for ModelEditing feature.
 * @author AI-generated
 */
public class ModelEditingSaver implements FeatureSaver<ModelEditing>{
	public void save(ModelEditing f){
		FileData data = (FileData) ProgramStarter.getStorageManager().getStorage();
		Object[] featureData = new Object[]{
			f.getGroups()
		};
		data.getFeaturesData().put(f.getFeatureName(), featureData);
	}
	
	public void load(ModelEditing f){
		FileData data = (FileData) ProgramStarter.getStorageManager().getStorage();
		Object[] featureData = data.getFeaturesData().get(f.getFeatureName());
		if(featureData != null){
			// Model is loaded via model supplier
		}
	}
}
