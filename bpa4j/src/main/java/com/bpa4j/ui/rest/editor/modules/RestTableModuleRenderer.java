package com.bpa4j.ui.rest.editor.modules;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import com.bpa4j.core.Editable;
import com.bpa4j.editor.EditorEntry;
import com.bpa4j.editor.ModularEditorRenderer.ModulesRenderingContext;
import com.bpa4j.editor.ModuleRenderer;
import com.bpa4j.editor.modules.TableModule;
import com.bpa4j.ui.rest.RestModularEditorRenderer.RestModulesRenderingContext;
import com.bpa4j.ui.rest.abstractui.Panel;
import com.bpa4j.ui.rest.abstractui.components.Button;
import com.bpa4j.ui.rest.abstractui.components.Label;
import com.bpa4j.ui.rest.abstractui.components.TextField;
import com.bpa4j.ui.rest.abstractui.layout.BorderLayout;
import com.bpa4j.ui.rest.abstractui.layout.GridLayout;

/**
 * REST renderer for TableModule - displays editable table of objects
 * @author AI-generated
 */
public class RestTableModuleRenderer implements ModuleRenderer<TableModule>{
	public void createTab(Editable editable,boolean isNew,Runnable deleter,TableModule module,ModulesRenderingContext context){
		RestModulesRenderingContext ctx=(RestModulesRenderingContext)context;
		Panel container=ctx.getTarget();
		container.removeAll();
		container.setLayout(new BorderLayout());
		
		try{
			@SuppressWarnings("unchecked")
			ArrayList<Object>items=(ArrayList<Object>)module.getField().get(editable);
			
			List<Field>fields=Stream.of(module.getType().getFields())
				.filter(f->f.isAnnotationPresent(EditorEntry.class))
				.toList();
			
			// Create table panel
			Panel tablePanel=new Panel(new GridLayout(0,fields.size()+1,5,5));
			tablePanel.setSize(container.getWidth(),400);
			
			// Header row
			for(Field f:fields){
				String fieldName=f.getAnnotation(EditorEntry.class).translation();
				tablePanel.add(new Label(fieldName));
			}
			tablePanel.add(new Label("Actions"));
			
			// Data rows
			for(Object item:items){
				for(Field f:fields){
					try{
						Object value=f.get(item);
						TextField tf=new TextField(value!=null?String.valueOf(value):"");
						tf.setOnTextChanged(newText->{
							try{
								// Simple conversion for basic types
								if(f.getType()==String.class){
									f.set(item,newText);
								}else if(f.getType()==int.class||f.getType()==Integer.class){
									f.set(item,newText.isBlank()?0:Integer.parseInt(newText));
								}
							}catch(Exception e){
								// Ignore conversion errors
							}
						});
						tablePanel.add(tf);
					}catch(IllegalAccessException e){
						tablePanel.add(new Label("N/A"));
					}
				}
				
				// Remove button for each row
				Button removeBtn=new Button("Remove");
				removeBtn.setOnClick(b->{
					items.remove(item);
					createTab(editable,isNew,deleter,module,context); // Refresh
				});
				tablePanel.add(removeBtn);
			}
			
			// Add button
			Panel addPanel=new Panel(new com.bpa4j.ui.rest.abstractui.layout.FlowLayout());
			addPanel.setSize(container.getWidth(),40);
			Button addBtn=new Button("Add");
			addBtn.setOnClick(b->{
				try{
					Object newItem=module.getType().getDeclaredConstructor().newInstance();
					items.add(newItem);
					createTab(editable,isNew,deleter,module,context); // Refresh
				}catch(ReflectiveOperationException ex){
					throw new RuntimeException(ex);
				}
			});
			addPanel.add(addBtn);
			
			BorderLayout layout=(BorderLayout)container.getLayout();
			layout.addLayoutComponent(tablePanel,BorderLayout.CENTER);
			layout.addLayoutComponent(addPanel,BorderLayout.SOUTH);
			
			container.add(tablePanel);
			container.add(addPanel);
			
		}catch(IllegalAccessException ex){
			throw new RuntimeException(ex);
		}
	}
}
