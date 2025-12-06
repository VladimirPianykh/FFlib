package com.bpa4j.ui.rest;

import com.bpa4j.core.WorkFrame;
import com.bpa4j.core.WorkFrameRenderer;
import com.bpa4j.ui.rest.abstractui.Panel;
import com.bpa4j.ui.rest.abstractui.UIState;
import com.bpa4j.ui.rest.abstractui.Window;
import com.bpa4j.ui.rest.abstractui.components.Button;
import com.bpa4j.ui.rest.abstractui.components.Label;
import com.bpa4j.ui.rest.abstractui.layout.BorderLayout;
import com.bpa4j.ui.rest.abstractui.layout.FlowLayout;
import com.bpa4j.ui.rest.abstractui.layout.GridLayout;

/**
 * @author AI-generated
 */
public class RestWorkFrameRenderer implements WorkFrameRenderer{
	private final WorkFrame workFrame;
	private final UIState state;
	private Window window;
	private Panel root;
	private Panel featureBar;
	private Panel content;
	public RestWorkFrameRenderer(WorkFrame workFrame,UIState state){
		this.workFrame=workFrame;
		this.state=state;
	}
	public WorkFrame getWorkFrame(){
		return workFrame;
	}
	public void showWorkFrame(){
		if(window!=null)return;
		// Validate that features are registered for user's role
		com.bpa4j.feature.Feature<?>[] features=com.bpa4j.core.User.ftrMap.get(workFrame.getUser().role);
		if(features==null||features.length==0){
			String errorMsg="ERROR: No features registered for role: "+workFrame.getUser().role+
				". This may indicate an outdated save file that needs to be deleted.";
			System.err.println(errorMsg);
			throw new IllegalStateException("ftrMap does not have elements for role "+workFrame.getUser().role);
		}
		root=new Panel(new BorderLayout());
		root.setSize(RestRenderingManager.DEFAULT_SIZE);
		featureBar=new Panel(new FlowLayout(FlowLayout.LEFT,FlowLayout.LTR,5,5));
		featureBar.setSize(root.getWidth(),80);
		content=new Panel(new GridLayout(1,1,5,5));
		content.setSize(root.getWidth(),root.getHeight()-featureBar.getHeight());
		BorderLayout layout=(BorderLayout)root.getLayout();
		layout.addLayoutComponent(featureBar,BorderLayout.NORTH);
		layout.addLayoutComponent(content,BorderLayout.CENTER);
		root.add(featureBar);
		root.add(content);
		Button exit=new Button("✕");
		exit.setOnClick(btn->workFrame.exit());
		featureBar.add(exit);
		for(WorkFrame.FeatureEntry<?>entry:workFrame.getFeatures()){
			Button b=new Button(entry.toString());
			b.setSize(150,35); // Make buttons larger for better visibility
			b.setOnClick(btn->{
				workFrame.selectFeature(entry);
				state.invalidate();
			});
			featureBar.add(b);
		}
		Label placeholder=new Label("Select a feature");
		content.add(placeholder);
		window=new Window(root);
		state.showWindow(window);
	}
	public RestFeatureRenderingContext getContext(WorkFrame.FeatureEntry<?>f){
		if(window==null||content==null){
			throw new IllegalStateException("Work frame not shown yet.");
		}
		return new RestFeatureRenderingContext(state,window,content,()->{
			workFrame.selectFeature(f);
			state.invalidate();
		});
	}
}
