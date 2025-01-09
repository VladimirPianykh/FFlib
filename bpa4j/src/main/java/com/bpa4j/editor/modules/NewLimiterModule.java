package com.bpa4j.editor.modules;

import javax.swing.JDialog;
import javax.swing.JPanel;

import com.bpa4j.core.Data.Editable;

public class NewLimiterModule implements EditorModule{
	private final EditorModule module;
	private final boolean isNew;
	public NewLimiterModule(EditorModule module,boolean isNew){this.module=module;this.isNew=isNew;}
	public JPanel createTab(JDialog editor,Editable editable,boolean isNew,Runnable deleter){
		if(this.isNew==isNew)return module.createTab(editor,editable,isNew,deleter);
		return null;
	}
}
