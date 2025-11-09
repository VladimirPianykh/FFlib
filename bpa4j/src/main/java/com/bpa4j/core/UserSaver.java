package com.bpa4j.core;

import java.util.HashMap;

public interface UserSaver{
    void saveUsers(HashMap<String,User>userMap);
    HashMap<String,User>loadUsers();
}
