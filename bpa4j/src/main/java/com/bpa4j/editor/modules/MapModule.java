package com.bpa4j.editor.modules;

import java.util.Map;
import java.util.function.Function;
import com.bpa4j.core.Editable;
import com.bpa4j.editor.EditorModule;
import com.bpa4j.editor.ModularEditorRenderer;
import com.bpa4j.editor.ModuleRenderer;

public class MapModule implements EditorModule{
    private final EditorModule defaultModule;
    private final Map<Class<?>,EditorModule> map;

    public MapModule(EditorModule defaultModule,Map<Class<?>,EditorModule> map){
        this.defaultModule=defaultModule;
        this.map=map;
    }

    public void createTab(Editable editable,boolean isNew,Runnable deleter,ModularEditorRenderer.ModulesRenderingContext ctx){
        EditorModule m=defaultModule;
        for(Map.Entry<Class<?>,EditorModule> entry:map.entrySet()){
            if(entry.getKey().isAssignableFrom(editable.getClass())){
                m=entry.getValue();
                break;
            }
        }
        m.createTab(editable,isNew,deleter,ctx);
    }

    public <M extends EditorModule> void setRendererSource(Function<M,? extends ModuleRenderer<M>> rendererSource){
        for(EditorModule m:map.values())
            m.setRendererSource(rendererSource);
        if(defaultModule!=null) defaultModule.setRendererSource(rendererSource);
    }
}
