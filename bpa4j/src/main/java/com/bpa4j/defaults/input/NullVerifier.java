package com.bpa4j.defaults.input;

import java.lang.reflect.Field;

import com.bpa4j.core.Data.Editable;
import com.bpa4j.editor.EditorEntry;
import com.bpa4j.editor.Verifier;

public class NullVerifier implements Verifier{  
	public String verify(Editable original,Editable e,boolean isNew){
		try{
            Field[]fields=e.getClass().getFields();
            for(Field f:fields)if(f.isAnnotationPresent(EditorEntry.class)&&f.get(e)==null)return f.getAnnotation(EditorEntry.class).translation()+" не задан(а).";
            return "";
        }catch(ReflectiveOperationException ex){throw new RuntimeException(ex);}
    }
}
