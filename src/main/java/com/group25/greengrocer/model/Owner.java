package com.group25.greengrocer.model;

/**
 * Represents an owner user in the greengrocer application.
 * The owner has administrative privileges to manage the entire system.
 * 
 * Owners can manage products, carriers, view all orders, handle customer messages,
 * set coupons and loyalty standards, and view various reports and statistics.
 */
public class Owner extends User {
    /**
     * Constructs a new Owner with the specified details.
     * 
     * @param id the unique identifier for the owner
     * @param username the username for authentication
     * @param password the password for authentication
     */
    public Owner(int id, String username, String password) {
        super(id, username, password, "owner");
    }
}
