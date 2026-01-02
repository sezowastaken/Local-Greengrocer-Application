package com.group25.greengrocer.service;

import com.group25.greengrocer.util.DbAdapter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoyaltyService {

    public double getLoyaltyDiscountRate() {
        String query = "SELECT v FROM app_settings WHERE k = 'LOYALTY_DISCOUNT_RATE'";
        try (Connection conn = DbAdapter.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);
                ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return Double.parseDouble(rs.getString("v"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0; // Default
    }

    public void setLoyaltyDiscountRate(double rate) {
        // Upsert logic or simple update if we assume row exists.
        // Using UPDATE safely first, then INSERT if needed, or ON DUPLICATE KEY logic
        // if 'k' is primary.
        // Schema says 'k' is primary key (implicitly or explicitly it was defined as
        // unique likely, let's assume row exists or use INSERT ON DUPLICATE)
        // Schema: `k` varchar(64) NOT NULL ...

        String query = "UPDATE app_settings SET v = ? WHERE k = 'LOYALTY_DISCOUNT_RATE'";
        try (Connection conn = DbAdapter.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, String.valueOf(rate));
            int rows = stmt.executeUpdate();

            if (rows == 0) {
                // Insert if not exists
                String insert = "INSERT INTO app_settings (k, v) VALUES ('LOYALTY_DISCOUNT_RATE', ?)";
                try (PreparedStatement insertStmt = conn.prepareStatement(insert)) {
                    insertStmt.setString(1, String.valueOf(rate));
                    insertStmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
