package com.group25.greengrocer.controller;

import com.group25.greengrocer.util.DbAdapter;
import com.group25.greengrocer.util.Session;
import com.group25.greengrocer.model.User;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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

        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            errorLabel.setText("Please enter both username and password.");
            errorLabel.getStyleClass().setAll("error-label");
            return;
        }

        String query = "SELECT id, role, password_hash FROM users WHERE username = ?";

        try (Connection conn = DbAdapter.getConnection();
                PreparedStatement currentState = conn.prepareStatement(query)) {

            currentState.setString(1, username);

            try (ResultSet result = currentState.executeQuery()) {
                if (result.next()) {
                    String storedPasswordHash = result.getString("password_hash");
                    String inputPasswordHash = hashPassword(password);

                    System.out.println("DEBUG: Username: " + username);
                    System.out.println("DEBUG: Input Password: " + password);
                    System.out.println("DEBUG: Stored Hash: " + storedPasswordHash);
                    System.out.println("DEBUG: Input Hash: " + inputPasswordHash);
                    System.out.println("DEBUG: Legacy Match: "
                            + (storedPasswordHash != null && storedPasswordHash.equals(password)));
                    System.out.println("DEBUG: Hash Match: "
                            + (storedPasswordHash != null && storedPasswordHash.equals(inputPasswordHash)));

                    // Check if password matches (either hashed or plain text for legacy support)
                    if (storedPasswordHash != null
                            && (storedPasswordHash.equals(inputPasswordHash) || storedPasswordHash.equals(password))) {
                        int id = result.getInt("id");
                        String role = result.getString("role");
                        System.out.println("DEBUG: Login Successful. Role: " + role);

                        // Create User object
                        User currentUser;
                        if ("carrier".equalsIgnoreCase(role)) {
                            currentUser = new com.group25.greengrocer.model.Carrier(id, username, password);
                        } else {
                            currentUser = new User(id, username, password, role) {
                            };
                        }

                        Session.setCurrentUser(currentUser);

                        errorLabel.setText("Login Successful! Welcome, " + role);
                        errorLabel.getStyleClass().setAll("success-label");

                        // Scene Switching
                        javafx.stage.Stage stage = (javafx.stage.Stage) usernameField.getScene().getWindow();
                        javafx.scene.Parent root = null;

                        if ("customer".equalsIgnoreCase(role)) {
                            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                                    getClass().getResource("/fxml/customer.fxml"));
                            root = loader.load();
                            // CustomerController needs session info:
                            CustomerController controller = loader.getController();
                            controller.setCustomerSession(id, username);
                        } else if ("carrier".equalsIgnoreCase(role)) {
                            root = javafx.fxml.FXMLLoader.load(getClass().getResource("/fxml/carrier.fxml"));
                        } else if ("owner".equalsIgnoreCase(role)) {
                            root = javafx.fxml.FXMLLoader.load(getClass().getResource("/fxml/owner.fxml"));
                        }

                        if (root != null) {
                            stage.getScene().setRoot(root);
                        }
                    } else {
                        errorLabel.setText("Invalid username or password.");
                        errorLabel.getStyleClass().setAll("error-label");
                    }
                } else {
                    errorLabel.setText("Invalid username or password.");
                    errorLabel.getStyleClass().setAll("error-label");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            errorLabel.setText("Database connection error.");
            errorLabel.getStyleClass().setAll("error-label");
        } catch (java.io.IOException e) {
            e.printStackTrace();
            errorLabel.setText("Error loading view: " + e.getMessage());
            errorLabel.getStyleClass().setAll("error-label");
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

    @FXML
    private void handleRegisterLink() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/fxml/register.fxml"));
            javafx.scene.Parent root = loader.load();
            usernameField.getScene().setRoot(root);
        } catch (java.io.IOException e) {
            e.printStackTrace();
            errorLabel.setText("Navigation failed.");
        }
    }
}
