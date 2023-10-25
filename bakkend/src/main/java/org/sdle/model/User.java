package org.sdle.model;

import org.sdle.service.UserRepository;

public class User {
    private String name;
    private String password;

    public User(){}

    public User(String name, String password){
        this.name = name;
        this.password = password;
    }

    public boolean authenticate(UserRepository repository){
        return false;
    }
}
