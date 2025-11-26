package com.bpa4j.feature;

import com.bpa4j.defaults.features.transmission_contracts.Report;

/**
 * Renderer interface for DataRenderers, analogous to ModuleRenderer.
 * Each UI implementation provides its own concrete DataRendererRenderer.
 * @param <D> the type of DataRenderer this renderer handles
 * @author AI-generated
 */
public interface DataRendererRenderer<D extends Report.DataRenderer> {
	/**
	 * Renders the data using the provided DataRenderer and context.
	 * @param dataRenderer the DataRenderer to render
	 * @param ctx the rendering context specific to the UI framework
	 */
	void render(D dataRenderer, ReportRenderer.DataRenderingContext ctx);
}
