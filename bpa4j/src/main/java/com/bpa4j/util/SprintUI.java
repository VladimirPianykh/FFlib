package com.bpa4j.util;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;

import com.bpa4j.ui.AutoLayout;

/**
 * A utility class designed to accelerate UI development.
 */
public final class SprintUI{
	private SprintUI(){}
	public static<T extends JComponent>T addStr(T t,String s,Color c){
		t.setBorder(BorderFactory.createTitledBorder(null,s,TitledBorder.DEFAULT_JUSTIFICATION,TitledBorder.DEFAULT_POSITION,UIManager.getFont("Button.font"),c));
		return t;
	}
	public static<T extends JComponent>T addStr(T t,String s){
		t.setBorder(BorderFactory.createTitledBorder(s));
		return t;
	}
	public static JMenuBar wrap(JMenu menu){
		JMenuBar bar=new JMenuBar();
		bar.setLayout(new GridLayout());
		bar.add(menu);
		bar.setToolTipText(menu.getText());
		return bar;
	}
	public static JMenu createMenu(JMenuItem...items){
		JMenu menu=new JMenu();
		for(JMenuItem item:items)menu.add(item);
		return menu;
	}
	public static JPanel join(Component...components){
		JPanel p=new JPanel(new AutoLayout());
		for(Component c:components)p.add(c);
		return p;
	}
}
