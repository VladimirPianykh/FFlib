package com.bpa4j.defaults.features.models;

import java.io.Serializable;
import java.util.HashMap;
import com.bpa4j.core.Editable;
import com.bpa4j.defaults.features.transmission_contracts.MappedList;
import com.bpa4j.feature.FeatureModel;

public class MappedListModel<T extends Editable,V extends Serializable> implements FeatureModel<MappedList<T,V>>{
    private MappedList<T,V> ftc;
    public final HashMap<T,V> objects=new HashMap<>();
    private Class<T> type;
    private Class<V> vType;
    public MappedListModel(MappedList<T,V> ftc,Class<T> type,Class<V> vType){
        this.ftc=ftc;
        this.type=type;
        this.vType=vType;
        ftc.setGetObjectsOp(()->getObjects());
        ftc.setGetMappingOp((t)->getMapping(t));
    }
    public MappedList<T,V> getTransmissionContract(){
        return ftc;
    }
    public HashMap<T,V> getObjects(){
        return objects;
    }
    public V getMapping(T t){
        try{
            if(!objects.containsKey(t)) objects.put(t,vType.getDeclaredConstructor(type).newInstance(t));
            return objects.get(t);
        }catch(ReflectiveOperationException ex){
            throw new IllegalStateException(ex);
        }
    }
}
