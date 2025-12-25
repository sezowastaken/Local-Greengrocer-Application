package com.group25.greengrocer.model;

import java.time.LocalDateTime;

public class Coupon {
    private long id;
    private String code;
    private String discountType; // 'PERCENT' or 'AMOUNT'
    private double discountValue;
    private double minOrderTotal;
    private LocalDateTime validFrom;
    private LocalDateTime validUntil;
    private boolean isActive;

    public Coupon(long id, String code, String discountType, double discountValue, double minOrderTotal,
            LocalDateTime validFrom, LocalDateTime validUntil, boolean isActive) {
        this.id = id;
        this.code = code;
        this.discountType = discountType;
        this.discountValue = discountValue;
        this.minOrderTotal = minOrderTotal;
        this.validFrom = validFrom;
        this.validUntil = validUntil;
        this.isActive = isActive;
    }

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDiscountType() {
        return discountType;
    }

    public void setDiscountType(String discountType) {
        this.discountType = discountType;
    }

    public double getDiscountValue() {
        return discountValue;
    }

    public void setDiscountValue(double discountValue) {
        this.discountValue = discountValue;
    }

    public double getMinOrderTotal() {
        return minOrderTotal;
    }

    public void setMinOrderTotal(double minOrderTotal) {
        this.minOrderTotal = minOrderTotal;
    }

    public LocalDateTime getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(LocalDateTime validFrom) {
        this.validFrom = validFrom;
    }

    public LocalDateTime getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(LocalDateTime validUntil) {
        this.validUntil = validUntil;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
