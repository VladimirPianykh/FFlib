package com.bpa4j.ui.rest.features;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import com.bpa4j.core.Editable;
import com.bpa4j.core.ProgramStarter;
import com.bpa4j.defaults.features.transmission_contracts.ItemList;
import com.bpa4j.feature.FeatureRenderer;
import com.bpa4j.feature.FeatureRenderingContext;
import com.bpa4j.ui.rest.RestFeatureRenderingContext;
import com.bpa4j.ui.rest.abstractui.Panel;
import com.bpa4j.ui.rest.abstractui.components.Button;
import com.bpa4j.ui.rest.abstractui.components.Label;
import com.bpa4j.ui.rest.abstractui.layout.BorderLayout;
import com.bpa4j.ui.rest.abstractui.layout.FlowLayout;

/**
 * REST renderer for ItemList feature with filter/sorter support.
 * Displays items with singular and collective actions.
 * @author AI-generated
 */
public class RestItemListRenderer<T extends Serializable> implements FeatureRenderer<ItemList<T>>{
	private final ItemList<T> contract;
	public RestItemListRenderer(ItemList<T> contract){
		this.contract=contract;
	}
	public ItemList<T> getTransmissionContract(){
		return contract;
	}
	public void render(FeatureRenderingContext ctx){
		RestFeatureRenderingContext rctx=(RestFeatureRenderingContext)ctx;
		Panel target=rctx.getTarget();
		target.removeAll();
		Panel root=new Panel(new BorderLayout());
		root.setSize(target.getWidth(),target.getHeight());

		// Create config panel for filter/sorter/actions/add
		Panel config=new Panel(new FlowLayout());
		config.setSize(root.getWidth(),60);

		// Title
		Label title=new Label(contract.getFeatureName());
		config.add(title);

		// Render filter and sorter configurators
		contract.renderFilter(rctx);
		contract.renderSorter(rctx);

		// Collective actions
		List<Consumer<List<T>>> collective=contract.getCollectiveActions();
		if(!collective.isEmpty()){
			AtomicInteger idx=new AtomicInteger(1);
			for(Consumer<List<T>> action:collective){
				int i=idx.getAndIncrement();
				Button b=new Button("Collective "+i);
				b.setOnClick(btn->{
					List<T> current=new ArrayList<>(contract.getObjects());
					action.accept(current);
					rctx.rebuild();
				});
				config.add(b);
			}
		}

		// Add button
		if(contract.getAllowCreation()){
			Button add=new Button("Add");
			add.setOnClick(b->{
				try{
					T o=getType().getDeclaredConstructor().newInstance();
					contract.addObject(o);
					if(o instanceof Editable editable){
						ProgramStarter.editor.constructEditor(editable,true,()->contract.removeObject(o),rctx);
					}
					rctx.rebuild();
				}catch(ReflectiveOperationException ex){
					throw new IllegalStateException(ex);
				}
			});
			config.add(add);
		}

		// Create list panel
		Panel list=new Panel(new FlowLayout(FlowLayout.LEFT,FlowLayout.TTB,5,5));
		list.setSize(root.getWidth(),root.getHeight()-config.getHeight());

		// Apply list customizer if available
		contract.customizeList(new RestListCustomizationRenderingContext(list));

		fillList(list,rctx);

		// Layout components
		BorderLayout layout=(BorderLayout)root.getLayout();
		layout.addLayoutComponent(config,BorderLayout.NORTH);
		layout.addLayoutComponent(list,BorderLayout.CENTER);
		root.add(config);
		root.add(list);
		target.add(root);
	}

	private void fillList(Panel list,RestFeatureRenderingContext rctx){
		ArrayList<T> objects=contract.getObjects();
		List<Consumer<T>> singular=contract.getSingularActions();

		for(T obj:objects){
			Panel row=new Panel(new FlowLayout());

			// Object label/button
			if(obj instanceof Editable){
				Button itemBtn=new Button(String.valueOf(obj));
				itemBtn.setOnClick(b->{
					ProgramStarter.editor.constructEditor((Editable)obj,false,()->contract.removeObject(obj),ProgramStarter.getRenderingManager().getDetachedFeatureRenderingContext());
					rctx.rebuild();
				});
				row.add(itemBtn);
			}else{
				Label l=new Label(String.valueOf(obj));
				row.add(l);
			}

			// Singular actions
			AtomicInteger idx=new AtomicInteger(1);
			for(Consumer<T> action:singular){
				int i=idx.getAndIncrement();
				Button b=new Button("Action "+i);
				b.setOnClick(btn->{
					action.accept(obj);
					rctx.rebuild();
				});
				row.add(b);
			}

			list.add(row);
		}
	}

	private Class<T> getType(){
		return contract.getType();
	}

	public void renderPreview(FeatureRenderingContext ctx){}
}
