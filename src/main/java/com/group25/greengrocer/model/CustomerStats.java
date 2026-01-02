package com.group25.greengrocer.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CustomerStats {
    private Long id;
    private String username;
    private String fullName;
    private LocalDateTime registeredDate;
    private int totalOrders;
    private BigDecimal totalSpent;
    private BigDecimal individualLoyaltyRate; // Nullable

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

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getFullName() {
        return fullName;
    }

    public LocalDateTime getRegisteredDate() {
        return registeredDate;
    }

    public int getTotalOrders() {
        return totalOrders;
    }

    public BigDecimal getTotalSpent() {
        return totalSpent;
    }

    public BigDecimal getIndividualLoyaltyRate() {
        return individualLoyaltyRate;
    }

    public void setIndividualLoyaltyRate(BigDecimal individualLoyaltyRate) {
        this.individualLoyaltyRate = individualLoyaltyRate;
    }
}
