package com.bpa4j.util;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ContainerAdapter;
import java.awt.event.ContainerEvent;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;

import com.bpa4j.ui.AutoLayout;

/**
 * A utility class designed to accelerate UI development.
 */
public final class SprintUI{
	private SprintUI(){}
	public static<T extends JComponent>T addStr(String s,Color c,T t){
		t.setBorder(BorderFactory.createTitledBorder(null,s,TitledBorder.DEFAULT_JUSTIFICATION,TitledBorder.DEFAULT_POSITION,UIManager.getFont("Button.font"),c));
		return t;
	}
	public static<T extends JComponent>T addStr(String s,T t){
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
	public static<T>JScrollPane wrap(JList<T>list){
		JScrollPane s=new JScrollPane(list);
		return s;
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
	public static JScrollPane createList(int displayedItems,JPanel panel){
		JScrollPane s=new JScrollPane(panel,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		s.addComponentListener(new ComponentAdapter(){
			public void componentResized(ComponentEvent e){componentShown(e);}
			public void componentShown(ComponentEvent e){
				panel.setPreferredSize(new Dimension(panel.getPreferredSize().width,s.getHeight()*panel.getComponentCount()/displayedItems));
				s.getVerticalScrollBar().setUnitIncrement(s.getHeight()/40);
			}
		});
		panel.setLayout(new GridLayout(0,1));
		panel.addContainerListener(new ContainerAdapter(){
			public void componentAdded(ContainerEvent e){
				panel.setPreferredSize(new Dimension(panel.getPreferredSize().width,s.getHeight()*panel.getComponentCount()/displayedItems));
			}
		});
		return s;
	}
}
