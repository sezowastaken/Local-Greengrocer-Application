package com.group25.greengrocer.model;

public class Customer extends User {
    private String address;
    private String phoneNumber;
    private String email;

    public Customer(int id, String username, String password, String address, String phoneNumber, String email) {
        super(id, username, password, "customer");
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
