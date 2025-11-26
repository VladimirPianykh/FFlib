package com.bpa4j.ui.rest.editor.modules;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import com.bpa4j.core.Editable;
import com.bpa4j.editor.EditorEntry;
import com.bpa4j.editor.ModularEditorRenderer.ModulesRenderingContext;
import com.bpa4j.editor.ModuleRenderer;
import com.bpa4j.editor.modules.FormModule;
import com.bpa4j.ui.rest.RestModularEditorRenderer.RestModulesRenderingContext;
import com.bpa4j.ui.rest.abstractui.Component;
import com.bpa4j.ui.rest.abstractui.Panel;
import com.bpa4j.ui.rest.abstractui.components.Button;
import com.bpa4j.ui.rest.abstractui.components.CheckBox;
import com.bpa4j.ui.rest.abstractui.components.Label;
import com.bpa4j.ui.rest.abstractui.components.TextField;
import com.bpa4j.ui.rest.abstractui.layout.GridLayout;

public class RestFormModuleRenderer implements ModuleRenderer<FormModule>{
	public void createTab(Editable editable,boolean isNew,Runnable deleter,FormModule module,ModulesRenderingContext ctx){
		RestModulesRenderingContext rctx=(RestModulesRenderingContext)ctx;
		Panel container=rctx.getTarget();
		container.removeAll();
		container.setLayout(new GridLayout(0,2,5,5));
		List<Runnable> savers=new ArrayList<>();
		Label nameLabel=new Label("Name");
		TextField nameField=new TextField(editable.name);
		savers.add(()->editable.name=nameField.getText());
		container.add(nameLabel);
		container.add(nameField);
		for(Field f:editable.getClass().getFields())try{
			EditorEntry a=f.getAnnotation(EditorEntry.class);
			if(a==null)continue;
			boolean hide=false,readonly=false;
			for(String p:a.properties()){
				if("hide".equals(p))hide=true;
				if("readonly".equals(p)||(!isNew&&"initonly".equals(p)))readonly=true;
			}
			if(hide)continue;
			Label fieldLabel=new Label(a.translation());
			container.add(fieldLabel);
			if(readonly){
				Object value=f.get(editable);
				Label valueLabel=new Label(value==null?"":String.valueOf(value));
				container.add(valueLabel);
				continue;
			}
			Component editorComponent=createEditorComponent(editable,f,savers);
			container.add(editorComponent);
		}catch(IllegalAccessException ex){
			throw new IllegalStateException(ex);
		}
		Button ok=new Button("OK");
		ok.setOnClick(b->{
			for(Runnable s:savers)s.run();
			rctx.getBase().rebuild();
		});
		container.add(new Label(""));
		container.add(ok);
	}
	private Component createEditorComponent(Editable editable,Field f,List<Runnable>savers) throws IllegalAccessException{
		Class<?>type=f.getType();
		if(type==String.class){
			String value=(String)f.get(editable);
			TextField tf=new TextField(value);
			savers.add(()->{
				try{
					f.set(editable,tf.getText());
				}catch(IllegalAccessException e){
					throw new IllegalStateException(e);
				}
			});
			return tf;
		}else if(type==boolean.class||type==Boolean.class){
			boolean value=type==boolean.class?f.getBoolean(editable):(Boolean)f.get(editable);
			CheckBox cb=new CheckBox();
			cb.setSelected(value);
			savers.add(()->{
				try{
					if(type==boolean.class)f.setBoolean(editable,cb.isSelected());
					else f.set(editable,Boolean.valueOf(cb.isSelected()));
				}catch(IllegalAccessException e){
					throw new IllegalStateException(e);
				}
			});
			return cb;
		}else{
			Object value=f.get(editable);
			TextField tf=new TextField(value==null?"":String.valueOf(value));
			savers.add(()->{
				try{
					String txt=tf.getText();
					if(type==int.class||type==Integer.class){
						Integer v=txt.isBlank()?null:Integer.parseInt(txt);
						if(type==int.class)f.setInt(editable,v==null?0:v);
						else f.set(editable,v);
					}else if(type==double.class||type==Double.class){
						Double v=txt.isBlank()?null:Double.parseDouble(txt);
						if(type==double.class)f.setDouble(editable,v==null?0d:v);
						else f.set(editable,v);
					}else if(type==float.class||type==Float.class){
						Float v=txt.isBlank()?null:Float.parseFloat(txt);
						if(type==float.class)f.setFloat(editable,v==null?0f:v);
						else f.set(editable,v);
					}else{
						f.set(editable,txt);
					}
				}catch(ReflectiveOperationException|NumberFormatException e){
					throw new IllegalStateException(e);
				}
			});
			return tf;
		}
	}
}
