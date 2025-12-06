package com.bpa4j.ui.rest.features;

import java.time.LocalDate;
import com.bpa4j.Dater;
import com.bpa4j.defaults.features.transmission_contracts.Calendar.DateRenderingContext;
import com.bpa4j.feature.DaterRenderer;

/**
 * REST renderer for EmptyDater. Does nothing.
 * @author AI-generated
 */
public class RestEmptyDaterRenderer<T> implements DaterRenderer<T> {
    @Override
    public void render(Dater<T> dater, T t, LocalDate date, DateRenderingContext ctx) {
        // Do nothing
    }
}
