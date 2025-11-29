package com.bpa4j.defaults.features.transmission_contracts;

import java.util.function.Consumer;
import java.util.function.Supplier;
import com.bpa4j.core.Editable;
import com.bpa4j.feature.Feature;
import com.bpa4j.feature.FeatureTransmissionContract;
import java.util.HashMap;
import java.util.Map;

public class DisposableDocument<T extends Editable> implements FeatureTransmissionContract{
	private static final Map<String,Feature<? extends DisposableDocument<?>>> registeredDocuments=new HashMap<>();
	public Supplier<T> createDocumentOp;
	public Consumer<Consumer<T>> setProcessorOp;
	public Consumer<T> processDocumentOp;
	private String name;
	private Class<T> type;
	public DisposableDocument(String name,Class<T>type){
		this.name=name;
		this.type=type;
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
		return type;
	}
	public String getFeatureName(){
		return name;
	}

	/**
	 * Registers a disposable document, if it is not registered yet.
	 * @return the feature, registered or already present
	 */
	public static <T extends Editable> Feature<DisposableDocument<T>> registerDocument(String name,Class<T> clazz){
		DisposableDocument<T> doc=new DisposableDocument<>(name,clazz);
		Feature<DisposableDocument<T>> feature=new Feature<>(doc);
		feature.load();
		return registerDocument(name,feature);
	}
	public static <T extends Editable> Feature<DisposableDocument<T>> registerDocument(String name,Feature<DisposableDocument<T>> feature){
		registeredDocuments.put(name,feature);
		return feature;
	}
	@SuppressWarnings("unchecked")
	public static <T extends Editable> DisposableDocument<T> getDocument(String name){
		Feature<?> feature=registeredDocuments.get(name);
		if(feature==null) throw new IllegalArgumentException("DisposableDocument with name '"+name+"' not found. Make sure to register it first.");
		return (DisposableDocument<T>)feature.getContract();
	}
}
