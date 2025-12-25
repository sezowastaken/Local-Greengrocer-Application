package com.group25.greengrocer.dao;

import com.group25.greengrocer.model.Coupon;
import com.group25.greengrocer.util.DbAdapter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CouponDao {

    public List<Coupon> getAllCoupons() {
        List<Coupon> coupons = new ArrayList<>();
        String query = "SELECT * FROM coupons ORDER BY created_at DESC";

        try (Connection conn = DbAdapter.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                coupons.add(new Coupon(
                        rs.getInt("id"),
                        rs.getString("code"),
                        rs.getString("discount_type"),
                        rs.getDouble("discount_value"),
                        rs.getDouble("min_order_total"),
                        rs.getTimestamp("valid_from") != null ? rs.getTimestamp("valid_from").toLocalDateTime() : null,
                        rs.getTimestamp("valid_until") != null ? rs.getTimestamp("valid_until").toLocalDateTime()
                                : null,
                        rs.getBoolean("is_active")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return coupons;
    }

    public void addCoupon(Coupon coupon) {
        String query = "INSERT INTO coupons (code, discount_type, discount_value, min_order_total, valid_from, valid_until, is_active) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DbAdapter.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, coupon.getCode());
            stmt.setString(2, coupon.getDiscountType());
            stmt.setDouble(3, coupon.getDiscountValue());
            stmt.setDouble(4, coupon.getMinOrderTotal());
            stmt.setTimestamp(5,
                    coupon.getValidFrom() != null ? java.sql.Timestamp.valueOf(coupon.getValidFrom()) : null);
            stmt.setTimestamp(6,
                    coupon.getValidUntil() != null ? java.sql.Timestamp.valueOf(coupon.getValidUntil()) : null);
            stmt.setBoolean(7, coupon.isActive());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteCoupon(long id) {
        String query = "DELETE FROM coupons WHERE id = ?";
        try (Connection conn = DbAdapter.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Toggle active status

    public void toggleActive(long id, boolean isActive) {
        String query = "UPDATE coupons SET is_active = ? WHERE id = ?";
        try (Connection conn = DbAdapter.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setBoolean(1, isActive);
            stmt.setLong(2, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Coupon findByCode(String code) throws SQLException {
        String sql = "SELECT * FROM coupons WHERE code = ? AND is_active = 1";

        try (Connection conn = DbAdapter.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, code);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    LocalDateTime validFrom = null;
                    Timestamp validFromTs = rs.getTimestamp("valid_from");
                    if (validFromTs != null)
                        validFrom = validFromTs.toLocalDateTime();

                    LocalDateTime validUntil = null;
                    Timestamp validUntilTs = rs.getTimestamp("valid_until");
                    if (validUntilTs != null)
                        validUntil = validUntilTs.toLocalDateTime();

                    return new Coupon(
                            rs.getLong("id"),
                            rs.getString("code"),
                            rs.getString("discount_type"),
                            rs.getDouble("discount_value"),
                            rs.getDouble("min_order_total"),
                            validFrom,
                            validUntil,
                            rs.getBoolean("is_active"));
                }
            }
        }
        return null;
    }
}
