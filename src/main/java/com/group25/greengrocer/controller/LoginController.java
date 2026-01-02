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

/**
 * Controller for the login screen.
 * 
 * This controller handles user authentication and role-based navigation.
 * It validates user credentials against the database, checks carrier approval status,
 * creates user sessions, and redirects users to their appropriate dashboard screens
 * based on their roles (customer, carrier, or owner).
 * 
 * Features:
 * - Username and password validation
 * - SHA-256 password hashing and verification
 * - Carrier approval status checking (PENDING carriers cannot login)
 * - Session management using the Session utility
 * - Role-based navigation to different dashboards
 * 
 * @see Session
 * @see User
 * @see Carrier
 */
public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    /**
     * Initializes the login form.
     * 
     * Currently empty as no initialization logic is required.
     * Previously contained database migration code which was removed per user request.
     */
    @FXML
    public void initialize() {
        // Migration removed per user request.
    }

    /**
     * Handles the login process when the user submits credentials.
     * 
     * Performs the following operations:
     * Validates that username and password fields are not empty
     * Queries the database for the user with the given username
     * Verifies the password using SHA-256 hash comparison
     *  Checks if carrier users have PENDING status (blocks login if pending)
     *  Creates a User or Carrier object and stores it in the session
     *  Loads the appropriate dashboard FXML based on user role:
     *    - customer -> customer.fxml
     *    - carrier -> carrier.fxml
     *    - owner -> owner.fxml
     * Initializes the respective controller with user session data
     * 
     * Error handling:
     * - Invalid credentials: displays error message
     * - Pending carrier: displays waiting message
     * - Database errors: displays connection error
     * - FXML loading errors: displays navigation error
     */
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

        String query = "SELECT id, role, password_hash, status FROM users WHERE username = ?";

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
                String status = result.getString("status");
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

                // Check if carrier is pending approval
                if ("carrier".equalsIgnoreCase(role) && "PENDING".equalsIgnoreCase(status)) {
                    errorLabel.setText(
                            "Your carrier application is pending approval. Please wait for the owner to review your application.");
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

    /**
     * Hashes a password using SHA-256 algorithm.
     * 
     * Converts the password string to bytes using UTF-8 encoding, applies SHA-256
     * hashing, and returns the result as a hexadecimal string.
     * 
     * @param password The plain text password to hash
     * @return The hashed password as a hexadecimal string, or null if hashing fails
     */
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

    /**
     * Navigates to the registration screen.
     * 
     * Loads the register.fxml file and replaces the current scene root.
     * Called when the user clicks the "Register" link on the login screen.
     */
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
