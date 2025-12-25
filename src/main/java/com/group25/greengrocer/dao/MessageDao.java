package com.group25.greengrocer.dao;

import com.group25.greengrocer.model.Message;
import com.group25.greengrocer.util.DbAdapter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
                        rs.getString("body"), // Column is 'body' in DB
                        rs.getTimestamp("created_at"), // Column is 'created_at' in DB
                        null, // 'reply' column does not exist in DB
                        null)); // 'reply_time' column does not exist in DB
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }

    public void replyToMessage(int messageId, String reply) {
        // String query = "UPDATE messages SET reply = ?, reply_time = NOW() WHERE id =
        // ?";
        // Columns reply and reply_time do not exist. Logic needs to be revised (e.g.
        // creating new message).
        System.err.println("replyToMessage not implemented: Schema missing reply columns.");
        /*
         * try (Connection conn = DbAdapter.getConnection();
         * PreparedStatement stmt = conn.prepareStatement(query)) {
         * 
         * stmt.setString(1, reply);
         * stmt.setInt(2, messageId);
         * stmt.executeUpdate();
         * } catch (SQLException e) {
         * e.printStackTrace();
         * }
         */
    }
}