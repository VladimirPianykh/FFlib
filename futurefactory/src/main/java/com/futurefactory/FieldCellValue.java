package com.futurefactory;

import java.lang.reflect.Field;

public class FieldCellValue{
	public Field f;
	public Object o;
	public FieldCellValue(Field f,Object o){this.f=f;this.o=o;}
	public String toString(){try{return f.get(o)==null?"-----":f.get(o).toString();}catch(IllegalAccessException ex){throw new RuntimeException(ex);}}
}
