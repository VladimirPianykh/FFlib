package com.bpa4j.defaults.features.transmission_contracts;

import java.util.function.Consumer;
import java.util.function.Supplier;
import com.bpa4j.core.Editable;
import com.bpa4j.feature.FeatureTransmissionContract;

public class DisposableDocument<T extends Editable> implements FeatureTransmissionContract{
	public Supplier<T> createDocumentOp;
	public Consumer<Consumer<T>> setProcessorOp;
	public Consumer<T> processDocumentOp;
	public Supplier<Class<T>> getTypeOp;
	private String name;
	public DisposableDocument(String name){
		this.name=name;
	}
	public void setCreateDocumentOp(Supplier<T> createDocumentOp){
		this.createDocumentOp=createDocumentOp;
	}
	public void setSetProcessorOp(Consumer<Consumer<T>> setProcessorOp){
		this.setProcessorOp=setProcessorOp;
	}
	public void setProcessDocumentOp(Consumer<T> processDocumentOp){
		this.processDocumentOp=processDocumentOp;
	}
	public void setGetTypeOp(Supplier<Class<T>> getTypeOp){
		this.getTypeOp=getTypeOp;
	}
	public T createDocument(){
		return createDocumentOp.get();
	}
	public void setProcessor(Consumer<T> processor){
		setProcessorOp.accept(processor);
	}
	public void processDocument(T document){
		processDocumentOp.accept(document);
	}
	public Class<T> getType(){
		return getTypeOp.get();
	}
	public String getFeatureName(){
		return name;
	}
}
