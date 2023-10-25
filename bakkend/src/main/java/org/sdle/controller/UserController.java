package org.sdle.controller;

import org.sdle.model.User;
import org.sdle.service.UserRepository;

public class UserController {
    private UserRepository repository;

    public UserController(UserRepository repository){
        this.repository = repository;
    }

    public User login(){
        return null;
    }

    public void logout(){
    }
}
