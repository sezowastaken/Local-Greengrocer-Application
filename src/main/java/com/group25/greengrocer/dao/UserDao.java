package com.group25.greengrocer.dao;

import com.group25.greengrocer.util.DbAdapter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDao {

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
                            rs.getString("city"));
                }
            }
        }
        return null;
    }

    /**
     * Update user profile information
     */
    public void updateProfile(long userId, String fullName, String phone, String addressLine, String city)
            throws SQLException {
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

        public UserProfile(long id, String username, String role, String fullName,
                String phone, String addressLine, String city) {
            this.id = id;
            this.username = username;
            this.role = role;
            this.fullName = fullName;
            this.phone = phone;
            this.addressLine = addressLine;
            this.city = city;
        }

        public long getId() {
            return id;
        }

        public String getUsername() {
            return username;
        }

        public String getRole() {
            return role;
        }

        public String getFullName() {
            return fullName != null ? fullName : "";
        }

        public String getPhone() {
            return phone != null ? phone : "";
        }

        public String getAddressLine() {
            return addressLine != null ? addressLine : "";
        }

        public String getCity() {
            return city != null ? city : "";
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public void setAddressLine(String addressLine) {
            this.addressLine = addressLine;
        }

        public void setCity(String city) {
            this.city = city;
        }
    }
}
