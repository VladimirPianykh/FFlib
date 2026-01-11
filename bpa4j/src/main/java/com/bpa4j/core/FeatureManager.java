package com.bpa4j.core;

import com.bpa4j.feature.Feature;
import com.bpa4j.feature.FeatureTransmissionContract;

/**
 * Manages a single {@code Feature} instance.
 */
public abstract class FeatureManager<T extends FeatureTransmissionContract>{
	private Feature<T> t;
	public T get(){
		if(t==null)t=construct();
		return t.getContract();
	}
	public Feature<T>getFeature(){
		if(t==null)t=construct();
		return t;
	}
	/**
	 * Constructs the target feature instance.
	 * Called once when this object is asked to give a feature for the first time.
	 */
	protected abstract Feature<T> construct();
}
