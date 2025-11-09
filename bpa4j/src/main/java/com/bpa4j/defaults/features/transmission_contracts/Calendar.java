package com.bpa4j.defaults.features.transmission_contracts;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;
import com.bpa4j.Dater;
import com.bpa4j.feature.FeatureTransmissionContract;

public class Calendar<T extends Calendar.Event> implements FeatureTransmissionContract{
	public static interface Event{}
	public Supplier<HashMap<LocalDate,List<T>>> getEventsOp;
	public BiFunction<LocalDate,List<T>,List<T>> getEventListOp;
	public Consumer<HashMap<LocalDate,List<T>>> clearEventsOp;
	public Consumer<Consumer<HashMap<LocalDate,List<T>>>> setEventFillerOp;
	public Consumer<Dater<List<T>>> setDaterOp;
	public Supplier<Consumer<HashMap<LocalDate,List<T>>>> getEventFillerOp;
	public Supplier<Dater<List<T>>> getDaterOp;
	private String name;
	public Calendar(String name){
		this.name=name;
	}
	public void setGetEventsOp(Supplier<HashMap<LocalDate,List<T>>> getEventsOp){
		this.getEventsOp=getEventsOp;
	}
	public void setGetEventListOp(BiFunction<LocalDate,List<T>,List<T>> getEventListOp){
		this.getEventListOp=getEventListOp;
	}
	public void setClearEventsOp(Consumer<HashMap<LocalDate,List<T>>> clearEventsOp){
		this.clearEventsOp=clearEventsOp;
	}
	public void setSetEventFillerOp(Consumer<Consumer<HashMap<LocalDate,List<T>>>> setEventFillerOp){
		this.setEventFillerOp=setEventFillerOp;
	}
	public void setSetDaterOp(Consumer<Dater<List<T>>> setDaterOp){
		this.setDaterOp=setDaterOp;
	}
	public void setGetEventFillerOp(Supplier<Consumer<HashMap<LocalDate,List<T>>>> getEventFillerOp){
		this.getEventFillerOp=getEventFillerOp;
	}
	public void setGetDaterOp(Supplier<Dater<List<T>>> getDaterOp){
		this.getDaterOp=getDaterOp;
	}
	public HashMap<LocalDate,List<T>> getEvents(){
		return getEventsOp.get();
	}
	public List<T> getEventList(LocalDate date){
		return getEventListOp.apply(date,getEvents().getOrDefault(date,null));
	}
	public void clearEvents(){
		clearEventsOp.accept(getEvents());
	}
	public void setEventFiller(Consumer<HashMap<LocalDate,List<T>>> eventFiller){
		setEventFillerOp.accept(eventFiller);
	}
	public void setDater(Dater<List<T>> dater){
		setDaterOp.accept(dater);
	}
	public Consumer<HashMap<LocalDate,List<T>>> getEventFiller(){
		return getEventFillerOp.get();
	}
	public Dater<List<T>> getDater(){
		return getDaterOp.get();
	}
	public String getFeatureName(){
		return name;
	}
}
