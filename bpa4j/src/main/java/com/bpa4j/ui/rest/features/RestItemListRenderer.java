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
		Panel header=new Panel(new FlowLayout());
		header.setSize(root.getWidth(),40);
		Label title=new Label(contract.getFeatureName());
		header.add(title);
		Panel list=new Panel(new FlowLayout(FlowLayout.LEFT,FlowLayout.TTB,5,5));
		list.setSize(root.getWidth(),root.getHeight()-header.getHeight());
		BorderLayout layout=(BorderLayout)root.getLayout();
		layout.addLayoutComponent(header,BorderLayout.NORTH);
		layout.addLayoutComponent(list,BorderLayout.CENTER);
		root.add(header);
		root.add(list);
		ArrayList<T> objects=contract.getObjects();
		List<Consumer<T>> singular=contract.getSingularActions();
		List<Consumer<List<T>>> collective=contract.getCollectiveActions();
		for(T obj:objects){
			Panel row=new Panel(new FlowLayout());
			Label l=new Label(String.valueOf(obj));
			row.add(l);
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
		if(!collective.isEmpty()){
			Panel actionsPanel=new Panel(new FlowLayout());
			AtomicInteger idx=new AtomicInteger(1);
			for(Consumer<List<T>> action:collective){
				int i=idx.getAndIncrement();
				Button b=new Button("Collective "+i);
				b.setOnClick(btn->{
					List<T> current=new ArrayList<>(contract.getObjects());
					action.accept(current);
					rctx.rebuild();
				});
				actionsPanel.add(b);
			}
			list.add(actionsPanel);
		}
		if(contract.getAllowCreation()){
			Button add=new Button("Add");
			add.setOnClick(b->{
				try{
					T o=getType().getDeclaredConstructor().newInstance();
					contract.addObject(o);
					if(o instanceof Editable editable)ProgramStarter.editor.constructEditor(editable,true,()->contract.removeObject(o),rctx);
					rctx.rebuild();
				}catch(ReflectiveOperationException ex){
					throw new IllegalStateException(ex);
				}
			});
			list.add(add);
		}
		target.add(root);
	}
	private Class<T> getType(){
		return contract.getType();
	}
	public void renderPreview(FeatureRenderingContext ctx){}
}
