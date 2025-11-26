package com.bpa4j.ui.rest.features;

import java.io.Serializable;
import java.lang.reflect.Field;
import com.bpa4j.core.Editable;
import com.bpa4j.core.ProgramStarter;
import com.bpa4j.defaults.features.transmission_contracts.MappedList;
import com.bpa4j.editor.EditorEntry;
import com.bpa4j.feature.FeatureRenderer;
import com.bpa4j.feature.FeatureRenderingContext;
import com.bpa4j.ui.rest.RestFeatureRenderingContext;
import com.bpa4j.ui.rest.abstractui.Panel;
import com.bpa4j.ui.rest.abstractui.components.Button;
import com.bpa4j.ui.rest.abstractui.components.Label;
import com.bpa4j.ui.rest.abstractui.layout.GridLayout;

public class RestMappedListRenderer<T extends Editable,V extends Serializable> implements FeatureRenderer<MappedList<T,V>>{
	private final MappedList<T,V> contract;
	public RestMappedListRenderer(MappedList<T,V> contract){
		this.contract=contract;
	}
	public MappedList<T,V> getTransmissionContract(){
		return contract;
	}
	public void render(FeatureRenderingContext ctx){
		RestFeatureRenderingContext rctx=(RestFeatureRenderingContext)ctx;
		Panel target=rctx.getTarget();
		target.removeAll();
		
		Field[] fields=contract.getVType().getFields();
		int columns=fields.length+1; // +1 for the object name
		
		// Use a grid layout for the table
		// We'll just add rows sequentially. GridLayout(0, columns) means any number of rows.
		target.setLayout(new GridLayout(0,columns,5,5));
		
		// Header
		target.add(new Label("Objects"));
		for(Field f:fields){
			EditorEntry entry=f.getAnnotation(EditorEntry.class);
			target.add(new Label(entry!=null?entry.translation():f.getName()));
		}
		
		// Rows
		for(T t:contract.getObjects().keySet()){
			Button itemBtn=new Button(t.name);
			itemBtn.setOnClick(b->{
				ProgramStarter.editor.constructEditor(t,false,null,null);
			});
			target.add(itemBtn);
			
			V value=contract.getMapping(t);
			for(Field f:fields){
				try{
					Object val=f.get(value);
					target.add(new Label(String.valueOf(val)));
				}catch(IllegalAccessException e){
					target.add(new Label("Error"));
				}
			}
		}
		
		// Add button row
		Button addBtn=new Button("Add");
		addBtn.setOnClick(b->{
			T o=contract.createObject();
			ProgramStarter.editor.constructEditor(o,true,null,ProgramStarter.getRenderingManager().getDetachedFeatureRenderingContext());
			rctx.rebuild();
		});
		target.add(addBtn);
		
		// Fill the rest of the last row with empty labels if needed
		for(int i=0;i<fields.length;i++){
			target.add(new Label(""));
		}
	}
	public void renderPreview(FeatureRenderingContext ctx){}
}
