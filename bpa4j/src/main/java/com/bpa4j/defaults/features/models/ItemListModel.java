package com.bpa4j.defaults.features.models;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import com.bpa4j.core.RenderingContext;
import com.bpa4j.defaults.features.transmission_contracts.Board;
import com.bpa4j.defaults.features.transmission_contracts.ItemList;
import com.bpa4j.defaults.features.transmission_contracts.ItemList.ListCustomizationRenderingContext;
import com.bpa4j.feature.FeatureModel;

public class ItemListModel<T extends Serializable> implements FeatureModel<ItemList<T>>{
	private ItemList<T> ftc;
	private ArrayList<T> objects=new ArrayList<T>(){
		private void writeObject(ObjectOutputStream out) throws IOException,ClassNotFoundException{
			if(elementSupplier!=null) clear();
			out.defaultWriteObject();
		}
	};
	private transient ArrayList<Consumer<List<T>>> collectiveActions=new ArrayList<>();
	private transient ArrayList<Consumer<T>> singularActions=new ArrayList<>();
	private transient Supplier<ArrayList<T>> elementSupplier;
	private transient boolean allowCreation;
	private transient Board.Sorter<T> sorter;
	private transient Board.Filter<T> filter;
	private transient Consumer<ListCustomizationRenderingContext>listCustomizer;
	private void readObject(ObjectInputStream is) throws IOException,ClassNotFoundException{
		is.defaultReadObject();
		collectiveActions=new ArrayList<>();
		singularActions=new ArrayList<>();
	}
	@SuppressWarnings("null")
	public ItemListModel(ItemList<T> ftc){
		this.ftc=ftc;
		ftc.setGetObjectsOp(this::getObjects);
		ftc.setAddObjectOp(this::addObject);
		ftc.setCreateObjectOp(this::createObject);
		ftc.setRemoveObjectOp(this::removeObject);
		ftc.setSetElementSupplierOp(this::setElementSupplier);
		ftc.setGetAllowCreationOp(this::getAllowCreation);
		ftc.setSetAllowCreationOp(this::setAllowCreation);
		ftc.setAddCollectiveActionOp(this::addCollectiveAction);
		ftc.setAddSingularActionOp(this::addSingularAction);
		ftc.setSetSlicerOp(this::setSlicer);
		ftc.setSetSorterOp(this::setSorter);
		ftc.setSetFilterOp(this::setFilter);
		ftc.setRenderSorterOp(this::renderSorter);
		ftc.setRenderFilterOp(this::renderFilter);
		ftc.setGetSingularActionsOp(this::getSingularActions);
		ftc.setGetCollectiveActionsOp(this::getCollectiveActions);
		ftc.setSetListCustomizerOp(this::setListCustomizer);
		ftc.setCustomizeListOp(this::customizeList);
	}
	public ItemList<T> getTransmissionContract(){
		return ftc;
	}
	@SuppressWarnings("unchecked")
	public ArrayList<T> getObjects(){
		ArrayList<T> o=objects;
		if(elementSupplier!=null) o=elementSupplier.get();
		if(sorter!=null) o.sort(sorter);
		if(filter!=null){
			o=(ArrayList<T>)objects.clone();
			o.removeIf(filter.negate());
		}
		return o;
	}
	public T createObject(){
		try{
			T t=getTransmissionContract().getType().getDeclaredConstructor().newInstance();
			objects.add(t);
			return t;
		}catch(ReflectiveOperationException ex){
			throw new IllegalStateException("Editables must have default constructor");
		}
	}
	public void addObject(T object){
		objects.add(object);
	}
	public void removeObject(T object){
		objects.remove(object);
	}
	public void setSlicer(Function<T,String> slicer){
		// Slicer implementation would go here - handled by renderer
	}
	public void setElementSupplier(Supplier<ArrayList<T>> supplier){
		this.elementSupplier=supplier;
	}
	public boolean getAllowCreation(){
		return allowCreation;
	}
	public void setAllowCreation(boolean allow){
		this.allowCreation=allow;
	}
	public void addCollectiveAction(Consumer<List<T>> action){
		collectiveActions.add(action);
	}
	public void addSingularAction(Consumer<T> action){
		singularActions.add(action);
	}
	public ArrayList<Consumer<List<T>>> getCollectiveActions(){
		return collectiveActions;
	}
	public ArrayList<Consumer<T>> getSingularActions(){
		return singularActions;
	}
	public void setSorter(Board.Sorter<T> sorter){
		this.sorter=sorter;
	}
	public void setFilter(Board.Filter<T> filter){
		this.filter=filter;
	}
	public void renderSorter(RenderingContext ctx){
		if(sorter!=null) sorter.renderConfigurator(ctx);
	}
	public void renderFilter(RenderingContext ctx){
		if(filter!=null) filter.renderConfigurator(ctx);
	}
	public Board.Sorter<T> getSorter(){
		return sorter;
	}
	public Board.Filter<T> getFilter(){
		return filter;
	}
	public void setListCustomizer(Consumer<ListCustomizationRenderingContext> listCustomizer){
		this.listCustomizer=listCustomizer;
	}
	public void customizeList(ListCustomizationRenderingContext ctx){
		if(listCustomizer!=null)listCustomizer.accept(ctx);
	}
}
