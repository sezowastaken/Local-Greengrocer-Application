package com.group25.greengrocer.dao;

import com.group25.greengrocer.model.Order;
import com.group25.greengrocer.model.OrderStatus;
import com.group25.greengrocer.util.DbAdapter;

import java.sql.*;
import java.time.LocalDateTime;
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
                orders.add(mapRowToOrder(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }

    // Transactional create method
    public long create(Order order, Connection conn) throws SQLException {
        String sql = "INSERT INTO orders (customer_id, status, subtotal, vat_rate, vat_total, discount_total, total, loyalty_discount_rate, requested_delivery_time, note) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setLong(1, order.getCustomerId());
            stmt.setString(2, order.getStatus().name());
            stmt.setDouble(3, order.getSubtotal());
            stmt.setDouble(4, order.getVatRate());
            stmt.setDouble(5, order.getVatTotal());
            stmt.setDouble(6, order.getDiscountTotal());
            stmt.setDouble(7, order.getTotal());
            stmt.setDouble(8, order.getLoyaltyDiscountRate());
            stmt.setObject(9, order.getRequestedDeliveryTime());
            stmt.setString(10, order.getNote());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating order failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getLong(1);
                } else {
                    throw new SQLException("Creating order failed, no ID obtained.");
                }
            }
        }
    }

    public List<Order> findByCustomerId(long customerId) throws SQLException {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE customer_id = ? ORDER BY order_time DESC";

        try (Connection conn = DbAdapter.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, customerId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    orders.add(mapRowToOrder(rs));
                }
            }
        }
        return orders;
    }

    public Map<String, Integer> getTopSellingProducts() {
        Map<String, Integer> stats = new HashMap<>();
        String query = "SELECT p.name, SUM(oi.quantity) as total_qty " +
                "FROM order_items oi " +
                "JOIN products p ON oi.product_id = p.id " +
                "JOIN orders o ON oi.order_id = o.id " +
                "WHERE o.status = 'DELIVERED' " +
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

    public List<Order> findAvailableOrders() throws SQLException {
        List<Order> orders = new ArrayList<>();
        // Assuming 'PLACED' is the status for orders ready to be picked by carriers
        String sql = "SELECT * FROM orders WHERE status IN ('PLACED', 'READY') AND carrier_id IS NULL ORDER BY requested_delivery_time ASC";

        try (Connection conn = DbAdapter.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                orders.add(mapRowToOrder(rs));
            }
        }
        return orders;
    }

    public void updateStatus(long orderId, OrderStatus status) throws SQLException {
        String sql = "UPDATE orders SET status = ? WHERE id = ?";
        try (Connection conn = DbAdapter.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status.name());
            stmt.setLong(2, orderId);
            stmt.executeUpdate();
        }
    }

    public void assignCarrier(long orderId, long carrierId) throws SQLException {
        // Atomic update to ensure no race condition ideally, but simple update for now
        String sql = "UPDATE orders SET carrier_id = ?, status = 'ASSIGNED' WHERE id = ? AND status IN ('PLACED', 'READY') AND carrier_id IS NULL";
        try (Connection conn = DbAdapter.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, carrierId);
            stmt.setLong(2, orderId);
            int rows = stmt.executeUpdate();
            if (rows == 0) {
                throw new SQLException("Order already assigned or not found.");
            }
        }
    }

    public List<Order> findByCarrierId(long carrierId) throws SQLException {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE carrier_id = ? AND status = 'ASSIGNED' ORDER BY requested_delivery_time ASC";

        try (Connection conn = DbAdapter.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, carrierId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    orders.add(mapRowToOrder(rs));
                }
            }
        }
        return orders;
    }

    public List<Order> findCompletedByCarrierId(long carrierId) throws SQLException {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE carrier_id = ? AND status = 'DELIVERED' ORDER BY delivered_time DESC";

        try (Connection conn = DbAdapter.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, carrierId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    orders.add(mapRowToOrder(rs));
                }
            }
        }
        return orders;
    }

    public void completeOrder(long orderId) throws SQLException {
        String sql = "UPDATE orders SET status = 'DELIVERED', delivered_time = NOW() WHERE id = ? AND status = 'ASSIGNED'";
        try (Connection conn = DbAdapter.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, orderId);
            int rows = stmt.executeUpdate();
            if (rows == 0) {
                throw new SQLException("Order not found or not in ASSIGNED state.");
            }
        }
    }

    private Order mapRowToOrder(ResultSet rs) throws SQLException {
        Order order = new Order();
        order.setId(rs.getLong("id"));
        order.setCustomerId(rs.getLong("customer_id"));

        long carrierId = rs.getLong("carrier_id");
        if (!rs.wasNull()) {
            order.setCarrierId(carrierId);
        }

        order.setStatus(OrderStatus.valueOf(rs.getString("status")));
        order.setOrderTime(rs.getTimestamp("order_time").toLocalDateTime());

        Timestamp reqDelTime = rs.getTimestamp("requested_delivery_time");
        if (reqDelTime != null)
            order.setRequestedDeliveryTime(reqDelTime.toLocalDateTime());

        Timestamp delTime = rs.getTimestamp("delivered_time");
        if (delTime != null)
            order.setDeliveredTime(delTime.toLocalDateTime());

        Timestamp cancelTime = rs.getTimestamp("cancelled_time");
        if (cancelTime != null)
            order.setCancelledTime(cancelTime.toLocalDateTime());

        order.setVatRate(rs.getDouble("vat_rate"));
        order.setSubtotal(rs.getDouble("subtotal"));
        order.setDiscountTotal(rs.getDouble("discount_total"));
        order.setVatTotal(rs.getDouble("vat_total"));
        order.setTotal(rs.getDouble("total"));

        long couponId = rs.getLong("applied_coupon_id");
        if (!rs.wasNull())
            order.setAppliedCouponId(couponId);

        order.setLoyaltyDiscountRate(rs.getDouble("loyalty_discount_rate"));
        order.setNote(rs.getString("note"));

        return order;
    }

    public void completeOrderWithDate(long orderId, java.time.LocalDateTime deliveredTime) throws SQLException {
        String sql = "UPDATE orders SET status = 'DELIVERED', delivered_time = ? WHERE id = ? AND status = 'ASSIGNED'";
        try (Connection conn = DbAdapter.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, deliveredTime);
            stmt.setLong(2, orderId);
            int rows = stmt.executeUpdate();
            if (rows == 0) {
                throw new SQLException("Order not found or not in ASSIGNED state.");
            }
        }
    }

    public void saveInvoice(long orderId, byte[] pdfData) throws SQLException {
        String sql = "INSERT INTO invoices (order_id, pdf_blob) VALUES (?, ?)";
        try (Connection conn = DbAdapter.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, orderId);
            stmt.setBytes(2, pdfData);
            stmt.executeUpdate();
            // If this method created its own connection (autocommit true usually), commit
            // isn't explicitly needed but good practice if safe.
            // But here we just rely on auto-commit if standalone.
        }
    }

    // Overloaded method for transaction participation (Matches create(Order,
    // Connection) pattern)
    public void saveInvoice(long orderId, byte[] pdfData, Connection conn) throws SQLException {
        String sql = "INSERT INTO invoices (order_id, pdf_blob) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, orderId);
            stmt.setBytes(2, pdfData);
            stmt.executeUpdate();
            // Do NOT close connection here!
        }
    }

    public byte[] getInvoice(long orderId) throws SQLException {
        String sql = "SELECT pdf_blob FROM invoices WHERE order_id = ?";
        try (Connection conn = DbAdapter.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, orderId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBytes("pdf_blob");
                }
            }
        }
        return null;
    }
}
