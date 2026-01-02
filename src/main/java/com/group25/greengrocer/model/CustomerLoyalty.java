package com.group25.greengrocer.model;

public class CustomerLoyalty {
    private long customerId;
    private String customerUsername;
    private double totalSpent;
    private String loyaltyTier;
    private int discountRate;

    public CustomerLoyalty(long customerId, String customerUsername, double totalSpent) {
        this.customerId = customerId;
        this.customerUsername = customerUsername;
        this.totalSpent = totalSpent;
        this.loyaltyTier = calculateTier(totalSpent);
        this.discountRate = calculateDiscount(totalSpent);
    }

    private String calculateTier(double total) {
        if (total >= 5000)
            return "PLATINUM";
        if (total >= 3000)
            return "GOLD";
        if (total >= 2000)
            return "SILVER";
        if (total >= 1000)
            return "BRONZE";
        return "NONE";
    }

    private int calculateDiscount(double total) {
        if (total >= 5000)
            return 20;
        if (total >= 3000)
            return 15;
        if (total >= 2000)
            return 10;
        if (total >= 1000)
            return 5;
        return 0;
    }

    // Getters and Setters
    public long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(long customerId) {
        this.customerId = customerId;
    }

    public String getCustomerUsername() {
        return customerUsername;
    }

    public void setCustomerUsername(String customerUsername) {
        this.customerUsername = customerUsername;
    }

    public double getTotalSpent() {
        return totalSpent;
    }

    public void setTotalSpent(double totalSpent) {
        this.totalSpent = totalSpent;
        this.loyaltyTier = calculateTier(totalSpent);
        this.discountRate = calculateDiscount(totalSpent);
    }

    public String getLoyaltyTier() {
        return loyaltyTier;
    }

    public int getDiscountRate() {
        return discountRate;
    }

    public String getTierDisplay() {
        switch (loyaltyTier) {
            case "PLATINUM":
                return "💎 PLATINUM";
            case "GOLD":
                return "🥇 GOLD";
            case "SILVER":
                return "🥈 SILVER";
            case "BRONZE":
                return "🥉 BRONZE";
            default:
                return "—";
        }
    }

    public String getDiscountDisplay() {
        return discountRate > 0 ? discountRate + "%" : "—";
    }
}
