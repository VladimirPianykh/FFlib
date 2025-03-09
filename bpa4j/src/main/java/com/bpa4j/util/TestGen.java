package com.bpa4j.util;

import java.lang.reflect.Field;
import com.bpa4j.core.Data;
import com.bpa4j.core.Data.Editable;
import com.bpa4j.core.Data.EditableGroup;
import com.bpa4j.defaults.editables.AbstractCustomer;
import com.bpa4j.editor.EditorEntry;
import com.bpa4j.editor.Input;
import com.bpa4j.editor.NameProvider;
import com.bpa4j.editor.Verifier;

/**
 * A utility for test data generation.
 */
public final class TestGen{
	private TestGen(){}
	public static String[]names={"Алиса","Вася","Саша","Петя","Лена","Женя","Коля","Гена","Полина","Лёша"};
	/**
	 * Generates objects for the given groups randomly.
	 * <p>Groups are processed in the order of passing.</p>
	 * <p>
	 * Generated objects meet the following conditions:
	 * <ul>
	 *   <li>
	 *     For each {@code Editable} field at least one of the following is true:
	 *     <ul>
	 *       <li>Each {@code Editable} from the same group is taken.</li>
	 *       <li>None of the same group is taken twice.</li>
	 *     </ul>
	 *   </li>
	 * </ul>
	 */
	@SuppressWarnings("unchecked")
	public static void generate(int amount,EditableGroup<?>...groups){
		for(EditableGroup<?>g:groups)try{
			Input a=g.type.getAnnotation(Input.class);
			NameProvider p=null;
			if(a!=null&&a.nameProvider()!=NameProvider.class)p=a.nameProvider().getDeclaredConstructor().newInstance();
			Verifier v=null;
			if(a!=null&&a.verifier()!=Verifier.class)v=a.verifier().getDeclaredConstructor().newInstance();
			for(int i=0;i<amount;++i){
				Editable e=g.type.getDeclaredConstructor().newInstance();
				for(Field f:g.type.getFields())if(Editable.class.isAssignableFrom(f.getType())&&f.isAnnotationPresent(EditorEntry.class)){
					try{
						EditableGroup<?>fieldGroup=Data.getInstance().getGroup((Class<? extends Editable>)f.getType());
						f.set(e,fieldGroup.get(i%fieldGroup.size()));
					}catch(IllegalArgumentException ex){}
				}
				if(p!=null)e.name=p.provideName(e);
				else if(e instanceof AbstractCustomer)e.name=names[i%names.length];
				else e.name+=" #"+(int)(Math.random()*1000000);
				if(v!=null){
					String verdict=v.verify(e,e,true);
					if(!verdict.isEmpty())System.err.println("WARNING: "+e.name+" of type "+g.type.getName()+" is invalid. Verifier verdict: "+verdict);
				}
				g.add(e);
			}
		}catch(ReflectiveOperationException ex){throw new IllegalStateException(ex);}
	}
}
