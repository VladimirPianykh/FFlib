package com.bpa4j.ui.rest.features;

import java.io.Serializable;
import java.util.ArrayList;
import com.bpa4j.defaults.features.transmission_contracts.Board;
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
public class RestBoardRenderer<T extends Serializable> implements FeatureRenderer<Board<T>>{
	private final Board<T> contract;
	public RestBoardRenderer(Board<T> contract){
		this.contract=contract;
	}
	public Board<T> getTransmissionContract(){
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
		for(T obj:objects){
			Panel row=new Panel(new FlowLayout());
			Label l=new Label(String.valueOf(obj));
			row.add(l);
			Button remove=new Button("Remove");
			remove.setOnClick(b->{
				contract.removeObject(obj);
				rctx.rebuild();
			});
			row.add(remove);
			list.add(row);
		}
		if(contract.getAllowCreation()){
			Button add=new Button("Add");
			add.setOnClick(b->{
				T o=contract.createObject();
				contract.addObject(o);
				rctx.rebuild();
			});
			list.add(add);
		}
		target.add(root);
	}
	public void renderPreview(FeatureRenderingContext ctx){}
}
