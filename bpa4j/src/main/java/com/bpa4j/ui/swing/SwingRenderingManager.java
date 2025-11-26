package com.bpa4j.ui.swing;

import java.awt.Window;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import javax.swing.JPanel;
import com.bpa4j.core.NavigatorRenderer;
import com.bpa4j.core.RegScreen;
import com.bpa4j.core.RenderingManager;
import com.bpa4j.core.WorkFrame;
import com.bpa4j.core.WorkFrameRenderer;
import com.bpa4j.editor.EditorRenderer;
import com.bpa4j.editor.IEditor;
import com.bpa4j.editor.ModularEditor;
import com.bpa4j.feature.FeatureRenderer;
import com.bpa4j.feature.FeatureRenderingContext;
import com.bpa4j.feature.FeatureTransmissionContract;

public class SwingRenderingManager implements RenderingManager{
	private static class DetachedCtx extends SwingFeatureRenderingContext{
		public DetachedCtx(){
			super(null,null);
		}
		public Window getWindow(){
			Window[] w=Window.getWindows();
			if(w.length==0) return null;
			else return w[w.length-1];
		}
		public JPanel getTarget(){
			return new JPanel();
		}
	}
	private Map<Class<?>,Function<? extends FeatureTransmissionContract,? extends FeatureRenderer<?>>> featureRenderers=new HashMap<>();
	private Map<Class<?>,Function<? extends IEditor,? extends EditorRenderer<?>>> editorRenderers=new HashMap<>();

	private SwingWorkFrameRenderer wfr;
	public SwingRenderingManager(){
		loadDefaults();
	}
	@SuppressWarnings("unchecked")
	public <F extends FeatureTransmissionContract> FeatureRenderer<F> getFeatureRenderer(F feature){
		Function<? super F,? extends FeatureRenderer<F>> renderer=(Function<? super F,? extends FeatureRenderer<F>>)featureRenderers.get(feature.getClass());
		Objects.requireNonNull(renderer,"Feature of type "+feature.getClass()+" does not have any renderer.");
		return renderer.apply(feature);
	}
	@SuppressWarnings("unchecked")
	public <E extends IEditor> EditorRenderer<E> getEditorRenderer(E editor){
		Function<? super E,? extends EditorRenderer<E>> renderer=(Function<? super E,? extends EditorRenderer<E>>)editorRenderers.get(editor.getClass());
		Objects.requireNonNull(renderer);
		return renderer.apply(editor);
	}
	public WorkFrameRenderer getWorkFrameRenderer(WorkFrame wf){
		return wfr=new SwingWorkFrameRenderer(wf);
	}
	public NavigatorRenderer getNavigatorRenderer(){
		return new SwingNavigatorRenderer();
	}
	public RegScreen getRegistrationScreen(){
		return new SwingRegScreen();
	}
	public void close(){
		if(wfr!=null) wfr.dispose();
	}
	public FeatureRenderingContext getDetachedFeatureRenderingContext(){
		return new DetachedCtx();
	}

	public <F extends FeatureTransmissionContract> void putFeatureRenderer(Class<F> e,Function<F,? extends FeatureRenderer<F>> renderer){
		featureRenderers.put(e,renderer);
	}
	public <E extends IEditor> void putEditorRenderer(Class<E> e,Function<E,? extends EditorRenderer<E>> renderer){
		editorRenderers.put(e,renderer);
	}
	@SuppressWarnings("unchecked")
	private void loadDefaults(){
		//Features
		putFeatureRenderer(com.bpa4j.defaults.features.transmission_contracts.ModelEditing.class,f->new com.bpa4j.ui.swing.features.SwingModelEditingRenderer(f));
		putFeatureRenderer(com.bpa4j.defaults.features.transmission_contracts.Board.class,f->new com.bpa4j.ui.swing.features.SwingBoardRenderer<>(f));
		putFeatureRenderer(com.bpa4j.defaults.features.transmission_contracts.Calendar.class,f->new com.bpa4j.ui.swing.features.SwingCalendarRenderer<>(f));
		putFeatureRenderer(com.bpa4j.defaults.features.transmission_contracts.DatedList.class,f->new com.bpa4j.ui.swing.features.SwingDatedListRenderer<>(f));
		putFeatureRenderer(com.bpa4j.defaults.features.transmission_contracts.DisposableDocument.class,f->new com.bpa4j.ui.swing.features.SwingDisposableDocumentRenderer<>(f));
		putFeatureRenderer(com.bpa4j.defaults.features.transmission_contracts.EditableList.class,f->new com.bpa4j.ui.swing.features.SwingEditableListRenderer<>(f));
		putFeatureRenderer(com.bpa4j.defaults.features.transmission_contracts.History.class,f->new com.bpa4j.ui.swing.features.SwingHistoryRenderer(f));
		putFeatureRenderer(com.bpa4j.defaults.features.transmission_contracts.ItemList.class,f->new com.bpa4j.ui.swing.features.SwingItemListRenderer<>(f));
		putFeatureRenderer(com.bpa4j.defaults.features.transmission_contracts.MappedList.class,f->new com.bpa4j.ui.swing.features.SwingMappedListRenderer<>(f));
		putFeatureRenderer(com.bpa4j.defaults.features.transmission_contracts.Report.class,f->new com.bpa4j.ui.swing.features.SwingReportRenderer(f));
		//Editors
		putEditorRenderer(ModularEditor.class,e->new SwingModularEditorRenderer());
	}
}
