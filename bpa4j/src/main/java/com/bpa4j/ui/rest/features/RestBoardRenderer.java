package com.bpa4j.ui.rest.features;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import com.bpa4j.core.Editable;
import com.bpa4j.core.ProgramStarter;
import com.bpa4j.defaults.features.transmission_contracts.Board;
import com.bpa4j.editor.EditorEntry;
import com.bpa4j.feature.FeatureRenderer;
import com.bpa4j.feature.FeatureRenderingContext;
import com.bpa4j.ui.rest.RestFeatureRenderingContext;
import com.bpa4j.ui.rest.abstractui.Panel;
import com.bpa4j.ui.rest.abstractui.components.Button;
import com.bpa4j.ui.rest.abstractui.components.Label;
import com.bpa4j.ui.rest.abstractui.layout.BorderLayout;
import com.bpa4j.ui.rest.abstractui.layout.FlowLayout;
import com.bpa4j.ui.rest.abstractui.layout.GridLayout;

/**
 * REST renderer for Board feature with table-based display.
 * Displays objects in a table with columns from EditorEntry annotations.
 * Supports filter/sorter configurators and item editing.
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

		// Create config panel for filter/sorter/add button
		Panel config=new Panel(new FlowLayout());
		config.setSize(root.getWidth(),40);

		// Render filter and sorter configurators
		contract.renderFilter(rctx);
		contract.renderSorter(rctx);

		// Add creation button if allowed
		if(contract.getAllowCreation()){
			Button add=new Button("Add");
			add.setOnClick(b->{
				try{
					T o=getType().getDeclaredConstructor().newInstance();
					contract.addObject(o);
					if(o instanceof Editable){
						Editable editable=(Editable)o;
						ProgramStarter.editor.constructEditor(editable,true,()->contract.removeObject(o),ProgramStarter.getRenderingManager().getDetachedFeatureRenderingContext());
					}
					rctx.rebuild();
				}catch(ReflectiveOperationException ex){
					throw new IllegalStateException(ex);
				}
			});
			config.add(add);
		}

		// Create table panel
		Panel tablePanel=new Panel(new FlowLayout(FlowLayout.LEFT,FlowLayout.TTB,0,0));
		tablePanel.setSize(root.getWidth(),root.getHeight()-config.getHeight());
		fillTable(tablePanel,rctx);

		// Layout components
		BorderLayout layout=(BorderLayout)root.getLayout();
		layout.addLayoutComponent(config,BorderLayout.NORTH);
		layout.addLayoutComponent(tablePanel,BorderLayout.CENTER);
		root.add(config);
		root.add(tablePanel);
		target.add(root);
	}

	private void fillTable(Panel tablePanel,RestFeatureRenderingContext rctx){
		ArrayList<T> objects=contract.getObjects();

		// Get fields with EditorEntry annotations
		ArrayList<Field> fields=new ArrayList<>();
		for(Field f:getType().getFields()){
			if(f.isAnnotationPresent(EditorEntry.class)){
				fields.add(f);
			}
		}

		if(fields.isEmpty()){
			// No annotated fields, use simple list view
			fillSimpleList(tablePanel,objects,rctx);
			return;
		}

		// Create table with columns: Object name + action buttons + fields
		int columns=2+fields.size(); // Object button + Edit + Remove + fields
		Panel table=new Panel(new GridLayout(0,columns,5,5));
		table.setSize(tablePanel.getWidth(),tablePanel.getHeight());

		// Header row
		table.add(new Label("Object"));
		table.add(new Label("Actions"));
		for(Field f:fields){
			String fieldName=f.getAnnotation(EditorEntry.class).translation();
			table.add(new Label(fieldName));
		}

		// Data rows
		for(T obj:objects){
			// Object name/identifier
			Label objLabel=new Label(String.valueOf(obj));
			table.add(objLabel);

			// Action buttons panel
			Panel actionsPanel=new Panel(new FlowLayout());

			Button edit=new Button("Edit");
			edit.setOnClick(b->{
				if(obj instanceof Editable){
					ProgramStarter.editor.constructEditor((Editable)obj,false,()->contract.removeObject(obj),ProgramStarter.getRenderingManager().getDetachedFeatureRenderingContext());
					rctx.rebuild();
				}
			});
			actionsPanel.add(edit);

			Button remove=new Button("Remove");
			remove.setOnClick(b->{
				contract.removeObject(obj);
				rctx.rebuild();
			});
			actionsPanel.add(remove);

			table.add(actionsPanel);

			// Field values
			for(Field f:fields){
				try{
					Object value=f.get(obj);
					table.add(new Label(value!=null?String.valueOf(value):""));
				}catch(IllegalAccessException e){
					table.add(new Label("N/A"));
				}
			}
		}

		tablePanel.add(table);
	}

	private void fillSimpleList(Panel list,ArrayList<T> objects,RestFeatureRenderingContext rctx){
		// Fallback for objects without EditorEntry fields
		for(T obj:objects){
			Panel row=new Panel(new FlowLayout());
			Label l=new Label(String.valueOf(obj));
			row.add(l);

			if(obj instanceof Editable){
				Button edit=new Button("Edit");
				edit.setOnClick(b->{
					ProgramStarter.editor.constructEditor((Editable)obj,false,()->contract.removeObject(obj),ProgramStarter.getRenderingManager().getDetachedFeatureRenderingContext());
					rctx.rebuild();
				});
				row.add(edit);
			}

			Button remove=new Button("Remove");
			remove.setOnClick(b->{
				contract.removeObject(obj);
				rctx.rebuild();
			});
			row.add(remove);
			list.add(row);
		}
	}

	private Class<T> getType(){
		return getTransmissionContract().getType();
	}

	public void renderPreview(FeatureRenderingContext ctx){}
}
