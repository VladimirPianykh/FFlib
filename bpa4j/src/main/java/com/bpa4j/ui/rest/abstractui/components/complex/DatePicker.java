package com.bpa4j.ui.rest.abstractui.components.complex;

import java.time.LocalDate;
import java.util.function.Consumer;
import com.bpa4j.ui.rest.abstractui.Panel;
import com.bpa4j.ui.rest.abstractui.components.Button;
import com.bpa4j.ui.rest.abstractui.components.Label;
import com.bpa4j.ui.rest.abstractui.layout.GridLayout;

/**
 * A date picker component that allows selecting a date using arrow buttons.
 * Composed of Panels, Buttons, and Labels - no custom JSON types.
 * @author AI-generated
 */
public class DatePicker extends Panel{
	private LocalDate date;
	private Consumer<LocalDate> onChange;
	
	private Label dayLabel;
	private Label monthLabel;
	private Label yearLabel;
	
	public DatePicker(){
		this(LocalDate.now());
	}
	
	public DatePicker(LocalDate initialDate){
		this.date=initialDate!=null?initialDate:LocalDate.now();
		
		// Use GridLayout: 3 rows (day, month, year), 3 columns (-, value, +)
		setLayout(new GridLayout(3,3,5,5));
		
		// Day row
		Button dayMinus=new Button("- day");
		dayLabel=new Label(String.valueOf(date.getDayOfMonth()));
		Button dayPlus=new Button("+ day");
		
		dayMinus.setOnClick(b->{
			date=date.minusDays(1);
			updateLabels();
			if(onChange!=null) onChange.accept(date);
		});
		dayPlus.setOnClick(b->{
			date=date.plusDays(1);
			updateLabels();
			if(onChange!=null) onChange.accept(date);
		});
		
		add(dayMinus);
		add(dayLabel);
		add(dayPlus);
		
		// Month row
		Button monthMinus=new Button("- month");
		monthLabel=new Label(String.valueOf(date.getMonthValue()));
		Button monthPlus=new Button("+ month");
		
		monthMinus.setOnClick(b->{
			date=date.minusMonths(1);
			updateLabels();
			if(onChange!=null) onChange.accept(date);
		});
		monthPlus.setOnClick(b->{
			date=date.plusMonths(1);
			updateLabels();
			if(onChange!=null) onChange.accept(date);
		});
		
		add(monthMinus);
		add(monthLabel);
		add(monthPlus);
		
		// Year row
		Button yearMinus=new Button("- year");
		yearLabel=new Label(String.valueOf(date.getYear()));
		Button yearPlus=new Button("+ year");
		
		yearMinus.setOnClick(b->{
			date=date.minusYears(1);
			updateLabels();
			if(onChange!=null) onChange.accept(date);
		});
		yearPlus.setOnClick(b->{
			date=date.plusYears(1);
			updateLabels();
			if(onChange!=null) onChange.accept(date);
		});
		
		add(yearMinus);
		add(yearLabel);
		add(yearPlus);
	}
	
	private void updateLabels(){
		dayLabel.setText(String.valueOf(date.getDayOfMonth()));
		monthLabel.setText(String.valueOf(date.getMonthValue()));
		yearLabel.setText(String.valueOf(date.getYear()));
	}
	
	public LocalDate getDate(){
		return date;
	}
	
	public void setDate(LocalDate date){
		this.date=date!=null?date:LocalDate.now();
		updateLabels();
	}
	
	public void setOnChange(Consumer<LocalDate> onChange){
		this.onChange=onChange;
	}
}
