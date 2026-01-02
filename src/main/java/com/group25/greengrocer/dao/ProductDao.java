package com.group25.greengrocer.dao;

import com.group25.greengrocer.model.Product;
import com.group25.greengrocer.util.DbAdapter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Product operations.
 * 
 * This class provides database operations for managing products, including
 * retrieving products by category, stock management, product CRUD operations,
 * and category management. It handles product images and stock validation.
 */
public class ProductDao {

    /**
     * Retrieves all product categories from the database.
     * 
     * @return A map where keys are category names and values are category IDs,
     *         sorted alphabetically by category name
     */
    public java.util.Map<String, Integer> getCategories() {
        java.util.Map<String, Integer> categories = new java.util.HashMap<>();
        String query = "SELECT id, name FROM categories ORDER BY name ASC";

        try (Connection conn = DbAdapter.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                categories.put(rs.getString("name"), rs.getInt("id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categories;
    }

    /**
     * Retrieves all products belonging to a specific category.
     * 
     * @param category The name of the category to filter by
     * @return A list of Product objects in the specified category, or empty list if none found
     */
    public List<Product> getProductsByCategory(String category) {
        List<Product> products = new ArrayList<>();
        String query = "SELECT p.*, c.name as category_name FROM products p JOIN categories c ON p.category_id = c.id WHERE c.name = ? AND p.is_deleted = 0 ORDER BY p.name ASC";

        try (Connection conn = DbAdapter.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, category);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    products.add(new Product(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getDouble("price"),
                            rs.getDouble("stock_qty"),
                            rs.getString("category_name"),
                            rs.getDouble("threshold_qty"),
                            "PCS".equalsIgnoreCase(rs.getString("unit")),
                            rs.getBinaryStream("image_blob")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    /**
     * Retrieves all products from the database.
     * 
     * @return A list of all Product objects, or empty list if none found
     */
    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        String query = "SELECT p.*, c.name as category_name FROM products p JOIN categories c ON p.category_id = c.id WHERE p.is_deleted = 0 ORDER BY p.name ASC";

        try (Connection conn = DbAdapter.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                products.add(new Product(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getDouble("price"),
                        rs.getDouble("stock_qty"),
                        rs.getString("category_name"),
                        rs.getDouble("threshold_qty"),
                        "PCS".equalsIgnoreCase(rs.getString("unit")),
                        rs.getBinaryStream("image_blob")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    /**
     * Adds a new product to the database.
     * 
     * @param product The Product object containing product details
     * @param categoryId The ID of the category this product belongs to
     * @param imageStream The input stream containing the product image data
     */
    public void addProduct(Product product, int categoryId, java.io.InputStream imageStream) {
        String query = "INSERT INTO products (name, price, stock_qty, threshold_qty, unit, category_id, image_blob) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DbAdapter.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, product.getName());
            stmt.setDouble(2, product.getPrice());
            stmt.setDouble(3, product.getStock());
            stmt.setDouble(4, product.getThreshold());
            stmt.setString(5, product.isPiece() ? "PCS" : "KG");
            stmt.setInt(6, categoryId);

            if (imageStream != null) {
                stmt.setBlob(7, imageStream);
            } else {
                stmt.setNull(7, java.sql.Types.BLOB);
            }

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates an existing product in the database.
     * 
     * @param product The Product object containing updated product details
     * @param categoryId The ID of the category this product belongs to
     * @param imageStream The input stream containing the new product image data, or null to keep existing image
     */
    public void updateProduct(Product product, int categoryId, java.io.InputStream imageStream) {
        StringBuilder query = new StringBuilder(
                "UPDATE products SET name=?, price=?, stock_qty=?, threshold_qty=?, unit=?, category_id=?");
        if (imageStream != null) {
            query.append(", image_blob=?");
        }
        query.append(" WHERE id=?");

        try (Connection conn = DbAdapter.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query.toString())) {

            stmt.setString(1, product.getName());
            stmt.setDouble(2, product.getPrice());
            stmt.setDouble(3, product.getStock());
            stmt.setDouble(4, product.getThreshold());
            stmt.setString(5, product.isPiece() ? "PCS" : "KG");
            stmt.setInt(6, categoryId);

            int paramIndex = 7;
            if (imageStream != null) {
                stmt.setBlob(paramIndex++, imageStream);
            }
            stmt.setInt(paramIndex, product.getId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Soft deletes a product by marking it as deleted.
     * 
     * @param productId The ID of the product to delete
     */
    public void deleteProduct(int productId) {
        System.out.println("DEBUG: ProductDao.deleteProduct (soft delete) called with ID: " + productId);
        String query = "UPDATE products SET is_deleted = 1 WHERE id = ?";

        try (Connection conn = DbAdapter.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, productId);
            int rowsAffected = stmt.executeUpdate();
            System.out.println("DEBUG: Rows marked as deleted: " + rowsAffected);
        } catch (SQLException e) {
            System.out.println("DEBUG: SQL Exception during soft delete: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Reduces the stock quantity of a product within a transaction.
     * 
     * @param productId The ID of the product to reduce stock for
     * @param quantity The amount of stock to reduce
     * @param conn The database connection to use (for transaction management)
     * @return true if stock was successfully reduced, false if insufficient stock
     * @throws SQLException if a database error occurs
     */
    public boolean reduceStock(long productId, double quantity, Connection conn) throws SQLException {
        String sql = "UPDATE products SET stock_qty = stock_qty - ? WHERE id = ? AND stock_qty >= ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDouble(1, quantity);
            stmt.setLong(2, productId);
            stmt.setDouble(3, quantity); // Ensure check constraint inside query

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }
}
