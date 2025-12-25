package com.group25.greengrocer.model;

public class Owner extends User {
    public Owner(int id, String username, String password) {
        super(id, username, password, "owner");
    }
}
