package com.bpa4j.core;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.UncheckedIOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Consumer;

import javax.swing.JPanel;

import com.bpa4j.defaults.DefaultPermission;
import com.bpa4j.defaults.DefaultRole;
import com.bpa4j.defaults.features.DefaultFeature;
import com.bpa4j.ui.Message;

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
	public static interface Role extends Serializable{}
	public static interface Feature extends Serializable{
		public void paint(Graphics2D g2,BufferedImage image,int h);
		public void fillTab(JPanel content,JPanel tab,Font font);
	}
	public static interface Permission extends Serializable{
		public String name();
	}
	private static HashMap<String,User>userMap;
	public static ArrayList<Role>registeredRoles=new ArrayList<>();
	public static ArrayList<Permission>registeredPermissions=new ArrayList<>();
	public static ArrayList<Feature>registeredFeatures=new ArrayList<>();
	public static HashMap<Role,Permission[]>permissions=new HashMap<>();
	static{
		for(Role r:DefaultRole.values())registeredRoles.add(r);
		for(Feature f:DefaultFeature.values())registeredFeatures.add(f);
		for(Permission f:DefaultPermission.values())registeredPermissions.add(f);
		permissions.put(DefaultRole.EMPTY,new Permission[]{});
		permissions.put(DefaultRole.ADMIN,registeredPermissions.toArray(new Permission[0]));
	}
	private static User currentUser;
	public String login,password;
	public Role role;
	public int passTries,tries;
	public LocalDateTime lockTime=null;
	public ArrayDeque<Authorization>history=new ArrayDeque<Authorization>();
	@SuppressWarnings("unchecked")
	private static void load(){
		try{
			FileInputStream fIS=new FileInputStream(Root.folder+"Users.ser"+ProgramStarter.version);
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
			if(u.lockTime.until(LocalDateTime.now(),ChronoUnit.MINUTES)<5)System.exit(1);
			else u.lockTime=null;
		}
	}
	public static User register(String login,String pass){
		if(userMap==null)load();
		currentUser=new User(login,pass,userMap.isEmpty()?DefaultRole.ADMIN:DefaultRole.EMPTY);
		userMap.put(login,currentUser);
		save();
		return currentUser;
	}
	public static User register(String login,String pass,Role role){
		if(userMap==null)load();
		currentUser=new User(login,pass,userMap.isEmpty()?DefaultRole.ADMIN:DefaultRole.EMPTY);
		currentUser.role=role;
		userMap.put(login,currentUser);
		save();
		return currentUser;
	}
	public static User getActiveUser(){
		if(currentUser==null)throw new RuntimeException("No user registered!");
		return currentUser;
	}
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
			new File(Root.folder).mkdirs();
			FileOutputStream fOS=new FileOutputStream(Root.folder+"Users.ser"+ProgramStarter.version);
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
		if(p==null)return false;
		for(Permission m:permissions.get(role))if(m==p)return true;
		return false;
	}
	public boolean login(String password){
		if(password.equals(this.password)){
			lockTime=null;
			currentUser=this;
			history.addFirst(new Authorization(this));
			passTries=0;tries=0;
			Runtime.getRuntime().addShutdownHook(new Thread(){public void run(){logout();}});
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
			new Message("Неверный пароль.",Color.RED);
			return false;
		}
	}
	public void logout(){
		history.getFirst().writeLogout();
		save();
	}
	public String toString(){return login;}
}