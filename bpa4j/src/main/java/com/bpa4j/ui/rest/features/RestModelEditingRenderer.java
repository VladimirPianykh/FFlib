package com.bpa4j.ui.rest.features;

import java.util.List;
import com.bpa4j.core.Editable;
import com.bpa4j.core.EditableGroup;
import com.bpa4j.core.ProgramStarter;

import com.bpa4j.defaults.features.transmission_contracts.ModelEditing;
import com.bpa4j.feature.FeatureRenderer;
import com.bpa4j.feature.FeatureRenderingContext;
import com.bpa4j.ui.rest.RestFeatureRenderingContext;
import com.bpa4j.ui.rest.RestRenderingManager;
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
		contract.setGetRenderingContextOp(()->ctx);
		RestFeatureRenderingContext rctx=(RestFeatureRenderingContext)ctx;
		Panel target=rctx.getTarget();
		target.removeAll();
		List<EditableGroup<?>> groups=contract.getGroups();

		int columns=0;
		for(EditableGroup<?> group:groups){
			if(!group.invisible) columns++;
		}

		if(columns==0){
			target.add(new Label("No groups available."));
			return;
		}

		int targetWidth=target.getWidth();
		int targetHeight=target.getHeight();
		if(targetWidth==0||targetHeight==0){
			targetWidth=RestRenderingManager.DEFAULT_SIZE.width();
			targetHeight=RestRenderingManager.DEFAULT_SIZE.height();
			target.setSize(targetWidth,targetHeight);
		}


		target.setLayout(new GridLayout(1,columns,10,10));
		// Calculate total height to ensure scrolling/visibility
		// Estimate height based on max group size
		int maxGroupSize=0;
		for(EditableGroup<?> g:groups){
			if(!g.invisible) maxGroupSize=Math.max(maxGroupSize,g.size());
		}
		int estimatedHeight=Math.max(400,(maxGroupSize+2)*40);
		target.setSize(targetWidth,estimatedHeight);

		// Calculate column width: distribute full width minus gaps evenly
		int totalGaps=(columns-1)*10;
		int columnWidth=(targetWidth-totalGaps)/columns;

		for(EditableGroup<?> group:groups){
			if(group.invisible)continue;
			// Use FlowLayout TTB for column
			Panel column=new Panel(new FlowLayout(FlowLayout.LEFT,FlowLayout.TTB,0,5));
			column.setSize(columnWidth,estimatedHeight);

			Label header=new Label(group.type.getSimpleName()+" ("+group.size()+")");
			header.setSize(columnWidth,25); // Stretch to column width
			column.add(header);

			for(Editable item:group){
				String name=item.name;
				if(name==null||name.isBlank()) name="[Unnamed]";
				Button itemBtn=new Button(name);
				itemBtn.setSize(columnWidth,30); // Stretch to column width
				itemBtn.setOnClick(b->{
					if(contract.editOp!=null) contract.editOp.accept(item);
					ProgramStarter.editor.constructEditor(item,false,null,rctx);
				});
				column.add(itemBtn);
			}

			Button addBtn=new Button("Add");
			addBtn.setSize(columnWidth,30); // Stretch to column width
			addBtn.setOnClick(b->{
				try{
					Editable newItem=(Editable)group.type.getDeclaredConstructor().newInstance();
					// group.add(newItem);
					if(contract.createOp!=null) contract.createOp.accept(groups.indexOf(group),newItem);

					ProgramStarter.editor.constructEditor(newItem,true,()->{
						group.remove(newItem);
					},rctx);
				}catch(ReflectiveOperationException e){
					throw new IllegalStateException("Failed to create new item",e);
				}
			});
			column.add(addBtn);
			
			target.add(column);
		}
		target.update();
	}
	public void renderPreview(FeatureRenderingContext ctx){}
}
