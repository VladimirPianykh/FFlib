package com.bpa4j.ui.swing.features;

import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.NoSuchElementException;
import javax.swing.JButton;
import javax.swing.JPanel;
import com.bpa4j.core.Editable;
import com.bpa4j.core.EditableGroup;
import com.bpa4j.core.ProgramStarter;
import com.bpa4j.core.User;
import com.bpa4j.defaults.DefaultPermission;
import com.bpa4j.defaults.features.transmission_contracts.ModelEditing;
import com.bpa4j.feature.FeatureRenderer;
import com.bpa4j.feature.FeatureRenderingContext;
import com.bpa4j.ui.swing.SwingFeatureRenderingContext;
import com.bpa4j.ui.swing.SwingWorkFrameRenderer.SwingPreviewRenderingContext;
import com.bpa4j.ui.swing.SwingWorkFrameRenderer.WorkTabButton;
import com.bpa4j.ui.swing.features.SwingEditableListRenderer.SwingItemRenderingContext;

public class SwingModelEditingRenderer implements FeatureRenderer<ModelEditing>{
	private ModelEditing contract;
	public SwingModelEditingRenderer(ModelEditing contract){
		this.contract=contract;
	}
	public void paint(Graphics2D g2,BufferedImage image,int h){
		g2.setStroke(new BasicStroke(h/40));
		g2.drawPolyline(new int[]{h/4,h/4+h/20,h*3/4+h/20,h*3/4+h/20+h/10,h*3/4+h/5,h*3/4+h/5,h*3/4+h/5-h/40,h*3/4+h/5-h/40,h*3/4+h/20+h/10,h*3/4+h/40},new int[]{h*3/4,h*3/4,h/4,h/4,h/4-h/20,h/4-h/20-h/10,h/4-h/20-h/10,h/4-h/10,h/4-h/20,h/4-h/20},10);
		for(int x=0;x<h;x++)for(int y=0;y<h-x;y++)image.setRGB(x,y,image.getRGB(h-y-1,h-x-1));
	}
	public void fillTab(JPanel content,JPanel tab,Font font,FeatureRenderingContext context){
		contract.setGetRenderingContextOp(()->context);
		SwingFeatureRenderingContext ctx=(SwingFeatureRenderingContext)context;
		List<EditableGroup<?>>groupList=getTransmissionContract().getGroups();
		int columns=0;
		for(EditableGroup<?>group:groupList)try{
			if(group.invisible)continue;
			boolean canSee=User.getActiveUser().hasPermission(DefaultPermission.READ)||User.getActiveUser().hasPermission(User.registeredPermissions.stream().filter(e->e.name().equals("READ_"+group.type.getSimpleName().toUpperCase())).findAny().get()),
			canCreate=User.getActiveUser().hasPermission(DefaultPermission.CREATE)||User.getActiveUser().hasPermission(User.registeredPermissions.stream().filter(e->e.name().equals("CREATE_"+group.type.getSimpleName().toUpperCase())).findAny().get());
			if(canSee||canCreate)++columns;
		}catch(NoSuchElementException ex){throw new NoSuchElementException("Permission for "+group.type+" not found. You must define READ_"+group.type.getSimpleName().toUpperCase()+" and CREATE_"+group.type.getSimpleName().toUpperCase()+" permissions.",ex);}
		for(EditableGroup<?>group:groupList)try{
			if(group.invisible)continue;
			boolean canSee=User.getActiveUser().hasPermission(DefaultPermission.READ)||User.getActiveUser().hasPermission(User.registeredPermissions.stream().filter(e->e.name().equals("READ_"+group.type.getSimpleName().toUpperCase())).findAny().get()),
			canCreate=User.getActiveUser().hasPermission(DefaultPermission.CREATE)||User.getActiveUser().hasPermission(User.registeredPermissions.stream().filter(e->e.name().equals("CREATE_"+group.type.getSimpleName().toUpperCase())).findAny().get());
			if(!canSee&&!canCreate)continue;
			int n=(canSee?group.size():0)+(canCreate?1:0);
			JPanel subTab=new JPanel(null);
			subTab.setOpaque(false);
			subTab.setSize(tab.getWidth()/columns,tab.getHeight());
			JPanel p=WorkTabButton.createTable(n,1,subTab,true);
			if(canSee)for(Editable r:group){
				ActionListener action=new ActionListener(){
					public void actionPerformed(ActionEvent e){
						ProgramStarter.editor.constructEditor(r,false,()->{
							group.remove(r);
							p.remove((JButton)e.getSource());
							int newRows=group.size()+1;
							((GridLayout)p.getLayout()).setRows(newRows);
							p.setPreferredSize(new java.awt.Dimension(subTab.getWidth(),subTab.getHeight()*newRows/7));
							p.setSize(p.getPreferredSize());
							p.revalidate();
						},ctx);
						((JButton)e.getSource()).setText(r.name);
						p.revalidate();
						p.repaint();
					}
				};
				SwingItemRenderingContext itemCtx=new SwingItemRenderingContext(p,action);
				group.renderElementButton(r,itemCtx);
			}
			if(canCreate){
				ActionListener action=new ActionListener(){
					public void actionPerformed(ActionEvent e){
						try{
							JButton addButton=(JButton)e.getSource();
							if(ProgramStarter.editor==null)throw new NullPointerException("Editor cannot be null.");
							Editable nEditable=(Editable)group.type.getDeclaredConstructor().newInstance();
							group.add(nEditable);

							int newRows=group.size()+1;
							((GridLayout)p.getLayout()).setRows(newRows);
							p.setPreferredSize(new java.awt.Dimension(subTab.getWidth(),subTab.getHeight()*newRows/7));
							p.setSize(p.getPreferredSize());

							p.remove(addButton);

							// Create context for the new item button
							ActionListener itemAction=new ActionListener(){
								public void actionPerformed(ActionEvent e){
									ProgramStarter.editor.constructEditor(nEditable,false,()->{
										group.remove(nEditable);
										p.remove((JButton)e.getSource());
										int updatedRows=group.size()+1;
										((GridLayout)p.getLayout()).setRows(updatedRows);
										p.setPreferredSize(new java.awt.Dimension(subTab.getWidth(),subTab.getHeight()*updatedRows/7));
										p.setSize(p.getPreferredSize());
										p.revalidate();
										p.repaint();
									},ctx);
									((JButton)e.getSource()).setText(nEditable.name);
									p.revalidate();
									p.repaint();
								}
							};
							SwingItemRenderingContext itemCtx=new SwingItemRenderingContext(p,itemAction);
							group.renderElementButton(nEditable,itemCtx);

							// Get the button we just added (it's the last one in p)
							java.awt.Component btn=p.getComponent(p.getComponentCount()-1);

							p.add(addButton);

							ProgramStarter.editor.constructEditor(nEditable,true,()->{
								group.remove(nEditable);
								p.remove(btn);
								int updatedRows=group.size()+1;
								((GridLayout)p.getLayout()).setRows(updatedRows);
								p.setPreferredSize(new java.awt.Dimension(subTab.getWidth(),subTab.getHeight()*updatedRows/7));
								p.setSize(p.getPreferredSize());
								p.revalidate();
								p.repaint();
							},ctx);

							if(btn instanceof JButton) ((JButton)btn).setText(nEditable.name);

							p.revalidate();
							p.repaint();
						}catch(ReflectiveOperationException ex){throw new IllegalStateException("Editable implementations must be passed as a `type` argument and have a default constructor.",ex);}
					}
				};
				SwingItemRenderingContext itemCtx=new SwingItemRenderingContext(p,action);
				group.renderAddButton(itemCtx);
			}
			tab.add(subTab);
		}catch(NoSuchElementException ex){throw new NoSuchElementException("Permission for "+group.type+" not found. You must define READ_"+group.type.getSimpleName().toUpperCase()+" and CREATE_"+group.type.getSimpleName().toUpperCase()+" permissions.",ex);}
		tab.setLayout(new GridLayout(1,0));
		tab.revalidate();
		tab.repaint(); 
	}
	public ModelEditing getTransmissionContract(){
		return contract;
	}
	public void render(FeatureRenderingContext context){
		SwingFeatureRenderingContext ctx=(SwingFeatureRenderingContext)context;
		JPanel content=ctx.getTarget();
		JPanel tab=new JPanel(null);
		tab.setSize(content.getSize());
		Font font=new Font(Font.DIALOG,Font.PLAIN,content.getHeight()/30);
		fillTab(content,tab,font,context);
		content.add(tab);
		content.revalidate();
	}
	public void renderPreview(FeatureRenderingContext context){
		SwingPreviewRenderingContext ctx=(SwingPreviewRenderingContext)context;
		Graphics2D g2=ctx.getPreviewTarget().createGraphics();
		paint(g2,ctx.getPreviewTarget(),ctx.getPreviewTarget().getHeight());
		g2.dispose();
	}
}
