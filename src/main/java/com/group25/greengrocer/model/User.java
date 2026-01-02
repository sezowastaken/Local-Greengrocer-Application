package com.group25.greengrocer.model;

/**
 * Abstract base class representing a user in the greengrocer application.
 * This class serves as the parent for specific user types such as Customer, Carrier, and Owner.
 * 
 * All users have common attributes including username, password, role, and status.
 * The status field determines whether the user account is active or not.
 */
public abstract class User {
    
    protected int id;
    
    protected String username;
    
    protected String password;
    
    protected String role;
    
    protected String status = "APPROVED";

    /**
     * Constructs a new User with the specified details.
     * 
     * @param id the unique identifier for the user
     * @param username the username for authentication
     * @param password the password for authentication
     * @param role the role of the user (CUSTOMER, CARRIER, or OWNER)
     */
    public User(int id, String username, String password, String role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    /**
     * Gets the unique identifier of the user.
     * 
     * @return the user ID
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the username of the user.
     * 
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Gets the password of the user.
     * 
     * @return the password (hashed)
     */
    public String getPassword() {
        return password;
    }

    /**
     * Gets the role of the user.
     * 
     * @return the user role (CUSTOMER, CARRIER, or OWNER)
     */
    public String getRole() {
        return role;
    }

    /**
     * Gets the status of the user account.
     * 
     * @return the account status (APPROVED, PENDING, or REJECTED)
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the status of the user account.
     * 
     * @param status the new account status
     */
    public void setStatus(String status) {
        this.status = status;
    }
}
