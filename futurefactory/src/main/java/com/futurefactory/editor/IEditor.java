package com.futurefactory.editor;

import com.futurefactory.core.Data.Editable;

/**
 * Should be implemented to provide opportunity to edit {@link Editable}s.
 */
public interface IEditor{
	public void constructEditor(Editable editable,boolean isNew);
}