package com.bpa4j.editor.modules;

import java.util.function.Function;
import com.bpa4j.core.Editable;
import com.bpa4j.defaults.editables.Processable;
import com.bpa4j.editor.EditorModule;
import com.bpa4j.editor.ModularEditorRenderer;
import com.bpa4j.editor.ModuleRenderer;

public class StageMapModule implements EditorModule{
    private Function<EditorModule,ModuleRenderer<StageMapModule>> rendererSource;
    
    public void createTab(Editable editable,boolean isNew,Runnable deleter,ModularEditorRenderer.ModulesRenderingContext ctx){
        if(!(editable instanceof Processable))throw new IllegalArgumentException("StageMapModule is only for Processables.");
        if(rendererSource!=null){
            rendererSource.apply(this).createTab(editable,isNew,deleter,this,ctx);
        }
    }
    
    @SuppressWarnings("unchecked")
    public <M extends EditorModule> void setRendererSource(Function<M,? extends ModuleRenderer<M>> rendererSource){
        this.rendererSource=(Function<EditorModule,ModuleRenderer<StageMapModule>>)rendererSource;
    }
}
