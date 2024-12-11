package com.futurefactory.editor;

import com.futurefactory.Wrapper;

import java.lang.reflect.Field;

import javax.swing.JComponent;

public interface EditorEntryBase {
    JComponent createEditorBase(Object o,Field f,Wrapper<Runnable>saver);
}
