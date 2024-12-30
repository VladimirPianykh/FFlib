package com.bpa4j.core;

import java.util.Arrays;

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
}
