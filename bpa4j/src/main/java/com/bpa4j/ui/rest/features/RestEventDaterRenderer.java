package com.bpa4j.ui.rest.features;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.List;
import com.bpa4j.Dater;
import com.bpa4j.defaults.features.transmission_contracts.Calendar;
import com.bpa4j.defaults.features.transmission_contracts.Calendar.DateRenderingContext;
import com.bpa4j.editor.EditorEntry;
import com.bpa4j.feature.DaterRenderer;
import com.bpa4j.ui.rest.RestRenderingManager.RestDateRenderingContext;
import com.bpa4j.ui.rest.abstractui.Panel;
import com.bpa4j.ui.rest.abstractui.components.Label;
import com.bpa4j.ui.rest.abstractui.layout.FlowLayout;

/**
 * REST renderer for EventDater. Renders a list of events with their properties.
 * @author AI-generated
 */
public class RestEventDaterRenderer<T extends Calendar.Event> implements DaterRenderer<List<T>> {
    @Override
    public void render(Dater<List<T>> dater, List<T> events, LocalDate date, DateRenderingContext ctx) {
        RestDateRenderingContext restCtx = (RestDateRenderingContext) ctx;
        Panel panel = restCtx.getPanel();
        
        if (events == null || events.isEmpty()) {
            return;
        }

        for (T event : events) {
            Panel eventPanel = new Panel(new FlowLayout(FlowLayout.LEFT, FlowLayout.TTB, 0, 2));
            
            // Render properties
            for (Field f : event.getClass().getFields()) {
                if (f.isAnnotationPresent(EditorEntry.class)) {
                    try {
                        Object value = f.get(event);
                        String label = f.getAnnotation(EditorEntry.class).translation();
                        Label l=new Label(label+": "+value);
                        eventPanel.add(l);
                    } catch (IllegalAccessException e) {
                        Label err=new Label("Error reading field");
                        eventPanel.add(err);
                    }
                }
            }
            panel.add(eventPanel);
        }
    }
}
