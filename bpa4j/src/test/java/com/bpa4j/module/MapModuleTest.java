// package com.bpa4j.module;

// import com.bpa4j.core.Editable;
// import com.bpa4j.editor.EditorModule;
// import com.bpa4j.editor.modules.MapModule;
// import org.junit.jupiter.api.Test;

// import javax.swing.*;
// import java.util.HashMap;

// import static org.junit.jupiter.api.Assertions.assertEquals;

// public class MapModuleTest {
//     @Test
//     public void test() {
//         var moduleMap = new HashMap<Class<?>, EditorModule>();
//         NotDefaultModule notDefMod = new NotDefaultModule();
//         moduleMap.put(TestClass.class, notDefMod);

//         DefaultModule defaultModule = new DefaultModule();
//         var mapModule = new MapModule(defaultModule, moduleMap);

//         assertEquals(defaultModule, mapModule.getDefaultModule());
//         //specified
//         assertEquals(notDefMod, mapModule.getModule(TestClass.class));
//         //inherited behavior
//         assertEquals(notDefMod, mapModule.getModule(Inheritor.class));
//         //not specified
//         assertEquals(defaultModule, mapModule.getModule(MapModuleTest.class));
//     }
// }

// class TestClass {
// }

// class Inheritor extends TestClass {}

// class DefaultModule implements EditorModule {
//     @Override
//     public JPanel createTab(JDialog editor, Editable editable, boolean isNew, Runnable deleter) {
//         return null;
//     }
// }

// class NotDefaultModule implements EditorModule {
//     @Override
//     public JPanel createTab(JDialog editor, Editable editable, boolean isNew, Runnable deleter) {
//         return null;
//     }
// }

