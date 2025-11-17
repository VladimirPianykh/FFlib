package com.bpa4j.editor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import com.bpa4j.core.Editable;
import com.bpa4j.defaults.editables.AbstractCustomer;
import com.bpa4j.editor.ModularEditorRenderer.ModulesRenderingContext;
import com.bpa4j.editor.modules.CustomerModule;
import com.bpa4j.editor.modules.FormModule;
import com.bpa4j.editor.modules.MapModule;
import com.bpa4j.feature.FeatureRenderingContext;

public class ModularEditor implements IEditor{
	private ArrayList<EditorModule> modules=new ArrayList<>();
	public ModularEditor(){
		HashMap<Class<?>,EditorModule> map=new HashMap<>();
		map.put(AbstractCustomer.class,new CustomerModule());
		modules.add(new MapModule(new FormModule(),map));
	}
	public ModularEditor(EditorModule...modules){
		List<EditorModule> moduleList=Arrays.asList(modules);
		moduleList.forEach(Objects::requireNonNull);
		this.modules.addAll(moduleList);
	}
	public void constructEditor(Editable editable,boolean isNew,Runnable deleter,FeatureRenderingContext ctx){
		ModularEditorRenderer r=(ModularEditorRenderer)getRenderer();
		ModulesRenderingContext mctx=r.getModulesRenderingContext(ctx);
		for(EditorModule m:modules){
			m.setRendererSource(e->r.getModuleRenderer(e));
			m.createTab(editable,isNew,deleter,mctx);
		}
		r.constructEditor(editable,isNew,deleter,this,ctx,mctx);
	}
	public ModularEditorRenderer getRenderer(){
		return (ModularEditorRenderer)IEditor.super.getRenderer();
	}
	public List<EditorModule> getModules(){
		return modules;
	}
}
