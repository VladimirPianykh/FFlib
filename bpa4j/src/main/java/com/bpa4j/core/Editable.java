package com.bpa4j.core;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;

import com.bpa4j.navigation.TaskLoc;

/**
 * An arbitrary object. Takes main place in the bpa4j system.
 * Override {@link #getImplementedInfo()} to insert navigation info.
 */
public abstract class Editable implements Serializable,TaskLoc{
	public static class ActionRecord implements Serializable{
		public String text;
		public User source;
		public LocalDateTime time;
		public ActionRecord(String text,User source){this.text=text;this.source=source;time=LocalDateTime.now();}
	}
	public String name;
	public ArrayList<ActionRecord>records=new ArrayList<ActionRecord>();
	public Editable(String name){
		this.name=name;
		records.add(new ActionRecord(":CREATED",User.getActiveUser()));
	}
	public String toString(){return name;}
}