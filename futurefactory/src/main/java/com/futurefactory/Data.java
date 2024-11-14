package com.futurefactory;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeSet;

/**
 * Represents all the data (except the user-data).
 */
public class Data implements Serializable{
	private static Data instance;
	public static class EditableGroup extends ArrayList<Editable>{
		public PathIcon icon;
		public EditableGroup(PathIcon icon,Editable...elements){
			this.icon=icon;
			addAll(Arrays.asList(elements));
		}
	}
	public static abstract class Editable{
		public static class ActionRecord{
			public String text;
			public User source;
			public LocalDateTime time;
			public ActionRecord(String text,User source){
				this.text=text;this.source=source;
				time=LocalDateTime.now();
			}
		}
		public String name;
		public ArrayList<ActionRecord>records=new ArrayList<ActionRecord>();
		public Editable(String name){
			this.name=name;
			records.add(new ActionRecord(":CREATED",User.getActiveUser()));
		}
	}
	public TreeSet<EditableGroup>editables=new TreeSet<EditableGroup>();
	private Data(){}
	public static Data getInstance(){
		if(instance==null)try{
			FileInputStream fIS=new FileInputStream(Root.folder+"Data.ser");
			ObjectInputStream oIS=new ObjectInputStream(fIS);
			instance=(Data)oIS.readObject();
			oIS.close();fIS.close();
		}catch(IOException ex){
			instance=new Data();
		}catch(ClassNotFoundException ex){throw new RuntimeException("FATAL ERROR: Data corrupted");}
		return instance;
	}
	static void save(){
		try{
			FileOutputStream fOS=new FileOutputStream(Root.folder+"Data.ser");
			ObjectOutputStream oOS=new ObjectOutputStream(fOS);
			oOS.writeObject(instance);
			oOS.close();fOS.close();
		}catch(IOException ex){ex.printStackTrace();}
	}
}