package com.futurefactory;

import java.util.Arrays;

import com.futurefactory.Data.EditableGroup;
import com.futurefactory.User.Feature;
import com.futurefactory.User.Permission;
import com.futurefactory.User.Role;

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
}
