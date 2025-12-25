package com.group25.greengrocer.model;

import java.sql.Timestamp;

public class Coupon {
    private int id;
    private String code;
    private String discountType; // PERCENT or AMOUNT
    private double discountValue;
    private double minOrderTotal;
    private Timestamp validFrom;
    private Timestamp validUntil;
    private boolean isActive;

    public Coupon(int id, String code, String discountType, double discountValue, double minOrderTotal,
            Timestamp validFrom, Timestamp validUntil, boolean isActive) {
        this.id = id;
        this.code = code;
        this.discountType = discountType;
        this.discountValue = discountValue;
        this.minOrderTotal = minOrderTotal;
        this.validFrom = validFrom;
        this.validUntil = validUntil;
        this.isActive = isActive;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public Timestamp getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(Timestamp validFrom) {
        this.validFrom = validFrom;
    }

    public Timestamp getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(Timestamp validUntil) {
        this.validUntil = validUntil;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
