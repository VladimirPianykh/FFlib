package com.futurefactory.defaults.ftr_attributes;

import java.util.ArrayList;
import java.util.function.Supplier;

import com.futurefactory.core.Data;
import com.futurefactory.core.Data.Editable;

public class GroupElementSupplier<T extends Editable>implements Supplier<ArrayList<T>>{
	private Class<T>type;
	public GroupElementSupplier(Class<T>type){this.type=type;}
	public ArrayList<T>get(){
		return Data.getInstance().getGroup(type);
	}
}
