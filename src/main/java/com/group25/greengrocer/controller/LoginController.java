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

        String query = "SELECT * FROM users WHERE username = ? AND password_hash = ?"; // Corrected to password_hash
                                                                                       // based on DB schema

        try (Connection conn = DbAdapter.getConnection();
                PreparedStatement currentState = conn.prepareStatement(query)) {

            currentState.setString(1, username);
            currentState.setString(2, password);

            try (ResultSet result = currentState.executeQuery()) {
                if (result.next()) {
                    int id = result.getInt("id");
                    String role = result.getString("role");

                    // Create User object (using anonymous subclass for concrete instantiation if
                    // needed or specific classes if they existed fully aligned,
                    // but simple anonymouse extension of abstract User is safe fallback or checking
                    // for specific subclasses)
                    // Given the User class is abstract:
                    User currentUser;
                    if ("carrier".equalsIgnoreCase(role)) {
                        currentUser = new com.group25.greengrocer.model.Carrier(id, username, password);
                    } else {
                        // For Owner/Customer if no specific classes exist yet or just generic User
                        // needed for Session
                        // Creating a concrete anonymous implementation or standard User if it wasn't
                        // abstract.
                        // Since User IS abstract, we need a concrete class.
                        // Let's assume generic user or create a simple concrete one if needed.
                        // Or we can just extend User inline.
                        currentUser = new User(id, username, password, role) {
                        };
                    }

                    com.group25.greengrocer.util.Session.setCurrentUser(currentUser);

                    errorLabel.setText("Login Successful! Welcome, " + role);
                    errorLabel.getStyleClass().setAll("success-label");

                    // Scene Switching via Stage
                    javafx.stage.Stage stage = (javafx.stage.Stage) usernameField.getScene().getWindow();
                    javafx.scene.Parent root = null;

                    if ("customer".equalsIgnoreCase(role)) {
                        root = javafx.fxml.FXMLLoader.load(getClass().getResource("/fxml/customer.fxml"));
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
