package com.futurefactory.defaults;

import com.futurefactory.core.User.Role;

public enum DefaultRole implements Role{
	EMPTY{
		public String toString(){return "НЕ ЗАДАНА";}
	},
	ADMIN{
		public String toString(){return "АДМИНИСТРАТОР";}
	}
}