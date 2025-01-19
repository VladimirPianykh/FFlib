package com.bpa4j.util.excel;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.function.Function;

@Retention(RetentionPolicy.RUNTIME)
public @interface Parseable{
	public Class<? extends Function<String,Object>>value();
}
