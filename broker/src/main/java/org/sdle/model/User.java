package org.sdle.model;

public class User {
    private String username;
    private String password;

    public User(){}

    public User(String userName, String password) {
        this.username = userName;
        this.password = password;
    }

    public void setPassword(String password){
        this.password = password;
    }

    public void setUsername(String username){
        this.username = username;
    }

    public String getPassword(){
        return this.password;
    }
}
