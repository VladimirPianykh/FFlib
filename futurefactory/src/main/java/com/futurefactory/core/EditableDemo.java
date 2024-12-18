package com.futurefactory.core;

import java.lang.reflect.Field;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

import com.futurefactory.core.Data.Editable;
import com.futurefactory.editor.EditorEntry;

public class EditableDemo implements Supplier<Editable>{
    private Class<? extends Editable>type;
    private List<Supplier<?>>savers;
    public EditableDemo(Class<? extends Editable>type,List<Supplier<?>>savers){this.type=type;this.savers=savers;}
	public Editable get(){
		try{
            Editable editable=type.getDeclaredConstructor().newInstance();
            List<Field>fields=Stream.of(type.getFields()).filter(f->f.isAnnotationPresent(EditorEntry.class)).toList();
            for(int i=0;i<fields.size();++i)fields.get(i).set(editable,savers.get(i).get());
            return editable;
        }catch(ReflectiveOperationException ex){throw new RuntimeException(ex);}
    }
}
