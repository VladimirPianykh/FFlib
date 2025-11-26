package com.bpa4j.feature;

import com.bpa4j.defaults.features.transmission_contracts.Report;

/**
 * Renderer interface for Configurators, analogous to ModuleRenderer.
 * Each UI implementation provides its own concrete ConfiguratorRenderer.
 * @param <C> the type of Configurator this renderer handles
 * @author AI-generated
 */
public interface ConfiguratorRenderer<C extends Report.Configurator> {
	/**
	 * Renders the configurator using the provided Configurator and context.
	 * @param configurator the Configurator to render
	 * @param ctx the rendering context specific to the UI framework
	 */
	void render(C configurator, ReportRenderer.ConfiguratorRenderingContext ctx);
}
