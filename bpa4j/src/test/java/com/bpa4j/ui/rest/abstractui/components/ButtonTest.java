package com.bpa4j.ui.rest.abstractui.components;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.Map;
import org.junit.jupiter.api.Test;
import com.bpa4j.ui.rest.abstractui.Color;
import com.bpa4j.ui.rest.abstractui.UIState.JsonVisualContext;

public class ButtonTest {

    @Test
    public void testButtonColor() {
        Button button = new Button("Test");
        assertNull(button.getBackground());

        Color red = new Color(255, 0, 0);
        button.setBackground(red);
        assertEquals(red, button.getBackground());

        JsonVisualContext ctx = new JsonVisualContext(0, 0);
        Map<String, Object> json = button.getJson(ctx);
        
        assertTrue(json.containsKey("background"));
        assertEquals(red.value(), json.get("background"));
        assertEquals((255 << 16), json.get("background"));
    }

    @Test
    public void testColorValidation() {
        assertThrows(IllegalArgumentException.class, () -> new Color(256, 0, 0));
        assertThrows(IllegalArgumentException.class, () -> new Color(-1, 0, 0));
    }
}
