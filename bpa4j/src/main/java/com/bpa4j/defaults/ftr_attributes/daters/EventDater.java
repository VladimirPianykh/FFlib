package com.bpa4j.defaults.ftr_attributes.daters;

import java.lang.reflect.Field;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JList;
import com.bpa4j.Dater;
import com.bpa4j.defaults.features.transmission_contracts.Calendar;
import com.bpa4j.defaults.features.transmission_contracts.Calendar.Event;
import com.bpa4j.editor.EditorEntry;

/**
 * A {@link Dater} that creates a dropdown list with all events (if present).
 */
public class EventDater<T extends Calendar.Event>implements Dater<List<T>>{
	private final Function<Event,JComponent>provider;
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
	public Function<Event,JComponent> getProvider(){
		return provider;
	}

	public static JComponent listProperties(Event t){
		DefaultListModel<String>m=new DefaultListModel<>();
		m.addAll(Stream.<Field>of(t.getClass().getFields()).filter(f->f.isAnnotationPresent(EditorEntry.class)).map(f->{
			try{
				return f.getAnnotation(EditorEntry.class).translation()+": "+f.get(t);
			}catch(IllegalAccessException ex){throw new IllegalStateException(ex);}
		}).toList());
		JList<String>p=new JList<>(m);
		return p;
	};
}
