package org.broker.model;

import org.broker.service.AuthService;

public class User {
    private final String userName;
    private final String password;


    public User(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    public String getPassword(){
        return this.password;
    }
}
