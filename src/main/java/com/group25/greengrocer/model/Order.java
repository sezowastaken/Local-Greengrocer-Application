package com.group25.greengrocer.model;

import java.sql.Timestamp;

public class Order {
    private int id;
    private int customerId;
    private int carrierId; // Can be 0 if not assigned/nullable
    private Timestamp orderTime;
    private Timestamp deliveryTime;
    private String status; // PLACED, ASSIGNED, DELIVERED, CANCELLED
    private double totalAmount;

    public Order(int id, int customerId, int carrierId, Timestamp orderTime, Timestamp deliveryTime, String status,
            double totalAmount) {
        this.id = id;
        this.customerId = customerId;
        this.carrierId = carrierId;
        this.orderTime = orderTime;
        this.deliveryTime = deliveryTime;
        this.status = status;
        this.totalAmount = totalAmount;
    }

    public int getId() {
        return id;
    }

    public int getCustomerId() {
        return customerId;
    }

    public int getCarrierId() {
        return carrierId;
    }

    public Timestamp getOrderTime() {
        return orderTime;
    }

    public Timestamp getDeliveryTime() {
        return deliveryTime;
    }

    public String getStatus() {
        return status;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setCarrierId(int carrierId) {
        this.carrierId = carrierId;
    }

    public void setDeliveryTime(Timestamp deliveryTime) {
        this.deliveryTime = deliveryTime;
    }
}