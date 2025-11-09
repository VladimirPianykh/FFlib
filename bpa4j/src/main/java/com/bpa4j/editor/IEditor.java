package com.bpa4j.editor;

import com.bpa4j.core.Editable;
import com.bpa4j.core.ProgramStarter;
import com.bpa4j.feature.FeatureRenderingContext;

/**
 * Should be implemented to provide opportunity to edit {@link Editable}s.
 */
public interface IEditor{
	default void constructEditor(Editable editable,boolean isNew,FeatureRenderingContext ctx){
		constructEditor(editable,isNew,null,ctx);
	}
	void constructEditor(Editable editable,boolean isNew,Runnable deleter,FeatureRenderingContext ctx);
	default EditorRenderer<?>getRenderer(){
		return ProgramStarter.getRenderingManager().getEditorRenderer(this);
	}
}