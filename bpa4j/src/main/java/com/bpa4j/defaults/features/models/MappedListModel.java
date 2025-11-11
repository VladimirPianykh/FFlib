package com.bpa4j.defaults.features.models;

import java.io.Serializable;
import java.util.HashMap;
import com.bpa4j.core.Editable;
import com.bpa4j.defaults.features.transmission_contracts.MappedList;
import com.bpa4j.feature.FeatureModel;

public class MappedListModel<T extends Editable,V extends Serializable> implements FeatureModel<MappedList<T,V>>{
    private MappedList<T,V> ftc;
    public final HashMap<T,V> objects=new HashMap<>();
    public MappedListModel(MappedList<T,V> ftc){
        this.ftc=ftc;
        ftc.setGetObjectsOp(this::getObjects);
        ftc.setGetMappingOp(MappedListModel.this::getMapping);
    }
    public MappedList<T,V> getTransmissionContract(){
        return ftc;
    }
    public HashMap<T,V> getObjects(){
        return objects;
    }
    public V getMapping(T t){
        try{
            if(!objects.containsKey(t)) objects.put(t,getTransmissionContract().getVType().getDeclaredConstructor(getTransmissionContract().getType()).newInstance(t));
            return objects.get(t);
        }catch(ReflectiveOperationException ex){
            throw new IllegalStateException(ex);
        }
    }
}
