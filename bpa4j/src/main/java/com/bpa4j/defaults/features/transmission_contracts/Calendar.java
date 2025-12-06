package com.bpa4j.defaults.features.transmission_contracts;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;
import com.bpa4j.Dater;
import com.bpa4j.feature.DaterRenderer;
import com.bpa4j.feature.Feature;
import com.bpa4j.feature.FeatureTransmissionContract;

public class Calendar<T extends Calendar.Event> implements FeatureTransmissionContract{
	public static interface Event extends Serializable{}
	public static interface DateRenderingContext{
		<E> DaterRenderer<E> getRenderer(Dater<E> dater);
	}
	private static final Map<String,Feature<? extends Calendar<?>>> registeredCalendars=new HashMap<>();
	private static final Map<Class<? extends Dater<?>>,DaterRenderer<?>> daterRenderers=new HashMap<>();

	public static void registerDaterRenderer(Class<? extends Dater<?>> daterClass,DaterRenderer<?> renderer){
		daterRenderers.put(daterClass,renderer);
	}
	@SuppressWarnings("unchecked")
	public static <E> DaterRenderer<E> getDaterRenderer(Class<? extends Dater<E>> daterClass){
		return (DaterRenderer<E>)daterRenderers.get(daterClass);
	}

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
	public Calendar<T> setEventFiller(Consumer<HashMap<LocalDate,List<T>>> eventFiller){
		setEventFillerOp.accept(eventFiller);
		return this;
	}
	public Calendar<T> setDater(Dater<List<T>> dater){
		setDaterOp.accept(dater);
		return this;
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

	/**
	 * Registers a calendar, if it is not registered yet.
	 * @return the feature, registered or already present
	 */
	public static <T extends Calendar.Event> Feature<Calendar<T>> registerCalendar(String name,Class<T> eventClass){
		Calendar<T> calendar=new Calendar<>(name);
		Feature<Calendar<T>> feature=new Feature<>(calendar);
		feature.load();
		return registerCalendar(name,feature);
	}
	public static <T extends Calendar.Event> Feature<Calendar<T>> registerCalendar(String name,Feature<Calendar<T>> feature){
		registeredCalendars.put(name,feature);
		return feature;
	}

	@SuppressWarnings("unchecked")
	public static <T extends Calendar.Event> Calendar<T> getCalendar(String name){
		Feature<?> feature=registeredCalendars.get(name);
		if(feature==null){ throw new IllegalArgumentException("Calendar with name '"+name+"' not found. Make sure to register it first."); }
		return (Calendar<T>)feature.getContract();
	}
}
