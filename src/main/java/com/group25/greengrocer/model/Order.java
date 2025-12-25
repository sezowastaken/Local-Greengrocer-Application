package com.group25.greengrocer.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Order {
    private long id;
    private long customerId;
    private Long carrierId; // Nullable
    private OrderStatus status; // CART, PLACED, ASSIGNED, DELIVERED, CANCELLED
    private LocalDateTime orderTime;
    private LocalDateTime requestedDeliveryTime;
    private LocalDateTime deliveredTime;
    private LocalDateTime cancelledTime;
    private double vatRate;
    private double subtotal;
    private double discountTotal;
    private double vatTotal;
    private double total;
    private Long appliedCouponId; // Nullable
    private double loyaltyDiscountRate;
    private String note;

    // Helper list for items
    private List<OrderItem> items = new ArrayList<>();

    public Order() {
        // Default constructor
    }

    public Order(long id, long customerId, Long carrierId, OrderStatus status, LocalDateTime orderTime,
            double total) {
        this.id = id;
        this.customerId = customerId;
        this.carrierId = carrierId;
        this.status = status;
        this.orderTime = orderTime;
        this.total = total;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(long customerId) {
        this.customerId = customerId;
    }

    public Long getCarrierId() {
        return carrierId;
    }

    public void setCarrierId(Long carrierId) {
        this.carrierId = carrierId;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public LocalDateTime getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(LocalDateTime orderTime) {
        this.orderTime = orderTime;
    }

    public LocalDateTime getRequestedDeliveryTime() {
        return requestedDeliveryTime;
    }

    public void setRequestedDeliveryTime(LocalDateTime requestedDeliveryTime) {
        this.requestedDeliveryTime = requestedDeliveryTime;
    }

    public LocalDateTime getDeliveredTime() {
        return deliveredTime;
    }

    public void setDeliveredTime(LocalDateTime deliveredTime) {
        this.deliveredTime = deliveredTime;
    }

    public LocalDateTime getCancelledTime() {
        return cancelledTime;
    }

    public void setCancelledTime(LocalDateTime cancelledTime) {
        this.cancelledTime = cancelledTime;
    }

    public double getVatRate() {
        return vatRate;
    }

    public void setVatRate(double vatRate) {
        this.vatRate = vatRate;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public double getDiscountTotal() {
        return discountTotal;
    }

    public void setDiscountTotal(double discountTotal) {
        this.discountTotal = discountTotal;
    }

    public double getVatTotal() {
        return vatTotal;
    }

    public void setVatTotal(double vatTotal) {
        this.vatTotal = vatTotal;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public Long getAppliedCouponId() {
        return appliedCouponId;
    }

    public void setAppliedCouponId(Long appliedCouponId) {
        this.appliedCouponId = appliedCouponId;
    }

    public double getLoyaltyDiscountRate() {
        return loyaltyDiscountRate;
    }

    public void setLoyaltyDiscountRate(double loyaltyDiscountRate) {
        this.loyaltyDiscountRate = loyaltyDiscountRate;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    public void addItem(OrderItem item) {
        this.items.add(item);
    }
}
