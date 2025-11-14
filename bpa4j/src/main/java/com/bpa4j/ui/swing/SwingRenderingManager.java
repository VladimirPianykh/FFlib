package com.bpa4j.ui.swing;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
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

public class SwingRenderingManager implements RenderingManager{
	private Map<Class<?>,Function<? extends FeatureTransmissionContract,? extends FeatureRenderer<?>>> featureRenderers=new HashMap<>();
	private Map<Class<?>,Function<? extends IEditor,? extends EditorRenderer<?>>> editorRenderers=new HashMap<>();
	private Map<Class<?>,Function<? extends EditorModule,? extends ModuleRenderer<?>>> moduleRenderers=new HashMap<>();

	private SwingWorkFrameRenderer wfr;
	public SwingRenderingManager(){
		loadDefaults();
	}
	@SuppressWarnings("unchecked")
	public <F extends FeatureTransmissionContract> FeatureRenderer<F> getFeatureRenderer(F feature){
		Function<? super F,? extends FeatureRenderer<F>> renderer=(Function<? super F,? extends FeatureRenderer<F>>)featureRenderers.get(feature.getClass());
		Objects.requireNonNull(renderer);
		return renderer.apply(feature);
	}
	@SuppressWarnings("unchecked")
	public <E extends IEditor> EditorRenderer<E> getEditorRenderer(E editor){
		Function<? super E,? extends EditorRenderer<E>> renderer=(Function<? super E,? extends EditorRenderer<E>>)editorRenderers.get(editor.getClass());
		Objects.requireNonNull(renderer);
		return renderer.apply(editor);
	}
	@SuppressWarnings("unchecked")
	public <M extends EditorModule> ModuleRenderer<M> getModuleRenderer(M module){
		Function<? super M,? extends ModuleRenderer<M>> renderer=(Function<? super M,? extends ModuleRenderer<M>>)moduleRenderers.get(module.getClass());
		Objects.requireNonNull(renderer);
		return renderer.apply(module);
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

	public <F extends FeatureTransmissionContract> void putFeatureRenderer(Class<F> e,Function<F,? extends FeatureRenderer<F>> renderer){
		featureRenderers.put(e,renderer);
	}
	public <E extends IEditor> void putEditorRenderer(Class<E> e,Function<E,? extends EditorRenderer<E>> renderer){
		editorRenderers.put(e,renderer);
	}
	public <M extends EditorModule> void putModuleRenderer(Class<M> e,Function<M,? extends ModuleRenderer<M>> renderer){
		moduleRenderers.put(e,renderer);
	}
	@SuppressWarnings("unchecked")
	private void loadDefaults(){
		putFeatureRenderer(com.bpa4j.defaults.features.transmission_contracts.Board.class,f->new com.bpa4j.ui.swing.features.SwingBoardRenderer<>(f));
		putFeatureRenderer(com.bpa4j.defaults.features.transmission_contracts.Calendar.class,f->new com.bpa4j.ui.swing.features.SwingCalendarRenderer<>(f));
		putFeatureRenderer(com.bpa4j.defaults.features.transmission_contracts.ItemList.class,f->new com.bpa4j.ui.swing.features.SwingItemListRenderer<>(f));
		putFeatureRenderer(com.bpa4j.defaults.features.transmission_contracts.Report.class,f->new com.bpa4j.ui.swing.features.SwingReportRenderer(f));
		putFeatureRenderer(com.bpa4j.defaults.features.transmission_contracts.DatedList.class,f->new com.bpa4j.ui.swing.features.SwingDatedListRenderer<>(f));
	}
}
