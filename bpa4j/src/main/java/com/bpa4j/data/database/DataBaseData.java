package com.bpa4j.data.database;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.TreeMap;
import com.bpa4j.core.Data;
import com.bpa4j.core.Editable;
import com.bpa4j.core.EditableGroup;
import com.bpa4j.core.User.Permission;
import com.bpa4j.core.User.Role;
import com.bpa4j.feature.Feature;

public class DataBaseData implements Data{
    private ArrayList<Role> registeredRoles=new ArrayList<>();
    private ArrayList<Permission> registeredPermissions=new ArrayList<>();
    private ArrayList<Feature<?>> registeredFeatures=new ArrayList<>();
    private ArrayList<EditableGroup<?>> registeredEditableGroups=new ArrayList<>();
    private TreeMap<String,Serializable> globals;

    public void register(Role...roles){
        Objects.requireNonNull(roles);
        for(Role role:roles){
            if(!registeredRoles.contains(role)){
                registeredRoles.add(role);
            }
        }
    }
    public void register(Permission...permissions){
        Objects.requireNonNull(permissions);
        for(Permission permission:permissions){
            if(!registeredPermissions.contains(permission)){
                registeredPermissions.add(permission);
            }
        }
    }
    public void register(Feature<?>...features){
        Objects.requireNonNull(features);
        for(Feature<?> feature:features){
            if(!registeredFeatures.contains(feature)){
                registeredFeatures.add(feature);
            }
        }
    }
    public void register(EditableGroup<?>...groups){
        Objects.requireNonNull(groups);
        for(EditableGroup<?> group:groups){
            if(!registeredEditableGroups.contains(group)){
                registeredEditableGroups.add(group);
            }
        }
    }

    public ArrayList<Role> getRegisteredRoles(){
        return registeredRoles;
    }
    public ArrayList<Feature<?>> getRegisteredFeatures(){
        return registeredFeatures;
    }
    public ArrayList<Permission> getRegisteredPermissions(){
        return registeredPermissions;
    }
    public ArrayList<EditableGroup<?>> getRegisteredEditableGroups(){
        return registeredEditableGroups;
    }

    public Serializable getGlobal(String key){
        return globals.get(key);
    }
    public void putGlobal(String key,Serializable value){
        globals.put(key,value);
    }

    void save(Connection conn)throws SQLException{
        @SuppressWarnings("rawtypes")
        Class<EditableGroup> groupClass=EditableGroup.class;

        if(!EditableORM.isGroupTablePresent(conn,groupClass))
            EditableORM.createGroupTable(conn,groupClass);
        else EditableORM.clearGroupTable(conn,groupClass);
        
        for(EditableGroup<?>g:registeredEditableGroups){
            EditableORM.writeToGroupTable(conn,g);
            if(!EditableORM.isTablePresent(conn,g.type))
                EditableORM.createTableFrom(conn,g.type);
            else EditableORM.clearTable(conn,g.type);
            for(Editable e:g)EditableORM.writeToTable(conn,e);
        }
    }
    void load(Connection conn)throws SQLException{
        if(!EditableORM.isGroupTablePresent(conn,EditableGroup.class))return;
        //FIXME: load
    }
}
