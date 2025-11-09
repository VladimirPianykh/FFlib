package com.bpa4j.editor.modules;

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import com.bpa4j.core.Editable;
import com.bpa4j.editor.EditorModule;
import com.bpa4j.editor.ModularEditorRenderer.ModulesRenderingContext;
import com.bpa4j.editor.ModuleRenderer;

public class MapModule implements EditorModule{
    private final EditorModule defaultModule;
    private final Map<Class<?>,EditorModule> moduleClassMap;
    public MapModule(EditorModule defaultModule,Map<Class<?>,EditorModule> moduleClassMap){
        Objects.requireNonNull(moduleClassMap);
        moduleClassMap.values().forEach(Objects::requireNonNull);
        this.defaultModule=defaultModule;
        this.moduleClassMap=moduleClassMap;
    }
    public EditorModule getDefaultModule(){
        return defaultModule;
    }
    public EditorModule getModule(Class<?> clazz){
        for(var pair:moduleClassMap.entrySet())
            if(pair.getKey().isAssignableFrom(clazz)) return pair.getValue();
        return defaultModule;
    }
    public void createTab(Editable editable,boolean isNew,Runnable deleter,ModulesRenderingContext ctx){
        getModule(editable.getClass()).createTab(editable,isNew,deleter,ctx);
    }
	public <M extends EditorModule> void setRendererSource(Function<M,? extends ModuleRenderer<M>> rendererSource){
		for(EditorModule m:moduleClassMap.values())
			m.setRendererSource(rendererSource);
		if(defaultModule!=null) defaultModule.setRendererSource(rendererSource);
	}
}