package com.bpa4j.ui.rest.features;


import java.time.LocalDate;
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
import com.bpa4j.ui.rest.abstractui.layout.BorderLayout;
import com.bpa4j.ui.rest.abstractui.layout.FlowLayout;

/**
 * REST renderer for DatedList feature.
 * Displays editable items with date-based scheduling.
 * 
 * Note: Full Dater visualization (7-day JComponent view) is not available in REST
 * as Dater.apply() returns JComponent which cannot be rendered in REST UI.
 * Instead, we provide a simplified date navigation interface.
 * 
 * @author AI-generated
 */
public class RestDatedListRenderer<T extends Editable> implements FeatureRenderer<DatedList<T>>{
	private final DatedList<T> contract;
	private LocalDate currentDate=LocalDate.now();

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
		Panel root=new Panel(new BorderLayout());
		root.setSize(target.getWidth(),target.getHeight());

		// Top panel with date navigation
		Panel topPanel=new Panel(new FlowLayout());
		topPanel.setSize(root.getWidth(),40);

		Button prevWeek=new Button("< Week");
		prevWeek.setOnClick(b->{
			currentDate=currentDate.minusDays(7);
			rctx.rebuild();
		});
		topPanel.add(prevWeek);

		Button prevDay=new Button("< Day");
		prevDay.setOnClick(b->{
			currentDate=currentDate.minusDays(1);
			rctx.rebuild();
		});
		topPanel.add(prevDay);

		Label dateLabel=new Label("Date: "+currentDate.toString());
		topPanel.add(dateLabel);

		Button nextDay=new Button("Day >");
		nextDay.setOnClick(b->{
			currentDate=currentDate.plusDays(1);
			rctx.rebuild();
		});
		topPanel.add(nextDay);

		Button nextWeek=new Button("Week >");
		nextWeek.setOnClick(b->{
			currentDate=currentDate.plusDays(7);
			rctx.rebuild();
		});
		topPanel.add(nextWeek);

		Button today=new Button("Today");
		today.setOnClick(b->{
			currentDate=LocalDate.now();
			rctx.rebuild();
		});
		topPanel.add(today);

		// List panel
		Panel listPanel=new Panel(new FlowLayout(FlowLayout.LEFT,FlowLayout.TTB,5,5));
		listPanel.setSize(root.getWidth(),root.getHeight()-80);
		
		Set<T> objects=contract.getObjects();
		for(T t:objects){
			Panel row=new Panel(new FlowLayout());

			Button itemBtn=new Button(t.name);
			itemBtn.setOnClick(b->{
				ProgramStarter.editor.constructEditor(t,false,()->contract.removeObject(t),ProgramStarter.getRenderingManager().getDetachedFeatureRenderingContext());
				rctx.rebuild();
			});
			row.add(itemBtn);
			
			// Dater logic
			var objectsWithDaters=contract.getObjectsWithDaters();
			Dater<T> dater=objectsWithDaters.get(t);
			if(dater==null&&contract.getDateProvider()!=null){
				dater=contract.getDateProvider().get();
				contract.putObject(t,dater);
			}
			if(dater!=null){
				// Note: In Swing, dater.apply(t, date) returns JComponent for 7-day view
				// In REST, we cannot render JComponents, so we show a simplified view
				// NO! TODO: paint customizable 7-day view
				Label daterInfo=new Label("[Schedule: "+currentDate+" to "+currentDate.plusDays(6)+"]");
				row.add(daterInfo);

				// TODO: If Dater has configurable fields (EditorEntry annotations),
				// we could add a configuration button here similar to Swing's popup menu
			}else{
				row.add(new Label("[No schedule]"));
			}
			
			listPanel.add(row);
		}
		
		// Bottom panel with add button
		Panel bottomPanel=new Panel(new FlowLayout());
		bottomPanel.setSize(root.getWidth(),40);

		Button addBtn=new Button("Add");
		addBtn.setOnClick(b->{
			try{
				T t=contract.getType().getDeclaredConstructor().newInstance();
				// Add to the objects set
				contract.getObjects().add(t); //TODO: fix
				ProgramStarter.editor.constructEditor(t,true,()->contract.removeObject(t),ProgramStarter.getRenderingManager().getDetachedFeatureRenderingContext());
				rctx.rebuild();
			}catch(Exception ex){
				throw new IllegalStateException(ex);
			}
		});
		bottomPanel.add(addBtn);

		// Layout
		BorderLayout layout=(BorderLayout)root.getLayout();
		layout.addLayoutComponent(topPanel,BorderLayout.NORTH);
		layout.addLayoutComponent(listPanel,BorderLayout.CENTER);
		layout.addLayoutComponent(bottomPanel,BorderLayout.SOUTH);
		root.add(topPanel);
		root.add(listPanel);
		root.add(bottomPanel);
		target.add(root);
	}
	public void renderPreview(FeatureRenderingContext ctx){}
}
