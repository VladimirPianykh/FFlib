package com.bpa4j.defaults.editables.processable_attributes;

import com.bpa4j.SerializableFunction;
import com.bpa4j.core.Editable;
import com.bpa4j.core.User;
import com.bpa4j.core.User.Permission;
import com.bpa4j.defaults.editables.Processable;
import com.bpa4j.ui.swing.util.Message;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class LimitToChecker<T extends Editable>implements SerializableFunction<Processable,Boolean>{
	private final SerializableFunction<Processable,Boolean>checker;
	private final Permission permission;
	public Boolean apply(Processable t){
		if(User.getActiveUser().hasPermission(permission))return checker.apply(t);
		else{
			new Message("У текущего пользователя нет разрешения на это действие.",java.awt.Color.RED);
			return false;
		}
	}
}
