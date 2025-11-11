package com.bpa4j.data.file.features;

import com.bpa4j.core.ProgramStarter;
import com.bpa4j.data.file.FileData;
import com.bpa4j.defaults.features.transmission_contracts.ItemList;
import com.bpa4j.feature.FeatureSaver;

/**
 * Saver for ItemList feature.
 * @author AI-generated
 */
@SuppressWarnings("rawtypes")
public class ItemListSaver implements FeatureSaver<ItemList>{
	public void save(ItemList f){
		FileData data = (FileData) ProgramStarter.getStorageManager().getStorage();
		// Do not save objects (may be cache from transient elementSupplier) or allowCreation (configuration)
		Object[] featureData = new Object[]{};
		data.getFeaturesData().put(f.getFeatureName(), featureData);
	}
	
	public void load(ItemList f){
		FileData data = (FileData) ProgramStarter.getStorageManager().getStorage();
		Object[] featureData = data.getFeaturesData().get(f.getFeatureName());
		if(featureData != null){
			// Nothing to restore - objects are cache, allowCreation is configuration
		}
	}
}
