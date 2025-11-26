package com.bpa4j.core;

import java.util.function.Function;
import com.bpa4j.editor.EditorRenderer;
import com.bpa4j.editor.IEditor;
import com.bpa4j.feature.FeatureRenderer;
import com.bpa4j.feature.FeatureRenderingContext;
import com.bpa4j.feature.FeatureTransmissionContract;

/**
 * Factory for rendering method providers.
 */
public interface RenderingManager{
	<F extends FeatureTransmissionContract>FeatureRenderer<F>getFeatureRenderer(F feature);
	<F extends FeatureTransmissionContract>void putFeatureRenderer(Class<F>e,Function<F,? extends FeatureRenderer<F>>renderer);
	<E extends IEditor>EditorRenderer<E>getEditorRenderer(E editor);
	<E extends IEditor>void putEditorRenderer(Class<E>e,Function<E,? extends EditorRenderer<E>>renderer);
	WorkFrameRenderer getWorkFrameRenderer(WorkFrame wf);
	NavigatorRenderer getNavigatorRenderer();
	RegScreen getRegistrationScreen();
	void close();
	FeatureRenderingContext getDetachedFeatureRenderingContext();
}
