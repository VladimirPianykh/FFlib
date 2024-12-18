package com.futurefactory.editor.modules;

import javax.swing.JDialog;
import javax.swing.JPanel;

import com.futurefactory.core.Data.Editable;

public interface EditorModule{
	public JPanel createTab(JDialog editor,Editable editable,boolean isNew);
}
