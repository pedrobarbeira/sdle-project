package org.broker.service;

import org.broker.model.User;

import java.util.HashMap;

public class UserRepository {
    private final HashMap<String, User> userMap;

    public UserRepository(HashMap<String, User> userMap) {
        this.userMap = userMap;
    }

    public User getUser(String username){
        return userMap.get(username);
    }


}
