package com.bpa4j.core;

import java.io.Serializable;
import java.util.List;
import com.bpa4j.core.User.Permission;
import com.bpa4j.core.User.Role;
import com.bpa4j.feature.Feature;

/**
 * Persistent storage for editable groups and globals and
 * non-persistent storage for roles, features and permission.
 */
public interface Data{
	void register(Role...r);
	void register(Feature<?>...r);
	void register(Permission...r);
	void register(EditableGroup<?>...r);
	default void register(Role role,Feature<?>[] features,Permission[] permissions){
		//Shiftmake?
		User.ftrMap.put(role,features);
		User.permissions.put(role,permissions);
		if(role!=null){
			register(role);
			if(features!=null) register(features);
			if(permissions!=null) register(permissions);
		}
	}

	List<User.Role>getRegisteredRoles();
	List<Feature<?>>getRegisteredFeatures();
	List<User.Permission>getRegisteredPermissions();
	List<EditableGroup<?>>getRegisteredEditableGroups();

	@SuppressWarnings("unchecked")
	default<E extends Editable>EditableGroup<E>getGroup(Class<E>type){
		for(EditableGroup<?>g:getRegisteredEditableGroups())
			if(g.type.equals(type))return (EditableGroup<E>)g;
		throw new IllegalArgumentException("There is no registered group with type "+type+".");
	}
	void putGlobal(String key,Serializable value);
	Serializable getGlobal(String key);
}
