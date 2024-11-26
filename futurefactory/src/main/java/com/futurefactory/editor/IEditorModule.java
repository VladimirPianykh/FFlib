package com.futurefactory.editor;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.futurefactory.Data.Editable;

public interface IEditorModule{
	public JPanel createTab(JDialog editor,Editable editable,JTextField nameField,JButton ok);
}
