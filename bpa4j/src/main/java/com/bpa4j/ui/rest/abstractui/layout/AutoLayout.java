package com.bpa4j.ui.rest.abstractui.layout;

import com.bpa4j.ui.rest.abstractui.Component;
import com.bpa4j.ui.rest.abstractui.LayoutManager;
import com.bpa4j.ui.rest.abstractui.Panel;

/**
 * An automatic layout manager that intelligently arranges components based on their count
 * and the container's aspect ratio. For small numbers of components (2, 3, 5), it uses
 * predefined optimal layouts. For larger numbers, it falls back to a grid layout.
 * 
 * <p>The layout adapts to whether the container is vertically or horizontally oriented,
 * providing different arrangements for each orientation to maximize space utilization.</p>
 * 
 * @author AI-generated
 */
public class AutoLayout implements LayoutManager{
	
	@Override
	public void layout(Panel target){
		int componentCount=target.getComponents().size();
		if(componentCount==0) return;
		
		int width=target.getWidth();
		int height=target.getHeight();
		
		// Calculate square root for grid fallback
		int gridSize=(int)Math.sqrt(componentCount);
		
		// Determine if container is vertically or horizontally oriented
		boolean isVertical=height>width;
		
		if(isVertical){
			layoutVertical(target,componentCount,width,height,gridSize);
		}else{
			layoutHorizontal(target,componentCount,width,height,gridSize);
		}
	}
	
	/**
	 * Layouts components when the container is vertically oriented (height > width).
	 */
	private void layoutVertical(Panel target,int count,int width,int height,int gridSize){
		switch(count){
			case 2->{
				// Stack vertically: two equal horizontal strips
				getComponent(target,0).setBounds(0,0,width,height/2);
				getComponent(target,1).setBounds(0,height/2,width,height/2);
			}
			case 3->{
				// Left column with 2 components, right column with 1 spanning full height
				getComponent(target,0).setBounds(0,0,width/2,height/2);
				getComponent(target,1).setBounds(0,height/2,width/2,height/2);
				getComponent(target,2).setBounds(width/2,0,width/2,height);
			}
			case 5->{
				// Top row: 2 components
				// Middle row: 1 component spanning full width
				// Bottom row: 2 components
				getComponent(target,0).setBounds(0,0,width/2,height/3);
				getComponent(target,1).setBounds(width/2,0,width/2,height/3);
				getComponent(target,2).setBounds(0,height/3,width,height/3);
				getComponent(target,3).setBounds(0,height*2/3,width/2,height/3);
				getComponent(target,4).setBounds(width/2,height*2/3,width/2,height/3);
			}
			default->{
				// Use grid layout with more rows than columns
				new GridLayout(0,gridSize).layout(target);
			}
		}
	}
	
	/**
	 * Layouts components when the container is horizontally oriented (width >= height).
	 */
	private void layoutHorizontal(Panel target,int count,int width,int height,int gridSize){
		switch(count){
			case 2->{
				// Side by side: two equal vertical strips
				getComponent(target,0).setBounds(0,0,width/2,height);
				getComponent(target,1).setBounds(width/2,0,width/2,height);
			}
			case 3->{
				// Top row with 2 components, bottom row with 1 spanning full width
				getComponent(target,0).setBounds(0,0,width/2,height/2);
				getComponent(target,1).setBounds(width/2,0,width/2,height/2);
				getComponent(target,2).setBounds(0,height/2,width,height/2);
			}
			default->{
				// Use grid layout with more columns than rows
				new GridLayout(gridSize,0).layout(target);
			}
		}
	}
	
	/**
	 * Safely retrieves a component from the target panel.
	 * @param target the panel containing components
	 * @param index the index of the component to retrieve
	 * @return the component at the specified index
	 */
	private Component getComponent(Panel target,int index){
		return target.getComponents().get(index);
	}
}
