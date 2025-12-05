package com.bpa4j;

import java.io.Serializable;
import java.time.LocalDate;
import com.bpa4j.defaults.features.transmission_contracts.Calendar.DateRenderingContext;

public interface Dater<T>extends Serializable{
	default void render(T t,LocalDate date,DateRenderingContext ctx){
		ctx.getRenderer(this).render(this,t,date,ctx);
	}
}
