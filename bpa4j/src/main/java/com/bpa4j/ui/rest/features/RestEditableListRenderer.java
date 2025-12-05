package com.bpa4j.ui.rest.features;

import java.util.function.BiConsumer;
import com.bpa4j.core.Editable;
import com.bpa4j.core.ProgramStarter;
import com.bpa4j.defaults.features.transmission_contracts.EditableList;
import com.bpa4j.defaults.features.transmission_contracts.EditableList.ItemRenderingContext;
import com.bpa4j.feature.FeatureRenderer;
import com.bpa4j.feature.FeatureRenderingContext;
import com.bpa4j.ui.rest.RestFeatureRenderingContext;
import com.bpa4j.ui.rest.RestRenderingManager;
import com.bpa4j.ui.rest.abstractui.Panel;
import com.bpa4j.ui.rest.abstractui.components.Button;
import com.bpa4j.ui.rest.abstractui.layout.GridLayout;

/**
 * REST renderer for EditableList feature.
 * Displays a list of editable items with add functionality.
 * @author AI-generated
 */
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
		int targetWidth=target.getWidth();
		int targetHeight=target.getHeight();
		if(targetWidth==0||targetHeight==0){
			targetWidth=RestRenderingManager.DEFAULT_SIZE.width();
			targetHeight=RestRenderingManager.DEFAULT_SIZE.height();
			target.setSize(targetWidth,targetHeight);
		}
		target.setLayout(new GridLayout(0,1,5,5));
		
		var group=contract.getGroup();

		// Get componentProvider or use default
		BiConsumer<T,ItemRenderingContext> componentProvider=contract.getComponentProvider();
		if(componentProvider==null){
			componentProvider=(t,itemCtx)->{
				RestItemRenderingContext restCtx=(RestItemRenderingContext)itemCtx;
				Button itemBtn=new Button(t.name);
				itemBtn.setOnClick(b->{
					ProgramStarter.editor.constructEditor(t,false,()->{
						group.remove(t);
						rctx.rebuild();
					},null);
				});
				restCtx.getTarget().add(itemBtn);
			};
		}
		final BiConsumer<T,ItemRenderingContext> finalProvider=componentProvider;

		for(T t:group){
			finalProvider.accept(t,new RestItemRenderingContext(target));
		}
		
		if(contract.getCanCreate()){
			Button addBtn=new Button("Add");
			addBtn.setOnClick(b->{
				try{
					T t=group.type.getDeclaredConstructor().newInstance();
					group.add(t);
					ProgramStarter.editor.constructEditor(t,true,()->group.remove(t),ProgramStarter.getRenderingManager().getDetachedFeatureRenderingContext());
					// Check if item still exists after editor (user might have cancelled)
					if(group.contains(t)){
						rctx.rebuild();
					}else{
						// Item was removed (cancelled), just rebuild to remove it from UI
						rctx.rebuild();
					}
				}catch(ReflectiveOperationException ex){
					throw new IllegalStateException(ex);
				}
			});
			target.add(addBtn);
		}
	}
	public void renderPreview(FeatureRenderingContext ctx){}
}
