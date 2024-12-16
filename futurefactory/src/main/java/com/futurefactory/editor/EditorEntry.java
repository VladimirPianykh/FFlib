package com.futurefactory.editor;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * An annotation to mark editable fields into {@link Editable}s.
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
	 * <br>Some arbitrary flags for the field.</br>
	 * <br>
	 * Supported flags:
	 * <ul>
	 * <li>hide - indicates, that this field will not be shown in the form of {@link com.futurefactory.editor.modules.FormModule FormModule}</li>
	 * </ul>
	 * </br>
	 */
	String[]properties()default{};
}
