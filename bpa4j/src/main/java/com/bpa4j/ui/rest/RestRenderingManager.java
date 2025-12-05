package com.bpa4j.ui.rest;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import com.bpa4j.core.EditableGroup;
import com.bpa4j.core.EditableGroupRenderer;
import com.bpa4j.core.NavigatorRenderer;
import com.bpa4j.core.RegScreen;
import com.bpa4j.core.RenderingManager;
import com.bpa4j.core.WorkFrame;
import com.bpa4j.core.WorkFrameRenderer;
import com.bpa4j.editor.EditorRenderer;
import com.bpa4j.editor.IEditor;
import com.bpa4j.feature.FeatureRenderer;
import com.bpa4j.feature.FeatureRenderingContext;
import com.bpa4j.feature.FeatureTransmissionContract;
import com.bpa4j.navigation.ImplementedInfo;
import com.bpa4j.navigation.Navigator;
import com.bpa4j.ui.rest.abstractui.Panel;
import com.bpa4j.ui.rest.abstractui.Size;
import com.bpa4j.ui.rest.abstractui.UIState;
import com.bpa4j.ui.rest.abstractui.Window;
import com.bpa4j.ui.rest.abstractui.layout.GridLayout;
import com.bpa4j.ui.rest.features.RestBoardRenderer;
import com.bpa4j.ui.rest.features.RestCalendarRenderer;
import com.bpa4j.ui.rest.features.RestDisposableDocumentRenderer;
import com.bpa4j.ui.rest.features.RestEditableListRenderer;
import com.bpa4j.ui.rest.features.RestHistoryRenderer;
import com.bpa4j.ui.rest.features.RestItemListRenderer;
import com.bpa4j.ui.rest.features.RestMappedListRenderer;
import com.bpa4j.ui.rest.features.RestModelEditingRenderer;
import com.bpa4j.ui.rest.features.RestDatedListRenderer;
import com.bpa4j.ui.rest.features.RestReportRenderer;

/**
 * @author AI-generated
 */
public class RestRenderingManager implements RenderingManager{
	private static class NoOpNavigatorRenderer implements NavigatorRenderer{
		public void render(Navigator navigator){}
		public void renderInfo(ImplementedInfo info){}
		public void init(){}
	}
	public static final Size DEFAULT_SIZE=new Size(1000,800);
	private final UIState state;
	private final UIServer server;
	private final Map<Class<?>,Function<? extends FeatureTransmissionContract,? extends FeatureRenderer<?>>>featureRenderers=new HashMap<>();
	private final Map<Class<?>,Function<? extends IEditor,? extends EditorRenderer<?>>>editorRenderers=new HashMap<>();
	private RestWorkFrameRenderer workFrameRenderer;
	public RestRenderingManager(UIState state){
		this.state=state;
		loadDefaults();
		server=new UIServer(state);
		server.start();
	}
	@SuppressWarnings("unchecked")
	public <F extends FeatureTransmissionContract>FeatureRenderer<F>getFeatureRenderer(F feature){
		Function<? super F,? extends FeatureRenderer<F>>renderer=(Function<? super F,? extends FeatureRenderer<F>>)featureRenderers.get(feature.getClass());
		Objects.requireNonNull(renderer,"Feature of type "+feature.getClass()+" does not have any REST renderer.");
		return renderer.apply(feature);
	}
	public <F extends FeatureTransmissionContract>void putFeatureRenderer(Class<F>e,Function<F,? extends FeatureRenderer<F>>renderer){
		featureRenderers.put(e,renderer);
	}
	@SuppressWarnings("unchecked")
	public <E extends IEditor>EditorRenderer<E>getEditorRenderer(E editor){
		Function<? super E,? extends EditorRenderer<E>>renderer=(Function<? super E,? extends EditorRenderer<E>>)editorRenderers.get(editor.getClass());
		if(renderer==null)throw new UnsupportedOperationException("REST editors are not implemented.");
		return renderer.apply(editor);
	}
	public <E extends IEditor>void putEditorRenderer(Class<E>e,Function<E,? extends EditorRenderer<E>>renderer){
		editorRenderers.put(e,renderer);
	}
	public WorkFrameRenderer getWorkFrameRenderer(WorkFrame wf){
		if(workFrameRenderer==null) workFrameRenderer=new RestWorkFrameRenderer(wf,state);
		return workFrameRenderer;
	}
	public NavigatorRenderer getNavigatorRenderer(){
		return new NoOpNavigatorRenderer();
	}
	public RegScreen getRegistrationScreen(){
		return new RestRegScreen(state);
	}
	public void close(){
		server.stop();
	}
	public FeatureRenderingContext getDetachedFeatureRenderingContext(){
		Panel p=new Panel(new GridLayout(1,1,5,5));
		p.setSize(RestRenderingManager.DEFAULT_SIZE);
		Window w=new Window(p);
		return new RestFeatureRenderingContext(state,w,p,null);
	}
	@SuppressWarnings({"unchecked"})
	private void loadDefaults(){
		putFeatureRenderer(com.bpa4j.defaults.features.transmission_contracts.Board.class,f->new RestBoardRenderer<>(f));
		putFeatureRenderer(com.bpa4j.defaults.features.transmission_contracts.ItemList.class,f->new RestItemListRenderer<>(f));
		putFeatureRenderer(com.bpa4j.defaults.features.transmission_contracts.Calendar.class,f->new RestCalendarRenderer<>(f));
		putFeatureRenderer(com.bpa4j.defaults.features.transmission_contracts.History.class,f->new RestHistoryRenderer(f));
		putFeatureRenderer(com.bpa4j.defaults.features.transmission_contracts.Report.class,f->new RestReportRenderer(f));
		putFeatureRenderer(com.bpa4j.defaults.features.transmission_contracts.DisposableDocument.class,f->new RestDisposableDocumentRenderer<>(f));
		putFeatureRenderer(com.bpa4j.defaults.features.transmission_contracts.EditableList.class,f->new RestEditableListRenderer<>(f));
		putFeatureRenderer(com.bpa4j.defaults.features.transmission_contracts.MappedList.class,f->new RestMappedListRenderer<>(f));
		putFeatureRenderer(com.bpa4j.defaults.features.transmission_contracts.ModelEditing.class,f->new RestModelEditingRenderer(f));
		putFeatureRenderer(com.bpa4j.defaults.features.transmission_contracts.DatedList.class,f->new RestDatedListRenderer<>(f));
		putEditorRenderer(com.bpa4j.editor.ModularEditor.class,e->new RestModularEditorRenderer());
	}
	@SuppressWarnings("unchecked")
	public <T extends EditableGroup<?>> EditableGroupRenderer<? super T> getEditableGroupRenderer(T group){
		return (EditableGroupRenderer<? super T>)new RestEditableGroupRenderer();
	}
}
