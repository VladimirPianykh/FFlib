package com.futurefactory.defaults;

import com.futurefactory.User.Role;

public enum DefaultRole implements Role{
	EMPTY{
		public String toString(){return "НЕ ЗАДАНА";}
	},
	ADMIN{
		public String toString(){return "АДМИНИСТРАТОР";}
	}
}