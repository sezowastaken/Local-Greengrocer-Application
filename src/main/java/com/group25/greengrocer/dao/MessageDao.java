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
 * including sending messages, retrieving conversations, marking messages as
 * read,
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

    /**
     * Retrieves the latest message for each distinct user who has communicated with
     * the owner.
     * This is used to build the conversation list in the Owner panel.
     */
    public List<Message> getRecentConversations(long ownerId) {
        List<Message> messages = new ArrayList<>();
        // Complex query to get the last message exchange with each user
        // We get distinct 'other' users, and for each, find the latest message ID
        String query = "SELECT m.*, " +
                "CASE WHEN m.sender_id = ? THEN r.username ELSE s.username END as other_username, " +
                "CASE WHEN m.sender_id = ? THEN m.receiver_id ELSE m.sender_id END as other_user_id " +
                "FROM messages m " +
                "LEFT JOIN users s ON m.sender_id = s.id " +
                "LEFT JOIN users r ON m.receiver_id = r.id " +
                "WHERE m.id IN ( " +
                "    SELECT MAX(id) " +
                "    FROM messages " +
                "    WHERE sender_id = ? OR receiver_id = ? " +
                "    GROUP BY CASE WHEN sender_id = ? THEN receiver_id ELSE sender_id END " +
                ") " +
                "ORDER BY m.created_at DESC";

        try (Connection conn = DbAdapter.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setLong(1, ownerId);
            stmt.setLong(2, ownerId);
            stmt.setLong(3, ownerId);
            stmt.setLong(4, ownerId);
            stmt.setLong(5, ownerId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String otherName = rs.getString("other_username");
                    // We construct the message. Note: We might overload Message or just use
                    // sender_name field for display
                    Message msg = new Message(
                            rs.getInt("id"),
                            rs.getInt("other_user_id"), // Use other_user_id as senderId for conversation list
                            otherName, // Hack: putting the conversation partner name here for list display
                            rs.getString("subject"),
                            rs.getString("body"),
                            rs.getTimestamp("created_at"));
                    messages.add(msg);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }

    /**
     * Retrieves the full conversation history between two users.
     */
    public List<Message> getConversation(long user1, long user2) {
        List<Message> messages = new ArrayList<>();
        String query = "SELECT m.*, s.username as sender_name FROM messages m " +
                "JOIN users s ON m.sender_id = s.id " +
                "WHERE (m.sender_id = ? AND m.receiver_id = ?) " +
                "   OR (m.sender_id = ? AND m.receiver_id = ?) " +
                "ORDER BY m.created_at ASC";

        try (Connection conn = DbAdapter.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setLong(1, user1);
            stmt.setLong(2, user2);
            stmt.setLong(3, user2);
            stmt.setLong(4, user1);

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