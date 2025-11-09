package com.bpa4j.core;

import java.io.Serializable;
import java.util.List;

import com.bpa4j.feature.Feature;

public interface Data{
    void register(User.Role...r);
	void register(Feature<?>...r);
	void register(User.Permission...r);
	void register(EditableGroup<?>...r);
	void register(User.Role r,Feature<?>[]f,User.Permission[]p);

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
	Object getGlobal(String key);
}
