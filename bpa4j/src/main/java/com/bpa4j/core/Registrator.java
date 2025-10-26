package com.bpa4j.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.bpa4j.core.Data.EditableGroup;
import com.bpa4j.core.User.Feature;
import com.bpa4j.core.User.Permission;
import com.bpa4j.core.User.Role;

/**
 * Is used to declare some data.
 */
public abstract class Registrator{
    private static void register(Role...r){User.registeredRoles.addAll(Arrays.asList(r));}
    private static void register(Feature...r){User.registeredFeatures.addAll(Arrays.asList(r));}
    public static void register(Permission...r){User.registeredPermissions.addAll(Arrays.asList(r));}
    public static void register(EditableGroup<?>...r){for(EditableGroup<?>g:r)Data.getInstance().editables.add(g);}
    public static void register(Role r,Feature[]f,Permission[]p){
        register(r);
        register(f);
        register(p);
        User.permissions.put(r,p);
        WorkFrame.ftrMap.put(r,f);
    }
    
    /**
     * Gets a list of all registered roles
     * @return list of roles
     */
    public static List<Role> getRegisteredRoles(){
        return new ArrayList<>(User.registeredRoles);
    }
    
    /**
     * Gets a list of all registered features
     * @return list of features
     */
    public static List<Feature> getRegisteredFeatures(){
        return new ArrayList<>(User.registeredFeatures);
    }
    
    /**
     * Gets a list of all registered permissions
     * @return list of permissions
     */
    public static List<Permission> getRegisteredPermissions(){
        return new ArrayList<>(User.registeredPermissions);
    }
    
    /**
     * Gets a list of all registered editable groups
     * @return list of EditableGroup instances
     */
    public static List<EditableGroup<?>> getRegisteredEditableGroups(){
        return new ArrayList<>(Data.getInstance().editables);
    }
}
