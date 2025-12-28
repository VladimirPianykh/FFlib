package com.bpa4j.core;

import java.util.HashMap;

public interface UserSaver{
    /**
     * Does not throw any checked exceptions, bad design for simplicity.
     */
    void saveUsers(HashMap<String,User>userMap);
    /**
     * Does not throw any checked exceptions, bad design for simplicity.
     */
    HashMap<String,User>loadUsers();
}
