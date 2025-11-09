package com.bpa4j.defaults.features.models;

import java.util.HashMap;
import java.util.Set;
import java.util.function.Supplier;
import com.bpa4j.Dater;
import com.bpa4j.core.Editable;
import com.bpa4j.defaults.features.transmission_contracts.DatedList;
import com.bpa4j.feature.FeatureModel;

public class DatedListModel<T extends Editable> implements FeatureModel<DatedList<T>>{
    private DatedList<T> ftc;
    private final HashMap<T,Dater<T>> objects=new HashMap<>();
    private transient Supplier<Dater<T>> dateProvider;
    public DatedListModel(DatedList<T> ftc){
        this.ftc=ftc;
        ftc.setGetObjectsOp(()->getObjects());
        ftc.setGetObjectsWithDatersOp(()->getObjectsWithDaters());
        ftc.setSetDateProviderOp((provider)->setDateProvider(provider));
        ftc.setRemoveObjectOp((object)->removeObject(object));
        ftc.setPutObjectOp((object,dater)->putObject(object,dater));
        ftc.setGetDateProviderOp(()->getDateProvider());
    }
    public DatedList<T> getTransmissionContract(){
        return ftc;
    }
    public Set<T> getObjects(){
        return objects.keySet();
    }
    public HashMap<T,Dater<T>> getObjectsWithDaters(){
        return objects;
    }
    public void setDateProvider(Supplier<Dater<T>> provider){
        this.dateProvider=provider;
    }
    public Supplier<Dater<T>> getDateProvider(){
        return dateProvider;
    }
    public void putObject(T object,Dater<T> dater){
        objects.put(object,dater);
    }
    public void removeObject(T object){
        objects.remove(object);
    }
}
