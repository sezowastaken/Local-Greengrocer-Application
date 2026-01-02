package com.group25.greengrocer.model;

/**
 * Represents a product (vegetable or fruit) in the greengrocer application.
 * Products can be sold by weight (kg) or by piece (pcs).
 * 
 * Each product has a threshold value. When stock falls to or below this
 * threshold,
 * the price is automatically doubled to manage inventory.
 */
public class Product {

    private int id;

    private String name;

    private double price;

    private double stock;

    private String category;

    private double threshold;

    private boolean isPiece;

    /** Product image for display in the UI */
    private javafx.scene.image.Image productImage;

    /**
     * Constructs a new Product with the specified details.
     * 
     * @param id           the unique identifier for the product
     * @param name         the name of the product
     * @param price        the base price per unit
     * @param stock        the current stock quantity
     * @param category     the category (Vegetable or Fruit)
     * @param threshold    the threshold for price doubling
     * @param isPiece      true if sold by piece, false if by weight
     * @param imageContent the product image as an InputStream
     */
    public Product(int id, String name, double price, double stock, String category, double threshold,
            boolean isPiece, java.io.InputStream imageContent) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.category = category;
        this.threshold = threshold;
        this.isPiece = isPiece;
        if (imageContent != null) {
            this.productImage = new javafx.scene.image.Image(imageContent);
        }
    }

    /**
     * Gets the unique identifier of the product.
     * 
     * @return the product ID
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the name of the product.
     * 
     * @return the product name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the base price per unit of the product.
     * 
     * @return the product price
     */
    public double getPrice() {
        return price;
    }

    /**
     * Gets the current stock quantity of the product.
     * 
     * @return the stock quantity
     */
    public double getStock() {
        return stock;
    }

    /**
     * Gets the category of the product.
     * 
     * @return the category (Vegetable or Fruit)
     */
    public String getCategory() {
        return category;
    }

    /**
     * Gets the threshold value for automatic price doubling.
     * When stock reaches or falls below this value, the price doubles.
     * 
     * @return the threshold value
     */
    public double getThreshold() {
        return threshold;
    }

    /**
     * Checks if the product is sold by piece.
     * 
     * @return true if sold by piece, false if sold by weight (kg)
     */
    public boolean isPiece() {
        return isPiece;
    }

    /**
     * Gets the product image for UI display.
     * 
     * @return the product image
     */
    public javafx.scene.image.Image getProductImage() {
        return productImage;
    }

    /**
     * Sets the current stock quantity of the product.
     * 
     * @param stock the new stock quantity
     */
    public void setStock(double stock) {
        this.stock = stock;
    }

    /**
     * Gets the dynamic price of the product.
     * If stock is less than the threshold, the price is doubled.
     * 
     * @return the dynamic price
     */
    public double getDynamicPrice() {
        if (stock < threshold) {
            return price * 2;
        }
        return price;
    }
}
