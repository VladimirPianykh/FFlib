package com.bpa4j.core;

import com.bpa4j.defaults.features.transmission_contracts.EditableList.ItemRenderingContext;

public interface EditableGroupRenderer<T extends EditableGroup<? extends Editable>>{
    public void renderElementButton(T group,Editable e,ItemRenderingContext ctx);
    public void renderAddButton(T group,ItemRenderingContext ctx);
}
