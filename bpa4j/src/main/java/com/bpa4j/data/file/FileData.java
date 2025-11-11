package com.bpa4j.data.file;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.bpa4j.core.Data;
import com.bpa4j.core.EditableGroup;
import com.bpa4j.core.User.Permission;
import com.bpa4j.core.User.Role;
import com.bpa4j.feature.Feature;
import java.io.ObjectOutputStream;

public class FileData implements Data{
    private final HashMap<String,Serializable> global=new HashMap<>();
    private final HashMap<String,Object[]> featuresData=new HashMap<>();
    private final List<Role> roles=new CopyOnWriteArrayList<>();
    private final List<Feature<?>> features=new CopyOnWriteArrayList<>();
    private final List<Permission> permissions=new CopyOnWriteArrayList<>();
    private final List<EditableGroup<?>> editableGroups=new CopyOnWriteArrayList<>();

    public HashMap<String,Object[]> getFeaturesData(){
        return featuresData;
    }
    void save(File file){
        try{
            ObjectOutputStream out=new ObjectOutputStream(new FileOutputStream(file));
            out.writeObject(global);
            out.close();
        }catch(IOException e){
            throw new RuntimeException("Failed to save FileData",e);
        }
    }

    public void register(Role...roles){
        if(roles!=null){
            for(Role role:roles){
                if(role!=null&&!this.roles.contains(role)){
                    this.roles.add(role);
                }
            }
        }
    }

    public void register(Feature<?>...features){
        if(features!=null){
            for(Feature<?> feature:features){
                if(feature!=null&&!this.features.contains(feature)){
                    this.features.add(feature);
                }
            }
        }
    }

    public void register(Permission...permissions){
        if(permissions!=null){
            for(Permission permission:permissions){
                if(permission!=null&&!this.permissions.contains(permission)){
                    this.permissions.add(permission);
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

    public void register(Role role,Feature<?>[] features,Permission[] permissions){
        if(role!=null){
            register(role);
            if(features!=null) register(features);
            if(permissions!=null) register(permissions);
        }
    }
    public List<Role> getRegisteredRoles(){
        return new ArrayList<>(roles);
    }

    public List<Feature<?>> getRegisteredFeatures(){
        return new ArrayList<>(features);
    }

    public List<Permission> getRegisteredPermissions(){
        return new ArrayList<>(permissions);
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
