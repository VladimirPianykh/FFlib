package com.futurefactory;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.function.Consumer;

public class User implements Serializable{
	public static class Authorization implements Serializable{
		public LocalDateTime inTime,outTime;
		public int tries;
		public String login;
		public InetAddress ip;
		public Authorization(User u){
			inTime=LocalDateTime.now();
			tries=u.tries;
			login=u.login;
			try{
				ip=InetAddress.getLocalHost();
			}catch(UnknownHostException ex){}
		}
		public void writeLogout(){outTime=LocalDateTime.now();}
	}
	public static enum Role{
		EMPTY,
		ADMIN,
		// STOREKEEPER,
		// ENGINEER,
		// TESTER,
		// PRODUCTION_MANAGER,
		// PROCUREMENT_MANAGER,
		// PD_MANAGER,
		// SD_MANAGER,
		// SALES_MANAGER,
	}
	public static enum Feature{
		HISTORY,
		ROLE_SETTING,
		MODEL_EDITING,
	}
	public static enum Permission{
		CREATE,
	}
	private static HashMap<String,User>userMap;
	private static HashMap<Role,Permission[]>permissions=new HashMap<Role,Permission[]>();
	static{
		permissions.put(Role.EMPTY,new Permission[]{});
		permissions.put(Role.ADMIN,Permission.values());
		// permissions.put(Role.,new Permission[]{});
	}
	private static User user;
	public String login,password;
	public Role role;
	public int passTries,tries;
	public LocalDateTime lockTime=null;
	public ArrayDeque<Authorization>history=new ArrayDeque<Authorization>();
	@SuppressWarnings("unchecked")
	private static void load(){
		try{
			FileInputStream fIS=new FileInputStream("C:/Users/user/AppData/Local/C1_factory/Users.ser");
			ObjectInputStream oIS=new ObjectInputStream(fIS);
			userMap=(HashMap<String,User>)oIS.readObject();
			oIS.close();fIS.close();
		}catch(IOException ex){
			userMap=new HashMap<String,User>();
		}catch(ClassNotFoundException ex){throw new RuntimeException("FATAL ERROR: Users corrupted");}
		for(User u:userMap.values())if(u.lockTime!=null){
			if(u.lockTime.until(LocalDateTime.now(),ChronoUnit.MINUTES)<5)System.exit(1);
			else u.lockTime=null;
		}
	}
	public static void register(String login,String pass){
		if(userMap==null)load();
		user=new User(login,pass,userMap.isEmpty()?Role.ADMIN:Role.EMPTY);
		userMap.put(login,user);
	}
	public static User getActiveUser(){return user;}
	public static int getUserCount(){
		if(userMap==null)load();
		return userMap.size();
	}
	public static User getUser(String s){
		if(userMap==null)load();
		return userMap.get(s);
	}
	public static boolean hasUser(String s){
		if(userMap==null)load();
		return userMap.containsKey(s);
	}
	public static void deleteUser(String login){userMap.remove(login);}
	public static void forEachUser(Consumer<User>c){
		if(userMap==null)load();
		for(User u:userMap.values())c.accept(u);
	}
	public static void save(){
		try{
			FileOutputStream fOS=new FileOutputStream("C:/Users/user/AppData/Local/C1_factory/Users.ser");
			ObjectOutputStream oOS=new ObjectOutputStream(fOS);
			oOS.writeObject(User.userMap);
			oOS.close();fOS.close();
		}catch(IOException ex){ex.printStackTrace();}
	}
	private User(String login,String password,Role role){
		this.login=login;this.password=password;this.role=role;
		userMap.put(login,this);
	}
	public boolean hasPermission(Permission p){
		for(Permission m:permissions.get(role))if(m==p)return true;
		return false;
	}
	public boolean login(String password){
		if(password.equals(this.password)){
			lockTime=null;
			user=this;
			history.addFirst(new Authorization(this));
			passTries=0;tries=0;
			ProgramStarter.frame=new WorkFrame(this);
			new Message("Logged in succesfully!");
			Runtime.getRuntime().addShutdownHook(new Thread(){
				public void run(){logout();}
			});
			return true;
		}
		else if(passTries>1){
			passTries=2;
			lockTime=LocalDateTime.now();
			save();
			System.exit(1);
			return false;
		}else{
			passTries++;tries++;
			save();
			new Message("Incorrect password.");
			return false;
		}
	}
	public void logout(){
		history.getFirst().writeLogout();
		save();
	}
}