package com.group25.greengrocer.dao;

import com.group25.greengrocer.model.Product;
import com.group25.greengrocer.util.DbAdapter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductDao {

    public List<Product> getProductsByCategory(String category) {
        List<Product> products = new ArrayList<>();
        String query = "SELECT p.*, c.name as category_name FROM products p JOIN categories c ON p.category_id = c.id WHERE c.name = ? ORDER BY p.name ASC";

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

    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        String query = "SELECT p.*, c.name as category_name FROM products p JOIN categories c ON p.category_id = c.id ORDER BY p.name ASC";

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

    public void deleteProduct(int productId) {
        String query = "DELETE FROM products WHERE id = ?";

        try (Connection conn = DbAdapter.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, productId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
