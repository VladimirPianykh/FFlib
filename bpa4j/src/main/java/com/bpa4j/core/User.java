package com.bpa4j.core;

import java.awt.Color;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import com.bpa4j.Wrapper;
import com.bpa4j.defaults.DefaultPermission;
import com.bpa4j.defaults.DefaultRole;
import com.bpa4j.feature.Feature;
import com.bpa4j.navigation.HelpView.StartInstruction;
import com.bpa4j.navigation.ImplementedInfo;
import com.bpa4j.navigation.TaskLoc;
import com.bpa4j.ui.swing.util.Message;

public class User implements Serializable{
	/**
	 * Pseudo-encapsulation mechanism to protect {@link #createUser(String,String,Role,int,int,LocalDateTime)}
	 * from unwanted access.
	 */
	public static class SerialUserFactory{
		public static User createUser(String login, String password, Role role, int fail2banTries, int tries, LocalDateTime lockTime) {
			return new User(login,password,role,fail2banTries,tries,lockTime);
		}
	}
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
		public void writeLogout(){
			outTime=LocalDateTime.now();
		}
	}
	public static interface Role extends Serializable,TaskLoc{
		default List<ImplementedInfo> getImplementedInfo(){
			Wrapper<User> user=new Wrapper<>(null);
			for(User u:User.userMap.values())
				if(u.role.equals(this)) user.var=u;
			if(user.var==null) return List.of();
			return Stream.of(User.ftrMap.get(this)).<ImplementedInfo>mapMulti((p,out)->p.getImplementedInfo().forEach(out)).<ImplementedInfo>map(info->info.appendInstruction(new StartInstruction(user.var))).toList();
		}
	}
	public static interface Permission extends Serializable{
		public String name();
	}
	private static HashMap<String,User> userMap;
	public static ArrayList<Role> registeredRoles=new ArrayList<>();
	public static ArrayList<Permission> registeredPermissions=new ArrayList<>();
	public static ArrayList<Feature<?>> registeredFeatures=new ArrayList<>();
	public static HashMap<Role,Permission[]> permissions=new HashMap<>();
	static{
		for(Role r:DefaultRole.values())
			registeredRoles.add(r);
		for(Permission f:DefaultPermission.values())
			registeredPermissions.add(f);
		permissions.put(DefaultRole.EMPTY,new Permission[]{});
		permissions.put(DefaultRole.ADMIN,registeredPermissions.toArray(new Permission[0]));
	}
	private static User currentUser;
	public String login,password;
	public Role role;
	/**
	 * Amount of consecutive attempts after the last successful login or the last fail2ban.
	 */
	private int fail2banTries;
	/**
	 * Amount of failed attempts after the last successfull login.
	 */
	private int tries;
	public LocalDateTime lockTime=null;
	public ArrayDeque<Authorization> history=new ArrayDeque<>();
	public static HashMap<Role,Feature<?>[]> ftrMap=new HashMap<>();
	public static User register(String login,String pass){
		if(userMap==null) load();
		currentUser=new User(login,pass,userMap.isEmpty()?DefaultRole.ADMIN:DefaultRole.EMPTY);
		userMap.put(login,currentUser);
		save();
		return currentUser;
	}
	public static User register(String login,String pass,Role role){
		if(userMap==null) load();
		currentUser=new User(login,pass,userMap.isEmpty()?DefaultRole.ADMIN:DefaultRole.EMPTY);
		currentUser.role=role;
		userMap.put(login,currentUser);
		save();
		return currentUser;
	}
	public static User getActiveUser(){
		if(currentUser==null) throw new RuntimeException("No user registered!");
		return currentUser;
	}
	public static int getUserCount(){
		if(userMap==null) load();
		return userMap.size();
	}
	public static User getUser(String s){
		if(userMap==null) load();
		return userMap.get(s);
	}
	public static boolean hasUser(String s){
		if(userMap==null) load();
		return userMap.containsKey(s);
	}
	public static void deleteUser(String login){
		userMap.remove(login);
	}
	public static void forEachUser(Consumer<User> c){
		if(userMap==null) load();
		for(User u:userMap.values())
			c.accept(u);
	}
	private static void load(){
		userMap=ProgramStarter.getStorageManager().getUserSaver().loadUsers();
	}
	public static void save(){
		ProgramStarter.getStorageManager().getUserSaver().saveUsers(userMap);;
	}
	private User(String login,String password,Role role,int fail2banTries,int tries,LocalDateTime lockTime){
		this.login=login;
		this.password=password;
		this.role=role;
		this.fail2banTries=fail2banTries;
		this.tries=tries;
		this.lockTime=lockTime;
	}
	private User(String login,String password,Role role){
		this.login=login;
		this.password=password;
		this.role=role;
		userMap.put(login,this);
	}
	public boolean hasPermission(Permission p){
		if(p==null) return false;
		for(Permission m:permissions.get(role))
			if(m==p) return true;
		return false;
	}
	public boolean login(String password){
		if(password.equals(this.password)){
			lockTime=null;
			currentUser=this;
			history.addFirst(new Authorization(this));
			fail2banTries=0;
			tries=0;
			Runtime.getRuntime().addShutdownHook(new Thread(){
				public void run(){
					logout();
				}
			});
			return true;
		}else if(fail2banTries>1){
			fail2banTries=2;
			lockTime=LocalDateTime.now();
			save();
			ProgramStarter.exit();
			return false;
		}else{
			fail2banTries++;
			tries++;
			save();
			new Message("Неверный пароль.",Color.RED);
			return false;
		}
	}
	public void logout(){
		history.getFirst().writeLogout();
		save();
	}
	public String toString(){
		return login;
	}
	public int getFail2banTries(){
		return fail2banTries;
	}
	public int getTries(){
		return tries;
	}
}