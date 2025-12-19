package com.bpa4j.core;

import java.util.ArrayList;
import java.util.Arrays;
import com.bpa4j.defaults.features.transmission_contracts.EditableList.ItemRenderingContext;
import com.bpa4j.ui.swing.util.PathIcon;

/**
 * An array with icons (icons are optional).
 * To change display buttons in the model editing tab, redefine {@code createElementButton} and {@code createAddButton}.
 */
public class EditableGroup<T extends Editable>extends ArrayList<T>{
	public Class<T>type;
	/**
	 * Creates a button for editing element.
	 * @param e Editable to create button for
	 */
	public void renderElementButton(Editable e,ItemRenderingContext context){
		ProgramStarter.getRenderingManager().getEditableGroupRenderer(this).renderElementButton(this,e,context);
	}
	public void renderAddButton(ItemRenderingContext context){
		ProgramStarter.getRenderingManager().getEditableGroupRenderer(this).renderAddButton(this,context);
	}
	public PathIcon elementIcon,addIcon;
	public boolean invisible;
	@SafeVarargs
	public EditableGroup(PathIcon elementIcon,PathIcon addIcon,Class<T>type,T...elements){
		this.elementIcon=elementIcon;
		this.addIcon=addIcon;
		this.type=type;
		addAll(Arrays.asList(elements));
	}
	@SafeVarargs
	public EditableGroup(Class<T>type,T...elements){
		this(null,null,type,elements);
		this.invisible=true;
	}
	/**
	 * Makes this group not display in the list.
	 * @return this group
	 */
	public EditableGroup<T>hide(){
		invisible=true;
		return this;
	}
	@SuppressWarnings("unchecked")
	public boolean add(Editable e){
		if(e.getClass()!=type)throw new RuntimeException(e.getClass()+" cannot be added to the group of type "+type+".");
		return super.add((T)e);
	}
	@SuppressWarnings("rawtypes")
	public boolean equals(Object o){return o instanceof EditableGroup&&((EditableGroup)o).type.equals(type);}
}