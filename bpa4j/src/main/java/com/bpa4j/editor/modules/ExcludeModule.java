package com.bpa4j.editor.modules;

import java.util.Arrays;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JPanel;

import com.bpa4j.core.Data.Editable;

public class ExcludeModule implements EditorModule{
	private final EditorModule module;
	List<Class<? extends Editable>>types;
	@SafeVarargs
	public ExcludeModule(EditorModule module,Class<? extends Editable>...types){this.types=Arrays.asList(types);this.module=module;}
	public JPanel createTab(JDialog editor,Editable editable,boolean isNew,Runnable deleter){
		boolean flag=false;
		for(Class<? extends Editable>m:types)if(m.isAssignableFrom(editable.getClass()))flag=true;
        return flag?null:module.createTab(editor,editable,isNew,deleter);
	}
}
