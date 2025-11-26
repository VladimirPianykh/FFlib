package com.bpa4j.ui.rest.editor.modules;

import com.bpa4j.core.Editable;
import com.bpa4j.core.User;
import com.bpa4j.defaults.editables.Processable;
import com.bpa4j.defaults.editables.Processable.Stage;
import com.bpa4j.editor.ModularEditorRenderer.ModulesRenderingContext;
import com.bpa4j.editor.ModuleRenderer;
import com.bpa4j.editor.modules.StageMapModule;
import com.bpa4j.ui.rest.RestModularEditorRenderer.RestModulesRenderingContext;
import com.bpa4j.ui.rest.abstractui.Panel;
import com.bpa4j.ui.rest.abstractui.components.Button;
import com.bpa4j.ui.rest.abstractui.components.Label;
import com.bpa4j.ui.rest.abstractui.components.TextArea;
import com.bpa4j.ui.rest.abstractui.layout.FlowLayout;

public class RestStageMapModuleRenderer implements ModuleRenderer<StageMapModule>{
	public void createTab(Editable editable,boolean isNew,Runnable deleter,StageMapModule module,ModulesRenderingContext ctx){
		if(!(editable instanceof Processable))throw new IllegalArgumentException("StageMapModule is only for Processables.");
		Processable p=(Processable)editable;
		RestModulesRenderingContext rctx=(RestModulesRenderingContext)ctx;
		Panel container=rctx.getTarget();
		container.removeAll();
		container.setLayout(new FlowLayout(FlowLayout.LEFT,FlowLayout.TTB,10,10));
		Panel stages=new Panel(new FlowLayout(FlowLayout.LEFT,FlowLayout.TTB,10,10));
		for(int i=0;i<p.stages.length;++i){
			Stage s=p.stages[i];
			String prefix=i<p.currentStage?"\u2713 ":i==p.currentStage?"\u25cf ":"\u25cb ";
			Label l=new Label(prefix+s.name);
			stages.add(l);
		}
		container.add(stages);
		Label commentLabel=new Label("Comment:");
		TextArea comment=new TextArea("");
		container.add(commentLabel);
		container.add(comment);
		boolean canApprove=!p.isLastStage()&&User.getActiveUser().hasPermission(p.getStage().approver);
		boolean canReject=p.getStage().rejecter!=null&&User.getActiveUser().hasPermission(p.getStage().rejecter);
		if(canApprove){
			Button approve=new Button("Approve");
			approve.setOnClick(b->{
				if(p.approve(comment.getText()))rctx.getBase().rebuild();
			});
			container.add(approve);
		}
		if(canReject){
			Button reject=new Button("Reject");
			reject.setOnClick(b->{
				p.reject(comment.getText());
				rctx.getBase().rebuild();
			});
			container.add(reject);
		}
	}
}
