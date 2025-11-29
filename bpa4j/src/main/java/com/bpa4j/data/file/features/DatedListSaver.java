package com.bpa4j.data.file.features;

import java.util.HashMap;
import com.bpa4j.core.ProgramStarter;
import com.bpa4j.data.file.FileData;
import com.bpa4j.defaults.features.transmission_contracts.DatedList;
import com.bpa4j.feature.FeatureSaver;

/**
 * Saver for DatedList feature.
 * @author AI-generated
 */
@SuppressWarnings("rawtypes")
public class DatedListSaver implements FeatureSaver<DatedList>{
	public void save(DatedList f){
		FileData data=(FileData)ProgramStarter.getStorageManager().getStorage();
		Object[] featureData=new Object[]{f.getObjects(),f.getObjectsWithDaters()};
		data.getFeaturesData().put(f.getFeatureName(),featureData);
	}

	@SuppressWarnings("unchecked")
	public void load(DatedList f){
		FileData data=(FileData)ProgramStarter.getStorageManager().getStorage();
		Object[] featureData=data.getFeaturesData().get(f.getFeatureName());
		if(featureData!=null){
			HashMap objectsWithDaters=(HashMap)featureData[1];

			f.clearObjects();

			// Restore objects with daters  
			for(Object entry:objectsWithDaters.entrySet()){
				java.util.Map.Entry e=(java.util.Map.Entry)entry;
				com.bpa4j.core.Editable key=(com.bpa4j.core.Editable)e.getKey();
				com.bpa4j.Dater dater=(com.bpa4j.Dater)e.getValue();
				((DatedList)f).putObject(key,dater);
			}
		}
	}
}
