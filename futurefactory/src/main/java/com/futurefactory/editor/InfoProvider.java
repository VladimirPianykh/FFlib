package com.futurefactory.editor;

import javax.swing.JComponent;

import com.futurefactory.core.EditableDemo;

public interface InfoProvider{
    public JComponent provideInfo(EditableDemo editable);
}
