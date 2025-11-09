package com.bpa4j.core;

import com.bpa4j.feature.FeatureRenderingContext;

/**
 * View of the {@code WorkFrame} model.
 * Shouldn't call features' methods itself, so we've hidden the FeatureContext's feature field at all.
 */
public interface WorkFrameRenderer{
    public WorkFrame getWorkFrame();
    public void showWorkFrame();
    public FeatureRenderingContext getContext(WorkFrame.FeatureEntry<?>f);
}
