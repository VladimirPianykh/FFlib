package com.bpa4j.core;

import com.bpa4j.feature.Feature;
import com.bpa4j.feature.FeatureTransmissionContract;

/**
 * Manages a Board-like Feature. Must be inherited by singletone.
 */
public abstract class BLFeatureManager<T extends FeatureTransmissionContract>{
	private Feature<T> t;
	public T get(){
		if(t==null)t=register();
		return t.getContract();
	}
	public Feature<T>getFeature(){
		if(t==null)t=register();
		return t;
	}
	public abstract Feature<T> register();
}
