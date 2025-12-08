package com.bpa4j.ui.rest;

import java.util.ArrayList;
import com.bpa4j.core.Editable;
import com.bpa4j.editor.EditorModule;
import com.bpa4j.editor.ModularEditor;
import com.bpa4j.editor.ModularEditorRenderer;
import com.bpa4j.editor.ModuleRenderer;
import com.bpa4j.editor.modules.FormModule;
import com.bpa4j.editor.modules.LogWatchModule;
import com.bpa4j.editor.modules.StageMapModule;
import com.bpa4j.feature.FeatureRenderingContext;
import com.bpa4j.ui.rest.abstractui.Panel;
import com.bpa4j.ui.rest.abstractui.Window;
import com.bpa4j.ui.rest.abstractui.components.Button;
import com.bpa4j.ui.rest.abstractui.components.Label;
import com.bpa4j.ui.rest.abstractui.layout.BorderLayout;
import com.bpa4j.ui.rest.abstractui.layout.FlowLayout;

/**
 * REST implementation of {@link ModularEditorRenderer}.
 * Provides tab navigation with left/right arrows like in Swing.
 */
public class RestModularEditorRenderer implements ModularEditorRenderer{
	public static class RestModulesRenderingContext implements ModulesRenderingContext{
		private final RestFeatureRenderingContext base;
		private Window w;
		private Panel target;
		private ArrayList<Panel> tabs=new ArrayList<>();
		private Panel currentTab=null;
		public RestModulesRenderingContext(RestFeatureRenderingContext base,Window w,Panel target){
			this.base=base;
			this.w=w;
			this.target=target;
		}
		// public RestFeatureRenderingContext getBase(){
		// 	return base;
		// }
		/**
		 * Returns a panel for the current module tab.
		 * Each module should create its own tab panel.
		 * The panel is automatically added to the tabs list when first accessed.
		 * Note: Each call to getTarget() within the same module returns the same panel.
		 * To start a new tab for the next module, the system automatically creates a new panel
		 * when a module's createTab method is called (by detecting when getTarget() is called
		 * after all previous tabs have been finalized).
		 */
		public Panel getTarget(){
			// If no current tab, create a new one for this module
			if(currentTab==null){
				currentTab=new Panel();
				currentTab.setSize(target.getWidth()>0?target.getWidth():RestRenderingManager.DEFAULT_SIZE.width(),target.getHeight()>0?target.getHeight():RestRenderingManager.DEFAULT_SIZE.height());
				tabs.add(currentTab);
			}
			return currentTab;
		}
		/**
		 * Finalizes the current tab and prepares for the next module.
		 * This should be called at the end of each module's createTab method
		 * to ensure the next module gets a new tab panel.
		 */
		public void finalizeCurrentTab(){
			currentTab=null;
		}
		/**
		 * Modules' tabs should be added directly to this list.
		 */
		public ArrayList<Panel> getTabs(){
			return tabs;
		}
		public void show(){
			base.getState().showWindow(w);
		}
		public void close(){
			base.getState().close(w);
			base.rebuild();
		}
		public void rebuild(){
			base.rebuild();
		}
	}
	private static class RestDummyModuleRenderer<M extends EditorModule> implements ModuleRenderer<M>{
		public void createTab(Editable editable,boolean isNew,Runnable deleter,M module,ModulesRenderingContext ctx){
			RestModulesRenderingContext rctx=(RestModulesRenderingContext)ctx;
			Panel tab=rctx.getTarget(); // Get or create tab panel for this module
			tab.removeAll();
			tab.setLayout(new FlowLayout());
			Label label=new Label("Module "+module.getClass().getSimpleName()+" is not implemented for REST UI.");
			tab.add(label);
			// Finalize this tab so next module gets a new one
			rctx.finalizeCurrentTab();
		}
	}
	public <M extends EditorModule> ModuleRenderer<M> getModuleRenderer(M m){
		if(m instanceof FormModule){
			@SuppressWarnings("unchecked")
			ModuleRenderer<M> r=(ModuleRenderer<M>)new com.bpa4j.ui.rest.editor.modules.RestFormModuleRenderer();
			return r;
		}
		if(m instanceof StageMapModule){
			@SuppressWarnings("unchecked")
			ModuleRenderer<M> r=(ModuleRenderer<M>)new com.bpa4j.ui.rest.editor.modules.RestStageMapModuleRenderer();
			return r;
		}
		if(m instanceof LogWatchModule){
			@SuppressWarnings("unchecked")
			ModuleRenderer<M> r=(ModuleRenderer<M>)new com.bpa4j.ui.rest.editor.modules.RestLogWatchModuleRenderer();
			return r;
		}
		return new RestDummyModuleRenderer<>();
	}
	public Panel content=new Panel();
	public Window window=new Window(content);
	public RestModularEditorRenderer(){
		window.setSize(RestRenderingManager.DEFAULT_SIZE);
		content.setSize(RestRenderingManager.DEFAULT_SIZE);
	}
	public ModulesRenderingContext getModulesRenderingContext(FeatureRenderingContext context){
		if(!(context instanceof RestFeatureRenderingContext))throw new IllegalArgumentException("RestModularEditorRenderer requires RestFeatureRenderingContext.");
		RestFeatureRenderingContext rctx=(RestFeatureRenderingContext)context;
		return new RestModulesRenderingContext(rctx,window,content);
	}
	public void constructEditor(Editable editable,boolean isNew,Runnable deleter,ModularEditor editor,FeatureRenderingContext context,ModulesRenderingContext moduleContext){
		RestModulesRenderingContext mctx=(RestModulesRenderingContext)moduleContext;
		Window w=mctx.w;
		Panel container=mctx.target;

		// Clear container and set up layout
		container.removeAll();
		BorderLayout mainLayout=new BorderLayout();
		container.setLayout(mainLayout);

		// Get all tabs that were created by modules
		ArrayList<Panel> tabs=mctx.getTabs();

		// If no tabs, return early
		if(tabs.isEmpty()) return;

		// Create main panel for tabs (CardLayout-like behavior)
		// This panel will contain the currently active tab
		final Panel mainPanel=new Panel();
		mainPanel.setSize(container.getWidth(),container.getHeight());

		// Current tab index (0-based)
		final int[] currentTabIndex={0};

		// Function to show a specific tab
		Runnable showTab=()->{
			mainPanel.removeAll();
			if(!tabs.isEmpty()&&currentTabIndex[0]>=0&&currentTabIndex[0]<tabs.size()){
				Panel currentTab=tabs.get(currentTabIndex[0]);
				currentTab.setSize(container.getWidth(),container.getHeight());
				mainPanel.add(currentTab);
			}
			mctx.rebuild();
		};

		// Show first tab
		showTab.run();

		// Create navigation buttons
		int buttonSize=w.getHeight()/10;
		Button left=new Button("◀");
		left.setSize(buttonSize,buttonSize);
		left.setOnClick(b->{
			if(tabs.isEmpty()) return;
			// Go to previous tab (circular)
			currentTabIndex[0]=(currentTabIndex[0]-1+tabs.size())%tabs.size();
			showTab.run();
		});

		Button right=new Button("▶");
		right.setSize(buttonSize,buttonSize);
		right.setOnClick(b->{
			if(tabs.isEmpty()) return;
			// Go to next tab (circular)
			currentTabIndex[0]=(currentTabIndex[0]+1)%tabs.size();
			showTab.run();
		});

		// Hide buttons if only one tab (by setting size to 0)
		if(tabs.size()==1){
			left.setSize(0,0);
			right.setSize(0,0);
		}

		// Position buttons (similar to Swing: left at 1% width, right at 99% width, both at 80% height)
		left.setLocation(w.getWidth()/100,w.getHeight()*4/5);
		right.setLocation(w.getWidth()*99/100-buttonSize,w.getHeight()*4/5);

		// Add components to container
		mainLayout.addLayoutComponent(mainPanel,BorderLayout.CENTER);
		mainLayout.addLayoutComponent(left,BorderLayout.WEST);
		mainLayout.addLayoutComponent(right,BorderLayout.EAST);
		container.add(mainPanel);
		container.add(left);
		container.add(right);

		mctx.show();
	}
}
