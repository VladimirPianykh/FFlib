package com.bpa4j.util.excel;

import com.bpa4j.editor.EditorEntry;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Class for parsing an Excel (xls/xlsx) file into a list of Java objects.
 * <p>
 * Example:
 * <pre>
 * {@code
 * List<SomeClass> parsedObjects = ExcelUtils.<SomeClass>createInstancesOf("file.xlsx", SomeClass.class)
 * }
 * </pre>
 */
public final class ExcelUtils{
	private ExcelUtils(){}
	static final DateTimeFormatter dateFormatter=DateTimeFormatter.ofPattern("yyyy-MM-dd");
	/**
     * Uses reflection to create a list of objects of the specified class from the given file.
     * <p>
     * Example:
     * <pre>
     * {@code
     * List<SomeClass> parsedObjects = ExcelUtils.<SomeClass>createInstancesOf("file.xlsx", SomeClass.class)
     * }
     * </pre>
     *
     * @param path - path to the file
     * @param type - class whose objects will be created
     * @param <T> the same class type (needed for creating the list)
     * @return list of created objects
     */
	public static<T>ArrayList<T>createInstancesOf(String path,Class<T>type){
		ArrayList<T>instances=new ArrayList<>();
		try(FileInputStream fis=new FileInputStream(path)){
			Workbook workbook=new XSSFWorkbook(fis);
			Sheet sheet=workbook.getSheetAt(0); //Reads the first Excel sheet
			Iterator<Row>rowIterator=sheet.iterator();
			//Skipping the header row if present
			if(rowIterator.hasNext())rowIterator.next();
			while(rowIterator.hasNext()){
				Row row=rowIterator.next();
				type.getDeclaredConstructor().setAccessible(true);
				T instance=type.getDeclaredConstructor().newInstance();
				Field[]fields=type.getDeclaredFields();
				for(int i=0;i<fields.length;i++){
					if(!fields[i].isAnnotationPresent(EditorEntry.class))continue;
					Parseable a=fields[i].getAnnotation(Parseable.class);
					Field field=fields[i];
					field.setAccessible(true);
					Cell cell=row.getCell(i,Row.CREATE_NULL_AS_BLANK);
					Object value=a==null?parseCellValue(cell,field.getType()):a.parser().getConstructor().newInstance().apply(cell.getStringCellValue());
					field.set(instance,value);
				}
				instances.add(instance);
			}
		}catch(IOException ex){throw new UncheckedIOException(ex);}
		catch(ReflectiveOperationException ex){throw new IllegalStateException(ex);}
		return instances;
	}
	private static Object parseCellValue(Cell cell,Class<?>targetType){
		switch(cell.getCellType()){
			case Cell.CELL_TYPE_STRING->{
				String stringValue=cell.getStringCellValue();
				if(targetType.isEnum()){
					for(var constant:targetType.getEnumConstants())
						if(constant.toString().equalsIgnoreCase(stringValue))return constant;
				}
				else if(targetType==LocalDate.class){return LocalDate.parse(cell.getStringCellValue(),dateFormatter);}
				return stringValue;
			}
			case Cell.CELL_TYPE_NUMERIC->{
				if(DateUtil.isCellDateFormatted(cell)&&targetType==LocalDate.class){return LocalDate.ofInstant(cell.getDateCellValue().toInstant(),ZoneId.systemDefault());}else if(targetType==int.class||targetType==Integer.class)return(int)cell.getNumericCellValue();else if(targetType==long.class||targetType==Long.class)return(long)cell.getNumericCellValue();else if(targetType==double.class||targetType==Double.class)return cell.getNumericCellValue();else if(targetType==float.class||targetType==Float.class)return(float)cell.getNumericCellValue();else if(targetType==String.class)return String.valueOf(cell.getNumericCellValue());
			}
			case Cell.CELL_TYPE_BOOLEAN->{
				boolean booleanValue=cell.getBooleanCellValue();
				if(targetType==boolean.class||targetType==Boolean.class)return booleanValue;
			}
			case Cell.CELL_TYPE_BLANK->{return null;}
			default->throw new UnsupportedOperationException("Cell type is not supported: "+cell.getCellType());
		}
		throw new UnsupportedOperationException("Failed to parse data");
	}
	/**
     * Uses reflection to save a list of objects to the given file.
     * <p>
     * Example:
     * <pre>
     * {@code
	 * List<TestClass> vals = List.of(new TestClass("inst 1"), new TestClass("inst 2"));
     * File resultFile = ExcelUtils.saveInstances(new File(outputPath), vals);
     * }
     * </pre>
     *
     * @param file - excel file
     * @param instances - list of instances to save
     * @return saved file
     */
	public static File saveInstances(File file,List<?>instances)throws IllegalAccessException{
		Workbook workbook=new XSSFWorkbook();
		Sheet sheet=workbook.createSheet("Data");
		Class<?>classType=instances.getFirst().getClass();
		// Получаем поля класса
		Field[]fields=classType.getDeclaredFields();
		// Создаем заголовки
		Row headerRow=sheet.createRow(0);
		for(int i=0;i<fields.length;i++){
			fields[i].setAccessible(true);
			if(fields[i].isAnnotationPresent(EditorEntry.class)){headerRow.createCell(i).setCellValue(fields[i].getAnnotation(EditorEntry.class).translation());}
			else{headerRow.createCell(i).setCellValue(fields[i].getName());}
		}
		// Записываем данные
		int rowIndex=1;
		for(var instance:instances){
			Row row=sheet.createRow(rowIndex++);
			for(int i=0;i<fields.length;i++){
				Cell cell=row.createCell(i);
				fields[i].setAccessible(true);
				Object value=fields[i].get(instance);
				if(value instanceof String){cell.setCellValue((String)value);}else if(value instanceof Number){cell.setCellValue(((Number)value).doubleValue());}else if(value instanceof LocalDate){cell.setCellValue(((LocalDate)value).format(dateFormatter));}else if(value instanceof Enum<?>){cell.setCellValue(value.toString());}else if(value!=null){cell.setCellValue(value.toString());}
			}
		}
		// Автоматическая настройка ширины колонок
		for(int i=0;i<fields.length;i++){sheet.autoSizeColumn(i);}
		// Сохраняем файл
		try (FileOutputStream fileOut = new FileOutputStream(file)) {
			workbook.write(fileOut);
		} catch (IOException e){
            throw new UncheckedIOException(e);
        }
		return file;
	}
}
