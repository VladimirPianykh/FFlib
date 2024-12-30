package com.bpa4j.editor;

import javax.swing.JComponent;

import com.bpa4j.core.EditableDemo;

public interface InfoProvider{
    public JComponent provideInfo(EditableDemo editable);
}
