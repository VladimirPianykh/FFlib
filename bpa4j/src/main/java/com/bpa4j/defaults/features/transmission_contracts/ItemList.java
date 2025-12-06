package com.bpa4j.defaults.features.transmission_contracts;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import com.bpa4j.core.RenderingContext;
import com.bpa4j.feature.Feature;
import com.bpa4j.feature.FeatureTransmissionContract;

public class ItemList<T extends Serializable> implements FeatureTransmissionContract{
	public static interface ListCustomizationRenderingContext{}
	private static final Map<String,Feature<? extends ItemList<?>>> registeredLists=new HashMap<>();

	private Supplier<ArrayList<T>> getObjectsOp;
	private Supplier<T> createObjectOp;
	private Consumer<T> addObjectOp;
	private Consumer<T> removeObjectOp;
	private Consumer<Function<T,String>> setSlicerOp;
	private Consumer<Supplier<ArrayList<T>>> setElementSupplierOp;
	private BooleanSupplier getAllowCreationOp;
	private Consumer<Boolean> setAllowCreationOp;
	private Consumer<Consumer<List<T>>> addCollectiveActionOp;
	private Consumer<Consumer<T>> addSingularActionOp;
	private Consumer<Board.Sorter<T>> setSorterOp;
	private Consumer<Board.Filter<T>> setFilterOp;
	private Consumer<RenderingContext> renderFilterOp;
	private Consumer<RenderingContext> renderSorterOp;
	private Supplier<List<Consumer<List<T>>>> getCollectiveActionsOp;
	private Supplier<List<Consumer<T>>> getSingularActionsOp;
	private Consumer<Consumer<ListCustomizationRenderingContext>> setListCustomizerOp;
	private Consumer<ListCustomizationRenderingContext> customizeListOp;

	private String name;
	private Class<T> type;
	public ItemList(String name,Class<T> type){
		this.name=name;
		this.type=type;
	}

	public void setGetObjectsOp(Supplier<ArrayList<T>> getObjectsOp){
		this.getObjectsOp=getObjectsOp;
	}
	public void setCreateObjectOp(Supplier<T> createObjectOp){
		this.createObjectOp=createObjectOp;
	}
	public void setAddObjectOp(Consumer<T> addObjectOp){
		this.addObjectOp=addObjectOp;
	}
	public void setRemoveObjectOp(Consumer<T> removeObjectOp){
		this.removeObjectOp=removeObjectOp;
	}
	public void setSetSlicerOp(Consumer<Function<T,String>> setSlicerOp){
		this.setSlicerOp=setSlicerOp;
	}
	public void setSetElementSupplierOp(Consumer<Supplier<ArrayList<T>>> setElementSupplierOp){
		this.setElementSupplierOp=setElementSupplierOp;
	}
	public void setGetAllowCreationOp(BooleanSupplier getAllowCreationOp){
		this.getAllowCreationOp=getAllowCreationOp;
	}
	public void setSetAllowCreationOp(Consumer<Boolean> setAllowCreationOp){
		this.setAllowCreationOp=setAllowCreationOp;
	}
	public void setAddCollectiveActionOp(Consumer<Consumer<List<T>>> addCollectiveActionOp){
		this.addCollectiveActionOp=addCollectiveActionOp;
	}
	public void setAddSingularActionOp(Consumer<Consumer<T>> addSingularActionOp){
		this.addSingularActionOp=addSingularActionOp;
	}
	public void setSetSorterOp(Consumer<Board.Sorter<T>> setSorterOp){
		this.setSorterOp=setSorterOp;
	}
	public void setSetFilterOp(Consumer<Board.Filter<T>> setFilterOp){
		this.setFilterOp=setFilterOp;
	}
	public void setRenderSorterOp(Consumer<RenderingContext> renderSorterOp){
		this.renderSorterOp=renderSorterOp;
	}
	public void setRenderFilterOp(Consumer<RenderingContext> renderFilterOp){
		this.renderFilterOp=renderFilterOp;
	}
	public void setGetSingularActionsOp(Supplier<List<Consumer<T>>> getSingularActionsOp){
		this.getSingularActionsOp=getSingularActionsOp;
	}
	public void setGetCollectiveActionsOp(Supplier<List<Consumer<List<T>>>> getCollectiveActionsOp){
		this.getCollectiveActionsOp=getCollectiveActionsOp;
	}
	public void setSetListCustomizerOp(Consumer<Consumer<ListCustomizationRenderingContext>> setListCustomizerOp){
		this.setListCustomizerOp=setListCustomizerOp;
	}
	public void setCustomizeListOp(Consumer<ListCustomizationRenderingContext> customizeListOp){
		this.customizeListOp=customizeListOp;
	}

