package com.futurefactory.editor;

import com.futurefactory.Data;
import com.futurefactory.Wrapper;

import java.lang.reflect.Field;

import javax.swing.JComponent;

public interface EditorEntryBase {
    JComponent createEditorBase(Data.Editable o,Field f,Wrapper<Runnable>saver);
}
