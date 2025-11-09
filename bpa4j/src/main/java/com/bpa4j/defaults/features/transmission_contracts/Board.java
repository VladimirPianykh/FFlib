package com.bpa4j.defaults.features.transmission_contracts;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import com.bpa4j.feature.FeatureTransmissionContract;

public class Board<T extends Serializable> implements FeatureTransmissionContract{
    public Supplier<ArrayList<T>> getObjectsOp;
    public Supplier<T> createObjectOp;
    public Consumer<T> addObjectOp;
    public Consumer<T> removeObjectOp;
    public Consumer<Function<T,String>> setSlicerOp;
    public Consumer<Supplier<ArrayList<T>>> setElementSupplierOp;
    public Supplier<Boolean> getAllowCreationOp;
    public Consumer<Boolean> setAllowCreationOp;

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
    public void setGetAllowCreationOp(Supplier<Boolean> getAllowCreationOp){
        this.getAllowCreationOp=getAllowCreationOp;
    }
    public void setSetAllowCreationOp(Consumer<Boolean> setAllowCreationOp){
        this.setAllowCreationOp=setAllowCreationOp;
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
    public void setSlicer(Function<T,String> slicer){
        setSlicerOp.accept(slicer);
    }
    public void setElementSupplier(Supplier<ArrayList<T>> supplier){
        setElementSupplierOp.accept(supplier);
    }
    public boolean getAllowCreation(){
        return getAllowCreationOp.get();
    }
    public void setAllowCreation(boolean allow){
        setAllowCreationOp.accept(allow);
    }
    public String getFeatureName(){
        return name;
    }
}
