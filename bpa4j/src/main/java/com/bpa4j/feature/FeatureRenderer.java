package com.bpa4j.feature;

/**
 * Feature renderer instance, bound to {@link FeatureTransmissionContract}.
 * Should get the contract via constructor.
 */
public interface FeatureRenderer<F extends FeatureTransmissionContract>{
	F getTransmissionContract();
	void render(FeatureRenderingContext ctx);
	public void renderPreview(FeatureRenderingContext ctx);
}
