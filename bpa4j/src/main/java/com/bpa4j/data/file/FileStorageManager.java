package com.bpa4j.data.file;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import com.bpa4j.core.Data;
import com.bpa4j.core.Root;
import com.bpa4j.core.StorageManager;
import com.bpa4j.core.User;
import com.bpa4j.core.UserSaver;
import com.bpa4j.data.file.features.BoardSaver;
import com.bpa4j.data.file.features.CalendarSaver;
import com.bpa4j.data.file.features.DatedListSaver;
import com.bpa4j.data.file.features.DisposableDocumentSaver;
import com.bpa4j.data.file.features.EditableListSaver;
import com.bpa4j.data.file.features.HistorySaver;
import com.bpa4j.data.file.features.ItemListSaver;
import com.bpa4j.data.file.features.MappedListSaver;
import com.bpa4j.data.file.features.ModelEditingSaver;
import com.bpa4j.data.file.features.ReportSaver;
import com.bpa4j.data.file.features.RoleSettingSaver;
import com.bpa4j.defaults.features.models.BoardModel;
import com.bpa4j.defaults.features.models.CalendarModel;
import com.bpa4j.defaults.features.models.DatedListModel;
import com.bpa4j.defaults.features.models.DisposableDocumentModel;
import com.bpa4j.defaults.features.models.EditableListModel;
import com.bpa4j.defaults.features.models.HistoryModel;
import com.bpa4j.defaults.features.models.ItemListModel;
import com.bpa4j.defaults.features.models.MappedListModel;
import com.bpa4j.defaults.features.models.ModelEditingModel;
import com.bpa4j.defaults.features.models.ReportModel;
import com.bpa4j.defaults.features.models.RoleSettingModel;
import com.bpa4j.defaults.features.transmission_contracts.Board;
import com.bpa4j.defaults.features.transmission_contracts.Calendar;
import com.bpa4j.defaults.features.transmission_contracts.DatedList;
import com.bpa4j.defaults.features.transmission_contracts.DisposableDocument;
import com.bpa4j.defaults.features.transmission_contracts.EditableList;
import com.bpa4j.defaults.features.transmission_contracts.History;
import com.bpa4j.defaults.features.transmission_contracts.ItemList;
import com.bpa4j.defaults.features.transmission_contracts.MappedList;
import com.bpa4j.defaults.features.transmission_contracts.ModelEditing;
import com.bpa4j.defaults.features.transmission_contracts.Report;
import com.bpa4j.defaults.features.transmission_contracts.RoleSetting;
import com.bpa4j.feature.Feature;
import com.bpa4j.feature.FeatureModel;
import com.bpa4j.feature.FeatureSaver;
import com.bpa4j.feature.FeatureTransmissionContract;

public class FileStorageManager implements StorageManager{
	private final Map<Class<?>,Function<? extends FeatureTransmissionContract,? extends FeatureModel<?>>> modelSuppliers=new HashMap<>();
	private final Map<Class<?>,Function<? extends FeatureTransmissionContract,? extends FeatureSaver<?>>> saverSuppliers=new HashMap<>();
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
		File file=new File(folder,"data.bin");
		firstLaunch=(!file.exists()&&url==null);
		loadDefaults();
		// Load persisted data
		data.load(file);
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
		Function<F,? extends FeatureModel<F>> modelFunction=(Function<F,? extends FeatureModel<F>>)modelSuppliers.get(f.getClass());
		Objects.requireNonNull(modelFunction);
		return modelFunction.apply(f);
	}
	@SuppressWarnings("unchecked")
	public <F extends FeatureTransmissionContract> FeatureSaver<F> getFeatureSaver(F f){
		Function<F,? extends FeatureSaver<F>> saverFunction=(Function<F,? extends FeatureSaver<F>>)saverSuppliers.get(f.getClass());
		Objects.requireNonNull(saverFunction);
		return saverFunction.apply(f);
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

	public <F extends FeatureTransmissionContract> void putModel(Class<F> f,Function<F,FeatureModel<F>> model){
		modelSuppliers.put(f,model);
	}
	public <F extends FeatureTransmissionContract> void putSaver(Class<F> f,Function<F,FeatureSaver<F>> saver){
		saverSuppliers.put(f,saver);
	}
	@SuppressWarnings("unchecked")
	private void loadDefaults(){
		//Models
		putModel(Board.class,BoardModel::new);
		putModel(Calendar.class,CalendarModel::new);
		putModel(DatedList.class,DatedListModel::new);
		putModel(DisposableDocument.class,DisposableDocumentModel::new);
		putModel(EditableList.class,EditableListModel::new);
		putModel(History.class,HistoryModel::new);
		putModel(ItemList.class,ItemListModel::new);
		putModel(MappedList.class,MappedListModel::new);
		putModel(ModelEditing.class,ModelEditingModel::new);
		putModel(Report.class,ReportModel::new);
		putModel(RoleSetting.class,RoleSettingModel::new);
		//Savers
		putSaver(Board.class,f->new BoardSaver());
		putSaver(Calendar.class,f->new CalendarSaver());
		putSaver(DatedList.class,f->new DatedListSaver());
		putSaver(DisposableDocument.class,f->new DisposableDocumentSaver());
		putSaver(EditableList.class,f->new EditableListSaver());
		putSaver(History.class,f->new HistorySaver());
		putSaver(ItemList.class,f->new ItemListSaver());
		putSaver(MappedList.class,f->new MappedListSaver());
		putSaver(ModelEditing.class,f->new ModelEditingSaver());
		putSaver(Report.class,f->new ReportSaver());
		putSaver(RoleSetting.class,f->new RoleSettingSaver());
	}
}
