package com.futurefactory.editor.modules;

import javax.swing.JDialog;
import javax.swing.JPanel;

import com.futurefactory.Data.Editable;

public interface IEditorModule{
	public JPanel createTab(JDialog editor,Editable editable,boolean isNew);
}
