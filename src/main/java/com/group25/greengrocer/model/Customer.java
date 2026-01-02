package com.group25.greengrocer.model;

public class Customer extends User {
    private String address;
    private String phoneNumber;
    private java.math.BigDecimal individualLoyaltyRate;

    public Customer(int id, String username, String password, String address, String phoneNumber) {
        super(id, username, password, "customer");
        this.address = address;
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public java.math.BigDecimal getIndividualLoyaltyRate() {
        return individualLoyaltyRate;
    }

    public void setIndividualLoyaltyRate(java.math.BigDecimal individualLoyaltyRate) {
        this.individualLoyaltyRate = individualLoyaltyRate;
    }
}
