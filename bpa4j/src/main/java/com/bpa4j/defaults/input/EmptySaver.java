package com.bpa4j.defaults.input;

import java.util.function.Supplier;

@SuppressWarnings("rawtypes")
public class EmptySaver implements Supplier{
	public Object get(){
		throw new AssertionError("This method should be never called.");
    }
}
