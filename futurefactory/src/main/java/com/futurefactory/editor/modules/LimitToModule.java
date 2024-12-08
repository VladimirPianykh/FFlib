package com.futurefactory.editor.modules;

import java.util.Arrays;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JPanel;

import com.futurefactory.Data.Editable;

public class LimitToModule implements IEditorModule{
    private IEditorModule module;
    List<Class<? extends Editable>>types;
    @SafeVarargs
	public LimitToModule(IEditorModule module,Class<? extends Editable>...types){this.types=Arrays.asList(types);this.module=module;}
	public JPanel createTab(JDialog editor,Editable editable,boolean isNew){
		return types.contains(editable.getClass())?module.createTab(editor,editable,isNew):null;
    }
}
