package com.bpa4j.defaults.features.transmission_contracts;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import com.bpa4j.core.RenderingContext;
import com.bpa4j.feature.FeatureTransmissionContract;

public class Board<T extends Serializable> implements FeatureTransmissionContract{
    public static interface Sorter<T> extends Comparator<T>{
        default void renderConfigurator(RenderingContext ctx){}
    }
    public static interface Filter<T extends Serializable>extends Predicate<T>{
        default void renderConfigurator(RenderingContext ctx){}
    }
    public Supplier<ArrayList<T>> getObjectsOp;
    public Supplier<T> createObjectOp;
    public Consumer<T> addObjectOp;
    public Consumer<T> removeObjectOp;
    public Consumer<Function<T,String>> setSlicerOp;
    public Consumer<Supplier<ArrayList<T>>> setElementSupplierOp;
    public BooleanSupplier getAllowCreationOp;
    public Consumer<Boolean> setAllowCreationOp;
    public Consumer<Board.Sorter<T>> setSorterOp;
    public Consumer<Board.Filter<T>> setFilterOp;
    private Consumer<RenderingContext> renderSorterOp;
    private Consumer<RenderingContext> renderFilterOp;

    private String name;
    public Board(String name){
        this.name=name;
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

    public ArrayList<T> getObjects(){
        return getObjectsOp.get();
    }
    public T createObject(){
        return createObjectOp.get();
    }
    public void addObject(T object){
        addObjectOp.accept(object);
    }
    public void removeObject(T object){
        removeObjectOp.accept(object);
    }
    public void setElementSupplier(Supplier<ArrayList<T>> supplier){
        setElementSupplierOp.accept(supplier);
    }
    public boolean getAllowCreation(){
        return getAllowCreationOp.getAsBoolean();
    }
    public void setAllowCreation(boolean allow){
        setAllowCreationOp.accept(allow);
    }
    public void setSorter(Board.Sorter<T> sorter){
        setSorterOp.accept(sorter);
    }
    public void setFilter(Board.Filter<T> filter){
        setFilterOp.accept(filter);
    }
    public void setSlicer(Function<T,String> slicer){
        setSlicerOp.accept(slicer);
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
}
