package com.futurefactory;

import com.futurefactory.Data.Editable;

/**
 * Should be implemented to provide opportunity to edit {@code Editable}-s.
 */
public interface IEditor{
	public void constructEditor(Editable editable);
}