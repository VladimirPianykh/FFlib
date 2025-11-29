package com.bpa4j.ui.rest.editor;

import java.lang.reflect.Field;
import java.util.Objects;
import java.util.function.Supplier;
import com.bpa4j.Wrapper;
import com.bpa4j.core.EditableDemo;
import com.bpa4j.defaults.input.FlagWEditor;
import com.bpa4j.editor.EditorEntryBase;
import com.bpa4j.editor.EditorEntryBaseRenderer;
import com.bpa4j.ui.rest.abstractui.Component;
import com.bpa4j.ui.rest.abstractui.Panel;
import com.bpa4j.ui.rest.abstractui.components.CheckBox;
import com.bpa4j.ui.rest.abstractui.layout.GridLayout;
import com.bpa4j.ui.rest.editor.modules.RestFormModuleRenderer;

/**
 * REST renderer for FlagWEditor.
 * @author AI-generated
 */
public class RestFlagWEditorRenderer implements EditorEntryBaseRenderer{
	public void renderEditorBase(Object o,Field f,Wrapper<Supplier<?>>saver,Wrapper<EditableDemo>demo,EditorEntryBase base,EditorEntryBase.EditorEntryRenderingContext context){
		try{
			EditorEntryBase editor=FlagWEditor.getTypes().get(f)==null?null:FlagWEditor.getTypes().get(f).getDeclaredConstructor().newInstance();
			Object d=FlagWEditor.getDefaults().get(f),init=FlagWEditor.getInitials().get(f).get();
			Panel p=new Panel();
			p.setLayout(new GridLayout(1,2,5,5));
			Wrapper<Supplier<?>>subSaver=new Wrapper<Supplier<?>>(null);
			CheckBox b=new CheckBox();
			boolean disabled=Objects.equals(f.get(o),d);
			if(disabled)f.set(o,init);
			Component c;
			if(editor==null){
				RestFormModuleRenderer.createEditorComponent(o,f,subSaver,p);
				c=p.getComponents().get(p.getComponents().size()-1);
			}else{
				editor.renderEditorBase(o,f,subSaver,demo,context);
				// For REST, we assume the component was added to a panel
				c=null; // Placeholder
			}
			if(disabled)f.set(o,d);
			b.setSelected(!disabled);
			p.add(b);
			if(c!=null&&!disabled){
				p.add(c);
			}
			saver.var=()->b.isSelected()?subSaver.var.get():d;
		}catch(ReflectiveOperationException ex){throw new IllegalStateException(ex);}
	}
}
