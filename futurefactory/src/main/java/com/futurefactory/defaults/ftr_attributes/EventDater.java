package com.futurefactory.defaults.ftr_attributes;

import java.awt.Color;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.function.Function;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.border.TitledBorder;

import com.futurefactory.Dater;
import com.futurefactory.defaults.features.Calendar;

public class EventDater<T extends Calendar.Event>implements Dater<ArrayList<T>>{
	private Function<T,JComponent>provider;
	/**
	 * Constructs a new {@code EventDater} with the component provider given.
	 * Component provider is a function that accepts {@code T} and returns a {@link JComponent}
	 * @param provider - provider to use
	 */
	public EventDater(Function<T,JComponent>provider){this.provider=provider;}
	@SuppressWarnings("unchecked")
	public JComponent apply(ArrayList<T>a,LocalDate u){
		JComponent c;
		if(a.isEmpty()){
			c=new JLabel();
			c.setOpaque(true);
		}else{
			JComboBox<Object>b=new JComboBox<>(a.toArray());
			b.addItemListener(e->{
				JPopupMenu m=new JPopupMenu();
				JPanel p=new JPanel(new GridLayout());
				p.add(provider.apply((T)b.getSelectedItem()));
				m.add(p);
				m.show(b.getTopLevelAncestor(),b.getLocationOnScreen().x,b.getLocationOnScreen().y);
				b.setSelectedItem(null);
			});
			c=b;
		}
		c.setBackground(Color.DARK_GRAY);
		c.setBorder(BorderFactory.createTitledBorder(null,u.toString(),TitledBorder.TOP,0,null,u.isEqual(LocalDate.now())?Color.GREEN:Color.WHITE));
		return c;
	}
}
