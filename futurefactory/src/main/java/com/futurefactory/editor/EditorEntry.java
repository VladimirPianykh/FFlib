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
    Class<? extends EditorEntryBase> editorBaseSource() default EditorEntryBase.class;
}
