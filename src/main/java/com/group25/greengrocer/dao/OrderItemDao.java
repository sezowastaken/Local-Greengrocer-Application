package com.group25.greengrocer.dao;

import com.group25.greengrocer.model.OrderItem;
import com.group25.greengrocer.model.UnitType;
import com.group25.greengrocer.util.DbAdapter;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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
        String sql = "SELECT * FROM order_items WHERE order_id = ?";

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
                    items.add(item);
                }
            }
        }
        return items;
    }
}
