package com.bpa4j.defaults.ftr_attributes.element_suppliers;

import java.util.ArrayList;
import java.util.function.Supplier;

import com.bpa4j.core.Data;
import com.bpa4j.core.Data.Editable;

public class GroupElementSupplier<T extends Editable>implements Supplier<ArrayList<T>>{
	private Class<T>type;
	public GroupElementSupplier(Class<T>type){this.type=type;}
	public ArrayList<T>get(){
		return Data.getInstance().getGroup(type);
	}
}
