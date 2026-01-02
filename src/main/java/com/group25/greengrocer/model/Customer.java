package com.group25.greengrocer.model;

/**
 * Represents a customer user in the greengrocer application.
 * Customers can browse products, add items to cart, place orders, and track deliveries.
 * 
 * Customers have additional attributes such as address and phone number for delivery purposes.
 * They may also have an individual loyalty rate that provides discounts on purchases.
 */
public class Customer extends User {

    private String address;
    
    private String phoneNumber;
    
    private java.math.BigDecimal individualLoyaltyRate;

    /**
     * Constructs a new Customer with the specified details.
     * 
     * @param id the unique identifier for the customer
     * @param username the username for authentication
     * @param password the password for authentication
     * @param address the delivery address
     * @param phoneNumber the contact phone number
     */
    public Customer(int id, String username, String password, String address, String phoneNumber) {
        super(id, username, password, "customer");
        this.address = address;
        this.phoneNumber = phoneNumber;
    }

    /**
     * Gets the delivery address of the customer.
     * 
     * @return the customer's address
     */
    public String getAddress() {
        return address;
    }

    /**
     * Gets the phone number of the customer.
     * 
     * @return the customer's phone number
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Sets the delivery address of the customer.
     * 
     * @param address the new delivery address
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Sets the phone number of the customer.
     * 
     * @param phoneNumber the new phone number
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * Gets the individual loyalty rate for this customer.
     * The loyalty rate is applied as a discount on purchases.
     * 
     * @return the loyalty rate as a BigDecimal
     */
    public java.math.BigDecimal getIndividualLoyaltyRate() {
        return individualLoyaltyRate;
    }

    /**
     * Sets the individual loyalty rate for this customer.
     * 
     * @param individualLoyaltyRate the new loyalty rate
     */
    public void setIndividualLoyaltyRate(java.math.BigDecimal individualLoyaltyRate) {
        this.individualLoyaltyRate = individualLoyaltyRate;
    }
}
