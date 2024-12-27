package com.futurefactory.editor.modules;

import java.util.Arrays;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JPanel;

import com.futurefactory.core.Data.Editable;

public class ExcludeModule implements EditorModule{
    private EditorModule module;
    List<Class<? extends Editable>>types;
    @SafeVarargs
	public ExcludeModule(EditorModule module,Class<? extends Editable>...types){this.types=Arrays.asList(types);this.module=module;}
	public JPanel createTab(JDialog editor,Editable editable,boolean isNew,Runnable deleter){
		return types.contains(editable.getClass())?null:module.createTab(editor,editable,isNew,deleter);
    }
}
