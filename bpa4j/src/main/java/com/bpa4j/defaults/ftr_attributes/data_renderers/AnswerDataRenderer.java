package com.bpa4j.defaults.ftr_attributes.data_renderers;

import java.awt.Font;
import java.util.Vector;
import java.util.function.Supplier;

import javax.swing.JComponent;
import javax.swing.JList;

import com.bpa4j.core.Root;

public class AnswerDataRenderer implements Supplier<JComponent>{
	private Supplier<String>[]answerers;
	@SafeVarargs
	public AnswerDataRenderer(Supplier<String>...answerers){this.answerers=answerers;}
	public JComponent get(){
		Vector<String>a=new Vector<>();
		for(Supplier<String>s:answerers)a.add(s.get());
		JList<String>l=new JList<>(a);
		l.setFont(new Font(Font.DIALOG,Font.PLAIN,Root.SCREEN_SIZE.height/50));
		return l;
	}
}
