package com.group25.greengrocer.model;

import java.sql.Timestamp;

public class CarrierRating {
    private int id;
    private int orderId;
    private int customerId;
    private int carrierId;
    private int rating;
    private String comment;
    private Timestamp createdAt;

    public CarrierRating(int id, int orderId, int customerId, int carrierId, int rating, String comment,
            Timestamp createdAt) {
        this.id = id;
        this.orderId = orderId;
        this.customerId = customerId;
        this.carrierId = carrierId;
        this.rating = rating;
        this.comment = comment;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public int getOrderId() {
        return orderId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public int getCarrierId() {
        return carrierId;
    }

    public int getRating() {
        return rating;
    }

    public String getComment() {
        return comment;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public String toString() {
        return "Rating: " + rating + "/5 - " + (comment != null ? comment : "No comment");
    }
}
