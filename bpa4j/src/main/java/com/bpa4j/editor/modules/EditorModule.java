package com.bpa4j.editor.modules;

import javax.swing.JDialog;
import javax.swing.JPanel;

import com.bpa4j.core.Data.Editable;

public interface EditorModule{
	public default JPanel createTab(JDialog editor,Editable editable,boolean isNew){return createTab(editor,editable,isNew,null);}
	public JPanel createTab(JDialog editor,Editable editable,boolean isNew,Runnable deleter);
}
