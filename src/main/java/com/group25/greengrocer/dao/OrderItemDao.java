package com.group25.greengrocer.dao;

import com.group25.greengrocer.model.OrderItem;
import com.group25.greengrocer.model.UnitType;
import com.group25.greengrocer.util.DbAdapter;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for OrderItem operations.
 * 
 * This class provides database operations for managing order items,
 * including creating order items within transactions and retrieving
 * items associated with specific orders.
 */
public class OrderItemDao {

    // Transactional create method
    public void create(OrderItem item, Connection conn) throws SQLException {
        String sql = "INSERT INTO order_items (order_id, product_id, unit, quantity, unit_price_snapshot, line_total) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, item.getOrderId());
            stmt.setLong(2, item.getProductId());
            stmt.setString(3, item.getUnit().name());
            stmt.setDouble(4, item.getQuantity());
            stmt.setDouble(5, item.getUnitPriceSnapshot());
            stmt.setDouble(6, item.getLineTotal());

            stmt.executeUpdate();
        }
    }

    public List<OrderItem> findByOrderId(long orderId) throws SQLException {
        List<OrderItem> items = new ArrayList<>();

        // JOIN with products table to get product names
        String sql = "SELECT oi.*, p.name as product_name " +
                "FROM order_items oi " +
                "JOIN products p ON oi.product_id = p.id " +
                "WHERE oi.order_id = ? " +
                "ORDER BY p.name";

        try (Connection conn = DbAdapter.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, orderId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    UnitType unit = UnitType.valueOf(rs.getString("unit"));
                    OrderItem item = new OrderItem(
                            rs.getLong("id"),
                            rs.getLong("order_id"),
                            rs.getLong("product_id"),
                            unit,
                            rs.getDouble("quantity"),
                            rs.getDouble("unit_price_snapshot"),
                            rs.getDouble("line_total"));

                    // Set product name from JOIN result
                    item.setProductName(rs.getString("product_name"));

                    items.add(item);
                }
            }
        }
        return items;
    }
}
