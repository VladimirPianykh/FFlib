package com.bpa4j.data.file.features;

import com.bpa4j.core.ProgramStarter;
import com.bpa4j.data.file.FileData;
import com.bpa4j.defaults.features.transmission_contracts.Calendar;
import com.bpa4j.feature.FeatureSaver;

/**
 * Saver for Calendar feature.
 * @author AI-generated
 */
@SuppressWarnings("rawtypes")
public class CalendarSaver implements FeatureSaver<Calendar>{
	@SuppressWarnings("unchecked")
	public void save(Calendar f){
		FileData data=(FileData)ProgramStarter.getStorageManager().getStorage();
		if(f.getEventFiller()!=null){
			// Save events - they are real state data, not cache
			Object[] featureData=new Object[]{
				new java.util.HashMap<>(f.getEvents())
			};
			data.getFeaturesData().put(f.getFeatureName(),featureData);
		}
		// Else do not save - they are provided by transient field eventFiller
	}

	@SuppressWarnings("unchecked")
	public void load(Calendar f){
		FileData data=(FileData)ProgramStarter.getStorageManager().getStorage();
		Object[] featureData=data.getFeaturesData().get(f.getFeatureName());
		if(featureData!=null){
			java.util.HashMap<java.time.LocalDate,java.util.List<?>> events=(java.util.HashMap<java.time.LocalDate,java.util.List<?>>)featureData[0];

			f.clearEvents();
			// Restore events by adding them to each date's list
			for(java.util.Map.Entry<java.time.LocalDate,java.util.List<?>> entry:events.entrySet()){
				java.time.LocalDate date=entry.getKey();
				java.util.List<?> eventList=entry.getValue();
				((java.util.List<Object>)f.getEventList(date)).addAll(eventList);
			}
		}
	}
}
