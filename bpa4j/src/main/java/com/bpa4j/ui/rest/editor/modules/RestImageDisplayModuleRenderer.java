package com.bpa4j.ui.rest.editor.modules;

import java.io.File;
import com.bpa4j.core.Editable;
import com.bpa4j.editor.ModularEditorRenderer.ModulesRenderingContext;
import com.bpa4j.editor.ModuleRenderer;
import com.bpa4j.editor.modules.ImageDisplayModule;
import com.bpa4j.ui.rest.RestModularEditorRenderer.RestModulesRenderingContext;
import com.bpa4j.ui.rest.abstractui.Panel;
import com.bpa4j.ui.rest.abstractui.components.Button;
import com.bpa4j.ui.rest.abstractui.components.Label;
import com.bpa4j.ui.rest.abstractui.layout.FlowLayout;

/**
 * REST renderer for ImageDisplayModule - displays image field
 * @author AI-generated
 */
public class RestImageDisplayModuleRenderer implements ModuleRenderer<ImageDisplayModule>{
	public void createTab(Editable editable,boolean isNew,Runnable deleter,ImageDisplayModule module,ModulesRenderingContext context){
		RestModulesRenderingContext ctx=(RestModulesRenderingContext)context;
		Panel container=ctx.getTarget();
		container.removeAll();
		container.setLayout(new FlowLayout());
		
		try{
			var f=module.getField();
			File imageFile=(File)f.get(editable);
			
			Label imageLabel=new Label(imageFile!=null?imageFile.getName():"No image");
			container.add(imageLabel);
			
			Button selectBtn=new Button("Select Image");
			selectBtn.setOnClick(b->{
				// In REST, file selection would need to be handled differently
				// For now, just show a placeholder
				Label info=new Label("File selection not available in REST");
				container.add(info);
				container.update();
			});
			container.add(selectBtn);
			
		}catch(IllegalAccessException ex){
			throw new RuntimeException(ex);
		}
	}
}
