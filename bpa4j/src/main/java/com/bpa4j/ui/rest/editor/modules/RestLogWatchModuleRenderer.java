package com.bpa4j.ui.rest.editor.modules;

import java.time.format.DateTimeFormatter;
import com.bpa4j.core.Editable;
import com.bpa4j.core.Editable.ActionRecord;
import com.bpa4j.editor.ModularEditorRenderer.ModulesRenderingContext;
import com.bpa4j.editor.ModuleRenderer;
import com.bpa4j.editor.modules.LogWatchModule;
import com.bpa4j.ui.rest.RestModularEditorRenderer.RestModulesRenderingContext;
import com.bpa4j.ui.rest.abstractui.Panel;
import com.bpa4j.ui.rest.abstractui.components.Label;
import com.bpa4j.ui.rest.abstractui.layout.GridLayout;

/**
 * REST renderer for LogWatchModule - displays action records log
 * @author AI-generated
 */
public class RestLogWatchModuleRenderer implements ModuleRenderer<LogWatchModule>{
	public void createTab(Editable editable,boolean isNew,Runnable deleter,LogWatchModule module,ModulesRenderingContext context){
		RestModulesRenderingContext ctx=(RestModulesRenderingContext)context;
		Panel container=ctx.getTarget();
		container.removeAll();
		container.setLayout(new GridLayout(0,1,5,5));
		
		if(editable.records.isEmpty()){
			container.add(new Label("No records"));
		}else{
			for(ActionRecord r:editable.records){
				Panel recordPanel=new Panel(new GridLayout(1,2,5,5));
				recordPanel.setSize(container.getWidth(),30);
				
				// Format record text
				String recordText=formatRecordText(r);
				recordPanel.add(new Label(recordText));
				
				// Add timestamp
				String time=r.time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
				recordPanel.add(new Label(time));
				
				container.add(recordPanel);
			}
		}
	}
	
	private String formatRecordText(ActionRecord r){
		if(r.text.startsWith(":")){
			String[]parts=r.text.split(":");
			if(parts.length>1){
				switch(parts[1]){
					case "CREATED": return "CREATED";
					case "DELETED": return "DELETED";
				}
			}
		}else if(r.text.startsWith("+")){
			return "Stage "+r.text.substring(1)+" completed";
		}else if(r.text.startsWith("-")){
			return "Rolled back to stage "+r.text.substring(1);
		}else if(r.text.startsWith(">")){
			return r.text.substring(1);
		}
		return r.text;
	}
}
