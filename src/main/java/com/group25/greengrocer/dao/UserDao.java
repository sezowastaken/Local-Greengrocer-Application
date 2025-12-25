package com.group25.greengrocer.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.group25.greengrocer.model.Carrier;
import com.group25.greengrocer.model.User;
import com.group25.greengrocer.util.DbAdapter;

public class UserDao {

    public List<User> getCarriers() {
        List<User> carriers = new ArrayList<>();
        String query = "SELECT id, username, password_hash FROM users WHERE role = 'carrier'";

        try (Connection conn = DbAdapter.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                carriers.add(new Carrier(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password_hash")
                ));
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
            stmt.setString(2, hashPassword(password));
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

    /**
     * Find user by ID
     */
    public UserProfile findById(long userId) throws SQLException {
        String sql = "SELECT id, username, role, full_name, phone, address_line, city FROM users WHERE id = ?";

        try (Connection conn = DbAdapter.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new UserProfile(
                            rs.getLong("id"),
                            rs.getString("username"),
                            rs.getString("role"),
                            rs.getString("full_name"),
                            rs.getString("phone"),
                            rs.getString("address_line"),
                            rs.getString("city")
                    );
                }
            }
        }
        return null;
    }

    /**
     * Update user profile information
     */
    public void updateProfile(long userId,
                              String fullName,
                              String phone,
                              String addressLine,
                              String city) throws SQLException {

        String sql = "UPDATE users SET full_name = ?, phone = ?, address_line = ?, city = ? WHERE id = ?";

        try (Connection conn = DbAdapter.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, fullName);
            stmt.setString(2, phone);
            stmt.setString(3, addressLine);
            stmt.setString(4, city);
            stmt.setLong(5, userId);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("User not found with ID: " + userId);
            }
        }
    }

    private String hashPassword(String password) {
        try {
            java.security.MessageDigest digest =
                    java.security.MessageDigest.getInstance("SHA-256");
            byte[] encodedhash =
                    digest.digest(password.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder(2 * encodedhash.length);
            for (byte b : encodedhash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Inner class to represent user profile data
     */
    public static class UserProfile {
        private final long id;
        private final String username;
        private final String role;
        private String fullName;
        private String phone;
        private String addressLine;
        private String city;

        public UserProfile(long id, String username, String role,
                           String fullName, String phone,
                           String addressLine, String city) {
            this.id = id;
            this.username = username;
            this.role = role;
            this.fullName = fullName;
            this.phone = phone;
            this.addressLine = addressLine;
            this.city = city;
        }

        public long getId() { return id; }
        public String getUsername() { return username; }
        public String getRole() { return role; }

        public String getFullName() { return fullName != null ? fullName : ""; }
        public String getPhone() { return phone != null ? phone : ""; }
        public String getAddressLine() { return addressLine != null ? addressLine : ""; }
        public String getCity() { return city != null ? city : ""; }

        public void setFullName(String fullName) { this.fullName = fullName; }
        public void setPhone(String phone) { this.phone = phone; }
        public void setAddressLine(String addressLine) { this.addressLine = addressLine; }
        public void setCity(String city) { this.city = city; }
    }
}
