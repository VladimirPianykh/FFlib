package com.bpa4j.editor;

import com.bpa4j.core.Editable;
import com.bpa4j.editor.ModularEditorRenderer.ModulesRenderingContext;
import com.bpa4j.feature.FeatureRenderingContext;

public interface EditorRenderer<E extends IEditor>{
    public void constructEditor(Editable editable,boolean isNew,Runnable deleter,E editor,FeatureRenderingContext context,ModulesRenderingContext moduleContext);
}
