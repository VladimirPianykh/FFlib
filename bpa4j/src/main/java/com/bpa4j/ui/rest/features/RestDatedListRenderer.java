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
import com.bpa4j.ui.rest.RestRenderingManager;
import com.bpa4j.ui.rest.RestTheme;
import com.bpa4j.ui.rest.abstractui.Panel;
import com.bpa4j.ui.rest.abstractui.components.Button;
import com.bpa4j.ui.rest.abstractui.components.Label;
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

		int targetWidth=target.getWidth();
		int targetHeight=target.getHeight();
		if(targetWidth==0||targetHeight==0){
			targetWidth=RestRenderingManager.DEFAULT_SIZE.width();
			targetHeight=RestRenderingManager.DEFAULT_SIZE.height();
			target.setSize(targetWidth,targetHeight);
		}

		// Use FlowLayout TTB for vertical stacking without huge gaps
		target.setLayout(new FlowLayout(FlowLayout.LEFT,FlowLayout.TTB,0,5));

		// Top panel with date navigation
		Panel topPanel=new Panel(new FlowLayout());
		topPanel.setSize(targetWidth,40);

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
		target.add(topPanel);

		// Calculate height for list panel
		Set<T> objects=contract.getObjects();
		int rowHeight=35;
		int totalListHeight=Math.max(100,objects.size()*rowHeight+(objects.size()-1)*5);

		// List panel
		Panel listPanel=new Panel(new FlowLayout(FlowLayout.LEFT,FlowLayout.TTB,0,5));
		listPanel.setSize(targetWidth,totalListHeight);

		for(T t:objects){
			Panel row=new Panel(new FlowLayout());
			row.setSize(targetWidth,35);

			Label itemLabel=new Label(t.name);
			itemLabel.setForeground(RestTheme.MAIN_TEXT);
			row.add(itemLabel);
			
			// Dater logic
			var objectsWithDaters=contract.getObjectsWithDaters();
			Dater<T> dater=objectsWithDaters.get(t);
			if(dater==null&&contract.getDateProvider()!=null){
				dater=contract.getDateProvider().get();
				contract.putObject(t,dater);
			}
			if(dater!=null){
				RestRenderingManager manager=(RestRenderingManager)ProgramStarter.getRenderingManager();
				RestRenderingManager.RestDateRenderingContext daterCtx=new RestRenderingManager.RestDateRenderingContext(row,manager);
				dater.render(t,currentDate,daterCtx);
			}else{
				row.add(new Label("[No schedule]"));
			}
			
			listPanel.add(row);
		}
		target.add(listPanel);
		
		// Bottom panel with add button (not mandatory -> ACCENT as foreground only)
		Panel bottomPanel=new Panel(new FlowLayout());
		bottomPanel.setSize(targetWidth,40);

		Button addBtn=new Button("Add");
		addBtn.setBackground(RestTheme.MAIN);
		addBtn.setForeground(RestTheme.ACCENT_TEXT);
		addBtn.setOnClick(b->{
			try{
				T t=contract.getType().getDeclaredConstructor().newInstance();
				contract.putObject(t,contract.getDateProvider().get());
				ProgramStarter.editor.constructEditor(t,true,()->contract.removeObject(t),ProgramStarter.getRenderingManager().getDetachedFeatureRenderingContext());
				rctx.rebuild();
			}catch(Exception ex){
				throw new IllegalStateException(ex);
			}
		});
		bottomPanel.add(addBtn);
		target.add(bottomPanel);

		// Resize target to fit everything
		target.setSize(targetWidth,40+totalListHeight+40+20); // Top + List + Bottom + gaps

		target.update();
	}
	public void renderPreview(FeatureRenderingContext ctx){}
}
