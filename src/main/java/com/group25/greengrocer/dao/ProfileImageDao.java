package com.group25.greengrocer.dao;

import com.group25.greengrocer.util.DbAdapter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Data Access Object for profile image operations.
 * 
 * This class provides database operations for managing user profile images,
 * including saving, updating, retrieving, and deleting profile pictures.
 * Images are stored as binary data in the database along with metadata.
 */
public class ProfileImageDao {

    /**
     * Save or update profile picture for a user
     * 
     * @param userId    User ID
     * @param imageData Image data as byte array
     * @param fileSize  Size of the file in bytes
     * @param fileType  File MIME type (e.g., "image/jpeg", "image/png")
     * @return true if successful, false otherwise
     */
    public boolean saveProfileImage(int userId, byte[] imageData, int fileSize, String fileType) {
        String checkSql = "SELECT user_id FROM profile_img WHERE user_id = ?";
        String insertSql = "INSERT INTO profile_img (user_id, image_data, file_size, file_type) VALUES (?, ?, ?, ?)";
        String updateSql = "UPDATE profile_img SET image_data = ?, file_size = ?, file_type = ?, upload_date = CURRENT_TIMESTAMP WHERE user_id = ?";

        try (Connection conn = DbAdapter.getConnection()) {
            // Check if profile image already exists
            boolean exists = false;
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setInt(1, userId);
                try (ResultSet rs = checkStmt.executeQuery()) {
                    exists = rs.next();
                }
            }

            // Insert or update accordingly
            if (exists) {
                try (PreparedStatement stmt = conn.prepareStatement(updateSql)) {
                    stmt.setBytes(1, imageData);
                    stmt.setInt(2, fileSize);
                    stmt.setString(3, fileType);
                    stmt.setInt(4, userId);
                    return stmt.executeUpdate() > 0;
                }
            } else {
                try (PreparedStatement stmt = conn.prepareStatement(insertSql)) {
                    stmt.setInt(1, userId);
                    stmt.setBytes(2, imageData);
                    stmt.setInt(3, fileSize);
                    stmt.setString(4, fileType);
                    return stmt.executeUpdate() > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Retrieve profile picture for a user
     * 
     * @param userId User ID
     * @return ProfileImage object containing image data, or null if not found
     */
    public ProfileImage getProfileImage(int userId) {
        String sql = "SELECT image_data, file_size, file_type, upload_date FROM profile_img WHERE user_id = ?";

        try (Connection conn = DbAdapter.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new ProfileImage(
                            userId,
                            rs.getBytes("image_data"),
                            rs.getInt("file_size"),
                            rs.getString("file_type"),
                            rs.getTimestamp("upload_date"));
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
    public boolean deleteProfileImage(int userId) {
        String sql = "DELETE FROM profile_img WHERE user_id = ?";

        try (Connection conn = DbAdapter.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Inner class to represent profile image data
     */
    public static class ProfileImage {
        private final int userId;
        private final byte[] imageData;
        private final int fileSize;
        private final String fileType;
        private final java.sql.Timestamp uploadDate;

        public ProfileImage(int userId, byte[] imageData, int fileSize, String fileType,
                java.sql.Timestamp uploadDate) {
            this.userId = userId;
            this.imageData = imageData;
            this.fileSize = fileSize;
            this.fileType = fileType;
            this.uploadDate = uploadDate;
        }

        public int getUserId() {
            return userId;
        }

        public byte[] getImageData() {
            return imageData;
        }

        public int getFileSize() {
            return fileSize;
        }

        public String getFileType() {
            return fileType;
        }

        public java.sql.Timestamp getUploadDate() {
            return uploadDate;
        }
    }
}
