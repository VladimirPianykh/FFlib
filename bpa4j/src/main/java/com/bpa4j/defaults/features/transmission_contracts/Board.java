package com.bpa4j.defaults.features.transmission_contracts;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import com.bpa4j.core.RenderingContext;
import com.bpa4j.feature.Feature;
import com.bpa4j.feature.FeatureTransmissionContract;

/**
 * A table representation of objects list.
 * We call {@code Feature} <b>Board-like</b> (or <b>BL</b>),
 * if it has {@code registerXXX()} and {@code getXXX()} static methods,
 * where {@code XXX} is some word like "List", "Table", etc,
 * and all instance methods are builder methods (except ones for operation registration).
 */
public class Board<T extends Serializable> implements FeatureTransmissionContract{
	public static interface TableCustomizationRenderingContext extends RenderingContext{}
	private static final Map<String,Feature<? extends Board<?>>> registeredBoards=new HashMap<>();
	public static interface Sorter<T> extends Comparator<T>{
		default void renderConfigurator(RenderingContext ctx){}
	}
	public static interface Filter<T extends Serializable>extends Predicate<T>{
		default void renderConfigurator(RenderingContext ctx){}
	}
	/**
	 * Basic interface for adding filtering by group.
	 * Must be overriden by the renderer to manage group's selection.
	 */
	public static abstract class Slicer<T extends Serializable> implements Filter<T>{
		private final Function<T,String>sliceFunction;
		private String currentGroup;
		public Slicer(Function<T,String>sliceFunction){
			Objects.requireNonNull(sliceFunction);
			this.sliceFunction=sliceFunction;
		}
		public String group(T t){
			return sliceFunction.apply(t);
		}
		public boolean test(T t){
			String cg=getCurrentGroup();
			return cg==null||group(t).equals(cg);
		}
		public String getCurrentGroup(){
			return currentGroup;
		}
		public void setCurrentGroup(String currentGroup){
			this.currentGroup=currentGroup;
		}
		public abstract void renderConfigurator(RenderingContext ctx);
	}
	private Supplier<ArrayList<T>> getObjectsOp;
	// private Supplier<T> createObjectOp;
	private Consumer<T> addObjectOp;
	private Consumer<T> removeObjectOp;
	private Consumer<Function<T,String>> setSlicerOp;
	private Consumer<Supplier<ArrayList<T>>> setElementSupplierOp;
	private BooleanSupplier getAllowCreationOp;
	private BooleanSupplier getAllowDeletionOp;
	private Consumer<Boolean> setAllowCreationOp;
	private Consumer<Board.Sorter<T>> setSorterOp;
	private Consumer<Board.Filter<T>> setFilterOp;
	private Consumer<RenderingContext> renderSorterOp;
	private Consumer<RenderingContext> renderFilterOp;
	private Consumer<Consumer<TableCustomizationRenderingContext>> setTableCustomizerOp;
	private Consumer<TableCustomizationRenderingContext> customizeTableOp;
	private Consumer<Boolean> setAllowDeletionOp;
	private Function<Function<T,String>,Slicer<T>> generateSlicerOp;

	private String name;
	private Class<T> type;
	public Board(String name,Class<T>type){
		this.name=name;
		this.type=type;
	}

	public void setGetObjectsOp(Supplier<ArrayList<T>> getObjectsOp){
		this.getObjectsOp=getObjectsOp;
	}
	// public void setCreateObjectOp(Supplier<T> createObjectOp){
	// 	this.createObjectOp=createObjectOp;
	// }
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
	public void setSetTableCustomizerOp(Consumer<Consumer<TableCustomizationRenderingContext>> setTableCustomizerOp){
		this.setTableCustomizerOp=setTableCustomizerOp;
	}
	public void setCustomizeTableOp(Consumer<TableCustomizationRenderingContext> customizeTableOp){
		this.customizeTableOp=customizeTableOp;
	}
	public void setGetAllowDeletionOp(BooleanSupplier getAllowDeletionOp){
		this.getAllowDeletionOp=getAllowDeletionOp;
	}
	public void setSetAllowDeletionOp(Consumer<Boolean> setAllowDeletionOp){
		this.setAllowDeletionOp=setAllowDeletionOp;
	}
	public void setGenerateSlicerOp(Function<Function<T,String>,Slicer<T>> generateSlicerOp){
		this.generateSlicerOp=generateSlicerOp;
	}

	public ArrayList<T> getObjects(){
		return getObjectsOp.get();
	}
	// public T createObject(){
	// 	return createObjectOp.get();
	// }
	public void addObject(T object){
		addObjectOp.accept(object);
	}
	public void removeObject(T object){
		removeObjectOp.accept(object);
	}
	public Board<T> setElementSupplier(Supplier<ArrayList<T>> supplier){
		setElementSupplierOp.accept(supplier);
		return this;
	}
	public boolean getAllowCreation(){
		return getAllowCreationOp.getAsBoolean();
	}
	public boolean getAllowDeletion(){
		return getAllowDeletionOp.getAsBoolean();
	}
	public Board<T> setAllowCreation(boolean allow){
		setAllowCreationOp.accept(allow);
		return this;
	}
	public Board<T> setSorter(Board.Sorter<T> sorter){
		setSorterOp.accept(sorter);
		return this;
	}
	public Board<T> setFilter(Board.Filter<T> filter){
		setFilterOp.accept(filter);
		return this;
	}
	public Board<T> setSlicer(Function<T,String> slicer){
		setSlicerOp.accept(slicer);
		return this;
	}
	public Board<T> setTableCustomizer(Consumer<TableCustomizationRenderingContext> customizer){
		setTableCustomizerOp.accept(customizer);
		return this;
	}
	public Board<T> setAllowDeletion(boolean allow){
		setAllowDeletionOp.accept(allow);
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
	public Slicer<T> generateSlicer(Function<T,String>sliceFunction){
		return generateSlicerOp.apply(sliceFunction);
	}
	public void customizeTable(TableCustomizationRenderingContext ctx){
		customizeTableOp.accept(ctx);
	}
	/**
	 * Registers a board, if it is not registered yet.
	 * @return the feature, registered or already present
	 */
	public static <T extends Serializable> Feature<Board<T>> registerBoard(String name,Class<T> clazz){
		Board<T> board=new Board<>(name,clazz);
		Feature<Board<T>> feature=new Feature<>(board);
		feature.load();
		return registerBoard(name,feature);
	}
	public static <T extends Serializable> Feature<Board<T>> registerBoard(String name,Feature<Board<T>>feature){
		registeredBoards.put(name,feature);
		return feature;
	}

	@SuppressWarnings("unchecked")
	public static <T extends Serializable> Board<T> getBoard(String name){
		Feature<?> feature=registeredBoards.get(name);
		if(feature==null) throw new IllegalArgumentException("Board with name '"+name+"' not found. Make sure to register it first."); 
		return (Board<T>)feature.getContract();
	}
}
