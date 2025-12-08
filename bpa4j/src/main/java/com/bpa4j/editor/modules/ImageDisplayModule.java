package com.bpa4j.editor.modules;

import java.lang.reflect.Field;
import java.util.function.Function;

import com.bpa4j.core.Editable;
import com.bpa4j.editor.EditorModule;
import com.bpa4j.editor.ModularEditorRenderer;
import com.bpa4j.editor.ModuleRenderer;

public class ImageDisplayModule implements EditorModule{
	private Field f;
	private Function<EditorModule,ModuleRenderer<ImageDisplayModule>> rendererSource;
	/**
	 * @param f - field to display
	 */
	public ImageDisplayModule(Field f){
		this.f=f;
	}
	public Field getField(){
		return f;
	}
	public void createTab(Editable editable,boolean isNew,Runnable deleter,ModularEditorRenderer.ModulesRenderingContext ctx){
		if(rendererSource!=null){
			rendererSource.apply(this).createTab(editable,isNew,deleter,this,ctx);
		}
	}
	@SuppressWarnings("unchecked")
	public <M extends EditorModule> void setRendererSource(Function<M,? extends ModuleRenderer<M>> rendererSource){
		this.rendererSource = (Function<EditorModule, ModuleRenderer<ImageDisplayModule>>) (Function<?, ?>) rendererSource;
	}
}
