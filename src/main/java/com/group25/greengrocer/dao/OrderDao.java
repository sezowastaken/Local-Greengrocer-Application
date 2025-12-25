package com.group25.greengrocer.dao;

import com.group25.greengrocer.model.Order;
import com.group25.greengrocer.util.DbAdapter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderDao {

    public List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>();
        String query = "SELECT * FROM orders ORDER BY order_time DESC";

        try (Connection conn = DbAdapter.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                orders.add(new Order(
                        rs.getInt("id"),
                        rs.getInt("customer_id"),
                        rs.getInt("carrier_id"),
                        rs.getTimestamp("order_time"),
                        rs.getTimestamp("delivery_time"),
                        rs.getString("status"),
                        rs.getDouble("total")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }

    public Map<String, Integer> getTopSellingProducts() {
        Map<String, Integer> stats = new HashMap<>();
        String query = "SELECT p.name, SUM(oi.quantity) as total_qty " +
                "FROM order_items oi " +
                "JOIN products p ON oi.product_id = p.id " +
                "GROUP BY p.name " +
                "ORDER BY total_qty DESC LIMIT 5";

        try (Connection conn = DbAdapter.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                stats.put(rs.getString("name"), rs.getInt("total_qty"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stats;
    }

    public Map<String, Double> getRevenueByDate() {
        Map<String, Double> stats = new HashMap<>();
        // Grouping by DATE(order_time)
        String query = "SELECT DATE(order_time) as date, SUM(total) as total " +
                "FROM orders " +
                "WHERE status = 'DELIVERED' " + // Only count delivered/completed orders for revenue
                "GROUP BY DATE(order_time) " +
                "ORDER BY date DESC LIMIT 7";

        try (Connection conn = DbAdapter.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                stats.put(rs.getString("date"), rs.getDouble("total"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stats;
    }
}
