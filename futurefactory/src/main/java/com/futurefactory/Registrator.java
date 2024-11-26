package com.futurefactory;

import java.util.Arrays;

import com.futurefactory.Data.EditableGroup;
import com.futurefactory.User.Feature;
import com.futurefactory.User.Permission;
import com.futurefactory.User.Role;

/**
 * Is used to register anything registerable.
 */
public abstract class Registrator{
    public static void register(Permission...r){User.registeredPermissions.addAll(Arrays.asList(r));}
    public static void register(Feature...r){User.registeredFeatures.addAll(Arrays.asList(r));}
    public static void register(Role...r){User.registeredRoles.addAll(Arrays.asList(r));}
    public static void register(EditableGroup<?>...r){for(EditableGroup<?>g:r)Data.getInstance().editables.add(g);}
}
