package com.bpa4j.ui.rest;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
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
import com.bpa4j.ui.rest.abstractui.UIState;
import com.bpa4j.ui.rest.abstractui.Window;
import com.bpa4j.ui.rest.abstractui.layout.GridLayout;
import com.bpa4j.ui.rest.features.RestBoardRenderer;
import com.bpa4j.ui.rest.features.RestItemListRenderer;

/**
 * @author AI-generated
 */
public class RestRenderingManager implements RenderingManager{
	private static class NoOpNavigatorRenderer implements NavigatorRenderer{
		public void render(Navigator navigator){}
		public void renderInfo(ImplementedInfo info){}
		public void init(){}
	}
	private final UIState state;
	private final Map<Class<?>,Function<? extends FeatureTransmissionContract,? extends FeatureRenderer<?>>>featureRenderers=new HashMap<>();
	private final Map<Class<?>,Function<? extends IEditor,? extends EditorRenderer<?>>>editorRenderers=new HashMap<>();
	private RestWorkFrameRenderer workFrameRenderer;
	public RestRenderingManager(UIState state){
		this.state=state;
		loadDefaults();
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
		workFrameRenderer=new RestWorkFrameRenderer(wf,state);
		return workFrameRenderer;
	}
	public NavigatorRenderer getNavigatorRenderer(){
		return new NoOpNavigatorRenderer();
	}
	public RegScreen getRegistrationScreen(){
		throw new UnsupportedOperationException("REST registration screen is not implemented.");
	}
	public void close(){}
	public FeatureRenderingContext getDetachedFeatureRenderingContext(){
		Panel p=new Panel(new GridLayout(1,1,5,5));
		p.setSize(1000,800);
		Window w=new Window(p);
		return new RestFeatureRenderingContext(state,w,p);
	}
	@SuppressWarnings({"unchecked"})
	private void loadDefaults(){
		putFeatureRenderer(com.bpa4j.defaults.features.transmission_contracts.Board.class,f->new RestBoardRenderer<>(f));
		putFeatureRenderer(com.bpa4j.defaults.features.transmission_contracts.ItemList.class,f->new RestItemListRenderer<>(f));
		putEditorRenderer(com.bpa4j.editor.ModularEditor.class,e->new RestModularEditorRenderer());
	}
}
