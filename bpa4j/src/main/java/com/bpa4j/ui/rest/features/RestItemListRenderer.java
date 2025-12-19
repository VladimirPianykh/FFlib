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
import com.bpa4j.ui.rest.RestRenderingManager;
import com.bpa4j.ui.rest.RestTheme;
import com.bpa4j.ui.rest.abstractui.Panel;
import com.bpa4j.ui.rest.abstractui.components.Button;
import com.bpa4j.ui.rest.abstractui.components.Label;
import com.bpa4j.ui.rest.abstractui.layout.FlowLayout;

/**
 * REST renderer for ItemList feature with selection-based UI.
 * Displays items with checkboxes for selection, allowing users to:
 * - Select individual items using checkboxes
 * - Execute singular actions on all selected items
 * - Execute collective actions on the selected items as a group
 * @author AI-generated
 */
public class RestItemListRenderer<T extends Serializable> implements FeatureRenderer<ItemList<T>>{
	private final ItemList<T> contract;
	private final List<T> selectedItems=new ArrayList<>();
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

		int targetWidth=target.getWidth();
		int targetHeight=target.getHeight();
		if(targetWidth==0||targetHeight==0){
			targetWidth=RestRenderingManager.DEFAULT_SIZE.width();
			targetHeight=RestRenderingManager.DEFAULT_SIZE.height();
			target.setSize(targetWidth,targetHeight);
		}

		// Use FlowLayout TTB for vertical stacking
		target.setLayout(new FlowLayout(FlowLayout.LEFT,FlowLayout.TTB,0,5));

		// Create config panel for filter/sorter/actions/add
		Panel config=new Panel(new FlowLayout());
		config.setSize(targetWidth,60);

		// Title
		Label title=new Label(contract.getTitle());
		config.add(title);

		// Render filter and sorter configurators
		contract.renderFilter(rctx);
		contract.renderSorter(rctx);

		// Select All / Deselect All button
		ArrayList<T> objects=contract.getObjects();
		if(!objects.isEmpty()){
			Button selectAllBtn=new Button(selectedItems.size()==objects.size()?"Deselect All":"Select All");
			selectAllBtn.setBackground(RestTheme.MAIN);
			selectAllBtn.setForeground(RestTheme.ACCENT_TEXT);
			selectAllBtn.setOnClick(b->{
				if(selectedItems.size()==objects.size()){
					selectedItems.clear();
				}else{
					selectedItems.clear();
					selectedItems.addAll(objects);
				}
				rctx.rebuild();
			});
			config.add(selectAllBtn);
		}

		// Singular actions - operate on individually selected items
		List<Consumer<T>> singular=contract.getSingularActions();
		if(!singular.isEmpty()){
			AtomicInteger idx=new AtomicInteger(1);
			for(Consumer<T> action:singular){
				int i=idx.getAndIncrement();
				Button b=new Button("Action "+i);
				b.setBackground(RestTheme.MAIN);
				b.setForeground(RestTheme.ACCENT_TEXT);
				b.setOnClick(btn->{
					// Execute action on each selected item
					for(T item:new ArrayList<>(selectedItems)){
						action.accept(item);
					}
					rctx.rebuild();
				});
				config.add(b);
			}
		}

		// Collective actions - operate on all selected items as a list
		List<Consumer<List<T>>> collective=contract.getCollectiveActions();
		if(!collective.isEmpty()){
			AtomicInteger idx=new AtomicInteger(1);
			for(Consumer<List<T>> action:collective){
				int i=idx.getAndIncrement();
				Button b=new Button("Collective "+i);
				b.setBackground(RestTheme.MAIN);
				b.setForeground(RestTheme.ACCENT_TEXT);
				b.setOnClick(btn->{
					action.accept(new ArrayList<>(selectedItems));
					rctx.rebuild();
				});
				config.add(b);
			}
		}

		// Add button (not mandatory action: ACCENT as foreground only)
		if(contract.getAllowCreation()){
			Button add=new Button("Add");
			add.setBackground(RestTheme.MAIN);
			add.setForeground(RestTheme.ACCENT_TEXT);
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
		target.add(config);

		// Calculate height for list
		ArrayList<T> obj=contract.getObjects();
		int rowHeight=35;
		int totalListHeight=Math.max(100,obj.size()*rowHeight+(obj.size()-1)*5);

		// Create list panel with FlowLayout TTB
		Panel list=new Panel(new FlowLayout(FlowLayout.LEFT,FlowLayout.TTB,0,5));
		list.setSize(targetWidth,totalListHeight);

		// Apply list customizer if available
		contract.customizeList(new RestListCustomizationRenderingContext(list));

		fillList(list,targetWidth,selectedItems,rctx);
		target.add(list);

		// Resize target to fit everything
		target.setSize(targetWidth,60+totalListHeight+20);

		target.update();
	}

	private void fillList(Panel list,int targetWidth,List<T> selectedItems,RestFeatureRenderingContext rctx){
		ArrayList<T> objects=contract.getObjects();

		for(T obj:objects){
			Panel row=new Panel(new FlowLayout());
			row.setSize(targetWidth,35);

			// Selection checkbox
			Button selectBtn=new Button(selectedItems.contains(obj)?"☑":"☐");
			selectBtn.setBackground(RestTheme.MAIN);
			selectBtn.setForeground(selectedItems.contains(obj)?RestTheme.ACCENT_TEXT:RestTheme.MAIN_TEXT);
			selectBtn.setOnClick(b->{
				if(selectedItems.contains(obj)){
					selectedItems.remove(obj);
				}else{
					selectedItems.add(obj);
				}
				rctx.rebuild();
			});
			row.add(selectBtn);

			// Object label
			Label l=new Label(String.valueOf(obj));
			l.setForeground(RestTheme.MAIN_TEXT);
			row.add(l);

			list.add(row);
		}
	}

	private Class<T> getType(){
		return contract.getType();
	}

	public void renderPreview(FeatureRenderingContext ctx){}
}
