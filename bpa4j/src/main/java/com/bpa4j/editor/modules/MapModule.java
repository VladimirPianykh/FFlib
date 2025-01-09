package com.bpa4j.editor.modules;

import com.bpa4j.core.Data;

import javax.swing.*;
import java.util.Map;

public class MapModule implements EditorModule {
    private final EditorModule defaultModule;
    private final Map<Class<?>, EditorModule> moduleClassMap;

    public MapModule(EditorModule defaultModule, Map<Class<?>, EditorModule> moduleClassMap) {
        this.defaultModule = defaultModule;
        this.moduleClassMap = moduleClassMap;
    }

    public EditorModule getDefaultModule() {
        return defaultModule;
    }

    public EditorModule getModule(Class<?> clazz) {
        return moduleClassMap.getOrDefault(clazz, defaultModule);
    }

    @Override
    public JPanel createTab(JDialog editor, Data.Editable editable, boolean isNew, Runnable deleter) {
        return getModule(editable.getClass()).createTab(editor, editable, isNew, deleter);
    }
}
