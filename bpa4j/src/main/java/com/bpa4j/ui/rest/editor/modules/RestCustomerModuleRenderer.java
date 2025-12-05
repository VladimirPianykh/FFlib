package com.bpa4j.ui.rest.editor.modules;

import java.time.temporal.ChronoUnit;
import com.bpa4j.core.Editable;
import com.bpa4j.core.Editable.ActionRecord;
import com.bpa4j.editor.ModularEditorRenderer.ModulesRenderingContext;
import com.bpa4j.editor.ModuleRenderer;
import com.bpa4j.editor.modules.CustomerModule;
import com.bpa4j.ui.rest.RestModularEditorRenderer.RestModulesRenderingContext;
import com.bpa4j.ui.rest.abstractui.Panel;
import com.bpa4j.ui.rest.abstractui.components.Button;
import com.bpa4j.ui.rest.abstractui.components.Label;
import com.bpa4j.ui.rest.abstractui.components.TextField;
import com.bpa4j.ui.rest.abstractui.layout.BorderLayout;
import com.bpa4j.ui.rest.abstractui.layout.FlowLayout;
import com.bpa4j.ui.rest.abstractui.layout.GridLayout;

/**
 * REST renderer for CustomerModule - displays customer info and purchase history
 * @author AI-generated
 */
public class RestCustomerModuleRenderer implements ModuleRenderer<CustomerModule>{
	public void createTab(Editable editable,boolean isNew,Runnable deleter,CustomerModule module,ModulesRenderingContext context){
		RestModulesRenderingContext ctx=(RestModulesRenderingContext)context;
		Panel container=ctx.getTarget();
		container.removeAll();
		container.setLayout(new BorderLayout());
		
		// Name field
		Panel namePanel=new Panel(new FlowLayout());
		namePanel.setSize(container.getWidth(),40);
		namePanel.add(new Label("Name:"));
		TextField nameField=new TextField(editable.name!=null?editable.name:"");
		nameField.setSize(200,30);
		namePanel.add(nameField);
		
		// Purchase history
		Panel historyPanel=new Panel(new GridLayout(0,1,5,5));
		historyPanel.setSize(container.getWidth(),300);
		
		boolean hasPurchases=false;
		for(ActionRecord r:editable.records){
			if(r.text.startsWith(">PAY:")){
				hasPurchases=true;
				String purchaseInfo="Paid "+r.text.substring(5)+" on "+r.time.toLocalDate()+" at "+r.time.toLocalTime().truncatedTo(ChronoUnit.MINUTES).toString();
				historyPanel.add(new Label(purchaseInfo));
			}
		}
		
		if(!hasPurchases){
			historyPanel.add(new Label("No purchase history"));
		}
		
		// Buttons
		Panel buttonPanel=new Panel(new FlowLayout());
		buttonPanel.setSize(container.getWidth(),40);
		
		Button okBtn=new Button("OK");
		okBtn.setOnClick(b->{
			if(!nameField.getText().isBlank()){
				editable.name=nameField.getText();
				ctx.close();
			}
		});
		buttonPanel.add(okBtn);
		
		if(deleter!=null){
			Button deleteBtn=new Button("Delete");
			deleteBtn.setOnClick(b->{
				deleter.run();
				ctx.close();
			});
			buttonPanel.add(deleteBtn);
		}
		
		BorderLayout layout=(BorderLayout)container.getLayout();
		layout.addLayoutComponent(namePanel,BorderLayout.NORTH);
		layout.addLayoutComponent(historyPanel,BorderLayout.CENTER);
		layout.addLayoutComponent(buttonPanel,BorderLayout.SOUTH);
		
		container.add(namePanel);
		container.add(historyPanel);
		container.add(buttonPanel);
	}
}
