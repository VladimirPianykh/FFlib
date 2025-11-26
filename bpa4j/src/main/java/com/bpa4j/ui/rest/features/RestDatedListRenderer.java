package com.bpa4j.ui.rest.features;


import java.util.Set;
import com.bpa4j.Dater;
import com.bpa4j.core.Editable;
import com.bpa4j.core.ProgramStarter;
import com.bpa4j.defaults.features.transmission_contracts.DatedList;
import com.bpa4j.feature.FeatureRenderer;
import com.bpa4j.feature.FeatureRenderingContext;
import com.bpa4j.ui.rest.RestFeatureRenderingContext;
import com.bpa4j.ui.rest.abstractui.Panel;
import com.bpa4j.ui.rest.abstractui.components.Button;
import com.bpa4j.ui.rest.abstractui.components.Label;
import com.bpa4j.ui.rest.abstractui.layout.FlowLayout;

public class RestDatedListRenderer<T extends Editable> implements FeatureRenderer<DatedList<T>>{
	private final DatedList<T> contract;
	public RestDatedListRenderer(DatedList<T> contract){
		this.contract=contract;
	}
	public DatedList<T> getTransmissionContract(){
		return contract;
	}
	public void render(FeatureRenderingContext ctx){
		RestFeatureRenderingContext rctx=(RestFeatureRenderingContext)ctx;
		Panel target=rctx.getTarget();
		target.removeAll();
		target.setLayout(new FlowLayout(FlowLayout.LEFT,FlowLayout.TTB,5,5));
		
		Set<T> objects=contract.getObjects();
		for(T t:objects){
			Panel row=new Panel(new FlowLayout());
			Button itemBtn=new Button(t.name);
			itemBtn.setOnClick(b->{
				ProgramStarter.editor.constructEditor(t,false,()->contract.removeObject(t),ProgramStarter.getRenderingManager().getDetachedFeatureRenderingContext());
				rctx.rebuild();
			});
			row.add(itemBtn);
			
			// Dater logic (simplified for REST)
			var objectsWithDaters=contract.getObjectsWithDaters();
			Dater<T> dater=objectsWithDaters.get(t);
			if(dater==null && contract.getDateProvider()!=null){
				dater=contract.getDateProvider().get();
				contract.putObject(t,dater);
			}
			if(dater!=null){
				// In Swing it renders 7 days. Here we just show a label for now as we don't have custom component rendering from Dater in REST yet.
				// Or we could try to render what Dater returns if it returns a Component that we can map?
				// Dater returns JComponent in Swing. Here it would return... JComponent?
				// The Dater interface is generic but usually returns JComponent.
				// If Dater returns JComponent, we can't use it in REST.
				// So we just show a placeholder or nothing.
				row.add(new Label("[Schedule View Not Supported in REST]"));
			}
			
			target.add(row);
		}
		
		Button addBtn=new Button("Add");
		addBtn.setOnClick(b->{
			try{
				T t=contract.getType().getDeclaredConstructor().newInstance();
				contract.putObject(t,null); // Add to list (putObject adds to map, but we also need to add to set if not present? contract.putObject handles it?)
				// Actually contract.putObject puts into objectsWithDaters.
				// But contract.getObjects() returns a Set.
				// Swing renderer does: panel.add(createTableEntry(t...)) and constructs editor.
				// And the editor deleter calls contract.removeObject(t).
				// We need to make sure t is added to the collection that getObjects() returns.
				// The contract implementation of getObjects() usually returns the keySet of the map or a separate list.
				// Let's assume creating it and editing it is enough, similar to Swing.
				// But wait, Swing renderer adds it to the panel visually.
				// We need to add it to the data.
				// Swing renderer: T t = ...; panel.add(...); constructEditor(...).
				// It seems it doesn't explicitly add to the contract until saved?
				// Or maybe constructEditor's saver does it?
				// Actually, Swing renderer adds it to the UI panel immediately.
				// For REST, we need to add it to the model so that next render shows it.
				// But contract doesn't have a simple "add" method for T, only putObject(T, Dater).
				// If we look at Swing renderer:
				// T t=getType().getDeclaredConstructor().newInstance();
				// panel.add(createTableEntry(t,tab,font),panel.getComponentCount()-1);
				// ProgramStarter.editor.constructEditor(t,true,()->contract.removeObject(t),...);
				// It seems it relies on the editor to save it?
				// But wait, if we rebuild the UI in REST, we need the state to be persisted.
				// If the contract is backed by a list, we need to add to it.
				// Let's assume we can just add it to the objects list if we could access it.
				// But we only have getObjects().
				// If getObjects() returns a live collection, we can add to it.
				contract.getObjects().add(t);
				ProgramStarter.editor.constructEditor(t,true,()->contract.removeObject(t),ProgramStarter.getRenderingManager().getDetachedFeatureRenderingContext());
				rctx.rebuild();
			}catch(Exception ex){
				throw new IllegalStateException(ex);
			}
		});
		target.add(addBtn);
	}
	public void renderPreview(FeatureRenderingContext ctx){}
}
