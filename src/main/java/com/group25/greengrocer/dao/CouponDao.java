package com.group25.greengrocer.dao;

import com.group25.greengrocer.model.Coupon;
import com.group25.greengrocer.util.DbAdapter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
                        rs.getTimestamp("valid_from"),
                        rs.getTimestamp("valid_until"),
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
            stmt.setTimestamp(5, coupon.getValidFrom());
            stmt.setTimestamp(6, coupon.getValidUntil());
            stmt.setBoolean(7, coupon.isActive());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteCoupon(int id) {
        String query = "DELETE FROM coupons WHERE id = ?";
        try (Connection conn = DbAdapter.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Toggle active status
    public void toggleActive(int id, boolean isActive) {
        String query = "UPDATE coupons SET is_active = ? WHERE id = ?";
        try (Connection conn = DbAdapter.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setBoolean(1, isActive);
            stmt.setInt(2, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
