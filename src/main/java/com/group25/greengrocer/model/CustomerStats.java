package com.group25.greengrocer.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Represents statistical information about a customer in the greengrocer application.
 * Used by the owner to view customer activity, spending, and loyalty information.
 * 
 * This class aggregates data from multiple sources to provide a comprehensive
 * view of customer behavior and value.
 * 
 */
public class CustomerStats {
    private Long id;
    
    private String username;
    
    private String fullName;
    
    private LocalDateTime registeredDate;
    
    private int totalOrders;
    
    private BigDecimal totalSpent;
    
    private BigDecimal individualLoyaltyRate;

    /**
     * Constructs a new CustomerStats with the specified details.
     * 
     * @param id the customer ID
     * @param username the customer username
     * @param fullName the customer's full name
     * @param registeredDate the registration date
     * @param totalOrders the total number of orders
     * @param totalSpent the total amount spent
     * @param individualLoyaltyRate the individual loyalty rate (can be null)
     */
    public CustomerStats(Long id, String username, String fullName, LocalDateTime registeredDate, int totalOrders,
            BigDecimal totalSpent, BigDecimal individualLoyaltyRate) {
        this.id = id;
        this.username = username;
        this.fullName = fullName;
        this.registeredDate = registeredDate;
        this.totalOrders = totalOrders;
        this.totalSpent = totalSpent;
        this.individualLoyaltyRate = individualLoyaltyRate;
    }

    /**
     * Gets the customer ID.
     * 
     * @return the customer ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Gets the customer username.
     * 
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Gets the customer's full name.
     * 
     * @return the full name
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * Gets the registration date.
     * 
     * @return the registered date
     */
    public LocalDateTime getRegisteredDate() {
        return registeredDate;
    }

    /**
     * Gets the total number of orders placed.
     * 
     * @return the total orders count
     */
    public int getTotalOrders() {
        return totalOrders;
    }

    /**
     * Gets the total amount spent by the customer.
     * 
     * @return the total spent amount
     */
    public BigDecimal getTotalSpent() {
        return totalSpent;
    }

    /**
     * Gets the individual loyalty discount rate.
     * 
     * @return the loyalty rate, or null if not set
     */
    public BigDecimal getIndividualLoyaltyRate() {
        return individualLoyaltyRate;
    }

    /**
     * Sets the individual loyalty discount rate.
     * 
     * @param individualLoyaltyRate the new loyalty rate
     */
    public void setIndividualLoyaltyRate(BigDecimal individualLoyaltyRate) {
        this.individualLoyaltyRate = individualLoyaltyRate;
    }
}
