package com.futurefactory.defaults.input;

import java.lang.reflect.Field;

import com.futurefactory.core.Data.Editable;
import com.futurefactory.editor.EditorEntry;
import com.futurefactory.editor.Verifier;

public class NullVerifier implements Verifier{  
	public String verify(Editable original,Editable e,boolean isNew){
		try{
            Field[]fields=e.getClass().getFields();
            for(Field f:fields)if(f.isAnnotationPresent(EditorEntry.class)&&f.get(e)==null)return f.getAnnotation(EditorEntry.class).translation()+" не задан(а).";
            return "";
        }catch(ReflectiveOperationException ex){throw new RuntimeException(ex);}
    }
}
