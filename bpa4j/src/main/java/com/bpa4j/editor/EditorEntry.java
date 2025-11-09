package com.bpa4j.editor;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * An annotation to mark editable fields into {@link com.bpa4j.core.Editable Editables}.
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
	 * <li>hide - hides this field in the form of {@link com.bpa4j.editor.modules.FormModule FormModule}</li>
	 * <li>readonly - forbids changes</li>
	 * <li>initonly - forbids changes after creation</li>
	 * <li>nexcel - stops this field from being exported/imported to/from Excel by {@link com.bpa4j.util.excel.ExcelUtils ExcelUtils}</li>
	 * </ul>
	 */
	String[]properties()default{};
}
