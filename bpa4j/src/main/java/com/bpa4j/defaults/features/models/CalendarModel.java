package com.bpa4j.defaults.features.models;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;
import com.bpa4j.Dater;
import com.bpa4j.defaults.features.transmission_contracts.Calendar;
import com.bpa4j.feature.FeatureModel;

public class CalendarModel<T extends Calendar.Event> implements FeatureModel<Calendar<T>>{
    private Calendar<T> ftc;
    private HashMap<LocalDate,List<T>> events=new HashMap<>();
    private transient Consumer<HashMap<LocalDate,List<T>>> eventFiller;
    private transient Dater<List<T>> dater;
    public CalendarModel(Calendar<T> ftc){
        this.ftc=ftc;
        ftc.setGetEventsOp(()->getEvents());
        ftc.setGetEventListOp((date,existing)->getEventList(date));
        ftc.setClearEventsOp((events)->clearEvents());
        ftc.setSetEventFillerOp((filler)->setEventFiller(filler));
        ftc.setSetDaterOp((dater)->setDater(dater));
        ftc.setGetEventFillerOp(()->getEventFiller());
        ftc.setGetDaterOp(()->getDater());
    }
    public Calendar<T> getTransmissionContract(){
        return ftc;
    }
    public HashMap<LocalDate,List<T>> getEvents(){
        return events;
    }
    public List<T> getEventList(LocalDate date){
        if(!events.containsKey(date)) events.put(date,new ArrayList<T>());
        return events.get(date);
    }
    public void clearEvents(){
        events.clear();
    }
    public void setEventFiller(Consumer<HashMap<LocalDate,List<T>>> eventFiller){
        this.eventFiller=eventFiller;
    }
    public void setDater(Dater<List<T>> dater){
        this.dater=dater;
    }
    public Dater<List<T>> getDater(){
        return dater;
    }
    public Consumer<HashMap<LocalDate,List<T>>> getEventFiller(){
        return eventFiller;
    }
}
