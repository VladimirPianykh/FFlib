package com.bpa4j.defaults.editables.processable_attributes;

import java.util.function.Consumer;
import com.bpa4j.SerializableFunction;
import com.bpa4j.Wrapper;
import com.bpa4j.core.Editable;
import com.bpa4j.core.ProgramStarter;
import com.bpa4j.defaults.editables.Processable;
import com.bpa4j.editor.ModularEditor;
import lombok.RequiredArgsConstructor;

/**
 * A checker that sets object construction as a Stage action.
 */
@RequiredArgsConstructor
public class ObjectCreationChecker<T extends Editable>implements SerializableFunction<Processable,Boolean>{
	private final Class<T>type;
	public final ModularEditor editor;
	public Consumer<T>action;
	public Boolean apply(Processable p){
		try{
			T t=type.getDeclaredConstructor().newInstance();
			Wrapper<T>w=new Wrapper<>(t);
			editor.constructEditor(t,true,()->w.var=null,ProgramStarter.getRenderingManager().getDetachedFeatureRenderingContext());
			if(w.var!=null){
				if(action==null) ProgramStarter.getStorageManager().getStorage().getGroup(type).add(t);
				else action.accept(t);
			}
			return w.var!=null;
		}catch(ReflectiveOperationException ex){throw new IllegalStateException(ex);}
	}
}
