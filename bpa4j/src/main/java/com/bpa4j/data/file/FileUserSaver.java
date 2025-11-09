package com.bpa4j.data.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UncheckedIOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;

import com.bpa4j.core.ProgramStarter;
import com.bpa4j.core.Root;
import com.bpa4j.core.User;
import com.bpa4j.core.UserSaver;

public class FileUserSaver implements UserSaver{
	private FileStorageManager sm;
	public FileUserSaver(FileStorageManager manager){
		this.sm=manager;
	}
	public void saveUsers(HashMap<String,User>userMap){
		try{
			new File(Root.folder).mkdirs();
			FileOutputStream fOS=new FileOutputStream(Root.folder+"Users.ser"+sm.getVersion());
			ObjectOutputStream oOS=new ObjectOutputStream(fOS);
			oOS.writeObject(userMap);
			oOS.close();fOS.close();
		}catch(IOException ex){ex.printStackTrace();}
	}
	@SuppressWarnings("unchecked")
	public HashMap<String,User>loadUsers(){
		HashMap<String,User>userMap=new HashMap<>();
		try{
			FileInputStream fIS=new FileInputStream(sm.getFolder()+"Users.ser"+sm.getVersion());
			ObjectInputStream oIS=new ObjectInputStream(fIS);
			userMap=(HashMap<String,User>)oIS.readObject();
			oIS.close();fIS.close();
		}catch(FileNotFoundException ex){
			try{
				InputStream is=Root.CL.getResourceAsStream("resources/initial/Users.ser");
				if(is==null)is=Root.RCL.getResourceAsStream("resources/initial/Users.ser");
				if(is==null)userMap=new HashMap<String,User>();
				else{
					ObjectInputStream oIS=new ObjectInputStream(is);
					userMap=(HashMap<String,User>)oIS.readObject();
					oIS.close();
				}
			}catch(IOException ex2){throw new UncheckedIOException(ex2);}
			catch(ClassNotFoundException ex2){throw new IllegalStateException(ex2);}
		}catch(IOException ex){throw new UncheckedIOException(ex);}
		catch(ClassNotFoundException ex){throw new IllegalStateException("FATAL ERROR: Users corrupted");}
		for(User u:userMap.values())if(u.lockTime!=null){
			if(u.lockTime.until(LocalDateTime.now(),ChronoUnit.MINUTES)<5)ProgramStarter.exit();
			else u.lockTime=null;
		}
		return userMap;
	}
}
