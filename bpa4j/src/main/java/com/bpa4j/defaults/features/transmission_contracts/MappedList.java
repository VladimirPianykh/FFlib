package com.bpa4j.defaults.features.transmission_contracts;

import java.io.Serializable;
import java.util.HashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import com.bpa4j.core.Editable;
import com.bpa4j.defaults.features.transmission_contracts.Board.TableCustomizationRenderingContext;
import com.bpa4j.defaults.features.transmission_contracts.EditableList.ItemRenderingContext;
import com.bpa4j.feature.Feature;
import com.bpa4j.feature.FeatureTransmissionContract;
import java.util.Map;

public class MappedList<T extends Editable,V extends Serializable> implements FeatureTransmissionContract{
	private static final Map<String,Feature<? extends MappedList<?,?>>> registeredLists=new HashMap<>();
	private Supplier<HashMap<T,V>> getObjectsOp;
	private Function<T,V> getMappingOp;
	private Supplier<T> createObjectOp;
	private Consumer<Supplier<HashMap<T,V>>> setMapSupplierOp;
	private Consumer<Consumer<TableCustomizationRenderingContext>> setTableCustomizerOp;
	private Consumer<TableCustomizationRenderingContext> customizeTableOp;
	private Consumer<Boolean> setAllowCreationOp;
	private Supplier<Boolean> getAllowCreationOp;
	private Consumer<BiConsumer<T,ItemRenderingContext>> setComponentProviderOp;
	private Supplier<BiConsumer<T,ItemRenderingContext>> getComponentProviderOp;
	private String name;
	private Class<T> type;
	private Class<V> vType;
	public MappedList(String name,Class<T> type,Class<V> vType){
		this.name=name;
		this.type=type;
		this.vType=vType;
	}
	public void setGetObjectsOp(Supplier<HashMap<T,V>> getObjectsOp){
		this.getObjectsOp=getObjectsOp;
	}
	public void setGetMappingOp(Function<T,V> getMappingOp){
		this.getMappingOp=getMappingOp;
	}
	public void setCreateObjectOp(Supplier<T> createObjectOp){
		this.createObjectOp=createObjectOp;
	}
	public void setSetMapSupplierOp(Consumer<Supplier<HashMap<T,V>>> setMapSupplierOp){
		this.setMapSupplierOp=setMapSupplierOp;
	}
	public void setSetTableCustomizerOp(Consumer<Consumer<TableCustomizationRenderingContext>> setTableCustomizerOp){
		this.setTableCustomizerOp=setTableCustomizerOp;
	}
	public void setCustomizeTableOp(Consumer<TableCustomizationRenderingContext> customizeTableOp){
		this.customizeTableOp=customizeTableOp;
	}
	public void setSetAllowCreationOp(Consumer<Boolean> setAllowCreationOp){
		this.setAllowCreationOp=setAllowCreationOp;
	}
	public void setGetAllowCreationOp(Supplier<Boolean> getAllowCreationOp){
		this.getAllowCreationOp=getAllowCreationOp;
	}
	public void setSetComponentProviderOp(Consumer<BiConsumer<T,ItemRenderingContext>> setComponentProviderOp){
		this.setComponentProviderOp=setComponentProviderOp;
	}
	public void setGetComponentProviderOp(Supplier<BiConsumer<T,ItemRenderingContext>> getComponentProviderOp){
		this.getComponentProviderOp=getComponentProviderOp;
	}

	public HashMap<T,V> getObjects(){
		return getObjectsOp.get();
	}
	public V getMapping(T t){
		return getMappingOp.apply(t);
	}
	public T createObject(){
		return createObjectOp.get();
	}
	public void setMapSupplier(Supplier<HashMap<T,V>> supplier){
		setMapSupplierOp.accept(supplier);
	}
	public void setTableCustomizer(Consumer<TableCustomizationRenderingContext> customizer){
		setTableCustomizerOp.accept(customizer);
	}
	public void customizeTable(TableCustomizationRenderingContext ctx){
		customizeTableOp.accept(ctx);
	}
	public String getFeatureName(){
		return name;
	}
	public Class<T> getType(){
		return type;
	}
	public Class<V> getVType(){
		return vType;
	}
	public boolean getAllowCreation(){
		return getAllowCreationOp.get();
	}
	public void setAllowCreation(boolean allowCreation){
		setAllowCreationOp.accept(allowCreation);
	}
	public BiConsumer<T,ItemRenderingContext> getComponentProvider(){
		return getComponentProviderOp.get();
	}
	public void setComponentProvider(BiConsumer<T,ItemRenderingContext> componentProvider){
		setComponentProviderOp.accept(componentProvider);
	}

	/**
	 * Registers a mapped list, if it is not registered yet.
	 * @return the feature, registered or already present
	 */
	public static <T extends Editable,V extends Serializable> Feature<MappedList<T,V>> registerList(String name,Class<T> type,Class<V> vType){
		MappedList<T,V> list=new MappedList<>(name,type,vType);
		Feature<MappedList<T,V>> feature=new Feature<>(list);
		feature.load();
		return registerList(name,feature);
	}
	public static <T extends Editable,V extends Serializable> Feature<MappedList<T,V>> registerList(String name,Feature<MappedList<T,V>> feature){
		registeredLists.put(name,feature);
		return feature;
	}
	@SuppressWarnings("unchecked")
	public static <T extends Editable,V extends Serializable> MappedList<T,V> getList(String name){
		Feature<?> feature=registeredLists.get(name);
		if(feature==null) throw new IllegalArgumentException("MappedList with name '"+name+"' not found. Make sure to register it first.");
		return (MappedList<T,V>)feature.getContract();
	}
}
