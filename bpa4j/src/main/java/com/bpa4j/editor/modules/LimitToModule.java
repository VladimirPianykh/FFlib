package com.bpa4j.editor.modules;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import com.bpa4j.core.Editable;
import com.bpa4j.editor.EditorModule;
import com.bpa4j.editor.ModularEditorRenderer.ModulesRenderingContext;
import com.bpa4j.editor.ModuleRenderer;

public class LimitToModule implements EditorModule{
	private final EditorModule module;
	List<Class<? extends Editable>> types;
	@SafeVarargs
	public LimitToModule(EditorModule module,Class<? extends Editable>...types){
		this.types=Arrays.asList(types);
		this.module=module;
	}
	public void createTab(Editable editable,boolean isNew,Runnable deleter,ModulesRenderingContext ctx){
		boolean flag=false;
		for(Class<? extends Editable> m:types)
			if(m.isAssignableFrom(editable.getClass())) flag=true;
		if(!flag) module.createTab(editable,isNew,deleter,ctx);
	}
	public <M extends EditorModule> void setRendererSource(Function<M,? extends ModuleRenderer<M>> rendererSource){
		module.setRendererSource(rendererSource);
	}
}
