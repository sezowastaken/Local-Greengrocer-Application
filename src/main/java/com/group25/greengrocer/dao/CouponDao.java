package com.group25.greengrocer.dao;

import com.group25.greengrocer.model.Coupon;
import com.group25.greengrocer.util.DbAdapter;

import java.sql.*;
import java.time.LocalDateTime;

public class CouponDao {

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
