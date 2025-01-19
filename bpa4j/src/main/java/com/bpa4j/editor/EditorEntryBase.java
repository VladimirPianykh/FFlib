package com.bpa4j.editor;

import java.lang.reflect.Field;
import java.util.function.Supplier;

import javax.swing.JComponent;

import com.bpa4j.Wrapper;
import com.bpa4j.core.EditableDemo;

public interface EditorEntryBase{
    /**
     * @param o - owner of the field
     * @param f - field to create editor for
     * @param saver - {@code Wrapper} which {@code var} field must be set to supplier that returns the updated field value
     * @param demo - {@code EditableDemo} link that <b>cannot be used during creation</b>
     */
    JComponent createEditorBase(Object o,Field f,Wrapper<Supplier<?>>saver,Wrapper<EditableDemo>demo);
}
