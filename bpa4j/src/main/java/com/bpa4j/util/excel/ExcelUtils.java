package com.bpa4j.util.excel;

import com.bpa4j.core.Data;
import com.bpa4j.core.Data.Editable;
import com.bpa4j.core.Data.EditableGroup;
import com.bpa4j.editor.EditorEntry;
import com.bpa4j.ui.Message;

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
import java.util.stream.Stream;

import javax.swing.JFileChooser;
import java.awt.Window;
import java.awt.Color;

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
	public static class ExcelLoader{
		private static class ExcelConfig{
			public final String dialogDescription;
			public final Class<? extends Editable>type;
			public final boolean parseName;
			public ExcelConfig(String dialogDescription,Class<? extends Editable>type,boolean parseName){
				this.dialogDescription=dialogDescription;
				this.type=type;
				this.parseName=parseName;
			}
		}
		private final ArrayList<ExcelConfig>list=new ArrayList<>();
		public ExcelLoader addParam(String dialogDescription,Class<? extends Editable>type,boolean parseName){
			list.add(new ExcelConfig(dialogDescription,type,parseName));
			return this;
		}
		public void load(){
			JFileChooser f=new JFileChooser(System.getProperty("user.home")+"/Downloads");
			new Message("Выберите файлы, соответствующие заголовкам диалоговых окон.",Color.WHITE);
			for(ExcelConfig c:list){
				f.setDialogTitle(c.dialogDescription);
				f.showOpenDialog(Window.getWindows()[0]);
				EditableGroup<?>g=Data.getInstance().getGroup(c.type);
				for(Editable e:ExcelUtils.createInstancesOf(f.getSelectedFile().toString(),c.type,c.parseName))g.add(e);
			}
			new Message("Готово! Файлы загружены из Excel!",Color.GREEN);
		}
	}
	private ExcelUtils(){}
	static final DateTimeFormatter dateFormatter=DateTimeFormatter.ofPattern("yyyy-MM-dd");
	/**
     * Parses a list of objects from the given file.
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
     * @param parseName - whether to parse the "name" field if object is instanceof {@link com.bpa4j.core.Data.Editable Editable}
     * @param <T> the same class type (needed for creating the list)
     * @return list of created objects
     */
	public static<T>ArrayList<T>createInstancesOf(String path,Class<T>type,boolean parseName){
		ArrayList<T>instances=new ArrayList<>();
		try(FileInputStream fis=new FileInputStream(path)){
			Workbook workbook=new XSSFWorkbook(fis);
			Sheet sheet=workbook.getSheetAt(0); //Reading the first Excel sheet
			Iterator<Row>rowIterator=sheet.iterator();
			//Skipping the header row if present
			if(rowIterator.hasNext())rowIterator.next();
			while(rowIterator.hasNext()){
				Row row=rowIterator.next();
				type.getDeclaredConstructor().setAccessible(true);
				T instance=type.getDeclaredConstructor().newInstance();
				int i=0;
				if(parseName&&instance instanceof Editable){
					Cell cell=row.getCell(0);
					((Editable)instance).name=(cell.getCellType()==Cell.CELL_TYPE_NUMERIC?String.valueOf(cell.getNumericCellValue()):cell.getStringCellValue()).trim();
					++i;
				}
				r:for(Field f:type.getDeclaredFields()){
					EditorEntry ee=f.getAnnotation(EditorEntry.class);
					if(ee==null)continue;
					for(String p:ee.properties())if(p.equals("nexcel"))continue r;
					Parseable a=f.getAnnotation(Parseable.class);
					Field field=f;
					field.setAccessible(true);
					Cell cell=row.getCell(i,Row.CREATE_NULL_AS_BLANK);
					try{
						Object value=a==null?parseCellValue(cell,field.getType()):a.value().getConstructor().newInstance().apply(cell.getStringCellValue());
						field.set(instance,value);
					}catch(IllegalStateException ex){throw new IllegalStateException("Cannot parse field "+field.getName()+" from column "+i+" (counting from 0).",ex);}
					++i;
				}
				instances.add(instance);
			}
		}catch(IOException ex){throw new UncheckedIOException(ex);}
		catch(ReflectiveOperationException ex){throw new IllegalStateException(ex);}
		return instances;
	}
	@SuppressWarnings("unchecked")
	private static Object parseCellValue(Cell cell,Class<?>targetType){
		switch(cell.getCellType()){
			case Cell.CELL_TYPE_STRING->{
				String stringValue=cell.getStringCellValue().trim();
				if(targetType.isEnum()){
					for(var constant:targetType.getEnumConstants())
						if(constant.toString().equalsIgnoreCase(stringValue))return constant;
				}else if(Editable.class.isAssignableFrom(targetType))return Data.getInstance().getGroup((Class<? extends Editable>)targetType).stream().filter(e->e.name.equalsIgnoreCase(stringValue)).findAny().orElseThrow(()->new IllegalStateException("There is no element of \""+stringValue+"\"."));
				else if(targetType==LocalDate.class)return LocalDate.parse(cell.getStringCellValue(),dateFormatter);
				if(targetType!=String.class)throw new IllegalStateException("Value \""+cell.getStringCellValue()+"\" cannot be treated as "+targetType.getName());
				return stringValue;
			}
			case Cell.CELL_TYPE_NUMERIC->{
				if(DateUtil.isCellDateFormatted(cell)&&targetType==LocalDate.class)return LocalDate.ofInstant(cell.getDateCellValue().toInstant(),ZoneId.systemDefault());
				else if(targetType==int.class||targetType==Integer.class)return(int)cell.getNumericCellValue();
				else if(targetType==long.class||targetType==Long.class)return(long)cell.getNumericCellValue();
				else if(targetType==double.class||targetType==Double.class)return cell.getNumericCellValue();
				else if(targetType==float.class||targetType==Float.class)return(float)cell.getNumericCellValue();
				else if(targetType==String.class)return String.valueOf(cell.getNumericCellValue());
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
		//Получаем поля класса
		Field[]fields=classType.getDeclaredFields();
		//Создаем заголовки
		Row headerRow=sheet.createRow(0);
		for(int i=0;i<fields.length;i++){
			fields[i].setAccessible(true);
			EditorEntry a=fields[i].getAnnotation(EditorEntry.class);
			if(a!=null&&!Stream.of(a.properties()).anyMatch(p->p.equals("nexcel")))headerRow.createCell(i).setCellValue(fields[i].getAnnotation(EditorEntry.class).translation());
		}
		//Записываем данные
		int rowIndex=1;
		for(var instance:instances){
			Row row=sheet.createRow(rowIndex++);
			for(int i=0;i<fields.length;i++){
				Cell cell=row.createCell(i);
				fields[i].setAccessible(true);
				Object value=fields[i].get(instance);
				EditorEntry a=fields[i].getAnnotation(EditorEntry.class);
				if(a==null){continue;}
				if(value instanceof String){cell.setCellValue((String)value);}else if(value instanceof Number){cell.setCellValue(((Number)value).doubleValue());}else if(value instanceof LocalDate){cell.setCellValue(((LocalDate)value).format(dateFormatter));}else if(value instanceof Enum<?>){cell.setCellValue(value.toString());}else if(value!=null){cell.setCellValue(value.toString());}
			}
		}
		//Автоматическая настройка ширины колонок
		for(int i=0;i<fields.length;i++){sheet.autoSizeColumn(i);}
		//Сохраняем файл
		try (FileOutputStream fileOut = new FileOutputStream(file)) {
			workbook.write(fileOut);
		} catch (IOException e){
            throw new UncheckedIOException(e);
        }
		return file;
	}
}