	public ArrayList<T> getObjects(){
		return getObjectsOp.get();
	}
	public void addObject(T object){
		addObjectOp.accept(object);
	}
	public void removeObject(T object){
		removeObjectOp.accept(object);
	}
	public ItemList<T> setElementSupplier(Supplier<ArrayList<T>> supplier){
		setElementSupplierOp.accept(supplier);
		return this;
	}
	public boolean getAllowCreation(){
		return getAllowCreationOp.getAsBoolean();
	}
	public ItemList<T> setAllowCreation(boolean allow){
		setAllowCreationOp.accept(allow);
		return this;
	}
	public ItemList<T> addCollectiveAction(Consumer<List<T>> action){
		addCollectiveActionOp.accept(action);
		return this;
	}
	public ItemList<T> addSingularAction(Consumer<T> action){
		addSingularActionOp.accept(action);
		return this;
	}
	public ItemList<T> setSlicer(Function<T,String> slicer){
		setSlicerOp.accept(slicer);
		return this;
	}
	public ItemList<T> setSorter(Board.Sorter<T> sorter){
		setSorterOp.accept(sorter);
		return this;
	}
	public ItemList<T> setFilter(Board.Filter<T> filter){
		setFilterOp.accept(filter);
		return this;
	}
	public void renderSorter(RenderingContext ctx){
		renderSorterOp.accept(ctx);
	}
	public void renderFilter(RenderingContext ctx){
		renderFilterOp.accept(ctx);
	}
	public String getFeatureName(){
		return name;
	}
	public Class<T> getType(){
		return type;
	}
	public List<Consumer<T>> getSingularActions(){
		return getSingularActionsOp.get();
	}
	public List<Consumer<List<T>>> getCollectiveActions(){
		return getCollectiveActionsOp.get();
	}
	public void setListCustomizer(Consumer<ListCustomizationRenderingContext> customizer){
		setListCustomizerOp.accept(customizer);
	}
	public void customizeList(ListCustomizationRenderingContext ctx){
		customizeListOp.accept(ctx);
	}
	public Consumer<Consumer<ListCustomizationRenderingContext>> getSetListCustomizerOp(){
		return setListCustomizerOp;
	}

	/**
	 * Registers an item list, if it is not registered yet.
	 * @return the feature, registered or already present
	 */
	public static <T extends Serializable> Feature<ItemList<T>> registerList(String name,Class<T> clazz){
		ItemList<T> list=new ItemList<>(name,clazz);
		Feature<ItemList<T>> feature=new Feature<>(list);
		feature.load();
		return registerList(name,feature);
	}
	public static <T extends Serializable> Feature<ItemList<T>> registerList(String name,Feature<ItemList<T>> feature){
		registeredLists.put(name,feature);
		return feature;
	}
	@SuppressWarnings("unchecked")
	public static <T extends Serializable> ItemList<T> getList(String name){
		Feature<?> feature=registeredLists.get(name);
		if(feature==null) throw new IllegalArgumentException("ItemList with name '"+name+"' not found. Make sure to register it first.");
		return (ItemList<T>)feature.getContract();
	}
}
