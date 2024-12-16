package com.futurefactory.editor;

import javax.swing.JComponent;

import com.futurefactory.EditableDemo;

public interface InfoProvider{
    public JComponent provideInfo(EditableDemo editable);
}
