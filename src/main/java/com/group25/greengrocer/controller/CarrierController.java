package com.group25.greengrocer.controller;

import com.group25.greengrocer.dao.OrderDao;
import com.group25.greengrocer.model.Order;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class CarrierController {

    @FXML
    private Text welcomeText;

    // Available Orders Table
    @FXML
    private TableView<OrderDisplay> availableOrdersTable;
    @FXML
    private TableColumn<OrderDisplay, Long> availOrderIdCol;
    @FXML
    private TableColumn<OrderDisplay, String> availCustomerNameCol;
    @FXML
    private TableColumn<OrderDisplay, String> availCustomerAddressCol;
    @FXML
    private TableColumn<OrderDisplay, String> availDeliveryDateCol;
    @FXML
    private TableColumn<OrderDisplay, String> availTotalCol;
    @FXML
    private TableColumn<OrderDisplay, Void> availActionsCol;

    // Current Orders Table
    @FXML
    private TableView<OrderDisplay> currentOrdersTable;
    @FXML
    private TableColumn<OrderDisplay, Long> currOrderIdCol;
    @FXML
    private TableColumn<OrderDisplay, String> currCustomerNameCol;
    @FXML
    private TableColumn<OrderDisplay, String> currCustomerAddressCol;
    @FXML
    private TableColumn<OrderDisplay, String> currDeliveryDateCol;
    @FXML
    private TableColumn<OrderDisplay, String> currTotalCol;
    @FXML
    private TableColumn<OrderDisplay, Void> currActionsCol;

    // Completed Orders Table
    @FXML
    private TableView<OrderDisplay> completedOrdersTable;
    @FXML
    private TableColumn<OrderDisplay, Long> compOrderIdCol;
    @FXML
    private TableColumn<OrderDisplay, String> compCustomerNameCol;
    @FXML
    private TableColumn<OrderDisplay, String> compRequestedDateCol;
    @FXML
    private TableColumn<OrderDisplay, String> compDeliveredDateCol;
    @FXML
    private TableColumn<OrderDisplay, String> compTotalCol;
    @FXML
    private TableColumn<OrderDisplay, Void> compActionsCol;

    // Order Details Popup elements
    @FXML
    private VBox orderDetailsPopup;
    @FXML
    private Label orderDetailsTitleLabel;
    @FXML
    private TableView<com.group25.greengrocer.model.OrderItem> orderDetailsTable;
    @FXML
    private TableColumn<com.group25.greengrocer.model.OrderItem, String> detailProductNameCol;
    @FXML
    private TableColumn<com.group25.greengrocer.model.OrderItem, String> detailQuantityCol;
    @FXML
    private TableColumn<com.group25.greengrocer.model.OrderItem, String> detailUnitCol;
    @FXML
    private TableColumn<com.group25.greengrocer.model.OrderItem, String> detailUnitPriceCol;
    @FXML
    private TableColumn<com.group25.greengrocer.model.OrderItem, String> detailLineTotalCol;

    @FXML
    private Label orderDetailsTotalLabel;

    private OrderDao orderDao = new OrderDao();
    private com.group25.greengrocer.dao.OrderItemDao orderItemDao = new com.group25.greengrocer.dao.OrderItemDao();
    private com.group25.greengrocer.dao.UserDao userDao = new com.group25.greengrocer.dao.UserDao();
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // Session data
    private long carrierId;
    private String carrierUsername;

    public void setCarrierSession(long id, String username) {
        this.carrierId = id;
        this.carrierUsername = username;
        welcomeText.setText("Welcome, " + username);
    }

    @FXML
    private void initialize() {
        // Setup table columns
        setupAvailableOrdersTable();
        setupCurrentOrdersTable();
        setupCompletedOrdersTable();
        setupOrderDetailsPopup();

        // Load initial data will be called after session is set
    }

    private void setupOrderDetailsPopup() {
        detailProductNameCol.setCellValueFactory(new PropertyValueFactory<>("productName"));
        detailQuantityCol.setCellValueFactory(new PropertyValueFactory<>("formattedQuantity"));
        detailUnitCol.setCellValueFactory(
                cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getUnit().name()));
        detailUnitPriceCol.setCellValueFactory(new PropertyValueFactory<>("formattedUnitPrice"));
        detailLineTotalCol.setCellValueFactory(new PropertyValueFactory<>("formattedLineTotal"));
    }

    @FXML
    private void handleViewDetails(OrderDisplay orderDisplay) {
        try {
            orderDetailsTitleLabel.setText("Order #" + orderDisplay.getOrderId() + " - Product List");

            java.util.List<com.group25.greengrocer.model.OrderItem> items = orderItemDao
                    .findByOrderId(orderDisplay.getOrderId());
            javafx.collections.ObservableList<com.group25.greengrocer.model.OrderItem> observableItems = javafx.collections.FXCollections
                    .observableArrayList(items);
            orderDetailsTable.setItems(observableItems);

            double total = items.stream()
                    .mapToDouble(com.group25.greengrocer.model.OrderItem::getLineTotal)
                    .sum();
            orderDetailsTotalLabel.setText(String.format("$%.2f", total));

            orderDetailsPopup.setVisible(true);
            orderDetailsPopup.setManaged(true);

        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to load order details: " + e.getMessage());
        }
    }

    @FXML
    private void handleCloseOrderDetails() {
        orderDetailsPopup.setVisible(false);
        orderDetailsPopup.setManaged(false);
    }

    private void setupAvailableOrdersTable() {
        availOrderIdCol.setCellValueFactory(new PropertyValueFactory<>("orderId"));
        availCustomerNameCol.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        availCustomerAddressCol.setCellValueFactory(new PropertyValueFactory<>("customerAddress"));
        availDeliveryDateCol.setCellValueFactory(new PropertyValueFactory<>("deliveryDate"));
        availTotalCol.setCellValueFactory(new PropertyValueFactory<>("total"));

        // Add "Select" button
        // Add "Select" and "View" buttons
        availActionsCol.setCellFactory(param -> new TableCell<>() {
            private final Button selectBtn = new Button("Select");
            private final Button viewBtn = new Button("View");
            private final HBox pane = new HBox(5, selectBtn, viewBtn);

            {
                selectBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;");
                viewBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");

                selectBtn.setOnAction(event -> {
                    OrderDisplay orderDisplay = getTableView().getItems().get(getIndex());
                    handleSelectOrder(orderDisplay);
                });

                viewBtn.setOnAction(event -> {
                    OrderDisplay orderDisplay = getTableView().getItems().get(getIndex());
                    handleViewDetails(orderDisplay);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });
    }

    private void setupCurrentOrdersTable() {
        currOrderIdCol.setCellValueFactory(new PropertyValueFactory<>("orderId"));
        currCustomerNameCol.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        currCustomerAddressCol.setCellValueFactory(new PropertyValueFactory<>("customerAddress"));
        currDeliveryDateCol.setCellValueFactory(new PropertyValueFactory<>("deliveryDate"));
        currTotalCol.setCellValueFactory(new PropertyValueFactory<>("total"));

        // Add "Complete" and "View" buttons
        currActionsCol.setCellFactory(param -> new TableCell<>() {
            private final Button completeBtn = new Button("Complete");
            private final Button viewBtn = new Button("View");
            private final HBox pane = new HBox(5, completeBtn, viewBtn);

            {
                completeBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;");
                viewBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");

                completeBtn.setOnAction(event -> {
                    OrderDisplay orderDisplay = getTableView().getItems().get(getIndex());
                    handleCompleteOrder(orderDisplay);
                });

                viewBtn.setOnAction(event -> {
                    OrderDisplay orderDisplay = getTableView().getItems().get(getIndex());
                    handleViewDetails(orderDisplay);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });
    }

    private void setupCompletedOrdersTable() {
        compOrderIdCol.setCellValueFactory(new PropertyValueFactory<>("orderId"));
        compCustomerNameCol.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        compRequestedDateCol.setCellValueFactory(new PropertyValueFactory<>("deliveryDate"));
        compDeliveredDateCol.setCellValueFactory(new PropertyValueFactory<>("deliveredDate"));
        compTotalCol.setCellValueFactory(new PropertyValueFactory<>("total"));
    }

    @FXML
    public void handleRefreshAll() {
        handleRefreshAvailable();
        handleRefreshCurrent();
        handleRefreshCompleted();
    }

    @FXML
    private void handleRefreshAvailable() {
        try {
            List<Order> orders = orderDao.findAvailableOrders();
            ObservableList<OrderDisplay> displayOrders = FXCollections.observableArrayList();

            for (Order order : orders) {
                com.group25.greengrocer.dao.UserDao.UserProfile user = userDao.findById(order.getCustomerId());
                String customerName = user != null ? user.getFullName() : "Unknown";
                String customerAddress = user != null ? user.getAddressLine() : "Unknown";

                displayOrders.add(new OrderDisplay(
                        order.getId(),
                        customerName,
                        customerAddress,
                        order.getRequestedDeliveryTime() != null
                                ? order.getRequestedDeliveryTime().format(dateFormatter)
                                : "N/A",
                        String.format("$%.2f", order.getTotal()),
                        null));
            }

            availableOrdersTable.setItems(displayOrders);
            System.out.println("Loaded " + orders.size() + " available orders");
        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to load available orders: " + e.getMessage());
        }
    }

    @FXML
    private void handleRefreshCurrent() {
        try {
            List<Order> orders = orderDao.findByCarrierId(carrierId);
            ObservableList<OrderDisplay> displayOrders = FXCollections.observableArrayList();

            for (Order order : orders) {
                com.group25.greengrocer.dao.UserDao.UserProfile user = userDao.findById(order.getCustomerId());
                String customerName = user != null ? user.getFullName() : "Unknown";
                String customerAddress = user != null ? user.getAddressLine() : "Unknown";

                displayOrders.add(new OrderDisplay(
                        order.getId(),
                        customerName,
                        customerAddress,
                        order.getRequestedDeliveryTime() != null
                                ? order.getRequestedDeliveryTime().format(dateFormatter)
                                : "N/A",
                        String.format("$%.2f", order.getTotal()),
                        null));
            }

            currentOrdersTable.setItems(displayOrders);
            System.out.println("Loaded " + orders.size() + " current orders");
        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to load current orders: " + e.getMessage());
        }
    }

    @FXML
    private void handleRefreshCompleted() {
        try {
            List<Order> orders = orderDao.findCompletedByCarrierId(carrierId);
            ObservableList<OrderDisplay> displayOrders = FXCollections.observableArrayList();

            for (Order order : orders) {
                com.group25.greengrocer.dao.UserDao.UserProfile user = userDao.findById(order.getCustomerId());
                String customerName = user != null ? user.getFullName() : "Unknown";

                displayOrders.add(new OrderDisplay(
                        order.getId(),
                        customerName,
                        null, // Address not needed for completed
                        order.getRequestedDeliveryTime() != null
                                ? order.getRequestedDeliveryTime().format(dateFormatter)
                                : "N/A",
                        String.format("$%.2f", order.getTotal()),
                        order.getDeliveredTime() != null
                                ? order.getDeliveredTime().format(dateFormatter)
                                : "N/A"));
            }

            completedOrdersTable.setItems(displayOrders);
            System.out.println("Loaded " + orders.size() + " completed orders");
        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to load completed orders: " + e.getMessage());
        }
    }

    private void handleSelectOrder(OrderDisplay orderDisplay) {
        try {
            orderDao.assignCarrier(orderDisplay.getOrderId(), carrierId);
            showInfo("Order #" + orderDisplay.getOrderId() + " assigned successfully!");
            handleRefreshAll();
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            if (e.getMessage().contains("Order already assigned") || e.getMessage().contains("not found")) {
                showError("Bu sipariş başka bir carrier tarafından alındı"); // Race condition message
            } else {
                showError("Failed to select order: " + e.getMessage());
            }
            handleRefreshAll(); // Refresh to remove the stale order
        } catch (Exception e) {
            e.printStackTrace();
            showError("An unexpected error occurred: " + e.getMessage());
        }
    }

    private void handleCompleteOrder(OrderDisplay orderDisplay) {
        try {
            orderDao.completeOrder(orderDisplay.getOrderId());
            showInfo("Order #" + orderDisplay.getOrderId() + " completed successfully!");
            handleRefreshAll();
        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to complete order: " + e.getMessage());
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Something went wrong");
        alert.setContentText(message);
        styleAlert(alert, "error-alert");
        alert.showAndWait();
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        styleAlert(alert, "success-alert");
        alert.showAndWait();
    }

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

    // Inner class for table display
    public static class OrderDisplay {
        private final long orderId;
        private final String customerName;
        private final String customerAddress;
        private final String deliveryDate;
        private final String total;
        private final String deliveredDate;

        public OrderDisplay(long orderId, String customerName, String customerAddress,
                String deliveryDate, String total, String deliveredDate) {
            this.orderId = orderId;
            this.customerName = customerName;
            this.customerAddress = customerAddress;
            this.deliveryDate = deliveryDate;
            this.total = total;
            this.deliveredDate = deliveredDate;
        }

        public long getOrderId() {
            return orderId;
        }

        public String getCustomerName() {
            return customerName;
        }

        public String getCustomerAddress() {
            return customerAddress;
        }

        public String getDeliveryDate() {
            return deliveryDate;
        }

        public String getTotal() {
            return total;
        }

        public String getDeliveredDate() {
            return deliveredDate;
        }
    }

    @FXML
    private void handleShowProfile() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                    getClass().getResource("/fxml/carrier_profile.fxml"));
            javafx.scene.Parent root = loader.load();

            // Pass session data to ProfileController
            CarrierProfileController controller = loader.getController();
            controller.setCarrierSession(carrierId);

            welcomeText.getScene().setRoot(root);
        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to load profile page: " + e.getMessage());
        }
    }
    /*
     * @FXML
     * private void handleUpdateProfile() {
     * // Removed embedded logic
     * }
     * 
     * @FXML
     * private void handleCloseProfile() {
     * // Removed embedded logic
     * }
     */
}
