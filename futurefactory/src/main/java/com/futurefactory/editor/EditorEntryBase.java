package com.futurefactory.editor;

import java.lang.reflect.Field;
import java.util.function.Supplier;

import javax.swing.JComponent;

import com.futurefactory.Wrapper;

public interface EditorEntryBase {
    JComponent createEditorBase(Object o,Field f,Wrapper<Supplier<?>>saver);
}
