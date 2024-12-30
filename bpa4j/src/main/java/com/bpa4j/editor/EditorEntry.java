package com.bpa4j.editor;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * An annotation to mark editable fields into {@link com.bpa4j.core.Data.Editable Editables}.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface EditorEntry{
	/**
	 * A display name of the editable field.
	 */
	String translation();
	/**
	 * An editor for the field. {@code EditorEntryBase.class} means default editor.
	 */
	Class<? extends EditorEntryBase> editorBaseSource()default EditorEntryBase.class;
	/**
	 * <p>Some arbitrary flags for the field.</p>
	 * <p>
	 * Supported flags:
	 * <ul>
	 * <li>hide - indicates, that this field will not be shown in the form of {@link com.bpa4j.editor.modules.FormModule FormModule}</li>
	 * </ul>
	 */
	String[]properties()default{};
}
