package com.bpa4j.ui.rest.features;

import com.bpa4j.core.User;
import com.bpa4j.defaults.features.transmission_contracts.History;
import com.bpa4j.feature.FeatureRenderer;
import com.bpa4j.feature.FeatureRenderingContext;
import com.bpa4j.ui.rest.RestFeatureRenderingContext;
import com.bpa4j.ui.rest.RestRenderingManager;
import com.bpa4j.ui.rest.abstractui.Panel;
import com.bpa4j.ui.rest.abstractui.components.Label;
import com.bpa4j.ui.rest.abstractui.layout.BorderLayout;
import com.bpa4j.ui.rest.abstractui.layout.FlowLayout;
import com.bpa4j.ui.rest.abstractui.layout.GridLayout;

/**
 * Rest renderer for History feature - displays user login/logout history
 * @author AI-generated
 */
public class RestHistoryRenderer implements FeatureRenderer<History> {
	private final History contract;
	
	public RestHistoryRenderer(History contract) {
		this.contract = contract;
	}
	
	public History getTransmissionContract() {
		return contract;
	}
	
	public void render(FeatureRenderingContext ctx) {
		RestFeatureRenderingContext rctx = (RestFeatureRenderingContext) ctx;
		Panel target = rctx.getTarget();
		target.removeAll();
		
		// Create root panel with border layout
		int targetWidth=target.getWidth();
		int targetHeight=target.getHeight();
		if(targetWidth==0||targetHeight==0){
			targetWidth=RestRenderingManager.DEFAULT_SIZE.width();
			targetHeight=RestRenderingManager.DEFAULT_SIZE.height();
			target.setSize(targetWidth,targetHeight);
		}
		Panel root = new Panel(new BorderLayout());
		root.setSize(targetWidth,targetHeight);
		
		// Create header
		Panel header = new Panel(new FlowLayout());
		header.setSize(targetWidth,40);
		Label title = new Label(contract.getFeatureName());
		header.add(title);
		
		// Create content area with table layout
		Panel content=new Panel(new GridLayout(0,1,5,5));
		// Size handled by BorderLayout
		
		// Add table headers
		Panel tableHeader = new Panel(new GridLayout(1, 4, 5, 5));
		tableHeader.setSize(content.getWidth(), 30);
		tableHeader.add(new Label("Логин"));
		tableHeader.add(new Label("Время входа"));
		tableHeader.add(new Label("Время выхода"));
		tableHeader.add(new Label("IP"));
		content.add(tableHeader);
		
		// Add history entries
		User.getActiveUser().history.descendingIterator().forEachRemaining(entry -> {
			Panel row = new Panel(new GridLayout(1, 4, 5, 5));
			row.setSize(content.getWidth(), 25);
			row.add(new Label(entry.login));
			row.add(new Label(entry.inTime.toString()));
			row.add(new Label(entry.outTime == null ? "---" : entry.outTime.toString()));
			row.add(new Label(entry.ip.toString()));
			content.add(row);
		});
		
		// Assemble layout
		BorderLayout layout = (BorderLayout) root.getLayout();
		layout.addLayoutComponent(header, BorderLayout.NORTH);
		layout.addLayoutComponent(content, BorderLayout.CENTER);
		root.add(header);
		root.add(content);
		
		target.add(root);
		target.update();
	}
	
	public void renderPreview(FeatureRenderingContext ctx) {
		// Preview rendering not needed for REST
	}
}
