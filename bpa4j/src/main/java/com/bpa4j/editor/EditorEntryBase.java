package com.bpa4j.editor;

import java.lang.reflect.Field;
import java.util.function.Supplier;

import javax.swing.JComponent;

import com.bpa4j.Wrapper;

public interface EditorEntryBase {
    JComponent createEditorBase(Object o,Field f,Wrapper<Supplier<?>>saver);
}
