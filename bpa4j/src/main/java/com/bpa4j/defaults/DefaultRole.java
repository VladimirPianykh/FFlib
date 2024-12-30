package com.bpa4j.defaults;

import com.bpa4j.core.User.Role;

public enum DefaultRole implements Role{
	EMPTY{
		public String toString(){return "НЕ ЗАДАНА";}
	},
	ADMIN{
		public String toString(){return "АДМИНИСТРАТОР";}
	}
}