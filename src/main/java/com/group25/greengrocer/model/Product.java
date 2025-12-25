package com.group25.greengrocer.model;

public class Product {
    private int id;
    private String name;
    private double price;
    private double stock;
    private String category; // "Vegetable" or "Fruit"
    private double threshold;
    private boolean isPiece; // True if sold by piece (integer amount), false if by kg (double)
    private javafx.scene.image.Image productImage;

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

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public double getStock() {
        return stock;
    }

    public String getCategory() {
        return category;
    }

    public double getThreshold() {
        return threshold;
    }

    public boolean isPiece() {
        return isPiece;
    }

    public javafx.scene.image.Image getProductImage() {
        return productImage;
    }

    public void setStock(double stock) {
        this.stock = stock;
    }
}
