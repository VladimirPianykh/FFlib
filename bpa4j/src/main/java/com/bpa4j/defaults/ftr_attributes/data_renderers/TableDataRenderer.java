package com.bpa4j.defaults.ftr_attributes.data_renderers;

import java.util.ArrayList;
import java.util.function.Supplier;
import com.bpa4j.defaults.features.transmission_contracts.Report;

/**
 * <p>Returns a table, rendering all editable fields of the component given.</p>
 * <p>You can create a separate info class or use an existing editable to fill the table.</p>
 * @author AI-generated
 */
public final class TableDataRenderer<T> implements Report.DataRenderer{
	// private Function<TableDataRenderer<T>,DataRendererRenderer<TableDataRenderer<T>>> rendererSource;
	private Supplier<ArrayList<T>> elementSupplier;
	private String title;
	private boolean allowExport;

	public TableDataRenderer(Supplier<ArrayList<T>> elementSupplier){
		this.elementSupplier=elementSupplier;
	}
	public TableDataRenderer(Supplier<ArrayList<T>> elementSupplier,String title){
		this(elementSupplier);
		this.title=title;
	}
	public TableDataRenderer(Supplier<ArrayList<T>> elementSupplier,String title,boolean allowExport){
		this(elementSupplier,title);
		this.allowExport=allowExport;
	}

	// @Override
	// @SuppressWarnings("unchecked")
	// public <D extends Report.DataRenderer> void setRendererSource(Function<D,? extends DataRendererRenderer<D>> rendererSource){
	// 	this.rendererSource=(Function<TableDataRenderer<T>,DataRendererRenderer<TableDataRenderer<T>>>)(Object)rendererSource;
	// }

	public Supplier<ArrayList<T>> getElementSupplier(){
		return elementSupplier;
	}

	public String getTitle(){
		return title;
	}

	public boolean getAllowExport(){
		return allowExport;
	}

	// public Function<TableDataRenderer<T>,DataRendererRenderer<TableDataRenderer<T>>> getRendererSource(){
	// 	return rendererSource;
	// }
}
