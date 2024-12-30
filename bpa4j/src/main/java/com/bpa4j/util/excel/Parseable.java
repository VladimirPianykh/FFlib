package com.bpa4j.util.excel;

import java.util.function.Function;

public @interface Parseable{
	public Class<? extends Function<String,Object>>parser();
}
