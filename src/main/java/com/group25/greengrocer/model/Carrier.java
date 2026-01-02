package com.group25.greengrocer.model;

/**
 * Represents a carrier user in the greengrocer application.
 * Carriers are responsible for delivering orders to customers.
 * 
 * <p>Carriers can view available orders, select orders to deliver, and mark orders as completed.
 * They store license images (front and back) for verification purposes.</p>
 */
public class Carrier extends User {
    
    private byte[] licenseFront;
    
    private byte[] licenseBack;

    /**
     * Constructs a new Carrier with the specified details.
     * 
     * @param id the unique identifier for the carrier
     * @param username the username for authentication
     * @param password the password for authentication
     */
    public Carrier(int id, String username, String password) {
        super(id, username, password, "carrier");
    }

    /**
     * Sets the front side image of the carrier's license.
     * 
     * @param licenseFront the license front image as byte array
     */
    public void setLicenseFront(byte[] licenseFront) {
        this.licenseFront = licenseFront;
    }

    /**
     * Gets the front side image of the carrier's license.
     * 
     * @return the license front image as byte array
     */
    public byte[] getLicenseFront() {
        return licenseFront;
    }

    /**
     * Sets the back side image of the carrier's license.
     * 
     * @param licenseBack the license back image as byte array
     */
    public void setLicenseBack(byte[] licenseBack) {
        this.licenseBack = licenseBack;
    }

    /**
     * Gets the back side image of the carrier's license.
     * 
     * @return the license back image as byte array
     */
    public byte[] getLicenseBack() {
        return licenseBack;
    }
}
