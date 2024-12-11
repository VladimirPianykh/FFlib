package com.futurefactory;

import java.util.ArrayList;

import com.futurefactory.Data.Editable;

public class Selectable<T extends Editable>extends ArrayList<T>{
	public Class<T>type;
	public Selectable(Class<T>type){this.type=type;}
	@SuppressWarnings("unchecked")
	public boolean add(Editable e){return super.add((T)e);}
}
