package com.bpa4j.feature;

import com.bpa4j.core.RenderingContext;
import com.bpa4j.defaults.features.transmission_contracts.Report;

/**
 * Renderer interface for Report features, following the ModularEditor pattern.
 * Provides specialized renderers for DataRenderers and Configurators.
 * @author AI-generated
 */
public interface ReportRenderer extends FeatureRenderer<Report> {
	/**
	 * Context for rendering data in reports.
	 */
	public static interface DataRenderingContext extends RenderingContext {}
	
	/**
	 * Context for rendering configurators in reports.
	 */
	public static interface ConfiguratorRenderingContext extends RenderingContext {}
	
	/**
	 * Gets a renderer for the specified DataRenderer.
	 * @param <D> the type of DataRenderer
	 * @param dataRenderer the DataRenderer instance
	 * @return the renderer for this DataRenderer
	 */
	<D extends Report.DataRenderer> DataRendererRenderer<D> getDataRendererRenderer(D dataRenderer);
	
	/**
	 * Gets a renderer for the specified Configurator.
	 * @param <C> the type of Configurator
	 * @param configurator the Configurator instance
	 * @return the renderer for this Configurator
	 */
	<C extends Report.Configurator> ConfiguratorRenderer<C> getConfiguratorRenderer(C configurator);
	
	/**
	 * Creates a data rendering context from the feature rendering context.
	 * @param context the feature rendering context
	 * @return the data rendering context
	 */
	DataRenderingContext getDataRenderingContext(FeatureRenderingContext context);
	
	/**
	 * Creates a configurator rendering context from the feature rendering context.
	 * @param context the feature rendering context
	 * @return the configurator rendering context
	 */
	ConfiguratorRenderingContext getConfiguratorRenderingContext(FeatureRenderingContext context);
}
