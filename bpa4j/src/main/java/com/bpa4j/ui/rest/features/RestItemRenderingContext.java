package com.bpa4j.ui.rest.features;

import com.bpa4j.defaults.features.transmission_contracts.EditableList.ItemRenderingContext;
import com.bpa4j.ui.rest.abstractui.Panel;

/**
 * REST implementation of ItemRenderingContext.
 * Provides a Panel target for rendering custom item components in REST UI.
 * This makes componentProvider UI-agnostic by allowing both Swing and REST implementations.
 * 
 * @author AI-generated
 */
public class RestItemRenderingContext implements ItemRenderingContext{
    private final Panel target;

    public RestItemRenderingContext(Panel target){
        this.target=target;
    }

    /**
     * Gets the target Panel where item components should be added.
     * @return the Panel target
     */
    public Panel getTarget(){
        return target;
    }
}
