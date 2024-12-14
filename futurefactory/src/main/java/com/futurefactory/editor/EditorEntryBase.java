package com.futurefactory.editor;

import com.futurefactory.Wrapper;

import java.lang.reflect.Field;
import java.util.function.Supplier;

import javax.swing.JComponent;

public interface EditorEntryBase {
    JComponent createEditorBase(Object o,Field f,Wrapper<Supplier<?>>saver);
}
