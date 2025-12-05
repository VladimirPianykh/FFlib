package com.bpa4j.data.file.features;

import com.bpa4j.core.ProgramStarter;
import com.bpa4j.data.file.FileData;
import com.bpa4j.defaults.features.transmission_contracts.MappedList;
import com.bpa4j.feature.FeatureSaver;

/**
 * Saver for MappedList feature.
 * @author AI-generated
 */
@SuppressWarnings("rawtypes")
public class MappedListSaver implements FeatureSaver<MappedList>{
	@SuppressWarnings("unchecked")
	public void save(MappedList f){
		FileData data=(FileData)ProgramStarter.getStorageManager().getStorage();
		// Save objects map - it's real state data, not cache
		Object[] featureData=new Object[]{
			new java.util.HashMap<>(f.getObjects())
		};
		data.getFeaturesData().put(f.getFeatureName(),featureData);
	}

	@SuppressWarnings("unchecked")
	public void load(MappedList f){
		FileData data=(FileData)ProgramStarter.getStorageManager().getStorage();
		Object[] featureData=data.getFeaturesData().get(f.getFeatureName());
		if(featureData!=null){
			java.util.HashMap objects=(java.util.HashMap)featureData[0];

			f.getObjects().clear();
			f.getObjects().putAll(objects);
		}
	}
}
