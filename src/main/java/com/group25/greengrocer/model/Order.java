package com.group25.greengrocer.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents an order in the greengrocer application.
 * Orders go through multiple states: CART, PLACED, ASSIGNED, DELIVERED, or CANCELLED.
 * 
 * An order contains multiple order items, pricing information including VAT and discounts,
 * and timestamps for order placement, delivery, and cancellation.
 * 
 */
public class Order {
    
    private long id;
    
    private long customerId;
    
    private Long carrierId;
    
    private OrderStatus status;
    
    private LocalDateTime orderTime;
    
    private LocalDateTime requestedDeliveryTime;
    
    private LocalDateTime deliveredTime;
    
    private LocalDateTime cancelledTime;
    
    private double vatRate;
    
    private double subtotal;
    
    private double discountTotal;
    
    private double vatTotal;
    
    private double total;
    
    private Long appliedCouponId;
    
    private double loyaltyDiscountRate;
    
    private String note;

    private List<OrderItem> items = new ArrayList<>();

    /**
     * Default constructor for creating an empty order.
     */
    public Order() {
        // Default constructor
    }

    /**
     * Constructs a new Order with basic details.
     * 
     * @param id the unique identifier for the order
     * @param customerId the ID of the customer
     * @param carrierId the ID of the assigned carrier (can be null)
     * @param status the current order status
     * @param orderTime the timestamp when order was placed
     * @param total the final total amount
     */
    public Order(long id, long customerId, Long carrierId, OrderStatus status, LocalDateTime orderTime,
            double total) {
        this.id = id;
        this.customerId = customerId;
        this.carrierId = carrierId;
        this.status = status;
        this.orderTime = orderTime;
        this.total = total;
    }

    /**
     * Gets the unique identifier of the order.
     * 
     * @return the order ID
     */
    public long getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the order.
     * 
     * @param id the order ID
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Gets the customer ID who placed the order.
     * 
     * @return the customer ID
     */
    public long getCustomerId() {
        return customerId;
    }

    /**
     * Sets the customer ID who placed the order.
     * 
     * @param customerId the customer ID
     */
    public void setCustomerId(long customerId) {
        this.customerId = customerId;
    }

    /**
     * Gets the carrier ID assigned to deliver the order.
     * 
     * @return the carrier ID, or null if not yet assigned
     */
    public Long getCarrierId() {
        return carrierId;
    }

    /**
     * Sets the carrier ID assigned to deliver the order.
     * 
     * @param carrierId the carrier ID
     */
    public void setCarrierId(Long carrierId) {
        this.carrierId = carrierId;
    }

    /**
     * Gets the current status of the order.
     * 
     * @return the order status
     */
    public OrderStatus getStatus() {
        return status;
    }

    /**
     * Sets the current status of the order.
     * 
     * @param status the new order status
     */
    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    /**
     * Gets the timestamp when the order was placed.
     * 
     * @return the order time
     */
    public LocalDateTime getOrderTime() {
        return orderTime;
    }

    /**
     * Sets the timestamp when the order was placed.
     * 
     * @param orderTime the order time
     */
    public void setOrderTime(LocalDateTime orderTime) {
        this.orderTime = orderTime;
    }

    /**
     * Gets the requested delivery date and time.
     * 
     * @return the requested delivery time
     */
    public LocalDateTime getRequestedDeliveryTime() {
        return requestedDeliveryTime;
    }

    /**
     * Sets the requested delivery date and time.
     * 
     * @param requestedDeliveryTime the requested delivery time
     */
    public void setRequestedDeliveryTime(LocalDateTime requestedDeliveryTime) {
        this.requestedDeliveryTime = requestedDeliveryTime;
    }

    /**
     * Gets the actual delivery timestamp.
     * 
     * @return the delivered time, or null if not yet delivered
     */
    public LocalDateTime getDeliveredTime() {
        return deliveredTime;
    }

