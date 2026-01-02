package com.group25.greengrocer.dao;

import com.group25.greengrocer.model.Message;
import com.group25.greengrocer.util.DbAdapter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Message operations.
 * 
 * This class provides database operations for managing messages between users,
 * including sending messages, retrieving conversations, marking messages as read,
 * and fetching message history.
 */
public class MessageDao {

    public List<Message> getAllMessages() {
        List<Message> messages = new ArrayList<>();
        // Joining with users to get sender name if possible, or just raw fetch
        String query = "SELECT m.*, u.username as sender_name FROM messages m " +
                "JOIN users u ON m.sender_id = u.id " +
                "ORDER BY m.created_at DESC";

        try (Connection conn = DbAdapter.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                messages.add(new Message(
                        rs.getInt("id"),
                        rs.getInt("sender_id"),
                        rs.getString("sender_name"),
                        rs.getString("subject"),
                        rs.getString("body"), // Column is 'body' in DB
                        rs.getTimestamp("created_at")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }

    public void sendMessage(long senderId, long receiverId, String subject, String body) {
        String sql = "INSERT INTO messages (sender_id, receiver_id, subject, body, is_read) VALUES (?, ?, ?, ?, 0)";
        try (Connection conn = DbAdapter.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, senderId);
            stmt.setLong(2, receiverId);
            stmt.setString(3, subject);
            stmt.setString(4, body);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Message> getMessagesForUser(long userId) {
        List<Message> messages = new ArrayList<>();
        // Get messages where user is sender or receiver
        String query = "SELECT m.*, s.username as sender_name FROM messages m " +
                "JOIN users s ON m.sender_id = s.id " +
                "WHERE m.sender_id = ? OR m.receiver_id = ? " +
                "ORDER BY m.created_at DESC";

        try (Connection conn = DbAdapter.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setLong(1, userId);
            stmt.setLong(2, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    messages.add(new Message(
                            rs.getInt("id"),
                            rs.getInt("sender_id"),
                            rs.getString("sender_name"),
                            rs.getString("subject"),
                            rs.getString("body"),
                            rs.getTimestamp("created_at")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }
}