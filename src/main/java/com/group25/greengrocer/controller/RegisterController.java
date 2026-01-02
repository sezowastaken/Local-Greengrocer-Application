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

/**
 * Controller for the user registration screen.
 * 
 * This controller manages the registration process for different user roles:
 * customers, carriers, and owners. It handles form validation, password hashing,
 * license image uploads for carriers, and database insertion.
 * 
 * Features:
 * - Role-based registration (customer, carrier, owner)
 * - Password confirmation validation
 * - SHA-256 password hashing
 * - Driver's license image upload for carriers (with PENDING status)
 * - Automatic redirect to login screen after successful registration
 * 
 * @see UserDao
 */
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

    /**
     * Initializes the registration form.
     * 
     * Sets up the role combo box with available roles (customer, carrier, owner)
     * and configures a listener to show/hide the license upload section based on
     * the selected role. Carriers must upload license images, while other roles do not.
     */
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

    /**
     * Handles the registration process when the user submits the form.
     * 
     * Performs the following validations and operations:
     * 1. Validates that username and password fields are not empty
     * 2. Validates that password and confirm password match
     * 3. For carriers: validates that both license images are uploaded
     * 4. Hashes the password using SHA-256
     * 5. Inserts the new user into the database with appropriate role
     * 6. Starts a countdown timer and redirects to login screen on success
     */
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
            String query = "INSERT INTO users (username, password_hash, role, individual_loyalty_rate) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, hashPassword(password)); // Hash this!
            stmt.setString(3, role);

            if ("customer".equalsIgnoreCase(role)) {
                stmt.setBigDecimal(4, java.math.BigDecimal.ZERO);
            } else {
                stmt.setBigDecimal(4, null);
            }

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

    /**
     * Handles the upload of the front side of the driver's license.
     * 
     * Opens a file chooser dialog for the user to select an image file,
     * reads the file into a byte array, updates the status label with the filename,
     * and displays a preview of the selected image.
     */
    @FXML
    private void handleUploadFront() {
        File file = chooseImageFile();
        if (file != null) {
            licenseFrontBytes = readFileToBytes(file);
            lblFrontStatus.setText(file.getName());
            imgLicenseFrontPreview.setImage(new Image(file.toURI().toString()));
        }
    }

    /**
     * Handles the upload of the back side of the driver's license.
     * 
     * Opens a file chooser dialog for the user to select an image file,
     * reads the file into a byte array, updates the status label with the filename,
     * and displays a preview of the selected image.
     */
    @FXML
    private void handleUploadBack() {
        File file = chooseImageFile();
        if (file != null) {
            licenseBackBytes = readFileToBytes(file);
            lblBackStatus.setText(file.getName());
            imgLicenseBackPreview.setImage(new Image(file.toURI().toString()));
        }
    }

    /**
     * Opens a file chooser dialog for selecting an image file.
     * 
     * @return The selected File, or null if the user cancels the dialog
     */
    private File chooseImageFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select License Image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        return fileChooser.showOpenDialog(usernameField.getScene().getWindow());
    }

    /**
     * Reads a file and converts its contents to a byte array.
     * 
     * @param file The file to read
     * @return A byte array containing the file contents, or null if an error occurs
     */
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

    /**
     * Hashes a password using SHA-256 algorithm.
     * 
     * @param password The plain text password to hash
     * @return The hashed password as a hexadecimal string, or null if hashing fails
     */
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

    /**
     * Starts a countdown timer for automatic redirect to login screen.
     * 
     * Displays a success message with a countdown (5 seconds) and automatically
     * redirects to the login screen when the countdown reaches zero.
     * Uses JavaFX Timeline animation for the countdown effect.
     */
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

    /**
     * Navigates to the login screen.
     * 
     * Loads the login.fxml file and replaces the current scene root.
     * Called either manually by the user or automatically after successful registration.
     */
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

    /**
     * Navigates to the carrier application screen.
     * 
     * Loads the carrier_application.fxml file and replaces the current scene root.
     * This method provides an alternative path for users who want to apply as carriers.
     */
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

    /**
     * Displays a message to the user in the message label.
     * 
     * @param text The message text to display
     * @param isError True to display as an error message (red), false for success (green)
     */
    private void setMessage(String text, boolean isError) {
        messageLabel.setText(text);
        messageLabel.getStyleClass().setAll(isError ? "error-label" : "success-label");
    }
}
