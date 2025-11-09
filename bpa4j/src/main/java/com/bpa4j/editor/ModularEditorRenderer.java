package com.bpa4j.editor;

import com.bpa4j.feature.FeatureRenderingContext;

/**
 * Makes {@link ModularEditor} accessible to the user and creates module renderers.
 * Some modules do not have any renderer.
 */
public interface ModularEditorRenderer extends EditorRenderer<ModularEditor>{
	/**
	 * Context for rendering all modules of one modular editor.
	 */
	public static interface ModulesRenderingContext{}
	public <M extends EditorModule> ModuleRenderer<M> getModuleRenderer(M m);
    public ModulesRenderingContext getModulesRenderingContext(FeatureRenderingContext context);
}
