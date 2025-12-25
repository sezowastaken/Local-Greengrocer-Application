package com.group25.greengrocer.controller;

import com.group25.greengrocer.util.DbAdapter;
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

        try {
            Connection conn = DbAdapter.getConnection();
            String query = "SELECT id, role FROM users WHERE username = ? AND password_hash = ?";
            PreparedStatement currentState = conn.prepareStatement(query);
            currentState.setString(1, username);
            currentState.setString(2, password);
            ResultSet result = currentState.executeQuery();

            if (result.next()) {
                String role = result.getString("role");
                int userId = result.getInt("id");

                if ("customer".equalsIgnoreCase(role)) {
                    javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                            getClass().getResource("/fxml/customer.fxml"));
                    javafx.scene.Parent root = loader.load();

                    // Pass session data
                    CustomerController controller = loader.getController();
                    controller.setCustomerSession(userId, username);

                    usernameField.getScene().setRoot(root);
                } else if ("carrier".equalsIgnoreCase(role)) {
                    javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                            getClass().getResource("/fxml/carrier.fxml"));
                    javafx.scene.Parent root = loader.load();
                    usernameField.getScene().setRoot(root);
                } else if ("owner".equalsIgnoreCase(role)) {
                    javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                            getClass().getResource("/fxml/owner.fxml"));
                    javafx.scene.Parent root = loader.load();
                    usernameField.getScene().setRoot(root);
                } else {
                    errorLabel.setText("Login Successful! Welcome, " + role);
                    errorLabel.getStyleClass().setAll("success-label");
                }
            } else {
                errorLabel.setText("Invalid username or password.");
                errorLabel.getStyleClass().setAll("error-label");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            errorLabel.setText("Database connection error.");
            errorLabel.getStyleClass().setAll("error-label");
        } catch (java.io.IOException e) {
            e.printStackTrace();
            errorLabel.setText("Error loading view.");
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
