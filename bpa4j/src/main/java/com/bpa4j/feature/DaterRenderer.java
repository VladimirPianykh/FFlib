package com.bpa4j.feature;

import java.time.LocalDate;

import com.bpa4j.Dater;
import com.bpa4j.defaults.features.transmission_contracts.Calendar.DateRenderingContext;

public interface DaterRenderer<T>{
    public void render(Dater<T>dater,T t,LocalDate date,DateRenderingContext ctx);
}
