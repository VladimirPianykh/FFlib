package com.bpa4j;

import java.io.Serializable;
import java.time.LocalDate;
import com.bpa4j.defaults.features.transmission_contracts.DatedList.DateRenderingContext;

public interface Dater<T>extends Serializable{
    public void render(T t,LocalDate date,DateRenderingContext ctx);
}
