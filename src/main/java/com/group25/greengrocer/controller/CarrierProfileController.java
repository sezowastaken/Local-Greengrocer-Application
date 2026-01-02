package com.group25.greengrocer.controller;

import com.group25.greengrocer.dao.UserDao;
import com.group25.greengrocer.dao.UserDao.UserProfile;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class CarrierProfileController {

    @FXML
    private Label usernameLabel;
    @FXML
    private Label roleLabel;
    @FXML
    private TextField fullNameField;
    @FXML
    private TextField phoneField;
    @FXML
    private TextField addressField;
    @FXML
    private TextField cityField;

    private UserDao userDao = new UserDao();
    private long carrierId;

    /**
     * Set carrier session and load profile data
     */
    public void setCarrierSession(long carrierId) {
        this.carrierId = carrierId;
        loadProfileData();
    }

    /**
     * Load current profile data from database
     */
    private void loadProfileData() {
        try {
            UserProfile profile = userDao.findById(carrierId);
            if (profile != null) {
                usernameLabel.setText(profile.getUsername());
                roleLabel.setText(profile.getRole());
                fullNameField.setText(profile.getFullName());
                phoneField.setText(profile.getPhone());
                addressField.setText(profile.getAddressLine());
                cityField.setText(profile.getCity());
            } else {
                showError("Failed to load profile data.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error loading profile: " + e.getMessage());
        }
    }

    /**
     * Handle Update Profile button
     */
    @FXML
    private void handleUpdateProfile() {
        // Validate input
        String fullName = fullNameField.getText().trim();
        String phone = phoneField.getText().trim();
        String address = addressField.getText().trim();
        String city = cityField.getText().trim();

        if (fullName.isEmpty()) {
            showError("Full Name is required!");
            return;
        }

        // Basic phone validation (optional field)
        if (!phone.isEmpty() && !isValidPhone(phone)) {
            showError("Invalid phone number format. Please use digits and optional + or - characters.");
            return;
        }

        try {
            userDao.updateProfile(carrierId, fullName, phone, address, city);
            showSuccess("Profile updated successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to update profile: " + e.getMessage());
        }
    }

    /**
     * Handle Back button - navigate back to carrier panel
     */
    @FXML
    private void handleBack() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                    getClass().getResource("/fxml/carrier.fxml"));
            javafx.scene.Parent root = loader.load();

            // Pass session data back to CarrierController
            CarrierController controller = loader.getController();
            UserProfile profile = userDao.findById(carrierId);
            if (profile != null) {
                controller.setCarrierSession(carrierId, profile.getUsername());
                controller.handleRefreshAll();
            }

            Stage stage = (Stage) fullNameField.getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to navigate back: " + e.getMessage());
        }
    }

    /**
     * Validate phone number format
     */
    private boolean isValidPhone(String phone) {
        // Allow digits, spaces, +, -, (, )
        return phone.matches("[0-9\\s+\\-()]+");
    }

    /**
     * Show error alert
     */
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Show success alert
     */
    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
