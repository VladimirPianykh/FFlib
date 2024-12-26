package com.futurefactory.util;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class ExcelUtilsTest {

    @Test
    void test() throws IOException, InterruptedException, IllegalAccessException {
        ArrayList<TestClass> vals = new ArrayList<>();
        vals.add(new TestClass(TestEnum.ONE, LocalDate.now(), "Text1", 3.14f));
        vals.add(new TestClass(TestEnum.TWO, LocalDate.now(), "", 3.14f));
        vals.add(new TestClass(TestEnum.THREE, LocalDate.now(), "24.09.2004", 3f));

        String home = System.getProperty("user.home");
        String outputPath = home + "\\Downloads\\" + "ExcelDataFFLib.xlsx";
        Desktop desktop = Desktop.getDesktop();

        File result = ExcelUtils.saveInstances(new File(outputPath), vals);
        desktop.open(result);

        var resultList = ExcelUtils.createInstancesOf(result.getPath(), TestClass.class);

        assertEquals(vals.size(), resultList.size(), "Количество исходных объектов не равно итоговому");
        for (int i = 0; i < vals.size(); i++) {
            assertEquals(vals.get(i), resultList.get(i));
            System.out.println("Given: " + vals.get(i));
            System.out.println("Parsed: " + resultList.get(i));
        }

        Thread.sleep(Duration.of(3, ChronoUnit.SECONDS)); // Наслаждаемся 3 секунды
        result.delete(); // Удаляем
    }
}