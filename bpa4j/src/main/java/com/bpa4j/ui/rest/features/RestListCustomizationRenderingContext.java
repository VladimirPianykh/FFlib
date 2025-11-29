package com.bpa4j.ui.rest.features;

import com.bpa4j.defaults.features.transmission_contracts.ItemList.ListCustomizationRenderingContext;
import com.bpa4j.ui.rest.abstractui.Panel;

/**
 * REST implementation of ListCustomizationRenderingContext.
 * Provides a Panel target for customizing list rendering in REST UI.
 * This allows users to customize list appearance and behavior in a UI-agnostic way.
 * 
 * @author AI-generated
 */
public class RestListCustomizationRenderingContext implements ListCustomizationRenderingContext{
    private final Panel target;

    public RestListCustomizationRenderingContext(Panel target){
        this.target=target;
    }

    /**
     * Gets the target Panel where list customization components should be added.
     * @return the Panel target
     */
    public Panel getTarget(){
        return target;
    }
}
