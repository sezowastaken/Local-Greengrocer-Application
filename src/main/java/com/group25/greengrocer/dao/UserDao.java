package com.group25.greengrocer.dao;

import com.group25.greengrocer.model.User;
import com.group25.greengrocer.model.Carrier;

import com.group25.greengrocer.util.DbAdapter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDao {

    public List<User> getCarriers() {
        List<User> carriers = new ArrayList<>();
        String query = "SELECT * FROM users WHERE role = 'carrier'";

        try (Connection conn = DbAdapter.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                // Assuming Carrier constructor matches User constructor or is simple
                carriers.add(new Carrier(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return carriers;
    }

    public void addCarrier(String username, String password) {
        String query = "INSERT INTO users (username, password, role) VALUES (?, ?, 'carrier')";

        try (Connection conn = DbAdapter.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            stmt.setString(2, password);
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
}