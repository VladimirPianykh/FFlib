package com.bpa4j.defaults.features.models;

import java.util.function.Consumer;
import com.bpa4j.core.Editable;
import com.bpa4j.defaults.features.transmission_contracts.DisposableDocument;
import com.bpa4j.feature.FeatureModel;

public class DisposableDocumentModel<T extends Editable> implements FeatureModel<DisposableDocument<T>>{
    private DisposableDocument<T> ftc;
    private transient Consumer<T> processor;
    public DisposableDocumentModel(DisposableDocument<T> ftc){
        this.ftc=ftc;
        ftc.setSetProcessorOp(this::setProcessor);
        ftc.setProcessDocumentOp(this::processDocument);
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
}
