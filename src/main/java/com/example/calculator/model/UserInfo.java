package com.example.calculator.model;

public class UserInfo {
    private final int id;
    private final String username;
    private final String password;

    public UserInfo(int id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
