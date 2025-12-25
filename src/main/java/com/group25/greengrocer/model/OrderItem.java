package com.group25.greengrocer.model;

public class OrderItem {
    private long id;
    private long orderId;
    private long productId;
    private UnitType unit;
    private double quantity;
    private double unitPriceSnapshot;
    private double lineTotal;

    // Additional fields for UI display (optional but useful)
    private Product product;

    public OrderItem(long id, long orderId, long productId, UnitType unit, double quantity, double unitPriceSnapshot,
            double lineTotal) {
        this.id = id;
        this.orderId = orderId;
        this.productId = productId;
        this.unit = unit;
        this.quantity = quantity;
        this.unitPriceSnapshot = unitPriceSnapshot;
        this.lineTotal = lineTotal;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    public UnitType getUnit() {
        return unit;
    }

    public void setUnit(UnitType unit) {
        this.unit = unit;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public double getUnitPriceSnapshot() {
        return unitPriceSnapshot;
    }

    public void setUnitPriceSnapshot(double unitPriceSnapshot) {
        this.unitPriceSnapshot = unitPriceSnapshot;
    }

    public double getLineTotal() {
        return lineTotal;
    }

    public void setLineTotal(double lineTotal) {
        this.lineTotal = lineTotal;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}
