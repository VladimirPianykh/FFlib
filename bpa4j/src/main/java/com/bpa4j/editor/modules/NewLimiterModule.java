package com.bpa4j.editor.modules;

import java.util.function.Function;
import com.bpa4j.core.Editable;
import com.bpa4j.editor.EditorModule;
import com.bpa4j.editor.ModularEditorRenderer.ModulesRenderingContext;
import com.bpa4j.editor.ModuleRenderer;

public class NewLimiterModule implements EditorModule{
	private final EditorModule module;
	private final boolean isNew;
	public NewLimiterModule(EditorModule module,boolean isNew){
		this.module=module;
		this.isNew=isNew;
	}
	public void createTab(Editable editable,boolean isNew,Runnable deleter,ModulesRenderingContext ctx){
		if(this.isNew==isNew)module.createTab(editable,isNew,deleter,ctx);
	}
	public <M extends EditorModule> void setRendererSource(Function<M,? extends ModuleRenderer<M>> rendererSource){
		module.setRendererSource(rendererSource);
	}
}
