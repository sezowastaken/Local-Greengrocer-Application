package com.group25.greengrocer.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.text.Text;

public class CarrierController {

    @FXML
    private Text welcomeText;

    // Available Orders Table
    @FXML
    private TableView<?> availableOrdersTable;
    @FXML
    private TableColumn<?, ?> availOrderIdCol;
    @FXML
    private TableColumn<?, ?> availCustomerNameCol;
    @FXML
    private TableColumn<?, ?> availCustomerAddressCol;
    @FXML
    private TableColumn<?, ?> availDeliveryDateCol;
    @FXML
    private TableColumn<?, ?> availTotalCol;
    @FXML
    private TableColumn<?, ?> availActionsCol;

    // Current Orders Table
    @FXML
    private TableView<?> currentOrdersTable;
    @FXML
    private TableColumn<?, ?> currOrderIdCol;
    @FXML
    private TableColumn<?, ?> currCustomerNameCol;
    @FXML
    private TableColumn<?, ?> currCustomerAddressCol;
    @FXML
    private TableColumn<?, ?> currDeliveryDateCol;
    @FXML
    private TableColumn<?, ?> currTotalCol;
    @FXML
    private TableColumn<?, ?> currActionsCol;

    // Completed Orders Table
    @FXML
    private TableView<?> completedOrdersTable;
    @FXML
    private TableColumn<?, ?> compOrderIdCol;
    @FXML
    private TableColumn<?, ?> compCustomerNameCol;
    @FXML
    private TableColumn<?, ?> compRequestedDateCol;
    @FXML
    private TableColumn<?, ?> compDeliveredDateCol;
    @FXML
    private TableColumn<?, ?> compTotalCol;

    @FXML
    private void initialize() {
        // TODO: Load carrier username from session
        welcomeText.setText("Welcome, Carrier");
    }

    @FXML
    private void handleRefreshAll() {
        handleRefreshAvailable();
        handleRefreshCurrent();
        handleRefreshCompleted();
    }

    @FXML
    private void handleRefreshAvailable() {
        // TODO: Implement available orders loading
        System.out.println("Refreshing available orders...");
    }

    @FXML
    private void handleRefreshCurrent() {
        // TODO: Implement current orders loading
        System.out.println("Refreshing current orders...");
    }

    @FXML
    private void handleRefreshCompleted() {
        // TODO: Implement completed orders loading
        System.out.println("Refreshing completed orders...");
    }

    @FXML
    private void handleLogout() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                    getClass().getResource("/fxml/login.fxml"));
            javafx.scene.Parent root = loader.load();
            welcomeText.getScene().setRoot(root);
        } catch (java.io.IOException e) {
            e.printStackTrace();
            System.err.println("Logout failed: " + e.getMessage());
        }
    }
}
