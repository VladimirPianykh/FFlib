package com.bpa4j.ui.rest;

import com.bpa4j.core.Editable;
import com.bpa4j.editor.EditorModule;
import com.bpa4j.editor.ModularEditor;
import com.bpa4j.editor.ModularEditorRenderer;
import com.bpa4j.editor.ModuleRenderer;
import com.bpa4j.editor.modules.FormModule;
import com.bpa4j.editor.modules.StageMapModule;
import com.bpa4j.feature.FeatureRenderingContext;
import com.bpa4j.ui.rest.abstractui.Panel;
import com.bpa4j.ui.rest.abstractui.components.Label;
import com.bpa4j.ui.rest.abstractui.layout.FlowLayout;
import com.bpa4j.ui.rest.abstractui.Window;

/**
 * REST implementation of {@link ModularEditorRenderer}.
 * For now, provides a simple placeholder UI for editor modules
 * so that editor integration works without Swing.
 */
public class RestModularEditorRenderer implements ModularEditorRenderer{
	public static class RestModulesRenderingContext implements ModulesRenderingContext{
		private final RestFeatureRenderingContext base;
		private Window w;
		private Panel target;
		public RestModulesRenderingContext(RestFeatureRenderingContext base,Window w,Panel target){
			this.base=base;
			this.w=w;
			this.target=target;
		}
		// public RestFeatureRenderingContext getBase(){
		// 	return base;
		// }
		public Panel getTarget(){
			return target;
		}
		public void show(){
			base.getState().showWindow(w);
		}
		public void close(){
			base.getState().close(w);
			base.rebuild();
		}
		public void rebuild(){
			base.rebuild();
		}
	}
	private static class RestDummyModuleRenderer<M extends EditorModule> implements ModuleRenderer<M>{
		public void createTab(Editable editable,boolean isNew,Runnable deleter,M module,ModulesRenderingContext ctx){
			RestModulesRenderingContext rctx=(RestModulesRenderingContext)ctx;
			Panel container=rctx.getTarget();
			Panel tab=new Panel(new FlowLayout());
			Label label=new Label("Module "+module.getClass().getSimpleName()+" is not implemented for REST UI.");
			tab.add(label);
			container.add(tab);
		}
	}
	public <M extends EditorModule> ModuleRenderer<M> getModuleRenderer(M m){
		if(m instanceof FormModule){
			@SuppressWarnings("unchecked")
			ModuleRenderer<M> r=(ModuleRenderer<M>)new com.bpa4j.ui.rest.editor.modules.RestFormModuleRenderer();
			return r;
		}
		if(m instanceof StageMapModule){
			@SuppressWarnings("unchecked")
			ModuleRenderer<M> r=(ModuleRenderer<M>)new com.bpa4j.ui.rest.editor.modules.RestStageMapModuleRenderer();
			return r;
		}
		return new RestDummyModuleRenderer<>();
	}
	public Panel content=new Panel();
	public Window window=new Window(content);
	public RestModularEditorRenderer(){
		window.setSize(RestRenderingManager.DEFAULT_SIZE);
		content.setSize(RestRenderingManager.DEFAULT_SIZE);
	}
	public ModulesRenderingContext getModulesRenderingContext(FeatureRenderingContext context){
		if(!(context instanceof RestFeatureRenderingContext))throw new IllegalArgumentException("RestModularEditorRenderer requires RestFeatureRenderingContext.");
		RestFeatureRenderingContext rctx=(RestFeatureRenderingContext)context;
		return new RestModulesRenderingContext(rctx,window,content);
	}
	public void constructEditor(Editable editable,boolean isNew,Runnable deleter,ModularEditor editor,FeatureRenderingContext context,ModulesRenderingContext moduleContext){
		RestModulesRenderingContext rctx=(RestModulesRenderingContext)moduleContext;
		rctx.show();
	}
}
