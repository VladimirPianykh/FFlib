package com.bpa4j.core;

import com.bpa4j.core.User.Feature;

/**
 * Manages a Board-like Feature. Must be inherited by singletone.
 */
public abstract class BLFeatureManager<T extends Feature>{
	private T t;
	public T get(){
		if(t==null)return t=register();
		else return t;
	}
	public abstract T register();
}
