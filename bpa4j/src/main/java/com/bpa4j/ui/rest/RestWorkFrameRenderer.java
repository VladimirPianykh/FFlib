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
		window=new Window();
		root=new Panel(new BorderLayout());
		root.setSize(1000,800);
		featureBar=new Panel(new FlowLayout(FlowLayout.LEFT,FlowLayout.TTB,5,5));
		featureBar.setSize(root.getWidth(),80);
		content=new Panel(new GridLayout(1,1,5,5));
		content.setSize(root.getWidth(),root.getHeight()-featureBar.getHeight());
		BorderLayout layout=(BorderLayout)root.getLayout();
		layout.addLayoutComponent(featureBar,BorderLayout.NORTH);
		layout.addLayoutComponent(content,BorderLayout.CENTER);
		root.add(featureBar);
		root.add(content);
		for(WorkFrame.FeatureEntry<?>entry:workFrame.getFeatures()){
			Button b=new Button(entry.toString());
			b.setOnClick(btn->{
				System.err.println("accepted!!!");
				workFrame.selectFeature(entry);
				state.invalidate();
			});
			featureBar.add(b);
		}
		Label placeholder=new Label("Select a feature");
		content.add(placeholder);
		window.setContent(root);
		state.showWindow(window);
	}
	public RestFeatureRenderingContext getContext(WorkFrame.FeatureEntry<?>f){
		if(window==null||content==null){
			throw new IllegalStateException("Work frame not shown yet.");
		}
		return new RestFeatureRenderingContext(state,window,content);
	}
}
