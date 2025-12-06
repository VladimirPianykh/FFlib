package com.bpa4j.ui.rest.features;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.function.BiConsumer;
import com.bpa4j.core.Editable;
import com.bpa4j.core.ProgramStarter;
import com.bpa4j.defaults.features.transmission_contracts.EditableList.ItemRenderingContext;
import com.bpa4j.defaults.features.transmission_contracts.MappedList;
import com.bpa4j.editor.EditorEntry;
import com.bpa4j.feature.FeatureRenderer;
import com.bpa4j.feature.FeatureRenderingContext;
import com.bpa4j.ui.rest.RestFeatureRenderingContext;
import com.bpa4j.ui.rest.RestRenderingManager;
import com.bpa4j.ui.rest.abstractui.Panel;
import com.bpa4j.ui.rest.abstractui.components.Button;
import com.bpa4j.ui.rest.abstractui.components.Label;
import com.bpa4j.ui.rest.abstractui.components.TextField;
import com.bpa4j.ui.rest.abstractui.layout.FlowLayout;
import com.bpa4j.ui.rest.abstractui.layout.GridLayout;

/**
 * REST renderer for MappedList feature with editable fields.
 * Displays objects with their mapped values in a table format.
 * Field values are editable via TextFields.
 * @author AI-generated
 */
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

		int targetWidth=target.getWidth();
		int targetHeight=target.getHeight();
		if(targetWidth==0||targetHeight==0){
			targetWidth=RestRenderingManager.DEFAULT_SIZE.width();
			targetHeight=RestRenderingManager.DEFAULT_SIZE.height();
			target.setSize(targetWidth,targetHeight);
		}

		// Use FlowLayout TTB for vertical stacking
		target.setLayout(new FlowLayout(FlowLayout.LEFT,FlowLayout.TTB,0,5));
		
		Field[] fields=contract.getVType().getFields();
		int columns=fields.length+1; // +1 for the object name
		int rows=contract.getObjects().size()+1; // +1 for header
		
		// Create table panel
		Panel tablePanel=new Panel(new GridLayout(rows,columns,5,5));
		// Calculate height similar to other renderers
		int rowHeight=40;
		int totalHeight=Math.max(100,rows*rowHeight+(rows-1)*5);
		tablePanel.setSize(targetWidth,totalHeight);
		
		// Header
		tablePanel.add(new Label("Objects"));
		for(Field f:fields){
			EditorEntry entry=f.getAnnotation(EditorEntry.class);
			tablePanel.add(new Label(entry!=null?entry.translation():f.getName()));
		}
		
		// Get componentProvider or use default
		BiConsumer<T,ItemRenderingContext> componentProvider=contract.getComponentProvider();
		if(componentProvider==null){
			componentProvider=(t,itemCtx)->{
				RestItemRenderingContext restCtx=(RestItemRenderingContext)itemCtx;
				Button itemBtn=new Button(t.name);
				itemBtn.setOnClick(b->{
					ProgramStarter.editor.constructEditor(t,false,null,null);
				});
				restCtx.getTarget().add(itemBtn);
			};
		}
		final BiConsumer<T,ItemRenderingContext> finalProvider=componentProvider;

		// Rows
		for(T t:contract.getObjects().keySet()){
			Panel itemPanel=new Panel(new FlowLayout());
			finalProvider.accept(t,new RestItemRenderingContext(itemPanel));
			tablePanel.add(itemPanel);
			
			V value=contract.getMapping(t);
			for(Field f:fields){
				try{
					Object val=f.get(value);
					TextField textField=new TextField(val!=null?String.valueOf(val):"");
					textField.setOnTextChanged(newText->{
						try{
							// Try to convert and set the value
							Object convertedValue=convertValue(newText,f.getType());
							f.set(value,convertedValue);
						}catch(Exception e){
							// Conversion failed, ignore or log
						}
					});
					tablePanel.add(textField);
				}catch(IllegalAccessException e){
					tablePanel.add(new Label("Error"));
				}
			}
		}
		target.add(tablePanel);
		
		// Add button panel
		Panel addPanel=new Panel(new FlowLayout());
		addPanel.setSize(targetWidth,40);
		Button addBtn=new Button("Add");
		addBtn.setOnClick(b->{
			T o=contract.createObject();
			ProgramStarter.editor.constructEditor(o,true,null,ProgramStarter.getRenderingManager().getDetachedFeatureRenderingContext());
			rctx.rebuild();
		});
		addPanel.add(addBtn);
		target.add(addPanel);
		
		// Resize target
		target.setSize(targetWidth,totalHeight+40+20);

		target.update();
	}

	/**
	 * Convert string value to the target type.
	 * Supports primitive types and their wrappers, plus String.
	 */
	private Object convertValue(String text,Class<?> targetType){
		if(targetType==String.class) return text;
		if(targetType==int.class||targetType==Integer.class) return Integer.parseInt(text);
		if(targetType==long.class||targetType==Long.class) return Long.parseLong(text);
		if(targetType==double.class||targetType==Double.class) return Double.parseDouble(text);
		if(targetType==float.class||targetType==Float.class) return Float.parseFloat(text);
		if(targetType==boolean.class||targetType==Boolean.class) return Boolean.parseBoolean(text);
		if(targetType==byte.class||targetType==Byte.class) return Byte.parseByte(text);
		if(targetType==short.class||targetType==Short.class) return Short.parseShort(text);
		if(targetType==char.class||targetType==Character.class) return text.isEmpty()?'\0':text.charAt(0);
		return text; // Fallback to string
	}

	public void renderPreview(FeatureRenderingContext ctx){}
}
