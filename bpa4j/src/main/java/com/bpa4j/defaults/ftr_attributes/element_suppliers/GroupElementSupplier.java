package com.bpa4j.defaults.ftr_attributes.element_suppliers;

import java.util.ArrayList;
import java.util.function.Supplier;

import com.bpa4j.core.Editable;
import com.bpa4j.core.ProgramStarter;

public class GroupElementSupplier<T extends Editable>implements Supplier<ArrayList<T>>{
	private Class<T>type;
	public GroupElementSupplier(Class<T>type){this.type=type;}
	public ArrayList<T>get(){
		return ProgramStarter.getStorageManager().getStorage().getGroup(type);
	}
}
