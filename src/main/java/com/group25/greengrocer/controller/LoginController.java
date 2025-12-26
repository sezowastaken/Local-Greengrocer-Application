package com.group25.greengrocer.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.group25.greengrocer.model.Carrier;
import com.group25.greengrocer.model.User;
import com.group25.greengrocer.util.DbAdapter;
import com.group25.greengrocer.util.Session;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    @FXML
    private void handleLogin() {

        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username == null || username.trim().isEmpty()
                || password == null || password.trim().isEmpty()) {
            errorLabel.setText("Please enter both username and password.");
            errorLabel.getStyleClass().setAll("error-label");
            return;
        }

        String query = "SELECT id, role, password_hash FROM users WHERE username = ?";

        try (Connection conn = DbAdapter.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);

            try (ResultSet result = stmt.executeQuery()) {

                if (!result.next()) {
                    errorLabel.setText("Invalid username or password.");
                    errorLabel.getStyleClass().setAll("error-label");
                    return;
                }

                int userId = result.getInt("id");
                String role = result.getString("role");
                String storedPasswordHash = result.getString("password_hash");
                String inputPasswordHash = hashPassword(password);

                // DEBUG (keep)
                System.out.println("DEBUG: Username: " + username);
                System.out.println("DEBUG: Stored Hash: " + storedPasswordHash);
                System.out.println("DEBUG: Input Hash: " + inputPasswordHash);

                boolean passwordMatches = storedPasswordHash != null &&
                        (storedPasswordHash.equals(inputPasswordHash)
                                || storedPasswordHash.equals(password));

                if (!passwordMatches) {
                    errorLabel.setText("Invalid username or password.");
                    errorLabel.getStyleClass().setAll("error-label");
                    return;
                }

                // Create session user
                User currentUser;
                if ("carrier".equalsIgnoreCase(role)) {
                    currentUser = new Carrier(userId, username, password);
                } else {
                    currentUser = new User(userId, username, password, role) {
                    };
                }

                Session.setCurrentUser(currentUser);

                errorLabel.setText("Login Successful! Welcome, " + role);
                errorLabel.getStyleClass().setAll("success-label");

                // Scene switching
                Stage stage = (Stage) usernameField.getScene().getWindow();
                Parent root = null;

                if ("customer".equalsIgnoreCase(role)) {
                    FXMLLoader loader = new FXMLLoader(
                            getClass().getResource("/fxml/customer.fxml"));
                    root = loader.load();

                    CustomerController controller = loader.getController();
                    controller.setCustomerSession(userId, username);

                } else if ("carrier".equalsIgnoreCase(role)) {
                    FXMLLoader loader = new FXMLLoader(
                            getClass().getResource("/fxml/carrier.fxml"));
                    root = loader.load();

                    CarrierController controller = loader.getController();
                    controller.setCarrierSession(userId, username);
                    controller.handleRefreshAll();

                } else if ("owner".equalsIgnoreCase(role)) {
                    FXMLLoader loader = new FXMLLoader(
                            getClass().getResource("/fxml/owner.fxml"));
                    root = loader.load();

                    OwnerController controller = loader.getController();
                    controller.setOwnerSession(userId, username);
                }

                if (root != null) {
                    stage.getScene().setRoot(root);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            errorLabel.setText("Database connection error.");
            errorLabel.getStyleClass().setAll("error-label");

        } catch (Exception e) {
            e.printStackTrace();
            errorLabel.setText("Error loading view.");
            errorLabel.getStyleClass().setAll("error-label");
        }
    }

    private String hashPassword(String password) {
        try {
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(password.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : encodedhash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1)
                    hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            return null;
        }
    }

    @FXML
    private void handleRegisterLink() {
        try {
            Parent root = FXMLLoader.load(
                    getClass().getResource("/fxml/register.fxml"));
            usernameField.getScene().setRoot(root);
        } catch (Exception e) {
            errorLabel.setText("Navigation failed.");
        }
    }
}
