package com.bpa4j.defaults.features.models;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.function.Function;
import java.util.function.Supplier;
import com.bpa4j.defaults.features.transmission_contracts.Board;
import com.bpa4j.feature.FeatureModel;

public class BoardModel<T extends Serializable> implements FeatureModel<Board<T>>{
    private Board<T> ftc;
    private ArrayList<T> objects=new ArrayList<T>(){
        private void writeObject(ObjectOutputStream out) throws IOException,ClassNotFoundException{
            if(elementSupplier!=null) clear();
            out.defaultWriteObject();
        }
    };
    private transient Supplier<ArrayList<T>> elementSupplier;
    private transient boolean allowCreation;
    public BoardModel(Board<T> ftc){
        this.ftc=ftc;
        ftc.setGetObjectsOp(()->getObjects());
        ftc.setAddObjectOp((o)->addObject(o));
        ftc.setRemoveObjectOp((o)->removeObject(o));
        ftc.setSetSlicerOp((slicer)->setSlicer(slicer));
        ftc.setSetElementSupplierOp((supplier)->setElementSupplier(supplier));
        ftc.setGetAllowCreationOp(()->getAllowCreation());
        ftc.setSetAllowCreationOp((allow)->setAllowCreation(allow));
    }
    public Board<T> getTransmissionContract(){
        return ftc;
    }
    public ArrayList<T> getObjects(){
        if(elementSupplier!=null) objects=elementSupplier.get();
        return objects;
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
}
