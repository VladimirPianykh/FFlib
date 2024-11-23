package com.futurefactory;

import java.util.Arrays;

import com.futurefactory.User.Feature;
import com.futurefactory.User.Permission;
import com.futurefactory.User.Role;

public abstract class Registrator{
    public static void register(Permission...r){User.registeredPermissions.addAll(Arrays.asList(r));}
    public static void register(Feature...r){User.registeredFeatures.addAll(Arrays.asList(r));}
    public static void register(Role...r){User.registeredRoles.addAll(Arrays.asList(r));}
}
