package com.group25.greengrocer.dao;

import com.group25.greengrocer.model.Order;
import com.group25.greengrocer.model.OrderStatus;
import com.group25.greengrocer.util.DbAdapter;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderDao {

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

    public List<Order> findAvailableOrders() throws SQLException {
        List<Order> orders = new ArrayList<>();
        // Assuming 'PLACED' is the status for orders ready to be picked by carriers
        String sql = "SELECT * FROM orders WHERE status = 'PLACED' ORDER BY requested_delivery_time ASC";

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
        String sql = "UPDATE orders SET carrier_id = ?, status = 'ASSIGNED' WHERE id = ? AND carrier_id IS NULL";
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
}
