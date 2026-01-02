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

    public java.util.List<User> getCarriers() {
        java.util.List<User> carriers = new ArrayList<>();
        String query = "SELECT id, username, password_hash FROM users WHERE role = 'carrier'";

        try (Connection conn = DbAdapter.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                carriers.add(new Carrier(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password_hash")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return carriers;
    }

    public int getCustomerCount() {
        String query = "SELECT COUNT(*) FROM users WHERE role = 'customer'";
        try (Connection conn = DbAdapter.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);
                ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getOwnerCount() {
        String query = "SELECT COUNT(*) FROM users WHERE role = 'owner'";
        try (Connection conn = DbAdapter.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);
                ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void addCarrier(String username, String password) {
        String query = "INSERT INTO users (username, password_hash, role, status) VALUES (?, ?, 'carrier', 'APPROVED')";
        try (Connection conn = DbAdapter.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, hashPassword(password));
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addCarrier(String username, String password, byte[] licenseFront, byte[] licenseBack)
            throws SQLException {
        String query = "INSERT INTO users (username, password_hash, role, license_front, license_back, status) VALUES (?, ?, 'carrier', ?, ?, 'PENDING')";

        try (Connection conn = DbAdapter.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            stmt.setString(2, hashPassword(password));
            stmt.setBytes(3, licenseFront);
            stmt.setBytes(4, licenseBack);
            stmt.executeUpdate();

        }
    }

    public java.util.List<Carrier> getPendingCarriers() {
        java.util.List<Carrier> carriers = new ArrayList<>();
        String query = "SELECT id, username, password_hash FROM users WHERE role = 'carrier' AND status = 'PENDING'";

        try (Connection conn = DbAdapter.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                carriers.add(new Carrier(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password_hash")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return carriers;
    }

    public boolean updateCarrierStatus(int userId, String status) {
        String query = "UPDATE users SET status = ? WHERE id = ?";
        try (Connection conn = DbAdapter.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, status);
            stmt.setInt(2, userId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Carrier getCarrierWithLicenses(int userId) {
        String query = "SELECT id, username, password_hash, license_front, license_back FROM users WHERE id = ?";
        try (Connection conn = DbAdapter.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Carrier carrier = new Carrier(
                            rs.getInt("id"),
                            rs.getString("username"),
                            rs.getString("password_hash"));
                    carrier.setLicenseFront(rs.getBytes("license_front"));
                    carrier.setLicenseBack(rs.getBytes("license_back"));
                    return carrier;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
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
                            rs.getString("city"));
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

    /**
     * Update user password
     */
    public boolean updatePassword(int userId, String newPasswordHash) {
        String sql = "UPDATE users SET password_hash = ? WHERE id = ?";

        try (Connection conn = DbAdapter.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newPasswordHash);
            stmt.setInt(2, userId);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Save or update profile picture for a user
     * 
     * @param userId      User ID
     * @param pictureData Image data as byte array
     * @return true if successful, false otherwise
     */
    public boolean saveProfilePicture(int userId, byte[] pictureData) {
        String sql = "UPDATE users SET profile_picture_blob = ? WHERE id = ?";

        try (Connection conn = DbAdapter.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBytes(1, pictureData);
            stmt.setInt(2, userId);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Get profile picture for a user
     * 
     * @param userId User ID
     * @return Profile picture as byte array, or null if not found
     */
    public byte[] getProfilePicture(int userId) {
        String sql = "SELECT profile_picture_blob FROM users WHERE id = ?";

        try (Connection conn = DbAdapter.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBytes("profile_picture_blob");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Delete profile picture for a user
     * 
     * @param userId User ID
     * @return true if successful, false otherwise
     */
    public boolean deleteProfilePicture(int userId) {
        String sql = "UPDATE users SET profile_picture_blob = NULL WHERE id = ?";

        try (Connection conn = DbAdapter.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private String hashPassword(String password) {
        try {
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(password.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder(2 * encodedhash.length);
            for (byte b : encodedhash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1)
                    hexString.append('0');
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

    public void setIndividualLoyaltyRate(long userId, java.math.BigDecimal rate) {
        String sql = "UPDATE users SET individual_loyalty_rate = ? WHERE id = ?";
        try (Connection conn = DbAdapter.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBigDecimal(1, rate);
            stmt.setLong(2, userId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public java.math.BigDecimal getIndividualLoyaltyRate(long userId) {
        String sql = "SELECT individual_loyalty_rate FROM users WHERE id = ?";
        try (Connection conn = DbAdapter.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal("individual_loyalty_rate");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get all customers with their loyalty tier information
     * Calculates tier based on total spending from delivered orders
     */
    public java.util.List<com.group25.greengrocer.model.CustomerLoyalty> getAllCustomerLoyalty() {
        java.util.List<com.group25.greengrocer.model.CustomerLoyalty> loyaltyList = new java.util.ArrayList<>();

        String query = "SELECT u.id, u.username, COALESCE(SUM(o.total), 0) as total_spent " +
                "FROM users u " +
                "LEFT JOIN orders o ON u.id = o.customer_id AND o.status = 'DELIVERED' " +
                "WHERE u.role = 'customer' " +
                "GROUP BY u.id, u.username " +
                "ORDER BY total_spent DESC";

        try (Connection conn = DbAdapter.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                long customerId = rs.getLong("id");
                String username = rs.getString("username");
                double totalSpent = rs.getDouble("total_spent");

                loyaltyList.add(new com.group25.greengrocer.model.CustomerLoyalty(
                        customerId, username, totalSpent));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return loyaltyList;
    }

    /**
     * Search customers by username with loyalty information
     */
    public java.util.List<com.group25.greengrocer.model.CustomerLoyalty> searchCustomerLoyalty(String searchQuery) {
        java.util.List<com.group25.greengrocer.model.CustomerLoyalty> loyaltyList = new java.util.ArrayList<>();

        String query = "SELECT u.id, u.username, COALESCE(SUM(o.total), 0) as total_spent " +
                "FROM users u " +
                "LEFT JOIN orders o ON u.id = o.customer_id AND o.status = 'DELIVERED' " +
                "WHERE u.role = 'customer' AND u.username LIKE ? " +
                "GROUP BY u.id, u.username " +
                "ORDER BY total_spent DESC";

        try (Connection conn = DbAdapter.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, "%" + searchQuery + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    long customerId = rs.getLong("id");
                    String username = rs.getString("username");
                    double totalSpent = rs.getDouble("total_spent");

                    loyaltyList.add(new com.group25.greengrocer.model.CustomerLoyalty(
                            customerId, username, totalSpent));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return loyaltyList;
    }

    /**
     * Update customer's individual_loyalty_rate based on tier
     * Converts percentage to decimal (e.g., 5% -> 0.05)
     */
    public void updateCustomerLoyaltyRate(long customerId, int discountPercentage) {
        String query = "UPDATE users SET individual_loyalty_rate = ? WHERE id = ?";

        try (Connection conn = DbAdapter.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            // Convert percentage to decimal (5% -> 0.05)
            java.math.BigDecimal rate = new java.math.BigDecimal(discountPercentage / 100.0);
            stmt.setBigDecimal(1, rate);
            stmt.setLong(2, customerId);

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
