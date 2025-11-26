package com.bpa4j.defaults.ftr_attributes.data_renderers;

import java.awt.Font;
import java.util.Vector;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.swing.JComponent;
import javax.swing.JList;

import com.bpa4j.core.Root;
import com.bpa4j.defaults.features.transmission_contracts.Report;
import com.bpa4j.feature.DataRendererRenderer;

/**
 * Data renderer for displaying a list of answers or text items.
 * @author AI-generated
 */
public final class AnswerDataRenderer implements Report.DataRenderer{
	private Function<AnswerDataRenderer,DataRendererRenderer<AnswerDataRenderer>> rendererSource;
	private Supplier<String>[] answerers;

	@SafeVarargs
	public AnswerDataRenderer(Supplier<String>...answerers){
		this.answerers=answerers;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <D extends Report.DataRenderer> void setRendererSource(Function<D,? extends DataRendererRenderer<D>> rendererSource){
		this.rendererSource=(Function<AnswerDataRenderer,DataRendererRenderer<AnswerDataRenderer>>)(Object)rendererSource;
	}

	public JComponent getComponent(){
		Vector<String> a=new Vector<>();
		for(Supplier<String> s:answerers)
			a.add(s.get());
		JList<String> l=new JList<>(a);
		l.setFont(new Font(Font.DIALOG,Font.PLAIN,Root.SCREEN_SIZE.height/50));
		return l;
	}

	public Supplier<String>[] getAnswerers(){
		return answerers;
	}

	public Function<AnswerDataRenderer,DataRendererRenderer<AnswerDataRenderer>> getRendererSource(){
		return rendererSource;
	}
}
