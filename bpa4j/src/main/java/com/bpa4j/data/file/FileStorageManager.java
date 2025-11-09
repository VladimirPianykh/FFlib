package com.bpa4j.data.file;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import com.bpa4j.core.Data;
import com.bpa4j.core.Root;
import com.bpa4j.core.StorageManager;
import com.bpa4j.core.User;
import com.bpa4j.core.UserSaver;
import com.bpa4j.feature.Feature;
import com.bpa4j.feature.FeatureModel;
import com.bpa4j.feature.FeatureSaver;
import com.bpa4j.feature.FeatureTransmissionContract;

public class FileStorageManager implements StorageManager{
	private final Map<Class<?>,Supplier<? extends FeatureModel<?>>> modelSuppliers=new HashMap<>();
	private final Map<Class<?>,Supplier<? extends FeatureSaver<?>>> saverSuppliers=new HashMap<>();
	public final boolean firstLaunch;
	public final String version;
	private FileUserSaver userSaver=new FileUserSaver(this);
	private FileData data=new FileData();
	private File folder;
	public FileStorageManager(File folder){
		this.folder=folder;
		folder.mkdirs();
		URL url=Root.CL.getResource("resources/initial/Data.ser");
		if(url==null) url=Root.RCL.getResource("resources/initial/Data.ser");
		InputStream is=Root.CL.getResourceAsStream("resources/version.txt");
		if(is==null) is=Root.RCL.getResourceAsStream("resources/version.txt");
		try{
			version=is==null?"":new String(is.readAllBytes());
		}catch(IOException ex){
			throw new UncheckedIOException(ex);
		}
		firstLaunch=(!new File(folder+"Data.ser"+version).exists()&&url==null);
	}
	public File getFolder(){
		return folder;
	}
	public String getVersion(){
		return version;
	}
	public boolean isFirstLaunch(){
		return firstLaunch;
	}
	@SuppressWarnings("unchecked")
	public <F extends FeatureTransmissionContract> FeatureModel<F> getFeatureModel(F f){
		return (FeatureModel<F>)modelSuppliers.get(f.getClass()).get();
	}
	@SuppressWarnings("unchecked")
	public <F extends FeatureTransmissionContract> FeatureSaver<F> getFeatureSaver(F f){
		return (FeatureSaver<F>)saverSuppliers.get(f.getClass()).get();
	}
	public UserSaver getUserSaver(){
		return userSaver;
	}
	public Data getStorage(){
		return data;
	}
	public void close(){
		User.save();
		for(Feature<?> f:data.getRegisteredFeatures())
			f.save();
		data.save(new File(folder,"data.bin"));
	}
	public <F extends FeatureTransmissionContract> void putModel(Class<F> f,Supplier<FeatureModel<F>> model){
		modelSuppliers.put(f,model);
	}
	public <F extends FeatureTransmissionContract> void putSaver(Class<F> f,Supplier<FeatureSaver<F>> saver){
		saverSuppliers.put(f,saver);
	}
}
