package com.group25.greengrocer.dao;

import com.group25.greengrocer.model.User;
import com.group25.greengrocer.model.Carrier;
import com.group25.greengrocer.util.DbAdapter;
// import com.group25.greengrocer.util.SecurityUtil; // Removed

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDao {

    public List<User> getCarriers() {
        List<User> carriers = new ArrayList<>();
        // Explicitly selecting password_hash
        String query = "SELECT id, username, password_hash FROM users WHERE role = 'carrier'";

        try (Connection conn = DbAdapter.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                carriers.add(new Carrier(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password_hash"))); // Correct column name
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return carriers;
    }

    public void addCarrier(String username, String password) {
        String query = "INSERT INTO users (username, password_hash, role) VALUES (?, ?, 'carrier')";

        try (Connection conn = DbAdapter.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            stmt.setString(2, hashPassword(password)); // Hash password
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteCarrier(int userId) {
        String query = "DELETE FROM users WHERE id = ? AND role = 'carrier'";

        try (Connection conn = DbAdapter.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String hashPassword(String password) {
        try {
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(password.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder(2 * encodedhash.length);
            for (byte b : encodedhash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
}