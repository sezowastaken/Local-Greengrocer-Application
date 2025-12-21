package com.group25.greengrocer.controller;

import com.group25.greengrocer.util.DbAdapter;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RegisterController {

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private ComboBox<String> roleComboBox;
    @FXML
    private Label messageLabel;

    @FXML
    public void initialize() {
        roleComboBox.getItems().addAll("customer", "carrier", "owner");
        roleComboBox.getSelectionModel().selectFirst();
    }

    @FXML
    private void handleRegister() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String confirm = confirmPasswordField.getText();
        String role = roleComboBox.getValue(); // Safe because we select first

        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            setMessage("Please fill all fields.", true);
            return;
        }

        if (!password.equals(confirm)) {
            setMessage("Passwords do not match.", true);
            return;
        }

        try {
            Connection conn = DbAdapter.getConnection();
            String query = "INSERT INTO users (username, password_hash, role) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, password); // In real app, hash this!
            stmt.setString(3, role);

            stmt.executeUpdate();

            startRedirectCountdown();

            // Optional: Auto-redirect or let them click the link

        } catch (SQLException e) {
            e.printStackTrace();
            if (e.getMessage().contains("Duplicate")) {
                setMessage("Username already exists.", true);
            } else {
                setMessage("Database error during registration.", true);
            }
        }
    }

    private void startRedirectCountdown() {
        javafx.animation.Timeline timeline = new javafx.animation.Timeline();
        java.util.concurrent.atomic.AtomicInteger count = new java.util.concurrent.atomic.AtomicInteger(5);

        messageLabel.getStyleClass().setAll("success-label");

        javafx.animation.KeyFrame keyFrame = new javafx.animation.KeyFrame(javafx.util.Duration.seconds(1), event -> {
            int current = count.getAndDecrement();
            if (current > 0) {
                messageLabel.setText("Registration Successful! Redirecting in " + current + "s...");
            } else {
                handleGoToLogin();
            }
        });

        timeline.getKeyFrames().add(keyFrame);
        timeline.setCycleCount(6); // 5 down to 1, then 0 triggers redirect
        timeline.play();
    }

    @FXML
    private void handleGoToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Parent root = loader.load();
            usernameField.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
            setMessage("Navigation failed.", true);
        }
    }

    private void setMessage(String text, boolean isError) {
        messageLabel.setText(text);
        messageLabel.getStyleClass().setAll(isError ? "error-label" : "success-label");
    }
}
