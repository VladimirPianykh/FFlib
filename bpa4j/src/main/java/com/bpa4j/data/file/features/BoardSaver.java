package com.bpa4j.data.file.features;

import com.bpa4j.core.ProgramStarter;
import com.bpa4j.data.file.FileData;
import com.bpa4j.defaults.features.transmission_contracts.Board;
import com.bpa4j.feature.FeatureSaver;

/**
 * Saver for Board feature.
 * @author AI-generated
 */
@SuppressWarnings("rawtypes")
public class BoardSaver implements FeatureSaver<Board>{
	public void save(Board f){
		FileData data=(FileData)ProgramStarter.getStorageManager().getStorage();
		// Do not save objects (may be cache from transient elementSupplier) or allowCreation (configuration)
		Object[] featureData=new Object[]{};
		data.getFeaturesData().put(f.getFeatureName(),featureData);
	}

	public void load(Board f){
		FileData data=(FileData)ProgramStarter.getStorageManager().getStorage();
		Object[] featureData=data.getFeaturesData().get(f.getFeatureName());
		if(featureData!=null){
			// Nothing to restore - objects are cache, allowCreation is configuration
		}
	}
}
