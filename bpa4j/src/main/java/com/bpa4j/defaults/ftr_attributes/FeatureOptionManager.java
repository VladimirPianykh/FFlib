package com.bpa4j.defaults.ftr_attributes;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.BoxLayout;
import javax.swing.JList;
import javax.swing.JPanel;

import com.bpa4j.ui.HButton;

public class FeatureOptionManager<T>{
	public Consumer<List<T>>collectiveAction;
	public Consumer<T>singularAction;
	public FeatureOptionManager<T>setCollective(Consumer<List<T>>collectiveAction){this.collectiveAction=collectiveAction;return this;}
	public FeatureOptionManager<T>setSingular(Consumer<T>singularAction){this.singularAction=singularAction;return this;}
	public void executeSingular(T element){}
	@SafeVarargs
	public final void executeCollective(T...elements){collectiveAction.accept(Arrays.asList(elements));}
	public final void executeCollective(List<T>list){collectiveAction.accept(list);}
	public JPanel createPanel(JList<T>list){
		JPanel p=new JPanel(new GridLayout(0,1)),buttonPanel=new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel,BoxLayout.Y_AXIS));
		if(collectiveAction!=null){
			HButton b=new HButton(){
				public void paint(Graphics g){
					int c=50-scale*4;
					if(getModel().isPressed())c-=25;
					g.setColor(new Color(c,c,c));
					g.fillRect(0,0,getWidth(),getHeight());
				}
			};
			b.addActionListener(e->executeCollective(list.getSelectedValuesList()));
			p.add(b);
		}
		if(singularAction!=null){
			HButton b=new HButton();
			b.addActionListener(e->executeSingular(list.getSelectedValue()));
			p.add(b);
		}
		p.add(buttonPanel);
		return p;
	}
}
