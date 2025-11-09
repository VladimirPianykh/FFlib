package com.bpa4j.ui.swing.util;

import java.awt.Graphics;
import java.awt.GridLayout;
import java.util.function.Consumer;

import javax.swing.JPanel;

public class LazyPanel extends JPanel{
	private boolean flag=true;
	private final Consumer<LazyPanel>filler;
	public LazyPanel(Consumer<LazyPanel>filler){
		setLayout(new GridLayout(1,1));
		setOpaque(false);
		this.filler=filler;
	}
	public void paint(Graphics g) {
		if(flag){
			filler.accept(this);
			flag=false;
		}
		super.paint(g);
	}
}
