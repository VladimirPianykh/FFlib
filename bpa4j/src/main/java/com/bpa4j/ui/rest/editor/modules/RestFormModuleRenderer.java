package com.bpa4j.ui.rest.editor.modules;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import com.bpa4j.Wrapper;
import com.bpa4j.core.Editable;
import com.bpa4j.core.EditableDemo;
import com.bpa4j.defaults.input.ConditionalWEditor;
import com.bpa4j.defaults.input.FlagWEditor;
import com.bpa4j.defaults.input.FunctionEditor;
import com.bpa4j.defaults.input.SelectFromEditor;
import com.bpa4j.defaults.input.SelectionListEditor;
import com.bpa4j.editor.EditorEntry;
import com.bpa4j.editor.EditorEntryBase;
import com.bpa4j.editor.EditorEntryBaseRenderer;
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
import com.bpa4j.ui.rest.editor.RestConditionalWEditorRenderer;
import com.bpa4j.ui.rest.editor.RestFlagWEditorRenderer;
import com.bpa4j.ui.rest.editor.RestFunctionEditorRenderer;
import com.bpa4j.ui.rest.editor.RestSelectFromEditorRenderer;
import com.bpa4j.ui.rest.editor.RestSelectionListEditorRenderer;

/**
 * @author AI-generated
 */
public class RestFormModuleRenderer implements ModuleRenderer<FormModule>{
	// Renderer registry for EditorEntryBase implementations
	private static final HashMap<Class<? extends EditorEntryBase>,EditorEntryBaseRenderer> editorRenderers=new HashMap<>();
	static{
		editorRenderers.put(FlagWEditor.class,new RestFlagWEditorRenderer());
		editorRenderers.put(ConditionalWEditor.class,new RestConditionalWEditorRenderer());
		editorRenderers.put(FunctionEditor.class,new RestFunctionEditorRenderer());
		editorRenderers.put(SelectFromEditor.class,new RestSelectFromEditorRenderer());
		editorRenderers.put(SelectionListEditor.class,new RestSelectionListEditorRenderer());
	}
	// EditorEntryRenderingContext implementation
	public static class RestEditorEntryRenderingContext implements EditorEntryBase.EditorEntryRenderingContext{
		private Panel target;
		public RestEditorEntryRenderingContext(Panel target){
			this.target=target;
		}
		public EditorEntryBaseRenderer getRenderer(EditorEntryBase base){
			EditorEntryBaseRenderer renderer=editorRenderers.get(base.getClass());
			if(renderer==null) throw new IllegalStateException("No renderer registered for "+base.getClass());
			return renderer;
		}
		public void addComponent(Component c){
			target.add(c);
		}
	}
	public void createTab(Editable editable,boolean isNew,Runnable deleter,FormModule module,ModulesRenderingContext ctx){
		RestModulesRenderingContext rctx=(RestModulesRenderingContext)ctx;
		Panel container=rctx.getTarget();
		container.removeAll();
		container.setLayout(new GridLayout(0,2,5,5));
		List<Supplier<?>> savers=new ArrayList<>();
		Label nameLabel=new Label("Name");
		TextField nameField=new TextField(editable.name);
		savers.add(()->editable.name=nameField.getText());
		container.add(nameLabel);
		container.add(nameField);
		ArrayList<Field> editableFields=new ArrayList<>();
		for(Field f:editable.getClass().getFields())try{
			EditorEntry a=f.getAnnotation(EditorEntry.class);
			if(a==null)continue;
			editableFields.add(f);
			boolean hide=false,readonly=false;
			for(String p:a.properties()){ //Handling entry flags
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
			Component editorComponent;
			if(a.editorBaseSource()==EditorEntryBase.class){
				Wrapper<Supplier<?>> nextSaver=new Wrapper<>(null);
				editorComponent=createEditorComponent(editable,f,nextSaver);
				savers.add(nextSaver.var);
			}else{
				try{
					EditorEntryBase editorBase=a.editorBaseSource().getDeclaredConstructor().newInstance();
					Panel target=new Panel();
					EditorEntryBase.EditorEntryRenderingContext renderingContext=new RestEditorEntryRenderingContext(target);
					Wrapper<Supplier<?>> saver=new Wrapper<>(null);
					Wrapper<EditableDemo> demo=new Wrapper<>(null);
					editorBase.renderEditorBase(editable,f,saver,demo,renderingContext);
					// Extract the component from the target panel
					editorComponent=target.getComponents().isEmpty()?null:target.getComponents().get(0);
					Objects.requireNonNull(saver.var,"All entry editors must set a saver.");
					savers.add(saver.var);
				}catch(ReflectiveOperationException e){
					throw new IllegalStateException(e);
				}
			}
			container.add(editorComponent);
		}catch(IllegalAccessException ex){
			throw new IllegalStateException(ex);
		}
		Button ok=new Button("OK");
		ok.setOnClick(b->{
			for(int i=0;i<editableFields.size();++i)try{
				editableFields.get(i).set(editable,savers.get(i).get());
			}catch(ReflectiveOperationException ex){
				throw new IllegalStateException(ex);
			}
			rctx.getBase().rebuild();
		});
		container.add(new Label(""));
		container.add(ok);
	}
	public static Component createEditorComponent(Object o,Field f,Wrapper<Supplier<?>> saver) throws IllegalAccessException{
		Class<?>type=f.getType();
		if(type==String.class){
			String value=(String)f.get(o);
			TextField tf=new TextField(value);
			saver.var=()->tf.getText();
			return tf;
		}else if(type==boolean.class||type==Boolean.class){
			boolean value=type==boolean.class?f.getBoolean(o):(Boolean)f.get(o);
			CheckBox cb=new CheckBox();
			cb.setSelected(value);
			saver.var=()->cb.isSelected();
			return cb;
		}else{
			Object value=f.get(o);
			TextField tf=new TextField(value==null?"":String.valueOf(value));
			saver.var=()->{
				try{
					String txt=tf.getText();
					if(type==int.class||type==Integer.class){
						Integer v=txt.isBlank()?null:Integer.parseInt(txt);
						return type==int.class?(v==null?Integer.valueOf(0):v):v;
					}else if(type==double.class||type==Double.class){
						Double v=txt.isBlank()?null:Double.parseDouble(txt);
						return type==double.class?(v==null?Double.valueOf(0d):v):v;
					}else if(type==float.class||type==Float.class){
						Float v=txt.isBlank()?null:Float.parseFloat(txt);
						return type==float.class?(v==null?Float.valueOf(0f):v):v;
					}else return txt;
				}catch(NumberFormatException e){
					throw new IllegalStateException(e);
				}
			};
			return tf;
		}
	}
}
