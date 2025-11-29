package com.bpa4j.ui.swing;

import java.awt.Window;
import javax.swing.JPanel;
import com.bpa4j.feature.FeatureRenderingContext;

public class SwingFeatureRenderingContext implements FeatureRenderingContext{
    private final Window window;
    private final JPanel target;
    public SwingFeatureRenderingContext(Window window,JPanel target){
        this.window=window;
        this.target=target;
    }
    public Window getWindow(){
        return window;
    }
    /**
     * Returns the target where the feature should be rendered.
     * The feature can do anything with it: change layout, and as many components as needed, etc.
     */
    public JPanel getTarget(){
        return target;
    }
}
