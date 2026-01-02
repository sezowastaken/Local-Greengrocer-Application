package com.group25.greengrocer.model;

/**
 * Represents a single item within an order in the greengrocer application.
 * Each order item links to a product and stores the quantity, unit price, and line total.
 * 
 * The unit price is stored as a snapshot at the time of order placement,
 * ensuring historical accuracy even if product prices change later.
 */
public class OrderItem {
    
    private long id;
    
    private long orderId;
    
    private long productId;
    
    private UnitType unit;
    
    private double quantity;
    
    private double unitPriceSnapshot;
    
    private double lineTotal;

    private Product product;
    
    private String productName;

    /**
     * Constructs a new OrderItem with the specified details.
     * 
     * @param id the unique identifier for the order item
     * @param orderId the ID of the order this item belongs to
     * @param productId the ID of the product
     * @param unit the unit type (KG or PCS)
     * @param quantity the quantity ordered
     * @param unitPriceSnapshot the unit price at time of order
     * @param lineTotal the total price for this line item
     */
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

    /**
     * Gets the unique identifier of the order item.
     * 
     * @return the order item ID
     */
    public long getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the order item.
     * 
     * @param id the order item ID
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Gets the ID of the order this item belongs to.
     * 
     * @return the order ID
     */
    public long getOrderId() {
        return orderId;
    }

    /**
     * Sets the ID of the order this item belongs to.
     * 
     * @param orderId the order ID
     */
    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    /**
     * Gets the product ID.
     * 
     * @return the product ID
     */
    public long getProductId() {
        return productId;
    }

    /**
     * Sets the product ID.
     * 
     * @param productId the product ID
     */
    public void setProductId(long productId) {
        this.productId = productId;
    }

    /**
     * Gets the unit type (KG or PCS).
     * 
     * @return the unit type
     */
    public UnitType getUnit() {
        return unit;
    }

    /**
     * Sets the unit type.
     * 
     * @param unit the unit type
     */
    public void setUnit(UnitType unit) {
        this.unit = unit;
    }

    /**
     * Gets the quantity ordered.
     * 
     * @return the quantity
     */
    public double getQuantity() {
        return quantity;
    }

    /**
     * Sets the quantity ordered.
     * 
     * @param quantity the quantity
     */
    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    /**
     * Gets the unit price snapshot at time of order.
     * This ensures historical accuracy even if prices change.
     * 
     * @return the unit price snapshot
     */
    public double getUnitPriceSnapshot() {
        return unitPriceSnapshot;
    }

    /**
     * Sets the unit price snapshot.
     * 
     * @param unitPriceSnapshot the unit price snapshot
     */
    public void setUnitPriceSnapshot(double unitPriceSnapshot) {
        this.unitPriceSnapshot = unitPriceSnapshot;
    }

    /**
     * Gets the line total (quantity * unit price).
     * 
     * @return the line total
     */
    public double getLineTotal() {
        return lineTotal;
    }

    /**
     * Sets the line total.
     * 
     * @param lineTotal the line total
     */
    public void setLineTotal(double lineTotal) {
        this.lineTotal = lineTotal;
    }

    /**
     * Gets the full Product object for this item.
     * 
     * @return the product, or null if not loaded
     */
    public Product getProduct() {
        return product;
    }

    /**
     * Sets the full Product object for this item.
     * 
     * @param product the product
     */
    public void setProduct(Product product) {
        this.product = product;
    }

    /**
     * Gets the product name for display.
     * 
     * @return the product name, or empty string if not set
     */
    public String getProductName() {
        return productName != null ? productName : "";
    }

    /**
     * Sets the product name for display.
     * 
     * @param productName the product name
     */
    public void setProductName(String productName) {
        this.productName = productName;
    }

    /**
     * Gets the formatted quantity string for UI display.
     * Pieces are shown with 0 decimals, kg with 3 decimals.
     * 
     * @return the formatted quantity string
     */
    public String getFormattedQuantity() {
        if (unit == UnitType.PCS) {
            return String.format("%.0f", quantity);
        } else {
            return String.format("%.3f", quantity);
        }
    }

    /**
     * Gets the formatted unit price string for UI display.
     * 
     * @return the formatted unit price with currency symbol
     */
    public String getFormattedUnitPrice() {
        return String.format("$%.2f", unitPriceSnapshot);
    }

    /**
     * Gets the formatted line total string for UI display.
     * 
     * @return the formatted line total with currency symbol
     */
    public String getFormattedLineTotal() {
        return String.format("$%.2f", lineTotal);
    }
}
