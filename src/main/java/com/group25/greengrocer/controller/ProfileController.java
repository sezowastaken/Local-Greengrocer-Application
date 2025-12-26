package com.group25.greengrocer.controller;

import com.group25.greengrocer.dao.UserDao;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class ProfileController {

    @FXML
    private Text pageTitleText;

    @FXML
    private TextField usernameField;

    @FXML
    private TextField roleField;

    @FXML
    private TextField fullNameField;

    @FXML
    private TextField phoneField;

    @FXML
    private TextArea addressLineField;

    @FXML
    private ComboBox<String> cityComboBox;

    @FXML
    private Label statusLabel;

    private UserDao userDao = new UserDao();

    // Session data
    private long userId;
    private String username;
    private String role;

    // Turkish cities (alphabetically sorted)
    private static final List<String> TURKISH_CITIES = Arrays.asList(
            "Adana", "Adıyaman", "Afyonkarahisar", "Ağrı", "Aksaray", "Amasya", "Ankara", "Antalya",
            "Ardahan", "Artvin", "Aydın", "Balıkesir", "Bartın", "Batman", "Bayburt", "Bilecik",
            "Bingöl", "Bitlis", "Bolu", "Burdur", "Bursa", "Çanakkale", "Çankırı", "Çorum",
            "Denizli", "Diyarbakır", "Düzce", "Edirne", "Elazığ", "Erzincan", "Erzurum", "Eskişehir",
            "Gaziantep", "Giresun", "Gümüşhane", "Hakkari", "Hatay", "Iğdır", "Isparta", "İstanbul",
            "İzmir", "Kahramanmaraş", "Karabük", "Karaman", "Kars", "Kastamonu", "Kayseri", "Kilis",
            "Kırıkkale", "Kırklareli", "Kırşehir", "Kocaeli", "Konya", "Kütahya", "Malatya", "Manisa",
            "Mardin", "Mersin", "Muğla", "Muş", "Nevşehir", "Niğde", "Ordu", "Osmaniye", "Rize",
            "Sakarya", "Samsun", "Şanlıurfa", "Siirt", "Sinop", "Sivas", "Şırnak", "Tekirdağ",
            "Tokat", "Trabzon", "Tunceli", "Uşak", "Van", "Yalova", "Yozgat", "Zonguldak");

    /**
     * Initialize the controller
     */
    @FXML
    public void initialize() {
        // Populate city dropdown with Turkish cities
        cityComboBox.setItems(FXCollections.observableArrayList(TURKISH_CITIES));

        // Add phone number input filter (only digits, max 10)
        phoneField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                phoneField.setText(newValue.replaceAll("[^\\d]", ""));
            }
            if (newValue.length() > 10) {
                phoneField.setText(newValue.substring(0, 10));
            }
        });
    }

    /**
     * Set the user session and load profile data
     */
    public void setUserSession(long userId, String username, String role) {
        this.userId = userId;
        this.username = username;
        this.role = role;

        loadProfileData();
    }

    /**
     * Load user profile data from database
     */
    private void loadProfileData() {
        try {
            UserDao.UserProfile profile = userDao.findById(userId);

            if (profile != null) {
                // Set read-only fields
                usernameField.setText(profile.getUsername());
                roleField.setText(capitalizeRole(profile.getRole()));

                // Set editable fields
                fullNameField.setText(profile.getFullName());

                // Set phone (remove +(90) prefix if exists)
                String phone = profile.getPhone();
                if (phone.startsWith("+90")) {
                    phone = phone.substring(3).trim();
                } else if (phone.startsWith("90")) {
                    phone = phone.substring(2).trim();
                }
                phoneField.setText(phone.replaceAll("\\D", "")); // Remove all non-digits

                addressLineField.setText(profile.getAddressLine());

                // Set city in ComboBox
                String city = profile.getCity();
                if (city != null && !city.isEmpty()) {
                    cityComboBox.setValue(city);
                }

                // Update page title based on role
                pageTitleText.setText(capitalizeRole(profile.getRole()) + " Profile");
            } else {
                showError("Failed to load profile data");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showError("An error occurred while loading profile: " + e.getMessage());
        }
    }

    /**
     * Handle profile update
     */
    @FXML
    private void handleUpdateProfile() {
        try {
            // Validate inputs
            String fullName = fullNameField.getText().trim();
            String phone = phoneField.getText().trim();
            String addressLine = addressLineField.getText().trim();
            String city = cityComboBox.getValue();

            if (fullName.isEmpty()) {
                showWarning("Please enter your full name");
                return;
            }

            if (phone.isEmpty() || phone.length() != 10) {
                showWarning("Please enter a valid 10-digit phone number");
                return;
            }

            if (addressLine.isEmpty()) {
                showWarning("Please enter your address");
                return;
            }

            if (city == null || city.isEmpty()) {
                showWarning("Please select your city");
                return;
            }

            // Add +(90) prefix to phone before saving
            String phoneWithPrefix = "+90" + phone;

            // Update profile in database
            userDao.updateProfile(userId, fullName, phoneWithPrefix, addressLine, city);

            showSuccess("Profile updated successfully!");

        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to update profile: " + e.getMessage());
        }
    }

    /**
     * Handle back button - return to appropriate dashboard
     */
    @FXML
    private void handleBack() {
        try {
            String fxmlPath = "";

            // Determine which dashboard to return to based on role
            switch (role.toLowerCase()) {
                case "owner":
                    fxmlPath = "/fxml/owner.fxml";
                    break;
                case "customer":
                    fxmlPath = "/fxml/customer.fxml";
                    break;
                case "carrier":
                    fxmlPath = "/fxml/carrier.fxml";
                    break;
                default:
                    fxmlPath = "/fxml/login.fxml";
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            // Set session for the returned controller
            if (role.equalsIgnoreCase("owner")) {
                OwnerController controller = loader.getController();
                controller.setOwnerSession(userId, username);
            } else if (role.equalsIgnoreCase("customer")) {
                CustomerController controller = loader.getController();
                controller.setCustomerSession(userId, username);
            } else if (role.equalsIgnoreCase("carrier")) {
                CarrierController controller = loader.getController();
                controller.setCarrierSession(userId, username);
            }

            usernameField.getScene().setRoot(root);

        } catch (IOException e) {
            e.printStackTrace();
            showError("Failed to return to dashboard: " + e.getMessage());
        }
    }

    /**
     * Capitalize role for display
     */
    private String capitalizeRole(String role) {
        if (role == null || role.isEmpty()) {
            return "";
        }
        return role.substring(0, 1).toUpperCase() + role.substring(1).toLowerCase();
    }

    /**
     * Show success message
     */
    private void showSuccess(String message) {
        statusLabel.setText(message);
        statusLabel.setStyle(
                "-fx-text-fill: #27ae60; -fx-background-color: #d4edda; -fx-font-size: 14px; -fx-padding: 10px; -fx-background-radius: 5px;");
        statusLabel.setVisible(true);
        statusLabel.setManaged(true);

        // Auto-hide after 3 seconds
        new Thread(() -> {
            try {
                Thread.sleep(3000);
                javafx.application.Platform.runLater(() -> {
                    statusLabel.setVisible(false);
                    statusLabel.setManaged(false);
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * Show warning message
     */
    private void showWarning(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText(null);
        alert.setContentText(message);
        styleAlert(alert, "warning-alert");
        alert.showAndWait();
    }

    /**
     * Show error message
     */
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Something went wrong");
        alert.setContentText(message);
        styleAlert(alert, "error-alert");
        alert.showAndWait();
    }

    /**
     * Style alert dialog
     */
    private void styleAlert(Alert alert, String styleClass) {
        DialogPane dialogPane = alert.getDialogPane();
        try {
            dialogPane.getStylesheets().add(getClass().getResource("/css/app.css").toExternalForm());
            dialogPane.getStyleClass().add("custom-alert");
            dialogPane.getStyleClass().add(styleClass);
        } catch (Exception e) {
            System.err.println("Could not load CSS for alert: " + e.getMessage());
        }
    }
}
