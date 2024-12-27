package com.futurefactory.editor.modules;

import javax.swing.JDialog;
import javax.swing.JPanel;

import com.futurefactory.core.Data.Editable;

public interface EditorModule{
	public default JPanel createTab(JDialog editor,Editable editable,boolean isNew){return createTab(editor,editable,isNew,null);}
	public JPanel createTab(JDialog editor,Editable editable,boolean isNew,Runnable deleter);
}
