package com.group25.greengrocer.model;

public class Carrier extends User {
    // Carrier specific fields if any, otherwise just role customization
    public Carrier(int id, String username, String password) {
        super(id, username, password, "carrier");
    }
}
