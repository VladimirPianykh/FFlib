package com.bpa4j.editor.modules;

import java.util.Arrays;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JPanel;

import com.bpa4j.core.Data.Editable;

public class LimitToModule implements EditorModule{
    private EditorModule module;
    List<Class<? extends Editable>>types;
    @SafeVarargs
	public LimitToModule(EditorModule module,Class<? extends Editable>...types){this.types=Arrays.asList(types);this.module=module;}
	public JPanel createTab(JDialog editor,Editable editable,boolean isNew,Runnable deleter){
		return types.contains(editable.getClass())?module.createTab(editor,editable,isNew,deleter):null;
    }
}
