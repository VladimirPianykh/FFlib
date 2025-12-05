package com.bpa4j.ui.rest;

import com.bpa4j.feature.FeatureRenderingContext;
import com.bpa4j.ui.rest.abstractui.Panel;
import com.bpa4j.ui.rest.abstractui.UIState;
import com.bpa4j.ui.rest.abstractui.Window;

/**
 * @author AI-generated
 */
public class RestFeatureRenderingContext implements FeatureRenderingContext{
	private final UIState state;
	// private final Window window;
	private final Panel target;
	private final Runnable rebuilder;
	public RestFeatureRenderingContext(UIState state,Window window,Panel target,Runnable rebuilder){
		this.state=state;
		// this.window=window;
		this.target=target;
		this.rebuilder=rebuilder;
	}
	// public Window getWindow(){
	// 	return window;
	// }
	public Panel getTarget(){
		return target;
	}
	public void rebuild(){
		if(rebuilder!=null) rebuilder.run();
		state.invalidate();
	}
	// public void show(){
	// 	state.showWindow(window);
	// 	state.invalidate();
	// }
	// public void close(){
	// 	state.close(window);
	// }
	public UIState getState(){
		return state;
	}
}
