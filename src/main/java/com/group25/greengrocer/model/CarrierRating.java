package com.group25.greengrocer.model;

import java.sql.Timestamp;

/**
 * Represents a customer's rating of a carrier after order delivery.
 * Ratings help track carrier performance and quality of service.
 * 
 * Each rating is associated with a specific order and includes
 * a numerical rating (1-5) and an optional comment.
 * 
 */
public class CarrierRating {
    private int id;
    
    private int orderId;
    
    private int customerId;
    
    private int carrierId;
    
    private int rating;
    
    private String comment;
    
    private Timestamp createdAt;

    /**
     * Constructs a new CarrierRating with the specified details.
     * 
     * @param id the unique identifier for the rating
     * @param orderId the ID of the order
     * @param customerId the ID of the customer
     * @param carrierId the ID of the carrier
     * @param rating the numerical rating (1-5)
     * @param comment optional comment
     * @param createdAt timestamp of rating creation
     */
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

    /**
     * Gets the unique identifier of the rating.
     * 
     * @return the rating ID
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the order ID this rating is for.
     * 
     * @return the order ID
     */
    public int getOrderId() {
        return orderId;
    }

    /**
     * Gets the customer ID who gave the rating.
     * 
     * @return the customer ID
     */
    public int getCustomerId() {
        return customerId;
    }

    /**
     * Gets the carrier ID being rated.
     * 
     * @return the carrier ID
     */
    public int getCarrierId() {
        return carrierId;
    }

    /**
     * Gets the numerical rating value.
     * 
     * @return the rating (typically 1-5)
     */
    public int getRating() {
        return rating;
    }

    /**
     * Gets the optional comment about the delivery.
     * 
     * @return the comment, or null if no comment provided
     */
    public String getComment() {
        return comment;
    }

    /**
     * Gets the timestamp when the rating was created.
     * 
     * @return the creation timestamp
     */
    public Timestamp getCreatedAt() {
        return createdAt;
    }

    /**
     * Returns a string representation of the rating.
     * 
     * @return formatted string with rating and comment
     */
    public String toString() {
        return "Rating: " + rating + "/5 - " + (comment != null ? comment : "No comment");
    }
}
