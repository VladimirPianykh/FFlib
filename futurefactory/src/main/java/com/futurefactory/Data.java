package com.futurefactory;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

class Data implements Serializable{
	private static Data instance;
	public static class Editable{
        //FILLIN
	}
    public ArrayList<Editable>editables=new ArrayList<Editable>();
	private Data(){
        //FILLIN
	}
	public static Data getInstance(){
		if(instance==null)try{
			FileInputStream fIS=new FileInputStream("C:/Users/user/AppData/Local/C1_factory/Data.ser");
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
			FileOutputStream fOS=new FileOutputStream("C:/Users/user/AppData/Local/C1_factory/Data.ser");
			ObjectOutputStream oOS=new ObjectOutputStream(fOS);
			oOS.writeObject(instance);
			oOS.close();fOS.close();
		}catch(IOException ex){ex.printStackTrace();}
	}
}