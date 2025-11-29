package com.bpa4j.ui.swing.editor;

import java.awt.GridLayout;
import java.lang.reflect.Field;
import java.util.Objects;
import java.util.function.Supplier;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import com.bpa4j.Wrapper;
import com.bpa4j.core.EditableDemo;
import com.bpa4j.editor.EditorEntryBase;
import com.bpa4j.editor.EditorEntryBaseRenderer;
import com.bpa4j.defaults.input.FlagWEditor;
import com.bpa4j.ui.swing.editor.modules.SwingFormModuleRenderer;

/**
 * Swing renderer for FlagWEditor.
 * @author AI-generated
 */
public class SwingFlagWEditorRenderer implements EditorEntryBaseRenderer{
	public void renderEditorBase(Object o,Field f,Wrapper<Supplier<?>>saver,Wrapper<EditableDemo>demo,EditorEntryBase base,EditorEntryBase.EditorEntryRenderingContext context){
		// Not used for Swing, see createEditorComponent
	}
	
	@Override
	public Object createEditorComponent(Object o,Field f,Wrapper<Supplier<?>>saver,Wrapper<EditableDemo>demo,EditorEntryBase base,EditorEntryBase.EditorEntryRenderingContext context){
		try{
			EditorEntryBase editor=FlagWEditor.getTypes().get(f)==null?null:FlagWEditor.getTypes().get(f).getDeclaredConstructor().newInstance();
			Object d=FlagWEditor.getDefaults().get(f),init=FlagWEditor.getInitials().get(f).get();
			JPanel p=new JPanel(new GridLayout());
			Wrapper<Supplier<?>>subSaver=new Wrapper<Supplier<?>>(null);
			JCheckBox b=new JCheckBox();
			boolean disabled=Objects.equals(f.get(o),d);
			if(disabled)f.set(o,init);
			JComponent c;
			if(editor==null){
				c=SwingFormModuleRenderer.wrapEditorComponent(SwingFormModuleRenderer.createEditorBase(o,f,subSaver),null);
			}else{
				Object editorComponent=context.getRenderer(editor).createEditorComponent(o,f,subSaver,demo,editor,context);
				c=(JComponent)editorComponent;
			}
			if(disabled)f.set(o,d);
			b.addActionListener(e->{
				if(b.isSelected())p.add(c);
				else p.remove(c);
				p.revalidate();
			});
			p.add(b);
			if(!disabled){
				b.setSelected(true);
				p.add(c);
			}
			saver.var=()->b.isSelected()?subSaver.var.get():d;
			return p;
		}catch(ReflectiveOperationException ex){throw new IllegalStateException(ex);}
	}
}
