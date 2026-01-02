package com.group25.greengrocer.dao;

import com.group25.greengrocer.model.CarrierRating;
import com.group25.greengrocer.util.DbAdapter;

import java.sql.*;

public class CarrierRatingDao {

    /**
     * Adds a new rating to the database.
     * 
     * @param rating the rating to add
     * @throws SQLException if a database error occurs
     */
    public void addRating(CarrierRating rating) throws SQLException {
        String sql = "INSERT INTO carrier_ratings (order_id, customer_id, carrier_id, rating) VALUES (?, ?, ?, ?)";
        try (Connection conn = DbAdapter.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, rating.getOrderId());
            stmt.setInt(2, rating.getCustomerId());
            stmt.setInt(3, rating.getCarrierId());
            stmt.setInt(4, rating.getRating());

            stmt.executeUpdate();
        }
    }

    /**
     * Checks if an order has already been rated.
     * 
     * @param orderId the order ID to check
     * @return true if rated, false otherwise
     */
    public boolean hasRated(long orderId) {
        String sql = "SELECT COUNT(*) FROM carrier_ratings WHERE order_id = ?";
        try (Connection conn = DbAdapter.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, orderId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Gets the average rating for a carrier.
     * 
     * @param carrierId the carrier ID
     * @return the average rating, or 0.0 if no ratings
     */
    public double getAverageRating(int carrierId) {
        String sql = "SELECT AVG(rating) FROM carrier_ratings WHERE carrier_id = ?";
        try (Connection conn = DbAdapter.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, carrierId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }
}
