package com.bpa4j.ui.swing;

import java.util.function.Supplier;
import javax.swing.JPanel;
import com.bpa4j.core.RegScreen;
import com.bpa4j.core.RenderingManager;
import com.bpa4j.core.WorkFrame;
import com.bpa4j.core.WorkFrameRenderer;
import com.bpa4j.editor.EditorModule;
import com.bpa4j.editor.EditorRenderer;
import com.bpa4j.editor.IEditor;
import com.bpa4j.editor.ModuleRenderer;
import com.bpa4j.feature.FeatureRenderer;
import com.bpa4j.feature.FeatureRenderingContext;
import com.bpa4j.feature.FeatureTransmissionContract;
import java.util.Map;
import java.util.Objects;
import java.util.HashMap;

public class SwingRenderingManager implements RenderingManager{
	private Map<Class<?>,Supplier<? extends FeatureRenderer<?>>> featureRenderers=new HashMap<>();
	private Map<Class<?>,Supplier<? extends EditorRenderer<?>>> editorRenderers=new HashMap<>();
	private Map<Class<?>,Supplier<? extends ModuleRenderer<?>>> moduleRenderers=new HashMap<>();

	private SwingWorkFrameRenderer wfr;
	public SwingRenderingManager(){
		//TODO: load defaults
	}
	@SuppressWarnings("unchecked")
	public <F extends FeatureTransmissionContract> FeatureRenderer<F> getFeatureRenderer(F feature){
		Supplier<? extends FeatureRenderer<?>> renderer=featureRenderers.get(feature.getClass());
		Objects.requireNonNull(renderer);
		return (FeatureRenderer<F>)renderer.get();
	}
	@SuppressWarnings("unchecked")
	public <E extends IEditor> EditorRenderer<E> getEditorRenderer(E editor){
		Supplier<? extends EditorRenderer<?>> renderer=editorRenderers.get(editor.getClass());
		Objects.requireNonNull(renderer);
		return (EditorRenderer<E>)renderer.get();
	}
	@SuppressWarnings("unchecked")
	public <M extends EditorModule> ModuleRenderer<M> getModuleRenderer(M module){
		Supplier<? extends ModuleRenderer<?>> renderer=moduleRenderers.get(module.getClass());
		Objects.requireNonNull(renderer);
		return (ModuleRenderer<M>)renderer.get();
	}
	public WorkFrameRenderer getWorkFrameRenderer(WorkFrame wf){
		return wfr=new SwingWorkFrameRenderer(wf);
	}
	public RegScreen getRegistrationScreen(){
		return new SwingRegScreen();
	}
	public void close(){
		if(wfr!=null) wfr.dispose();
	}
	public FeatureRenderingContext getDetachedFeatureRenderingContext(){
		return new SwingFeatureRenderingContext(null,new JPanel());
	}

	public <F extends FeatureTransmissionContract> void putFeatureRenderer(Class<F> e,Supplier<FeatureRenderer<F>> renderer){
		featureRenderers.put(e,renderer);
	}
	public <E extends IEditor> void putEditorRenderer(Class<E> e,Supplier<EditorRenderer<E>> renderer){
		editorRenderers.put(e,renderer);
	}
	public <M extends EditorModule> void putModuleRenderer(Class<M> e,Supplier<ModuleRenderer<M>> renderer){
		moduleRenderers.put(e,renderer);
	}
}
