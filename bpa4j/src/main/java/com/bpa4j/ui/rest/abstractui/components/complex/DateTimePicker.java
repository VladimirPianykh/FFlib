package com.bpa4j.ui.rest.abstractui.components.complex;

import java.time.LocalDateTime;
import java.util.function.Consumer;
import com.bpa4j.ui.rest.abstractui.Panel;
import com.bpa4j.ui.rest.abstractui.components.Label;
import com.bpa4j.ui.rest.abstractui.layout.GridLayout;

/**
 * A date-time picker component combining DatePicker and TimePicker.
 * Composed of Panels, Buttons, and Labels - no custom JSON types.
 * @author AI-generated
 */
public class DateTimePicker extends Panel{
	private LocalDateTime dateTime;
	private Consumer<LocalDateTime> onChange;
	
	private DatePicker datePicker;
	private TimePicker timePicker;
	
	public DateTimePicker(){
		this(LocalDateTime.now());
	}
	
	public DateTimePicker(LocalDateTime initialDateTime){
		this.dateTime=initialDateTime!=null?initialDateTime:LocalDateTime.now();
		
		// Use GridLayout: 3 rows (label, date, time)
		setLayout(new GridLayout(3,1,5,5));
		
		// Labels row
		add(new Label("Date:"));
		
		// Date picker
		datePicker=new DatePicker(dateTime.toLocalDate());
		datePicker.setOnChange(date->{
			dateTime=LocalDateTime.of(date,dateTime.toLocalTime());
			if(onChange!=null) onChange.accept(dateTime);
		});
		add(datePicker);
		
		// Time label
		add(new Label("Time:"));
		
		// Time picker
		timePicker=new TimePicker(dateTime.toLocalTime());
		timePicker.setOnChange(time->{
			dateTime=LocalDateTime.of(dateTime.toLocalDate(),time);
			if(onChange!=null) onChange.accept(dateTime);
		});
		add(timePicker);
	}
	
	public LocalDateTime getDateTime(){
		return dateTime;
	}
	
	public void setDateTime(LocalDateTime dateTime){
		this.dateTime=dateTime!=null?dateTime:LocalDateTime.now();
		datePicker.setDate(this.dateTime.toLocalDate());
		timePicker.setTime(this.dateTime.toLocalTime());
	}
	
	public void setOnChange(Consumer<LocalDateTime> onChange){
		this.onChange=onChange;
	}
}
