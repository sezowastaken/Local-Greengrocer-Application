package com.group25.greengrocer.dao;

import com.group25.greengrocer.model.CarrierRating;
import com.group25.greengrocer.util.DbAdapter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RatingDao {

    public List<CarrierRating> getRatingsByCarrierId(int carrierId) {
        List<CarrierRating> ratings = new ArrayList<>();
        String query = "SELECT * FROM carrier_ratings WHERE carrier_id = ? ORDER BY created_at DESC";

        try (Connection conn = DbAdapter.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, carrierId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ratings.add(new CarrierRating(
                            rs.getInt("id"),
                            rs.getInt("order_id"),
                            rs.getInt("customer_id"),
                            rs.getInt("carrier_id"),
                            rs.getInt("rating"),
                            rs.getString("comment"),
                            rs.getTimestamp("created_at")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ratings;
    }

    // Calculate average rating
    public double getAverageRating(int carrierId) {
        String query = "SELECT AVG(rating) as avg_rating FROM carrier_ratings WHERE carrier_id = ?";
        try (Connection conn = DbAdapter.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, carrierId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("avg_rating");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    public boolean hasRatingForOrder(long orderId) {
        String query = "SELECT COUNT(*) FROM carrier_ratings WHERE order_id = ?";
        try (Connection conn = DbAdapter.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
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

    public boolean addRating(CarrierRating rating) {
        String query = "INSERT INTO carrier_ratings (order_id, customer_id, carrier_id, rating, comment, created_at) VALUES (?, ?, ?, ?, ?, NOW())";
        try (Connection conn = DbAdapter.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, rating.getOrderId());
            stmt.setInt(2, rating.getCustomerId());
            stmt.setInt(3, rating.getCarrierId());
            stmt.setInt(4, rating.getRating());
            stmt.setString(5, rating.getComment());
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
