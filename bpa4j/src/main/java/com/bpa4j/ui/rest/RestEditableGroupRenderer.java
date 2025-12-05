package com.bpa4j.ui.rest;

import com.bpa4j.core.Editable;
import com.bpa4j.core.EditableGroup;
import com.bpa4j.core.EditableGroupRenderer;
import com.bpa4j.defaults.features.transmission_contracts.EditableList.ItemRenderingContext;
import com.bpa4j.ui.rest.abstractui.components.Button;
import com.bpa4j.ui.rest.features.RestItemRenderingContext;

/**
 * REST implementation of EditableGroupRenderer.
 * Renders buttons for editable group elements and add functionality in REST UI.
 * 
 * @author AI-generated
 */
public class RestEditableGroupRenderer implements EditableGroupRenderer<EditableGroup<Editable>>{
	
	@Override
	public void renderElementButton(EditableGroup<Editable> group, Editable e, ItemRenderingContext context){
		RestItemRenderingContext ctx = (RestItemRenderingContext) context;
		
		// Create a button with the element's name and icon
		Button elementBtn = new Button(e.name);
		
		// Add the button to the target panel
		ctx.getTarget().add(elementBtn);
	}
	
	@Override
	public void renderAddButton(EditableGroup<Editable> group, ItemRenderingContext context){
		RestItemRenderingContext ctx = (RestItemRenderingContext) context;
		
		// Create an "Add" button
		Button addBtn = new Button("Add");
		
		// Add the button to the target panel
		ctx.getTarget().add(addBtn);
	}
}
