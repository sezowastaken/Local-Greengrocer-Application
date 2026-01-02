package com.group25.greengrocer.model;

import java.time.LocalDateTime;

/**
 * Represents a discount coupon in the greengrocer application.
 * Coupons can provide either percentage-based or fixed amount discounts.
 * 
 * Coupons have validity periods and minimum order requirements.
 * They can be activated or deactivated by the owner.
 * 
 */
public class Coupon {
    
    private long id;
    
    private String code;
    
    private String discountType;
    
    private double discountValue;
    
    private double minOrderTotal;
    
    private LocalDateTime validFrom;
    
    private LocalDateTime validUntil;
    
    private boolean isActive;

    /**
     * Constructs a new Coupon with the specified details.
     * 
     * @param id the unique identifier for the coupon
     * @param code the coupon code
     * @param discountType the type of discount (PERCENT or AMOUNT)
     * @param discountValue the discount value
     * @param minOrderTotal the minimum order total required
     * @param validFrom the start date of validity
     * @param validUntil the end date of validity
     * @param isActive whether the coupon is active
     */
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

    /**
     * Gets the unique identifier of the coupon.
     * 
     * @return the coupon ID
     */
    public long getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the coupon.
     * 
     * @param id the coupon ID
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Gets the coupon code.
     * 
     * @return the coupon code
     */
    public String getCode() {
        return code;
    }

    /**
     * Sets the coupon code.
     * 
     * @param code the coupon code
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * Gets the discount type.
     * 
     * @return the discount type (PERCENT or AMOUNT)
     */
    public String getDiscountType() {
        return discountType;
    }

    /**
     * Sets the discount type.
     * 
     * @param discountType the discount type
     */
    public void setDiscountType(String discountType) {
        this.discountType = discountType;
    }

    /**
     * Gets the discount value.
     * For PERCENT type, this is a percentage (e.g., 10 for 10%).
     * For AMOUNT type, this is a fixed amount.
     * 
     * @return the discount value
     */
    public double getDiscountValue() {
        return discountValue;
    }

    /**
     * Sets the discount value.
     * 
     * @param discountValue the discount value
     */
    public void setDiscountValue(double discountValue) {
        this.discountValue = discountValue;
    }

    /**
     * Gets the minimum order total required to use this coupon.
     * 
     * @return the minimum order total
     */
    public double getMinOrderTotal() {
        return minOrderTotal;
    }

    /**
     * Sets the minimum order total required.
     * 
     * @param minOrderTotal the minimum order total
     */
    public void setMinOrderTotal(double minOrderTotal) {
        this.minOrderTotal = minOrderTotal;
    }

    /**
     * Gets the start date and time of coupon validity.
     * 
     * @return the valid from date
     */
    public LocalDateTime getValidFrom() {
        return validFrom;
    }

    /**
     * Sets the start date and time of validity.
     * 
     * @param validFrom the valid from date
     */
    public void setValidFrom(LocalDateTime validFrom) {
        this.validFrom = validFrom;
    }

    /**
     * Gets the end date and time of coupon validity.
     * 
     * @return the valid until date
     */
    public LocalDateTime getValidUntil() {
        return validUntil;
    }

    /**
     * Sets the end date and time of validity.
     * 
     * @param validUntil the valid until date
     */
    public void setValidUntil(LocalDateTime validUntil) {
        this.validUntil = validUntil;
    }

    /**
     * Checks if the coupon is currently active.
     * 
     * @return true if active, false otherwise
     */
    public boolean isActive() {
        return isActive;
    }

    /**
     * Sets the active status of the coupon.
     * 
     * @param active the active status
     */
    public void setActive(boolean active) {
        isActive = active;
    }
}
