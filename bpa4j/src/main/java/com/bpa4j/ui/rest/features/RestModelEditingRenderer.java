package com.bpa4j.ui.rest.features;

import java.util.List;
import com.bpa4j.core.Editable;
import com.bpa4j.core.EditableGroup;

import com.bpa4j.defaults.features.transmission_contracts.ModelEditing;
import com.bpa4j.feature.FeatureRenderer;
import com.bpa4j.feature.FeatureRenderingContext;
import com.bpa4j.ui.rest.RestFeatureRenderingContext;
import com.bpa4j.ui.rest.abstractui.Panel;
import com.bpa4j.ui.rest.abstractui.components.Button;
import com.bpa4j.ui.rest.abstractui.components.Label;
import com.bpa4j.ui.rest.abstractui.layout.FlowLayout;
import com.bpa4j.ui.rest.abstractui.layout.GridLayout;

public class RestModelEditingRenderer implements FeatureRenderer<ModelEditing>{
	private final ModelEditing contract;
	public RestModelEditingRenderer(ModelEditing contract){
		this.contract=contract;
	}
	public ModelEditing getTransmissionContract(){
		return contract;
	}
	public void render(FeatureRenderingContext ctx){
		RestFeatureRenderingContext rctx=(RestFeatureRenderingContext)ctx;
		Panel target=rctx.getTarget();
		target.removeAll();
		List<EditableGroup<?>> groups=contract.getGroups();
		int columns=groups.size();
		if(columns==0){
			target.add(new Label("No groups available."));
			return;
		}
		target.setLayout(new GridLayout(1,columns,10,10));
		for(EditableGroup<?> group:groups){
			if(group.invisible)continue;
			Panel column=new Panel(new FlowLayout(FlowLayout.LEFT,FlowLayout.TTB,5,5));
			// Header
			column.add(new Label(group.type.getSimpleName()));
			
			// Items
			for(Editable item:group){
				Button itemBtn=new Button(item.name);
				itemBtn.setOnClick(b->{
					contract.edit(item);
					rctx.rebuild();
				});
				column.add(itemBtn);
			}
			
			// Add button
			Button addBtn=new Button("Add");
			addBtn.setOnClick(b->{
				try{
					Editable newItem=(Editable)group.type.getDeclaredConstructor().newInstance();
					group.add(newItem);
					contract.create(groups.indexOf(group),newItem);
					rctx.rebuild();
				}catch(ReflectiveOperationException e){
					throw new IllegalStateException("Failed to create new item",e);
				}
			});
			column.add(addBtn);
			
			target.add(column);
		}
	}
	public void renderPreview(FeatureRenderingContext ctx){}
}
