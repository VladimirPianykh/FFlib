package com.bpa4j.ui.rest.features;

import com.bpa4j.core.Editable;
import com.bpa4j.core.ProgramStarter;
import com.bpa4j.defaults.features.transmission_contracts.EditableList;
import com.bpa4j.feature.FeatureRenderer;
import com.bpa4j.feature.FeatureRenderingContext;
import com.bpa4j.ui.rest.RestFeatureRenderingContext;
import com.bpa4j.ui.rest.abstractui.Panel;
import com.bpa4j.ui.rest.abstractui.components.Button;
import com.bpa4j.ui.rest.abstractui.layout.FlowLayout;

public class RestEditableListRenderer<T extends Editable> implements FeatureRenderer<EditableList<T>>{
	private final EditableList<T> contract;
	public RestEditableListRenderer(EditableList<T> contract){
		this.contract=contract;
	}
	public EditableList<T> getTransmissionContract(){
		return contract;
	}
	public void render(FeatureRenderingContext ctx){
		RestFeatureRenderingContext rctx=(RestFeatureRenderingContext)ctx;
		Panel target=rctx.getTarget();
		target.removeAll();
		target.setLayout(new FlowLayout(FlowLayout.LEFT,FlowLayout.TTB,5,5));
		
		var group=contract.getGroup();
		for(T t:group){
			Button itemBtn=new Button(t.name);
			itemBtn.setOnClick(b->{
				ProgramStarter.editor.constructEditor(t,false,()->{
					group.remove(t);
					rctx.rebuild();
				},null);
			});
			target.add(itemBtn);
		}
		
		if(contract.getCanCreate()){
			Button addBtn=new Button("Add");
			addBtn.setOnClick(b->{
				try{
					T t=group.type.getDeclaredConstructor().newInstance();
					group.add(t);
					ProgramStarter.editor.constructEditor(t,true,()->group.remove(t),ProgramStarter.getRenderingManager().getDetachedFeatureRenderingContext());
					rctx.rebuild();
				}catch(ReflectiveOperationException ex){
					throw new IllegalStateException(ex);
				}
			});
			target.add(addBtn);
		}
	}
	public void renderPreview(FeatureRenderingContext ctx){}
}
