package com.group25.greengrocer.dao;

import com.group25.greengrocer.model.CustomerStats;
import com.group25.greengrocer.util.DbAdapter;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CustomerStatsDao {

    public List<CustomerStats> getAllCustomerStats() {
        List<CustomerStats> stats = new ArrayList<>();
        String sql = "SELECT u.id, u.username, u.full_name, u.created_at, u.individual_loyalty_rate, " +
                "COUNT(o.id) as total_orders, " +
                "SUM(o.total) as total_spent " +
                "FROM users u " +
                "LEFT JOIN orders o ON u.id = o.customer_id AND o.status = 'DELIVERED' " +
                "WHERE u.role = 'customer' " +
                "GROUP BY u.id " +
                "ORDER BY u.id ASC";

        try (Connection conn = DbAdapter.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Long id = rs.getLong("id");
                String username = rs.getString("username");
                String fullName = rs.getString("full_name");
                Timestamp createdAtTs = rs.getTimestamp("created_at");
                LocalDateTime registeredDate = createdAtTs != null ? createdAtTs.toLocalDateTime() : null;

                BigDecimal rate = rs.getBigDecimal("individual_loyalty_rate"); // Can be null

                int totalOrders = rs.getInt("total_orders");
                BigDecimal totalSpent = rs.getBigDecimal("total_spent");
                if (totalSpent == null) {
                    totalSpent = BigDecimal.ZERO;
                }

                stats.add(new CustomerStats(id, username, fullName, registeredDate, totalOrders, totalSpent, rate));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stats;
    }
}
