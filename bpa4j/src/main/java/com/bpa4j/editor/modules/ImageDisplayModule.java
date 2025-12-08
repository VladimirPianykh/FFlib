package com.bpa4j.editor.modules;

import java.lang.reflect.Field;
import java.util.function.Function;

import com.bpa4j.core.Editable;
import com.bpa4j.editor.EditorModule;
import com.bpa4j.editor.ModularEditorRenderer;
import com.bpa4j.editor.ModuleRenderer;

/**
 * A module that displays an image field.
 */
public class ImageDisplayModule implements EditorModule{
	private Field f;
	private Function<EditorModule,ModuleRenderer<ImageDisplayModule>> rendererSource;
	/**
	 * Constructs a new ImageDisplayModule for the given field.
	 * @param f - field to display
	 */
	public ImageDisplayModule(Field f){
		this.f=f;
	}
	/**
	 * Gets the field that this module displays.
	 * @return the field to display
	 */
	public Field getField(){
		return f;
	}
	/**
	 * Creates a tab for displaying the image field.
	 * @param editable - the editable object containing the field
	 * @param isNew - whether this is a new editable object
	 * @param deleter - runnable to delete the editable object
	 * @param ctx - the rendering context
	 */
	public void createTab(Editable editable,boolean isNew,Runnable deleter,ModularEditorRenderer.ModulesRenderingContext ctx){
		if(rendererSource!=null){
			rendererSource.apply(this).createTab(editable,isNew,deleter,this,ctx);
		}
	}
	/**
	 * Sets the renderer source for this module.
	 * @param rendererSource - the function that provides the renderer
	 * @param <M> - the type of editor module
	 */
	@SuppressWarnings("unchecked")
	public <M extends EditorModule> void setRendererSource(Function<M,? extends ModuleRenderer<M>> rendererSource){
		this.rendererSource = (Function<EditorModule, ModuleRenderer<ImageDisplayModule>>) (Function<?, ?>) rendererSource;
	}
}
