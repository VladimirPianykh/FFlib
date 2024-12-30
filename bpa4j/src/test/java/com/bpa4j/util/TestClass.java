package com.bpa4j.util;

import com.bpa4j.editor.EditorEntry;

import java.time.LocalDate;
import java.util.Objects;

public class TestClass {
    @EditorEntry(translation = "перечисление")
    public TestEnum enumField;
    @EditorEntry(translation = "дата")
    public LocalDate date;
    @EditorEntry(translation = "текст")
    public String text;
    @EditorEntry(translation = "действительное число")
    public float floatVal;

    public TestClass(TestEnum enumField, LocalDate date, String text, float floatVal) {
        this.enumField = enumField;
        this.date = date;
        this.text = text;
        this.floatVal = floatVal;
    }

    public TestClass() {
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        TestClass testClass = (TestClass) o;
        return Float.compare(floatVal, testClass.floatVal) == 0 && enumField == testClass.enumField && Objects.equals(date, testClass.date) && Objects.equals(text, testClass.text);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(enumField);
        result = 31 * result + Objects.hashCode(date);
        result = 31 * result + Objects.hashCode(text);
        result = 31 * result + Float.hashCode(floatVal);
        return result;
    }

    @Override
    public String toString() {
        return "TestClass{" +
                "enumField=" + enumField +
                ", date=" + date +
                ", text='" + text + '\'' +
                ", floatVal=" + floatVal +
                '}';
    }
}