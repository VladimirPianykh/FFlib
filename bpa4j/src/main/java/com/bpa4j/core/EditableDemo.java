package com.bpa4j.core;

import java.lang.reflect.Field;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

import com.bpa4j.defaults.input.EmptySaver;
import com.bpa4j.editor.EditorEntry;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class EditableDemo implements Supplier<Editable>{
    private final Class<? extends Editable>type;
    private final List<Supplier<?>>savers;
    private final Supplier<String>nameSupplier;
	public Editable get(){
		try{
            Editable editable=type.getDeclaredConstructor().newInstance();
            List<Field>fields=Stream.of(type.getFields()).filter(f->f.isAnnotationPresent(EditorEntry.class)).toList();
            for(int i=0;i<fields.size();++i)if(!(savers.get(i)instanceof EmptySaver))fields.get(i).set(editable,savers.get(i).get());
            editable.name=nameSupplier.get();
            return editable;
        }catch(ReflectiveOperationException ex){throw new IllegalStateException(ex);}
    }
}
