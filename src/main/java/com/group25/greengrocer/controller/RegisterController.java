package com.group25.greengrocer.controller;

import com.group25.greengrocer.util.DbAdapter;
// import com.group25.greengrocer.util.SecurityUtil; // Removed
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javafx.stage.FileChooser;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import com.group25.greengrocer.dao.UserDao;

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
    private VBox licenseUploadSection;
    @FXML
    private ImageView imgLicenseFrontPreview;
    @FXML
    private ImageView imgLicenseBackPreview;
    @FXML
    private Label lblFrontStatus;
    @FXML
    private Label lblBackStatus;

    private byte[] licenseFrontBytes;
    private byte[] licenseBackBytes;

    @FXML
    public void initialize() {
        roleComboBox.getItems().addAll("customer", "carrier", "owner");
        roleComboBox.getSelectionModel().selectFirst();

        // Listener for role selection
        roleComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            boolean isCarrier = "carrier".equalsIgnoreCase(newVal);
            if (licenseUploadSection != null) {
                licenseUploadSection.setVisible(isCarrier);
                licenseUploadSection.setManaged(isCarrier);
            }
        });

        // Initial state
        if (licenseUploadSection != null) {
            licenseUploadSection.setVisible(false);
            licenseUploadSection.setManaged(false);
        }
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

        if ("carrier".equalsIgnoreCase(role)) {
            if (licenseFrontBytes == null || licenseBackBytes == null) {
                setMessage("Carriers must upload both license photos.", true);
                return;
            }
            // Use UserDao for carrier to handle PENDING status and blobs
            UserDao dao = new UserDao();
            try {
                dao.addCarrier(username, password, licenseFrontBytes, licenseBackBytes);
                startRedirectCountdown();
            } catch (SQLException e) {
                e.printStackTrace();
                if (e.getMessage().contains("Unknown column")) {
                    setMessage("System Error: Database schema outdated. Please contact admin.", true);
                } else if (e.getMessage().contains("Duplicate")) {
                    setMessage("Username already exists.", true);
                } else {
                    setMessage("Registration failed: " + e.getMessage(), true);
                }
            }
            return;
        }

        try {
            Connection conn = DbAdapter.getConnection();
            String query = "INSERT INTO users (username, password_hash, role) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, hashPassword(password)); // Hash this!
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

    @FXML
    private void handleUploadFront() {
        File file = chooseImageFile();
        if (file != null) {
            licenseFrontBytes = readFileToBytes(file);
            lblFrontStatus.setText(file.getName());
            imgLicenseFrontPreview.setImage(new Image(file.toURI().toString()));
        }
    }

    @FXML
    private void handleUploadBack() {
        File file = chooseImageFile();
        if (file != null) {
            licenseBackBytes = readFileToBytes(file);
            lblBackStatus.setText(file.getName());
            imgLicenseBackPreview.setImage(new Image(file.toURI().toString()));
        }
    }

    private File chooseImageFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select License Image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        return fileChooser.showOpenDialog(usernameField.getScene().getWindow());
    }

    private byte[] readFileToBytes(File file) {
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] data = new byte[(int) file.length()];
            fis.read(data);
            return data;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
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

    @FXML
    private void handleApplyAsCarrier() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/carrier_application.fxml"));
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
