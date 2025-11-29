package com.bpa4j.data.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import com.bpa4j.core.Data;
import com.bpa4j.core.EditableGroup;
import com.bpa4j.core.User;
import com.bpa4j.core.User.Permission;
import com.bpa4j.core.User.Role;
import com.bpa4j.feature.Feature;

public class FileData implements Data{
	private final HashMap<String,Serializable> global=new HashMap<>();
	private final HashMap<String,Object[]> featuresData=new HashMap<>();
	private final List<EditableGroup<?>> editableGroups=new CopyOnWriteArrayList<>();

	public HashMap<String,Object[]> getFeaturesData(){
		return featuresData;
	}

	void save(File file){
		try{
			file.getParentFile().mkdirs();
			try(ZipOutputStream zos=new ZipOutputStream(new FileOutputStream(file))){
				zos.putNextEntry(new ZipEntry("global.ser"));
				ObjectOutputStream oos=new ObjectOutputStream(zos);
				oos.writeObject(global);
				oos.flush();
				zos.closeEntry();

				zos.putNextEntry(new ZipEntry("featuresData.ser"));
				oos=new ObjectOutputStream(zos);
				oos.writeObject(featuresData);
				oos.flush();
				zos.closeEntry();

				zos.putNextEntry(new ZipEntry("editableGroups.ser"));
				oos=new ObjectOutputStream(zos);
				oos.writeObject(new ArrayList<>(editableGroups));
				oos.flush();
				zos.closeEntry();
			}
		}catch(IOException ex){
			throw new UncheckedIOException(ex);
		}
	}
	@SuppressWarnings("unchecked")
	void load(File file){
		if(!file.exists()) return;
		try{
			try(ZipInputStream zis=new ZipInputStream(new FileInputStream(file))){
				ZipEntry entry;
				while((entry=zis.getNextEntry())!=null){
					ObjectInputStream ois=new ObjectInputStream(zis);
					switch(entry.getName()){
						case "global.ser"->{
							HashMap<String,Serializable> loadedGlobal=(HashMap<String,Serializable>)ois.readObject();
							global.clear();
							global.putAll(loadedGlobal);
						}
						case "featuresData.ser"->{
							HashMap<String,Object[]> loadedFeaturesData=(HashMap<String,Object[]>)ois.readObject();
							featuresData.clear();
							featuresData.putAll(loadedFeaturesData);
						}
						case "editableGroups.ser"->{
							List<EditableGroup<?>> loadedGroups=(List<EditableGroup<?>>)ois.readObject();
							editableGroups.clear();
							editableGroups.addAll(loadedGroups);
						}
					}
					zis.closeEntry();
				}
			}
		}catch(IOException ex){
			throw new UncheckedIOException(ex);
		}catch(ClassNotFoundException ex){
			throw new IllegalStateException(ex);
		}
	}

	public void register(Role...roles){
		if(roles!=null){
			for(Role role:roles){
				if(role!=null&&!User.registeredRoles.contains(role)){
					User.registeredRoles.add(role);
				}
			}
		}
	}
	public void register(Feature<?>...features){
		if(features!=null){
			for(Feature<?> feature:features){
				if(feature!=null&&!User.registeredFeatures.contains(feature)){
					User.registeredFeatures.add(feature);
				}
			}
		}
	}
	public void register(Permission...permissions){
		if(permissions!=null){
			for(Permission permission:permissions){
				if(permission!=null&&!User.registeredPermissions.contains(permission)){
					User.registeredPermissions.add(permission);
				}
			}
		}
	}
	public void register(EditableGroup<?>...groups){
		if(groups!=null){
			for(EditableGroup<?> group:groups){
				if(group!=null&&!this.editableGroups.contains(group)){
					this.editableGroups.add(group);
				}
			}
		}
	}

	public List<Role> getRegisteredRoles(){
		return new ArrayList<>(User.registeredRoles);
	}
	public List<Feature<?>> getRegisteredFeatures(){
		return new ArrayList<>(User.registeredFeatures);
	}
	public List<Permission> getRegisteredPermissions(){
		return new ArrayList<>(User.registeredPermissions);
	}
	public List<EditableGroup<?>> getRegisteredEditableGroups(){
		return new ArrayList<>(editableGroups);
	}

	public void putGlobal(String key,Serializable value){
		if(key==null) throw new IllegalArgumentException("Key cannot be null");
		global.put(key,value);
	}
	public Object getGlobal(String key){
		if(key==null) throw new IllegalArgumentException("Key cannot be null");
		return global.get(key);
	}
}
