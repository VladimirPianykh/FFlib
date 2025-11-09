package com.bpa4j.editor;

import java.util.function.Function;
import com.bpa4j.core.Editable;
import com.bpa4j.editor.ModularEditorRenderer.ModulesRenderingContext;

public abstract interface EditorModule{
	void createTab(Editable editable,boolean isNew,Runnable deleter,ModulesRenderingContext ctx);
	<M extends EditorModule>void setRendererSource(Function<M,? extends ModuleRenderer<M>>rendererSource);
}
