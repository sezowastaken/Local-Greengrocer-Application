package com.group25.greengrocer.util;

import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.stage.StageStyle;
import java.util.Objects;

/**
 * Utility class for displaying standardized alerts/notifications/messages
 * consistent with the application's design system.
 * Supports: Success (Green), Error (Red), Info (Blue).
 */
public class NotificationUtil {

    /**
     * Show a standardized Success Alert (Green theme).
     * 
     * @param title   Title of the dialog
     * @param message Message content
     */
    public static void showSuccess(String title, String message) {
        showAlert(Alert.AlertType.INFORMATION, title, message, "success-alert");
    }

    /**
     * Show a standardized Error Alert (Red theme).
     * 
     * @param title   Title of the dialog
     * @param message Message content
     */
    public static void showError(String title, String message) {
        showAlert(Alert.AlertType.ERROR, title, message, "error-alert");
    }

    /**
     * Show a standardized Information Alert (Blue theme).
     * 
     * @param title   Title of the dialog
     * @param message Message content
     */
    public static void showInfo(String title, String message) {
        showAlert(Alert.AlertType.INFORMATION, title, message, "info-alert");
    }

    /**
     * Show a standardized Confirmation Alert (Orange theme).
     * 
     * @param title   Title of the dialog
     * @param message Message content
     * @return true if YES/OK is clicked, false otherwise
     */
    public static boolean showConfirmation(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        // Use UTILITY style
        alert.initStyle(StageStyle.UTILITY);

        DialogPane dialogPane = alert.getDialogPane();

        // Load CSS
        try {
            String cssPath = Objects.requireNonNull(NotificationUtil.class.getResource("/css/app.css"))
                    .toExternalForm();
            dialogPane.getStylesheets().add(cssPath);
        } catch (Exception e) {
            System.err.println("Could not load app.css for Alert: " + e.getMessage());
        }

        // Apply Custom Classes
        dialogPane.getStyleClass().add("custom-alert");
        dialogPane.getStyleClass().add("confirmation-alert");

        // Custom buttons if needed, but standard types usually work with CSS if
        // matched.
        // AlertType.CONFIRMATION has OK/Cancel by default. We want YES/NO?
        // Let's stick to default buttons but styling them.
        // Or we can set button types.
        // The user's original code had YES/NO.
        java.util.Optional<javafx.scene.control.ButtonType> result = alert.showAndWait();
        return result.isPresent() && (result.get() == javafx.scene.control.ButtonType.OK
                || result.get() == javafx.scene.control.ButtonType.YES);
    }

    /**
     * Internal helper to build and show the alert.
     */
    private static void showAlert(Alert.AlertType type, String title, String message, String styleClass) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null); // No header text, just title and content
        alert.setContentText(message);

        // Use UTILITY style for a cleaner look or DECORATED for standard window
        alert.initStyle(StageStyle.UTILITY);

        DialogPane dialogPane = alert.getDialogPane();

        // Load CSS
        try {
            String cssPath = Objects.requireNonNull(NotificationUtil.class.getResource("/css/app.css"))
                    .toExternalForm();
            dialogPane.getStylesheets().add(cssPath);
        } catch (Exception e) {
            System.err.println("Could not load app.css for Alert: " + e.getMessage());
        }

        // Apply Custom Classes
        dialogPane.getStyleClass().add("custom-alert");
        dialogPane.getStyleClass().add(styleClass);

        alert.showAndWait();
    }
}
