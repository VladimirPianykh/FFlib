package com.bpa4j.module;

import com.bpa4j.core.Data;
import com.bpa4j.editor.modules.EditorModule;
import com.bpa4j.editor.modules.MapModule;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MapModuleTest {
    @Test
    public void test() {
        var moduleMap = new HashMap<Class<?>, EditorModule>();
        NotDefaultModule notDefMod = new NotDefaultModule();
        moduleMap.put(TestClass.class, notDefMod);

        DefaultModule defaultModule = new DefaultModule();
        var mapModule = new MapModule(defaultModule, moduleMap);

        assertEquals(defaultModule, mapModule.getDefaultModule());
        //specified
        assertEquals(notDefMod, mapModule.getModule(TestClass.class));
        //not specified
        assertEquals(defaultModule, mapModule.getModule(MapModuleTest.class));
    }
}

class TestClass {
}

class DefaultModule implements EditorModule {
    @Override
    public JPanel createTab(JDialog editor, Data.Editable editable, boolean isNew, Runnable deleter) {
        return null;
    }
}

class NotDefaultModule implements EditorModule {
    @Override
    public JPanel createTab(JDialog editor, Data.Editable editable, boolean isNew, Runnable deleter) {
        return null;
    }
}
