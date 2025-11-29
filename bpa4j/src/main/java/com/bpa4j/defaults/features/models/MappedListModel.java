package com.bpa4j.defaults.features.models;

import java.io.Serializable;
import java.util.HashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import com.bpa4j.core.Editable;
import com.bpa4j.defaults.features.transmission_contracts.Board.TableCustomizationRenderingContext;
import com.bpa4j.defaults.features.transmission_contracts.EditableList.ItemRenderingContext;
import com.bpa4j.defaults.features.transmission_contracts.MappedList;
import com.bpa4j.feature.FeatureModel;

public class MappedListModel<T extends Editable,V extends Serializable> implements FeatureModel<MappedList<T,V>>{
	private MappedList<T,V> ftc;
	private HashMap<T,V> objects=new HashMap<>();
	private transient Supplier<HashMap<T,V>> mapSupplier;
	private transient boolean allowCreation;
	private transient BiConsumer<T,ItemRenderingContext> componentProvider;
	@SuppressWarnings("null")
	public MappedListModel(MappedList<T,V> ftc){
		this.ftc=ftc;
		ftc.setGetObjectsOp(this::getObjects);
		ftc.setGetMappingOp(this::getMapping);
		ftc.setCreateObjectOp(this::createObject);
		ftc.setSetMapSupplierOp(this::setMapSupplier);
		ftc.setCustomizeTableOp(this::customizeTable);
		ftc.setSetTableCustomizerOp(this::setTableCustomizer);
		ftc.setSetAllowCreationOp(this::setAllowCreation);
		ftc.setGetAllowCreationOp(this::getAllowCreation);
		ftc.setSetComponentProviderOp(this::setComponentProvider);
		ftc.setGetComponentProviderOp(this::getComponentProvider);
	}
	public MappedList<T,V> getTransmissionContract(){
		return ftc;
	}
	public HashMap<T,V> getObjects(){
		objects=mapSupplier.get();
		return objects;
	}
	public T createObject(){
		try{
			return getTransmissionContract().getType().getDeclaredConstructor().newInstance();
		}catch(ReflectiveOperationException ex){
			throw new IllegalStateException(ex);
		}
	}
	public V getMapping(T t){
		try{
			if(!objects.containsKey(t)) objects.put(t,getTransmissionContract().getVType().getDeclaredConstructor(getTransmissionContract().getType()).newInstance(t));
			return objects.get(t);
		}catch(ReflectiveOperationException ex){
			throw new IllegalStateException(ex);
		}
	}
	public void setMapSupplier(Supplier<HashMap<T,V>> supplier){
		mapSupplier=supplier;
	}
	public void setTableCustomizer(Consumer<TableCustomizationRenderingContext> customizer){
		ftc.setTableCustomizer(customizer);
	}
	public void customizeTable(TableCustomizationRenderingContext ctx){
		ftc.customizeTable(ctx);
	}
	public void setAllowCreation(boolean allowCreation){
		if(mapSupplier!=null&&allowCreation) throw new IllegalArgumentException("Cannot add elements when mapSupplier is present.");
		this.allowCreation=allowCreation;
	}
	public boolean getAllowCreation(){
		return allowCreation;
	}
	public void setComponentProvider(BiConsumer<T,ItemRenderingContext> componentProvider){
		this.componentProvider=componentProvider;
	}
	public BiConsumer<T,ItemRenderingContext> getComponentProvider(){
		return componentProvider;
	}
}
