package com.group25.greengrocer.controller;

import java.time.format.DateTimeFormatter;
import java.util.List;

import com.group25.greengrocer.dao.OrderDao;
import com.group25.greengrocer.model.Order;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

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
    private TableColumn<OrderDisplay, String> compRatingCol;
    @FXML
    private TableColumn<OrderDisplay, Void> compActionsCol;

    // Order Details Popup elements
    @FXML
    private VBox orderDetailsPopup;
    @FXML
    private Label orderDetailsTitleLabel;
    @FXML
    private Label orderDetailsNoteLabel;
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

    @FXML
    private Label ratingLabel;

    // ...
    private com.group25.greengrocer.dao.RatingDao ratingDao = new com.group25.greengrocer.dao.RatingDao();

    public void setCarrierSession(long id, String username) {
        this.carrierId = id;
        this.carrierUsername = username;
        welcomeText.setText("Welcome, " + username);
        updateRatingDisplay();
    }

    private void updateRatingDisplay() {
        if (ratingLabel != null) {
            double avg = ratingDao.getAverageRating((int) carrierId);
            ratingLabel.setText(String.format("Rating: %.1f/5", avg));
        }
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
            if (orderDetailsNoteLabel != null) {
                String note = orderDisplay.getNote();
                orderDetailsNoteLabel.setText((note != null && !note.isEmpty()) ? "Note: " + note : "No Data");
            }

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
        compRatingCol.setCellValueFactory(new PropertyValueFactory<>("rating"));
        compTotalCol.setCellValueFactory(new PropertyValueFactory<>("total"));

        compActionsCol.setCellFactory(param -> new TableCell<>() {
            private final Button viewBtn = new Button("View");
            {
                viewBtn.setStyle(
                        "-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-size: 11px; -fx-padding: 5px 10px; -fx-background-radius: 3px;");
                viewBtn.setOnAction(event -> {
                    OrderDisplay orderDisplay = getTableView().getItems().get(getIndex());
                    handleViewDetails(orderDisplay);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : viewBtn);
            }
        });
    }

    @FXML
    public void handleRefreshAll() {
        handleRefreshAvailable();
        handleRefreshCurrent();
        handleRefreshCompleted();
        updateRatingDisplay();
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
                        null,
                        null,
                        order.getNote()));
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
                        null,
                        null,
                        order.getNote()));
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

            List<com.group25.greengrocer.model.CarrierRating> ratings = ratingDao
                    .getRatingsByCarrierId((int) carrierId);
            java.util.Map<Integer, Integer> ratingMap = new java.util.HashMap<>();
            for (com.group25.greengrocer.model.CarrierRating r : ratings) {
                ratingMap.put(r.getOrderId(), r.getRating());
            }

            for (Order order : orders) {
                com.group25.greengrocer.dao.UserDao.UserProfile user = userDao.findById(order.getCustomerId());
                String customerName = user != null ? user.getFullName() : "Unknown";

                String ratingStr = "-";
                if (ratingMap.containsKey((int) order.getId())) {
                    ratingStr = String.valueOf(ratingMap.get((int) order.getId()));
                }

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
                                : "N/A",
                        ratingStr,
                        order.getNote()));
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
        // Create a dialog to enter delivery date
        javafx.scene.control.Dialog<java.time.LocalDateTime> dialog = new javafx.scene.control.Dialog<>();
        dialog.setTitle("Complete Order");
        dialog.setHeaderText("Enter delivery date and time for Order #" + orderDisplay.getOrderId());

        // Set the button types
        javafx.scene.control.ButtonType confirmButtonType = new javafx.scene.control.ButtonType("Complete",
                javafx.scene.control.ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(confirmButtonType, javafx.scene.control.ButtonType.CANCEL);

        // Create the date and time pickers
        javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

        javafx.scene.control.DatePicker datePicker = new javafx.scene.control.DatePicker(java.time.LocalDate.now());
        javafx.scene.control.Spinner<Integer> hourSpinner = new javafx.scene.control.Spinner<>(0, 23,
                java.time.LocalTime.now().getHour());
        javafx.scene.control.Spinner<Integer> minuteSpinner = new javafx.scene.control.Spinner<>(0, 59,
                java.time.LocalTime.now().getMinute());

        hourSpinner.setEditable(true);
        minuteSpinner.setEditable(true);

        grid.add(new javafx.scene.control.Label("Delivery Date:"), 0, 0);
        grid.add(datePicker, 1, 0);
        grid.add(new javafx.scene.control.Label("Time (Hour):"), 0, 1);
        grid.add(hourSpinner, 1, 1);
        grid.add(new javafx.scene.control.Label("Time (Minute):"), 0, 2);
        grid.add(minuteSpinner, 1, 2);

        dialog.getDialogPane().setContent(grid);

        // Style the dialog
        styleAlert(new Alert(Alert.AlertType.INFORMATION), "success-alert"); // Reuse styling

        // Convert the result to LocalDateTime when the confirm button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == confirmButtonType) {
                java.time.LocalDate date = datePicker.getValue();
                int hour = hourSpinner.getValue();
                int minute = minuteSpinner.getValue();
                return java.time.LocalDateTime.of(date, java.time.LocalTime.of(hour, minute));
            }
            return null;
        });

        java.util.Optional<java.time.LocalDateTime> result = dialog.showAndWait();

        result.ifPresent(deliveryDateTime -> {
            try {
                // Update the order with the delivery date
                orderDao.completeOrderWithDate(orderDisplay.getOrderId(), deliveryDateTime);
                showInfo("Order #" + orderDisplay.getOrderId() + " completed successfully!\nDelivery: "
                        + deliveryDateTime.format(dateFormatter));
                handleRefreshAll();
            } catch (Exception e) {
                e.printStackTrace();
                showError("Failed to complete order: " + e.getMessage());
            }
        });
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
        private final String rating;
        private final String note;

        public OrderDisplay(long orderId, String customerName, String customerAddress,
                String deliveryDate, String total, String deliveredDate, String rating, String note) {
            this.orderId = orderId;
            this.customerName = customerName;
            this.customerAddress = customerAddress;
            this.deliveryDate = deliveryDate;
            this.total = total;
            this.deliveredDate = deliveredDate;
            this.rating = rating;
            this.note = note;
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

        public String getRating() {
            return rating;
        }

        public String getNote() {
            return note;
        }
    }

    @FXML
    private void handleShowProfile() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                    getClass().getResource("/fxml/profile.fxml"));
            javafx.scene.Parent root = loader.load();

            ProfileController profileController = loader.getController();
            profileController.setUserSession(carrierId, carrierUsername, "carrier");

            welcomeText.getScene().setRoot(root);
        } catch (java.io.IOException e) {
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
