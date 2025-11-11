package com.bpa4j.data.file.features;

import com.bpa4j.core.ProgramStarter;
import com.bpa4j.data.file.FileData;
import com.bpa4j.defaults.features.transmission_contracts.Report;
import com.bpa4j.feature.FeatureSaver;

/**
 * Saver for Report feature.
 * @author AI-generated
 */
public class ReportSaver implements FeatureSaver<Report>{
	public void save(Report f){
		FileData data = (FileData) ProgramStarter.getStorageManager().getStorage();
		Object[] featureData = new Object[]{
			f.getDataRenderers(),
			f.getConfigurators()
		};
		data.getFeaturesData().put(f.getFeatureName(), featureData);
	}
	
	public void load(Report f){
		FileData data = (FileData) ProgramStarter.getStorageManager().getStorage();
		Object[] featureData = data.getFeaturesData().get(f.getFeatureName());
		if(featureData != null){
			// Renderers and configurators are loaded via add operations
		}
	}
}
