package com.bpa4j.ui.swing.features.daters;

import java.awt.Color;
import java.time.LocalDate;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.border.TitledBorder;

import com.bpa4j.Dater;
import com.bpa4j.defaults.features.transmission_contracts.Calendar.DateRenderingContext;
import com.bpa4j.feature.DaterRenderer;
import com.bpa4j.ui.swing.features.SwingCalendarRenderer.SwingDateRenderingContext;

public class SwingEmptyDaterRenderer<T> implements DaterRenderer<T>{
    public void render(Dater<T> dater,T t,LocalDate u,DateRenderingContext ctx){
        SwingDateRenderingContext swingCtx=(SwingDateRenderingContext)ctx;

        JLabel l=new JLabel();
        l.setOpaque(true);
        l.setBackground(Color.DARK_GRAY);
        l.setBorder(BorderFactory.createTitledBorder(null,u.toString(),TitledBorder.TOP,0,null,u.isEqual(LocalDate.now())?Color.GREEN:Color.WHITE));

        swingCtx.addComponent(l);
    }
}
