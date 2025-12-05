package com.bpa4j.ui.rest.editor.modules;

import com.bpa4j.core.Editable;
import com.bpa4j.core.User;
import com.bpa4j.defaults.editables.Processable;
import com.bpa4j.editor.ModularEditorRenderer.ModulesRenderingContext;
import com.bpa4j.editor.ModuleRenderer;
import com.bpa4j.editor.modules.StageApprovalModule;
import com.bpa4j.ui.rest.RestModularEditorRenderer.RestModulesRenderingContext;
import com.bpa4j.ui.rest.abstractui.Panel;
import com.bpa4j.ui.rest.abstractui.components.Button;
import com.bpa4j.ui.rest.abstractui.components.Label;
import com.bpa4j.ui.rest.abstractui.components.TextField;
import com.bpa4j.ui.rest.abstractui.layout.BorderLayout;
import com.bpa4j.ui.rest.abstractui.layout.FlowLayout;

/**
 * REST renderer for StageApprovalModule - extends FormModule with approval/rejection
 * @author AI-generated
 */
public class RestStageApprovalModuleRenderer implements ModuleRenderer<StageApprovalModule>{
	public void createTab(Editable editable,boolean isNew,Runnable deleter,StageApprovalModule module,ModulesRenderingContext context){
		if(!(editable instanceof Processable))throw new IllegalArgumentException("StageApprovalModule is only for Processables.");
		
		RestModulesRenderingContext ctx=(RestModulesRenderingContext)context;
		Processable p=(Processable)editable;
		
		// First create the form using RestFormModuleRenderer
		RestFormModuleRenderer formRenderer=new RestFormModuleRenderer();
		formRenderer.createTab(editable,isNew,deleter,module,context);
		
		Panel container=ctx.getTarget();
		
		// Add approval controls if user has permission
		if(User.getActiveUser().hasPermission(p.getStage().approver)){
			Panel approvalPanel=new Panel(new BorderLayout());
			approvalPanel.setSize(container.getWidth(),150);
			
			Label stageLabel=new Label("Stage: "+p.getStage().name);
			Panel stageLabelPanel=new Panel(new FlowLayout());
			stageLabelPanel.setSize(approvalPanel.getWidth(),30);
			stageLabelPanel.add(stageLabel);
			
			Panel commentPanel=new Panel(new FlowLayout());
			commentPanel.setSize(approvalPanel.getWidth(),50);
			commentPanel.add(new Label("Comment:"));
			TextField commentField=new TextField("");
			commentField.setSize(300,30);
			commentPanel.add(commentField);
			
			Panel buttonPanel=new Panel(new FlowLayout());
			buttonPanel.setSize(approvalPanel.getWidth(),40);
			
			Button approveBtn=new Button("Approve");
			approveBtn.setOnClick(b->{
				p.approve(commentField.getText());
				ctx.close();
			});
			buttonPanel.add(approveBtn);
			
			if(User.getActiveUser().hasPermission(p.getStage().rejecter)){
				Button rejectBtn=new Button("Reject");
				rejectBtn.setOnClick(b->{
					p.reject(commentField.getText());
					ctx.close();
				});
				buttonPanel.add(rejectBtn);
			}
			
			BorderLayout layout=(BorderLayout)approvalPanel.getLayout();
			layout.addLayoutComponent(stageLabelPanel,BorderLayout.NORTH);
			layout.addLayoutComponent(commentPanel,BorderLayout.CENTER);
			layout.addLayoutComponent(buttonPanel,BorderLayout.SOUTH);
			
			approvalPanel.add(stageLabelPanel);
			approvalPanel.add(commentPanel);
			approvalPanel.add(buttonPanel);
			
			container.add(approvalPanel);
		}
	}
}
