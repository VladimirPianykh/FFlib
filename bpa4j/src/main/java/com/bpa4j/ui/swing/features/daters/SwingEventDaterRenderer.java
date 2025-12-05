package com.bpa4j.ui.swing.features.daters;

import java.awt.Color;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.border.TitledBorder;
import com.bpa4j.Dater;
import com.bpa4j.defaults.features.transmission_contracts.Calendar.DateRenderingContext;
import com.bpa4j.defaults.features.transmission_contracts.Calendar.Event;
import com.bpa4j.defaults.ftr_attributes.daters.EventDater;
import com.bpa4j.feature.DaterRenderer;
import com.bpa4j.ui.swing.features.SwingCalendarRenderer.SwingDateRenderingContext;

public class SwingEventDaterRenderer<T extends Event> implements DaterRenderer<List<T>> {

    @Override
    @SuppressWarnings("unchecked")
    public void render(Dater<List<T>> dater, List<T> a, LocalDate u, DateRenderingContext ctx) {
        SwingDateRenderingContext swingCtx = (SwingDateRenderingContext) ctx;
        EventDater<T> eventDater = (EventDater<T>) dater;

        JComponent c;
        if (a.isEmpty()) {
            c = new JLabel();
            c.setOpaque(true);
        } else {
            JComboBox<Object> b = new JComboBox<>(a.toArray());
            b.setSelectedItem(null);
            b.addActionListener(e -> {
                JPopupMenu m = new JPopupMenu(u.toString());
                JPanel p = new JPanel(new GridLayout());
                p.add(eventDater.getProvider().apply((T) b.getSelectedItem()));
                m.add(p);
                m.show(b.getTopLevelAncestor(), b.getLocationOnScreen().x, b.getLocationOnScreen().y);
                b.setSelectedItem(null);
            });
            c = b;
        }
        c.setBackground(Color.DARK_GRAY);
        c.setForeground(Color.WHITE);
        c.setBorder(BorderFactory.createTitledBorder(null, u.toString(), TitledBorder.TOP, 0, null, u.isEqual(LocalDate.now()) ? Color.GREEN : Color.WHITE));
        
        swingCtx.addComponent(c);
    }
}
