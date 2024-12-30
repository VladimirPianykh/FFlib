package com.bpa4j.defaults.ftr_attributes.daters;

import java.awt.Color;
import java.awt.GridLayout;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.border.TitledBorder;

import com.bpa4j.Dater;
import com.bpa4j.defaults.features.Calendar;
import com.bpa4j.defaults.features.Calendar.Event;
import com.bpa4j.editor.EditorEntry;

/**
 * A {@link Dater} that creates a drop down list with all events, if present.
 */
public class EventDater<T extends Calendar.Event>implements Dater<List<T>>{
	private Function<Event,JComponent>provider;
	/**
	 * Constructs a new {@code EventDater} with the component provider given.
	 * Component provider is a function that accepts {@code T} and returns a {@link JComponent}
	 * There are several methods that can be used here through method reference:
	 * <ul>
	 *   <li>listProperties - renders the list of all event properties and their values.</li>
	 * </ul>
	 * @param provider - provider to use
	 */
	public EventDater(Function<Event,JComponent>provider){this.provider=provider;}
	@SuppressWarnings("unchecked")
	public JComponent apply(List<T>a,LocalDate u){
		JComponent c;
		if(a.isEmpty()){
			c=new JLabel();
			c.setOpaque(true);
		}else{
			JComboBox<Object>b=new JComboBox<>(a.toArray());
			b.setSelectedItem(null);
			b.addActionListener(e->{
				JPopupMenu m=new JPopupMenu(u.toString());
				JPanel p=new JPanel(new GridLayout());
				p.add(provider.apply((T)b.getSelectedItem()));
				m.add(p);
				m.show(b.getTopLevelAncestor(),b.getLocationOnScreen().x,b.getLocationOnScreen().y);
				b.setSelectedItem(null);
			});
			c=b;
		}
		c.setBackground(Color.DARK_GRAY);
		c.setForeground(Color.WHITE);
		c.setBorder(BorderFactory.createTitledBorder(null,u.toString(),TitledBorder.TOP,0,null,u.isEqual(LocalDate.now())?Color.GREEN:Color.WHITE));
		return c;
	}
	public static JComponent listProperties(Event t){
		DefaultListModel<String>m=new DefaultListModel<>();
		m.addAll(Stream.<Field>of(t.getClass().getFields()).filter(f->f.isAnnotationPresent(EditorEntry.class)).map(f->{
			try{
				return f.getAnnotation(EditorEntry.class).translation()+": "+String.valueOf(f.get(t));
			}catch(IllegalAccessException ex){throw new RuntimeException(ex);}
		}).toList());
		JList<String>p=new JList<>(m);
		return p;
	};
}
