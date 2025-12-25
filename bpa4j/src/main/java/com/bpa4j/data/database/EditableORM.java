package com.bpa4j.data.database;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import com.bpa4j.core.Editable;
import com.bpa4j.editor.EditorEntry;

public final class EditableORM{
	public static String createTableFrom(Class<? extends Editable> e){
		StringBuilder s=new StringBuilder();
		String tableName=e.getSimpleName();
		s.append("CREATE TABLE "+tableName+"(");

		//Metadata
		s.append("id BIGINT AUTO_INCREMENT PRIMARY KEY");
		s.append("name VARCHAR(255) NOT NULL");

		//Fields
		for(Field f:e.getFields()){
			if(!f.isAnnotationPresent(EditorEntry.class)) continue;
			s.append(getTableVariableString(f));
			s.append(',');
		}
		s.deleteCharAt(s.length()-1); //Remove ','

		s.append(")");
		return s.toString();
	}
	public static String writeToTable(Editable e){
		StringBuilder s=new StringBuilder();
		String tableName=e.getClass().getSimpleName();
		s.append("INSERT INTO "+tableName+"(");
		s.append(Stream.of(e.getClass().getFields()).map(EditableORM::getDBName).collect(Collectors.joining(" ")));
		s.append(") VALUES");
		s.append(Stream.of(e.getClass().getFields()).map(f->{
			try{
				return f.get(e);
			}catch(ReflectiveOperationException ex){
				throw new IllegalStateException(ex);
			}
		}).map(EditableORM::convertToDBValue).collect(Collectors.joining(" ")));
		return s.toString();
	}
	private static String getTableVariableString(Field f){
		Class<?> type=f.getType();
		return String.format("%s %s",getDBName(f),convertToDBType(type));
	}
	private static String getDBName(Field f){
		return f.getName();
	}
	private static String convertToDBValue(Object o){
		if(o==null) return "NULL";
		if(o instanceof String s) return "'"+s.replace("'","''")+"'";
		if(o instanceof Number) return o.toString();
		if(o instanceof Boolean) return ((Boolean)o)?"1":"0";
		if(o instanceof Enum<?>) return "'"+((Enum<?>)o).name().replace("'","''")+"'";
		if(o instanceof java.time.LocalDate) return "'"+o.toString()+"'";
		if(o instanceof java.time.LocalDateTime) return "'"+o.toString()+"'";
		ByteArrayOutputStream b=new ByteArrayOutputStream();
		try(ObjectOutputStream oos=new ObjectOutputStream(b)){
			oos.writeObject(o);
			
		}catch(IOException ex){
			throw new IllegalStateException(ex);
		}
	}
	private static String convertToDBType(Class<?> type){
		if(type.isEnum()) return "VARCHAR";
		if(type==int.class||type==Integer.class) return "INT";
		if(type==long.class||type==Long.class) return "BIGINT";
		if(type==double.class||type==Double.class) return "DOUBLE";
		if(type==float.class||type==Float.class) return "FLOAT";
		if(type==boolean.class||type==Boolean.class) return "BOOLEAN";
		if(type==String.class) return "VARCHAR(255)";
		if(type==LocalDate.class) return "DATE";
		if(type==LocalDateTime.class) return "TIMESTAMP";
		return "BLOB";
	}
}
