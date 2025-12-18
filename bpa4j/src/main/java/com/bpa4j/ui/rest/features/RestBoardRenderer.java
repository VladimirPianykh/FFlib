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
import com.bpa4j.ui.rest.RestRenderingManager;
import com.bpa4j.ui.rest.RestTheme;
import com.bpa4j.ui.rest.abstractui.Panel;
import com.bpa4j.ui.rest.abstractui.components.Button;
import com.bpa4j.ui.rest.abstractui.components.Label;
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

		int targetWidth=target.getWidth();
		int targetHeight=target.getHeight();
		if(targetWidth==0||targetHeight==0){
			targetWidth=RestRenderingManager.DEFAULT_SIZE.width();
			targetHeight=RestRenderingManager.DEFAULT_SIZE.height();
			target.setSize(targetWidth,targetHeight);
		}

		// Use FlowLayout TTB for main structure to stack header and table
		target.setLayout(new FlowLayout(FlowLayout.LEFT,FlowLayout.TTB,0,0));

		// Create header panel for filter/sorter/add button
		// We use a sub-context to direct filter/sorter components into this panel
		Panel headerPanel=new Panel(new FlowLayout(FlowLayout.LEFT,FlowLayout.LTR,5,5));
		headerPanel.setSize(targetWidth,60); // Initial height, will grow if needed

		RestFeatureRenderingContext headerCtx=new RestFeatureRenderingContext(rctx.getState(),null,headerPanel,rctx::rebuild);

		// Render filter and sorter configurators into header
		contract.renderFilter(headerCtx);
		contract.renderSorter(headerCtx);

		// Add creation button if allowed (not mandatory -> ACCENT as foreground only)
		if(contract.getAllowCreation()){
			Button add=new Button("Add");
			add.setBackground(RestTheme.MAIN);
			add.setForeground(RestTheme.ACCENT_TEXT);
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
			headerPanel.add(add);
		}

		target.add(headerPanel);

		// Create table panel taking remaining space
		// Estimate header height usage (FlowLayout might expand it)
		// For now, give table a fixed large height to fill view, relying on FlowLayout
		// TTB
		int tableHeight=Math.max(400,targetHeight-80);
		Panel tablePanel=new Panel(new FlowLayout(FlowLayout.LEFT,FlowLayout.TTB,0,0));
		tablePanel.setSize(targetWidth,tableHeight);

		fillTable(tablePanel,targetWidth,rctx);

		target.add(tablePanel);
		target.update();
	}

	private void fillTable(Panel tablePanel,int targetWidth,RestFeatureRenderingContext rctx){
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
		int rows=objects.size()+1; // +1 for header
		int rowHeight=40;
		int totalHeight=Math.max(100,rows*rowHeight+(rows-1)*5); // gaps

		// Ensure minimum width to prevent content crushing/wrapping
		int minTableWidth=columns*150;
		int finalTableWidth=Math.max(targetWidth,minTableWidth);

		Panel table=new Panel(new GridLayout(rows,columns,5,5));
		table.setSize(finalTableWidth,totalHeight);
		tablePanel.setSize(finalTableWidth,totalHeight);

		// Resize target to fit the table including header
		Panel target=rctx.getTarget();
		// header is ~60
		target.setSize(finalTableWidth,60+totalHeight+20);

		// Header row
		Label objectHeader=new Label("Object");
		table.add(objectHeader);
		Label actionsHeader=new Label("Actions");
		table.add(actionsHeader);
		for(Field f:fields){
			String fieldName=f.getAnnotation(EditorEntry.class).translation();
			Label h=new Label(fieldName);
			table.add(h);
		}

		// Data rows
		for(T obj:objects){
			// Object name/identifier
			Label objLabel=new Label(String.valueOf(obj));
			table.add(objLabel);

			// Action buttons - wrap both in a single panel for proper grid cell display
			// Use GridLayout to guarantee 1 row and prevent wrapping over other rows
			Panel actionsWrapper=new Panel(new GridLayout(1,2,5,0));
			actionsWrapper.setSize(150,rowHeight);

			Button edit=new Button("Edit");
			edit.setBackground(RestTheme.MAIN);
			edit.setForeground(RestTheme.ACCENT_TEXT);
			edit.setOnClick(b->{
				if(obj instanceof Editable){
					Runnable deleter=contract.getAllowDeletion()?()->contract.removeObject(obj):null;
					ProgramStarter.editor.constructEditor((Editable)obj,false,deleter,ProgramStarter.getRenderingManager().getDetachedFeatureRenderingContext());
					rctx.rebuild();
				}
			});
			actionsWrapper.add(edit);

			if(contract.getAllowDeletion()){
				Button remove=new Button("Remove");
				remove.setBackground(RestTheme.DANGER);
				remove.setForeground(RestTheme.ON_DANGER);
				remove.setOnClick(b->{
					contract.removeObject(obj);
					rctx.rebuild();
				});
				actionsWrapper.add(remove);
			}

			table.add(actionsWrapper);

			// Field values
			for(Field f:fields){
				try{
					Object value=f.get(obj);
					Label cell=new Label(value!=null?String.valueOf(value):"");
					table.add(cell);
				}catch(IllegalAccessException e){
					Label na=new Label("N/A");
					table.add(na);
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
				edit.setBackground(RestTheme.MAIN);
				edit.setForeground(RestTheme.ACCENT_TEXT);
				edit.setOnClick(b->{
					Runnable deleter=contract.getAllowDeletion()?()->contract.removeObject(obj):null;
					ProgramStarter.editor.constructEditor((Editable)obj,false,deleter,ProgramStarter.getRenderingManager().getDetachedFeatureRenderingContext());
					rctx.rebuild();
				});
				row.add(edit);
			}

			if(contract.getAllowDeletion()){
				Button remove=new Button("Remove");
				remove.setBackground(RestTheme.DANGER);
				remove.setForeground(RestTheme.ON_DANGER);
				remove.setOnClick(b->{
					contract.removeObject(obj);
					rctx.rebuild();
				});
				row.add(remove);
			}
			list.add(row);
		}
	}

	private Class<T> getType(){
		return getTransmissionContract().getType();
	}

	public void renderPreview(FeatureRenderingContext ctx){}
}
