package com.bpa4j.editor;

import javax.swing.JComponent;

import com.bpa4j.core.Data.Editable;
import com.bpa4j.core.EditableDemo;

public interface InfoProvider{
    public JComponent provideInfo(Editable original,EditableDemo editable);
}
