package com.bpa4j.editor.modules;

import java.util.function.Function;
import com.bpa4j.core.Editable;
import com.bpa4j.defaults.editables.AbstractCustomer;
import com.bpa4j.editor.EditorModule;
import com.bpa4j.editor.ModuleRenderer;
import com.bpa4j.editor.ModularEditorRenderer.ModulesRenderingContext;

public class CustomerModule implements EditorModule{
	private Function<EditorModule,ModuleRenderer<CustomerModule>> rendererSource;
	public void createTab(Editable editable,boolean isNew,Runnable deleter,ModulesRenderingContext ctx){
		if(!(editable instanceof AbstractCustomer)) throw new IllegalArgumentException(editable+" is not a customer and thus cannot be edited in CustomerModule.");
		rendererSource.apply(this).createTab(editable,isNew,deleter,this,ctx);
	}
	@SuppressWarnings("unchecked")
	public <M extends EditorModule> void setRendererSource(Function<M,? extends ModuleRenderer<M>> rendererSource){
		this.rendererSource=(Function<EditorModule,ModuleRenderer<CustomerModule>>)rendererSource;
	}
}