    /**
     * Sets the actual delivery timestamp.
     * 
     * @param deliveredTime the delivered time
     */
    public void setDeliveredTime(LocalDateTime deliveredTime) {
        this.deliveredTime = deliveredTime;
    }

    /**
     * Gets the cancellation timestamp.
     * 
     * @return the cancelled time, or null if not cancelled
     */
    public LocalDateTime getCancelledTime() {
        return cancelledTime;
    }

    /**
     * Sets the cancellation timestamp.
     * 
     * @param cancelledTime the cancelled time
     */
    public void setCancelledTime(LocalDateTime cancelledTime) {
        this.cancelledTime = cancelledTime;
    }

    /**
     * Gets the VAT rate applied to the order.
     * 
     * @return the VAT rate
     */
    public double getVatRate() {
        return vatRate;
    }

    /**
     * Sets the VAT rate for the order.
     * 
     * @param vatRate the VAT rate
     */
    public void setVatRate(double vatRate) {
        this.vatRate = vatRate;
    }

    /**
     * Gets the subtotal before discounts and VAT.
     * 
     * @return the subtotal amount
     */
    public double getSubtotal() {
        return subtotal;
    }

    /**
     * Sets the subtotal before discounts and VAT.
     * 
     * @param subtotal the subtotal amount
     */
    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    /**
     * Gets the total discount amount applied.
     * 
     * @return the discount total
     */
    public double getDiscountTotal() {
        return discountTotal;
    }

    /**
     * Sets the total discount amount.
     * 
     * @param discountTotal the discount total
     */
    public void setDiscountTotal(double discountTotal) {
        this.discountTotal = discountTotal;
    }

    /**
     * Gets the total VAT amount.
     * 
     * @return the VAT total
     */
    public double getVatTotal() {
        return vatTotal;
    }

    /**
     * Sets the total VAT amount.
     * 
     * @param vatTotal the VAT total
     */
    public void setVatTotal(double vatTotal) {
        this.vatTotal = vatTotal;
    }

    /**
     * Gets the final total amount including VAT and discounts.
     * 
     * @return the total amount
     */
    public double getTotal() {
        return total;
    }

    /**
     * Sets the final total amount.
     * 
     * @param total the total amount
     */
    public void setTotal(double total) {
        this.total = total;
    }

    /**
     * Gets the ID of the coupon applied to this order.
     * 
     * @return the coupon ID, or null if no coupon applied
     */
    public Long getAppliedCouponId() {
        return appliedCouponId;
    }

    /**
     * Sets the ID of the coupon to apply to this order.
     * 
     * @param appliedCouponId the coupon ID
     */
    public void setAppliedCouponId(Long appliedCouponId) {
        this.appliedCouponId = appliedCouponId;
    }

    /**
     * Gets the loyalty discount rate applied to this order.
     * 
     * @return the loyalty discount rate
     */
    public double getLoyaltyDiscountRate() {
        return loyaltyDiscountRate;
    }

    /**
     * Sets the loyalty discount rate for this order.
     * 
     * @param loyaltyDiscountRate the loyalty discount rate
     */
    public void setLoyaltyDiscountRate(double loyaltyDiscountRate) {
        this.loyaltyDiscountRate = loyaltyDiscountRate;
    }

    /**
     * Gets the optional note or special instructions for this order.
     * 
     * @return the order note
     */
    public String getNote() {
        return note;
    }

    /**
     * Sets the optional note or special instructions.
     * 
     * @param note the order note
     */
    public void setNote(String note) {
        this.note = note;
    }

    /**
     * Gets the list of items in this order.
     * 
     * @return the list of order items
     */
    public List<OrderItem> getItems() {
        return items;
    }

    /**
     * Sets the list of items in this order.
     * 
     * @param items the list of order items
     */
    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    /**
     * Adds a single item to this order.
     * 
     * @param item the order item to add
     */
    public void addItem(OrderItem item) {
        this.items.add(item);
    }
}
