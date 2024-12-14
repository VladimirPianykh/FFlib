package com.futurefactory.exel;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Класс для парсинга exel (xls/xlsx) файла в список Java объектов
 * </p>
 * Пример:
 * <pre>
 * {@code
 * List<SomeClass> parsedObjects = ExcelUtils.<SomeClass>createInstancesOf("file.xlsx", SomeClass.class)
 * }
 * </pre>
 */
public class ExcelUtils {
    /**
     * Через рефлексию создает список объектов заданного класса из указанного файла
     * </p>
     * Пример:
     * <pre>
     * {@code
     * List<SomeClass> parsedObjects = ExcelUtils.<SomeClass>createInstancesOf("file.xlsx", SomeClass.class)
     * }
     * </pre>
     *
     * @param path путь к файлу
     * @param type класс, объекты которого будут создаваться
     * @param <T>  тот же тип класса(нужен для создания списка)
     * @return список созданных объектов
     */
    public static <T> ArrayList<T> createInstancesOf(String path, Class<T> type) {
        ArrayList<T> instances = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(path)) {
            Workbook workbook = new XSSFWorkbook(fis);

            Sheet sheet = workbook.getSheetAt(0); // Читаем первый лист Excel
            Iterator<Row> rowIterator = sheet.iterator();

            // Пропускаем заголовок, если он есть
            if (rowIterator.hasNext()) {
                rowIterator.next();
            }

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                T instance = type.getDeclaredConstructor().newInstance();

                Field[] fields = type.getDeclaredFields();
                for (int i = 0; i < fields.length; i++) {
                    Field field = fields[i];
                    field.setAccessible(true);

                    Cell cell = row.getCell(i, Row.CREATE_NULL_AS_BLANK);
                    Object value = parseCellValue(cell, field.getType());
                    field.set(instance, value);
                }

                instances.add(instance);
            }

        } catch (IOException | ReflectiveOperationException e) {
            e.printStackTrace();
        }

        return instances;
    }

    private static Object parseCellValue(Cell cell, Class<?> targetType) {
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_STRING -> {
                return cell.getStringCellValue();
            }
            case Cell.CELL_TYPE_NUMERIC -> {
                if (DateUtil.isCellDateFormatted(cell) && targetType == LocalDate.class) {
                    return LocalDate.ofInstant(cell.getDateCellValue().toInstant(), ZoneId.systemDefault());
                } else if (targetType == int.class || targetType == Integer.class) {
                    return (int) cell.getNumericCellValue();
                } else if (targetType == long.class || targetType == Long.class) {
                    return (long) cell.getNumericCellValue();
                } else if (targetType == double.class || targetType == Double.class) {
                    return cell.getNumericCellValue();
                } else if (targetType == float.class || targetType == Float.class) {
                    return (float) cell.getNumericCellValue();
                } else if (targetType == String.class) {
                    return String.valueOf(cell.getNumericCellValue());
                }
            }
            case Cell.CELL_TYPE_BOOLEAN -> {
                boolean booleanValue = cell.getBooleanCellValue();
                if (targetType == boolean.class || targetType == Boolean.class) {
                    return booleanValue;
                }
            }
            case Cell.CELL_TYPE_BLANK -> {
                return null;
            }
            default -> throw new UnsupportedOperationException("Тип ячейки не поддерживается: " + cell.getCellType());
        }
        throw new UnsupportedOperationException("Не получилось спарсить данные");
    }
}
