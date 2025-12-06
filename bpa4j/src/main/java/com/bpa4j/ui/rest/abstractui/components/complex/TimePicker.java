package com.bpa4j.ui.rest.abstractui.components.complex;

import java.time.LocalTime;
import java.util.function.Consumer;
import com.bpa4j.ui.rest.abstractui.Panel;
import com.bpa4j.ui.rest.abstractui.components.Button;
import com.bpa4j.ui.rest.abstractui.components.Label;
import com.bpa4j.ui.rest.abstractui.layout.GridLayout;

/**
 * A time picker component that allows selecting time using arrow buttons.
 * Composed of Panels, Buttons, and Labels - no custom JSON types.
 * @author AI-generated
 */
public class TimePicker extends Panel{
	private LocalTime time;
	private Consumer<LocalTime> onChange;
	
	private Label hourLabel;
	private Label minuteLabel;
	private Label secondLabel;
	
	public TimePicker(){
		this(LocalTime.now());
	}
	
	public TimePicker(LocalTime initialTime){
		this.time=initialTime!=null?initialTime:LocalTime.now();
		
		// Use GridLayout: 3 rows (hour, minute, second), 3 columns (-, value, +)
		setLayout(new GridLayout(3,3,5,5));
		
		// Hour row
		Button hourMinus=new Button("- hour");
		hourLabel=new Label(String.format("%02d",time.getHour()));
		Button hourPlus=new Button("+ hour");
		
		hourMinus.setOnClick(b->{
			time=time.minusHours(1);
			updateLabels();
			if(onChange!=null) onChange.accept(time);
		});
		hourPlus.setOnClick(b->{
			time=time.plusHours(1);
			updateLabels();
			if(onChange!=null) onChange.accept(time);
		});
		
		add(hourMinus);
		add(hourLabel);
		add(hourPlus);
		
		// Minute row
		Button minuteMinus=new Button("- minute");
		minuteLabel=new Label(String.format("%02d",time.getMinute()));
		Button minutePlus=new Button("+ minute");
		
		minuteMinus.setOnClick(b->{
			time=time.minusMinutes(1);
			updateLabels();
			if(onChange!=null) onChange.accept(time);
		});
		minutePlus.setOnClick(b->{
			time=time.plusMinutes(1);
			updateLabels();
			if(onChange!=null) onChange.accept(time);
		});
		
		add(minuteMinus);
		add(minuteLabel);
		add(minutePlus);
		
		// Second row
		Button secondMinus=new Button("- second");
		secondLabel=new Label(String.format("%02d",time.getSecond()));
		Button secondPlus=new Button("+ second");
		
		secondMinus.setOnClick(b->{
			time=time.minusSeconds(1);
			updateLabels();
			if(onChange!=null) onChange.accept(time);
		});
		secondPlus.setOnClick(b->{
			time=time.plusSeconds(1);
			updateLabels();
			if(onChange!=null) onChange.accept(time);
		});
		
		add(secondMinus);
		add(secondLabel);
		add(secondPlus);
	}
	
	private void updateLabels(){
		hourLabel.setText(String.format("%02d",time.getHour()));
		minuteLabel.setText(String.format("%02d",time.getMinute()));
		secondLabel.setText(String.format("%02d",time.getSecond()));
	}
	
	public LocalTime getTime(){
		return time;
	}
	
	public void setTime(LocalTime time){
		this.time=time!=null?time:LocalTime.now();
		updateLabels();
	}
	
	public void setOnChange(Consumer<LocalTime> onChange){
		this.onChange=onChange;
	}
}
