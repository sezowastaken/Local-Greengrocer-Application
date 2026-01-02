package com.group25.greengrocer.service;

import com.group25.greengrocer.util.DbAdapter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Service class for managing loyalty program discount rates.
 * 
 * This class provides functionality to retrieve and update the loyalty discount rate
 * stored in the application settings. The discount rate is used to calculate
 * discounts for loyal customers when they place orders.
 */
public class LoyaltyService {

    /**
     * Retrieves the current loyalty discount rate from the application settings.
     * 
     * The discount rate is stored in the app_settings table with the key 'LOYALTY_DISCOUNT_RATE'.
     * If the setting is not found in the database, returns 0.0 as the default value.
     * 
     * @return The loyalty discount rate as a double (e.g., 0.05 for 5% discount),
     *         or 0.0 if not found
     */
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

    /**
     * Updates the loyalty discount rate in the application settings.
     * 
     * This method performs an upsert operation: it first attempts to update the existing
     * setting, and if no rows are affected (setting doesn't exist), it inserts a new record.
     * 
     * The discount rate is stored as a string value in the app_settings table with
     * the key 'LOYALTY_DISCOUNT_RATE'.
     * 
     * @param rate The new loyalty discount rate to set (e.g., 0.05 for 5% discount)
     */
    public void setLoyaltyDiscountRate(double rate) {

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
