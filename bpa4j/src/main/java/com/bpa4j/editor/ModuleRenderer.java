package com.bpa4j.editor;

import com.bpa4j.core.Editable;
import com.bpa4j.editor.ModularEditorRenderer.ModulesRenderingContext;

public interface ModuleRenderer<M extends EditorModule>{
    public void createTab(Editable editable,boolean isNew,Runnable deleter,M editorModule,ModulesRenderingContext ctx);
}
