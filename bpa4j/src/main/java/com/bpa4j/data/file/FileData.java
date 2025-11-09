package com.bpa4j.data.file;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import com.bpa4j.core.Data;
import com.bpa4j.core.EditableGroup;
import com.bpa4j.core.User.Permission;
import com.bpa4j.core.User.Role;
import com.bpa4j.feature.Feature;
import java.io.ObjectOutputStream;

public class FileData implements Data{
	private HashMap<String,Serializable> global=new HashMap<>();
	void save(File file){
		try{
			ObjectOutputStream out=new ObjectOutputStream(new FileOutputStream(file));
			out.writeObject(global);
			out.close();
		}catch(IOException e){
			throw new RuntimeException("Failed to save FileData",e);
		}
	}
	public void register(Role...r){
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'register'");
	}
	public void register(Feature<?>...r){
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'register'");
	}
	public void register(Permission...r){
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'register'");
	}
	public void register(EditableGroup<?>...r){
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'register'");
	}
	public void register(Role r,Feature<?>[] f,Permission[] p){
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'register'");
	}
	public List<Role> getRegisteredRoles(){
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'getRegisteredRoles'");
	}
	public List<Feature<?>> getRegisteredFeatures(){
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'getRegisteredFeatures'");
	}
	public List<Permission> getRegisteredPermissions(){
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'getRegisteredPermissions'");
	}
	public List<EditableGroup<?>> getRegisteredEditableGroups(){
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'getRegisteredEditableGroups'");
	}
	public void putGlobal(String key,Serializable value){
		global.put(key,value);
		int a=0;
	}
	public Object getGlobal(String key){
		return global.get(key);
	}
}
