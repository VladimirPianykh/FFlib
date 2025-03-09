package com.bpa4j;
import java.io.Serializable;
import java.util.function.Function;

public interface SerializableFunction<T,R>extends Function<T,R>,Serializable{}
