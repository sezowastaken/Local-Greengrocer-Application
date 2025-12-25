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
}
