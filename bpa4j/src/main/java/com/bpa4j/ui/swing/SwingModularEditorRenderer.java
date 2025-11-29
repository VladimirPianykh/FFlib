package com.bpa4j.ui.swing;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dialog.ModalityType;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Supplier;
import javax.swing.AbstractAction;
import javax.swing.JDialog;
import javax.swing.JPanel;
import com.bpa4j.core.Editable;
import com.bpa4j.core.Root;
import com.bpa4j.editor.EditorModule;
import com.bpa4j.editor.ModularEditor;
import com.bpa4j.editor.ModularEditorRenderer;
import com.bpa4j.editor.ModuleRenderer;
import com.bpa4j.feature.FeatureRenderingContext;
import com.bpa4j.ui.swing.util.HButton;
import com.bpa4j.ui.swing.util.PathIcon;

public class SwingModularEditorRenderer implements ModularEditorRenderer{
	public static class SwingModuleRenderingContext implements ModulesRenderingContext{
		private ArrayList<JPanel> tabs=new ArrayList<>();
        private JDialog dialog;
		public SwingModuleRenderingContext(JDialog dialog){
			this.dialog=dialog;
		}
		/**
		 * Modules' tabs should be added directly to this list.
		 */
		public ArrayList<JPanel> getTabs(){
			return tabs;
		}
		public JDialog getEditorDialog(){
			return dialog;
		}
	}
	public HashMap<Class<? extends EditorModule>,Supplier<? extends ModuleRenderer<?>>>moduleRenderers=new HashMap<>();
	public Supplier<? extends ModuleRenderer<?>>defaultModuleRenderer=()->null;
	public SwingModularEditorRenderer(){
		loadDefaultRenderers();
	}
	public ModulesRenderingContext getModulesRenderingContext(FeatureRenderingContext context){
		SwingFeatureRenderingContext ctx=(SwingFeatureRenderingContext)context;
		JDialog d=new JDialog(ctx.getWindow(),ModalityType.APPLICATION_MODAL);
		d.setSize(Root.SCREEN_SIZE);
		return new SwingModuleRenderingContext(d);
	}
	public void constructEditor(Editable editable,boolean isNew,Runnable deleter,ModularEditor editor,FeatureRenderingContext context,ModulesRenderingContext moduleContext){
		// SwingFeatureRenderingContext ctx=(SwingFeatureRenderingContext)context;
		SwingModuleRenderingContext mctx=(SwingModuleRenderingContext)moduleContext;
		JDialog d=mctx.getEditorDialog();
		d.setSize(Root.SCREEN_SIZE);
		d.setUndecorated(true);
		d.setLayout(null);
		CardLayout layout=new CardLayout();
		JPanel mainPanel=new JPanel();
		mainPanel.setLayout(layout);
		mainPanel.setBounds(0,0,d.getWidth(),d.getHeight());
		PathIcon leftIcon=new PathIcon("ui/left.png",d.getHeight()/13,d.getHeight()/13),r=new PathIcon("ui/right.png",d.getHeight()/13,d.getHeight()/13);
		HButton left=new HButton(10,7){
			public void paintComponent(Graphics g){
				Graphics2D g2=(Graphics2D)g;
				int c=scale*5;
				g2.setColor(new Color(c,pressed?c:c*2,c));
				g2.fillRect(0,0,getWidth(),getHeight());
				leftIcon.paintIcon(this,g2,(getWidth()-leftIcon.getIconWidth())/2,(getHeight()-leftIcon.getIconHeight())/2);
			}
		},right=new HButton(10,7){
			public void paintComponent(Graphics g){
				Graphics2D g2=(Graphics2D)g;
				int c=scale*5;
				g2.setColor(new Color(c,pressed?c:c*2,c));
				g2.fillRect(0,0,getWidth(),getHeight());
				r.paintIcon(this,g2,(getWidth()-r.getIconWidth())/2,(getHeight()-r.getIconHeight())/2);
			}
		};
		left.setAction(new AbstractAction(){
			public void actionPerformed(ActionEvent e){
				layout.previous(mainPanel);
				d.repaint();
			}
		});
		right.setAction(new AbstractAction(){
			public void actionPerformed(ActionEvent e){
				layout.next(mainPanel);
				d.repaint();
			}
		});
		left.setBounds(d.getWidth()/100,d.getHeight()*4/5,d.getHeight()/10,d.getHeight()/10);
		right.setBounds(d.getWidth()*99/100-d.getHeight()/10,d.getHeight()*4/5,d.getHeight()/10,d.getHeight()/10);
		left.setFocusable(false);
		right.setFocusable(false);
		d.add(left);
		d.add(right);
		d.add(mainPanel);
		int k=1;
		for(JPanel tab:mctx.getTabs()){
			mainPanel.add(tab,"tab"+k);
			++k;
		}
		if(k==1) return;
		else if(k==2){
			left.setVisible(false);
			right.setVisible(false);
		}
		layout.show(mainPanel,"tab1");
		d.setVisible(true);
	}
	@SuppressWarnings("unchecked")
	public <M extends EditorModule> ModuleRenderer<M> getModuleRenderer(M m){
		Supplier<? extends ModuleRenderer<?>> renderer=moduleRenderers.get(m.getClass());
		if(renderer==null) renderer=defaultModuleRenderer;
		if(renderer==null) throw new IllegalArgumentException("No renderer for "+m+".");
		return (ModuleRenderer<M>)renderer.get();
	}
	public <M extends EditorModule> void putModuleRenderer(Class<M> e,Supplier<ModuleRenderer<M>>renderer){
		moduleRenderers.put(e,renderer);
	}
	public void setDefaultModuleRenderer(Supplier<? extends ModuleRenderer<?>>renderer){
		defaultModuleRenderer=renderer;
	}
	private void loadDefaultRenderers(){
		putModuleRenderer(com.bpa4j.editor.modules.CustomerModule.class,()->new com.bpa4j.ui.swing.editor.modules.SwingCustomerModuleRenderer());
		// putModuleRenderer(com.bpa4j.editor.modules.ExcludeModule.class,null);
		putModuleRenderer(com.bpa4j.editor.modules.FormModule.class,()->new com.bpa4j.ui.swing.editor.modules.SwingFormModuleRenderer());
		putModuleRenderer(com.bpa4j.editor.modules.ImageDisplayModule.class,()->new com.bpa4j.ui.swing.editor.modules.SwingImageDisplayModuleRenderer());
		putModuleRenderer(com.bpa4j.editor.modules.LimitToModule.class,null);
		putModuleRenderer(com.bpa4j.editor.modules.LogWatchModule.class,()->new com.bpa4j.ui.swing.editor.modules.SwingLogWatchModuleRenderer());
		// putModuleRenderer(com.bpa4j.editor.modules.MapModule.class,null);
		// putModuleRenderer(com.bpa4j.editor.modules.NewLimiterModule.class,null);
		putModuleRenderer(com.bpa4j.editor.modules.StageApprovalModule.class,()->new com.bpa4j.ui.swing.editor.modules.SwingStageApprovalModuleRenderer());
		putModuleRenderer(com.bpa4j.editor.modules.StageMapModule.class,()->new com.bpa4j.ui.swing.editor.modules.SwingStageMapModuleRenderer());
		putModuleRenderer(com.bpa4j.editor.modules.TableModule.class,()->new com.bpa4j.ui.swing.editor.modules.SwingTableModuleRenderer());

	}
}
