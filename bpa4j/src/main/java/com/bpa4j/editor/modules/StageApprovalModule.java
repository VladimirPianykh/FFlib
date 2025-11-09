package com.bpa4j.editor.modules;

import com.bpa4j.core.Editable;
import com.bpa4j.defaults.editables.Processable;
import com.bpa4j.editor.ModularEditorRenderer.ModulesRenderingContext;

/**
 * A module for handling {@link Processable}s.
 */
public class StageApprovalModule extends FormModule{
	public void createTab(Editable editable,boolean isNew,Runnable deleter,ModulesRenderingContext ctx){
		super.createTab(editable,isNew,deleter,ctx);
	}
}
