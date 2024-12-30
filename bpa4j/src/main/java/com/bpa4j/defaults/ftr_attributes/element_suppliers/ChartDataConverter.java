package com.bpa4j.defaults.ftr_attributes.element_suppliers;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.function.Supplier;

import com.bpa4j.editor.EditorEntry;

public class ChartDataConverter<T>implements Supplier<ArrayList<Object[]>>{
    private Supplier<ArrayList<T>>elementSupplier;
    public ChartDataConverter(Supplier<ArrayList<T>>elementSupplier){this.elementSupplier=elementSupplier;}
	public ArrayList<Object[]>get(){
		ArrayList<Object[]>a=new ArrayList<>();
        ArrayList<T>o=elementSupplier.get();
        if(o.isEmpty())return a;
        Field[]allFields=o.get(0).getClass().getFields();
        ArrayList<Field>fields=new ArrayList<>();
        for(Field f:allFields)if(f.isAnnotationPresent(EditorEntry.class))fields.add(f);
        for(T t:o){
            Object[]obj=new Object[fields.size()];
            for(int i=0;i<fields.size();++i)try{
                obj[i]=fields.get(i).get(t);
            }catch(IllegalAccessException ex){throw new RuntimeException(ex);}
            a.add(obj);
        }
        return a;
    }
}
