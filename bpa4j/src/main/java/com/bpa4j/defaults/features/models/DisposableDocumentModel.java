package com.bpa4j.defaults.features.models;

import java.util.function.Consumer;
import com.bpa4j.core.Editable;
import com.bpa4j.defaults.features.transmission_contracts.DisposableDocument;
import com.bpa4j.feature.FeatureModel;

public class DisposableDocumentModel<T extends Editable> implements FeatureModel<DisposableDocument<T>>{
    private DisposableDocument<T> ftc;
    private transient Consumer<T> processor;
    private Class<T> type;
    public DisposableDocumentModel(DisposableDocument<T> ftc,Class<T> type){
        this.ftc=ftc;
        this.type=type;
        ftc.setSetProcessorOp((processor)->setProcessor(processor));
        ftc.setProcessDocumentOp((document)->processDocument(document));
        ftc.setGetTypeOp(()->getType());
    }
    public DisposableDocument<T> getTransmissionContract(){
        return ftc;
    }
    public void setProcessor(Consumer<T> processor){
        this.processor=processor;
    }
    public void processDocument(T document){
        if(processor!=null) processor.accept(document);
    }
    public Class<T> getType(){
        return type;
    }
}
