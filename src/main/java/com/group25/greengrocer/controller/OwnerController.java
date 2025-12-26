package com.group25.greengrocer.controller;

import com.group25.greengrocer.dao.MessageDao;
import com.group25.greengrocer.dao.OrderDao;
import com.group25.greengrocer.dao.ProductDao;
import com.group25.greengrocer.dao.UserDao;
import com.group25.greengrocer.model.Message;
import com.group25.greengrocer.model.Order;
import com.group25.greengrocer.model.Product;
import com.group25.greengrocer.model.User;
import com.group25.greengrocer.util.Session; // Added Session import
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Map;

public class OwnerController {

    // DAOs
    private ProductDao productDao = new ProductDao();
    private UserDao userDao = new UserDao();
    private OrderDao orderDao = new OrderDao();
    private MessageDao messageDao = new MessageDao();
    private com.group25.greengrocer.dao.CouponDao couponDao = new com.group25.greengrocer.dao.CouponDao();
    private com.group25.greengrocer.service.LoyaltyService loyaltyService = new com.group25.greengrocer.service.LoyaltyService();

    // --- Product Tab ---
    @FXML
    private TableView<Product> productTable;
    @FXML
    private TableColumn<Product, String> colProdName;
    @FXML
    private TableColumn<Product, Double> colProdPrice;
    @FXML
    private TableColumn<Product, Double> colProdStock;
    @FXML
    private TableColumn<Product, Double> colProdThreshold;
    @FXML
    private TableColumn<Product, String> colProdCategory;

    @FXML
    private TextField txtProdName;
    @FXML
    private TextField txtProdPrice;
    @FXML
    private TextField txtProdStock;
    @FXML
    private TextField txtProdThreshold;
    @FXML
    private ComboBox<String> comboProdCategory; // "Vegetable", "Fruit"
    @FXML
    private CheckBox chkIsPiece; // True for count, False for KG
    @FXML
    private Label lblImageStatus;

    private File selectedImageFile;

    // --- Carrier Tab ---
    @FXML
    private TableView<User> carrierTable; // Displaying Carriers
    @FXML
    private TableColumn<User, String> colCarrUsername;
    @FXML
    private TextField txtCarrUsername;
    @FXML
    private TextField txtCarrPassword;

    // --- Order Tab ---
    @FXML
    private TableView<Order> orderTable;
    @FXML
    private TableColumn<Order, Integer> colOrderId;
    @FXML
    private TableColumn<Order, String> colOrderStatus;
    @FXML
    private TableColumn<Order, Double> colOrderTotal;
    @FXML
    private TableColumn<Order, String> colOrderDate;

    // --- Message Tab ---
    @FXML
    private TableView<Message> messageTable;
    @FXML
    private TableColumn<Message, String> colMsgSender;
    @FXML
    private TableColumn<Message, String> colMsgContent;
    @FXML
    private TableColumn<Message, String> colMsgReply;
    @FXML
    private TextArea txtReply;

    // --- Reports Tab ---
    @FXML
    private BarChart<String, Number> chartTopProducts;
    @FXML
    private BarChart<String, Number> chartRevenue;

    // --- Marketing Tab ---
    @FXML
    private TextField txtLoyaltyRate;
    @FXML
    private TableView<com.group25.greengrocer.model.Coupon> couponTable;
    @FXML
    private TableColumn<com.group25.greengrocer.model.Coupon, String> colCouponCode;
    @FXML
    private TableColumn<com.group25.greengrocer.model.Coupon, String> colCouponType;
    @FXML
    private TableColumn<com.group25.greengrocer.model.Coupon, Double> colCouponValue;
    @FXML
    private TableColumn<com.group25.greengrocer.model.Coupon, Double> colCouponMin;
    @FXML
    private TableColumn<com.group25.greengrocer.model.Coupon, String> colCouponValid;
    @FXML
    private TableColumn<com.group25.greengrocer.model.Coupon, Boolean> colCouponActive;

    // Session data for owner
    private long ownerId;
    private String ownerUsername;

    @FXML
    private TextField txtCouponCode;
    @FXML
    private ComboBox<String> comboDiscountType;
    @FXML
    private TextField txtDiscountValue;
    @FXML
    private TextField txtMinOrder;
    @FXML
    private DatePicker dpValidUntil;

    @FXML
    public void initialize() {
        // Role Guard: STRICT access control for Owner
        User currentUser = com.group25.greengrocer.util.Session.getCurrentUser();
        if (currentUser == null || !"owner".equalsIgnoreCase(currentUser.getRole())) {
            showAlert("Access Denied", "You do not have permission to view this page.");
            // Close the current stage to prevent unauthorized access
            if (productTable.getScene() != null && productTable.getScene().getWindow() != null) {
                ((javafx.stage.Stage) productTable.getScene().getWindow()).close();
            }
            return;
        }

        setupProductTable();
        setupCarrierTable();
        setupOrderTable();
        setupMessageTable();
        setupCouponTable();

        // Initialize Combos
        comboProdCategory.setItems(FXCollections.observableArrayList("Vegetable", "Fruit"));
        comboDiscountType.setItems(FXCollections.observableArrayList("PERCENT", "AMOUNT"));

        loadAllData();
    }

    /**
     * Set owner session data
     */
    public void setOwnerSession(long id, String username) {
        this.ownerId = id;
        this.ownerUsername = username;
    }

    @FXML
    private void handleShowProfile() {
        try {
            // Get userId from Session if not already set
            if (ownerId == 0) {
                User currentUser = com.group25.greengrocer.util.Session.getCurrentUser();
                if (currentUser != null) {
                    this.ownerId = currentUser.getId();
                    this.ownerUsername = currentUser.getUsername();
                }
            }

            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                    getClass().getResource("/fxml/profile.fxml"));
            javafx.scene.Parent root = loader.load();

            ProfileController profileController = loader.getController();
            profileController.setUserSession(ownerId, ownerUsername, "owner");

            productTable.getScene().setRoot(root);
        } catch (java.io.IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load profile page: " + e.getMessage());
        }
    }

    @FXML
    private void handleLogout() {
        com.group25.greengrocer.util.Session.clear();
        try {
            javafx.stage.Stage stage = (javafx.stage.Stage) productTable.getScene().getWindow();
            javafx.scene.Parent root = javafx.fxml.FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
            stage.getScene().setRoot(root);
        } catch (java.io.IOException e) {
            e.printStackTrace();
            showAlert("Error", "Could not load login screen.");
        }
    }

    private void loadAllData() {
        loadProducts();
        loadCarriers();
        loadOrders();
        loadMessages();
        loadReports();
        loadMarketingData();
    }

    private void loadMarketingData() {
        // Loyalty
        double rate = loyaltyService.getLoyaltyDiscountRate();
        txtLoyaltyRate.setText(String.valueOf(rate));

        // Coupons
        couponTable.setItems(FXCollections.observableArrayList(couponDao.getAllCoupons()));
    }

    // --- Marketing Logic ---
    private void setupCouponTable() {
        colCouponCode.setCellValueFactory(new PropertyValueFactory<>("code"));
        colCouponType.setCellValueFactory(new PropertyValueFactory<>("discountType"));
        colCouponValue.setCellValueFactory(new PropertyValueFactory<>("discountValue"));
        colCouponMin.setCellValueFactory(new PropertyValueFactory<>("minOrderTotal"));
        colCouponValid.setCellValueFactory(new PropertyValueFactory<>("validUntil"));
        colCouponActive.setCellValueFactory(new PropertyValueFactory<>("active"));
    }

    @FXML
    private void handleSaveLoyalty() {
        try {
            double rate = Double.parseDouble(txtLoyaltyRate.getText());
            if (rate < 0 || rate > 1) {
                showAlert("Invalid Input", "Rate must be between 0.0 and 1.0");
                return;
            }
            loyaltyService.setLoyaltyDiscountRate(rate);
            showAlert("Success", "Loyalty discount rate updated.");
        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Please enter a valid number.");
        }
    }

    @FXML
    private void handleAddCoupon() {
        try {
            String code = txtCouponCode.getText();
            String type = comboDiscountType.getValue();
            double value = Double.parseDouble(txtDiscountValue.getText());
            double minOrder = Double.parseDouble(txtMinOrder.getText());

            if (code.isEmpty() || type == null) {
                showAlert("Invalid Input", "Code and Type are required.");
                return;
            }

            java.time.LocalDateTime validUntil = null;
            if (dpValidUntil.getValue() != null) {
                validUntil = dpValidUntil.getValue().atStartOfDay();
            }

            com.group25.greengrocer.model.Coupon coupon = new com.group25.greengrocer.model.Coupon(
                    0, code, type, value, minOrder,
                    java.time.LocalDateTime.now(), // Valid from now
                    validUntil,
                    true);

            couponDao.addCoupon(coupon);
            loadMarketingData();

            // Clear
            txtCouponCode.clear();
            txtDiscountValue.clear();
            txtMinOrder.clear();
            dpValidUntil.setValue(null);

        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Check numeric fields.");
        }
    }

    @FXML
    private void handleToggleCoupon() {
        com.group25.greengrocer.model.Coupon selected = couponTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            couponDao.toggleActive(selected.getId(), !selected.isActive());
            loadMarketingData();
        }
    }

    @FXML
    private void handleDeleteCoupon() {
        com.group25.greengrocer.model.Coupon selected = couponTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            couponDao.deleteCoupon(selected.getId());
            loadMarketingData();
        }
    }

    // --- Product Logic ---
    // ... existing setupProductTable ...
    private void setupProductTable() {
        colProdName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colProdPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colProdStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
        colProdThreshold.setCellValueFactory(new PropertyValueFactory<>("threshold"));
        colProdCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
    }

    // ... existing loadProducts ...
    private void loadProducts() {
        productTable.setItems(FXCollections.observableArrayList(productDao.getAllProducts()));
    }

    // ... existing handleBrowseImage ...
    @FXML
    private void handleBrowseImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters()
                .add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        selectedImageFile = fileChooser.showOpenDialog(null);
        if (selectedImageFile != null) {
            lblImageStatus.setText("Selected: " + selectedImageFile.getName());
        }
    }

    @FXML
    private void handleAddProduct() {
        try {
            String name = txtProdName.getText();
            double price = Double.parseDouble(txtProdPrice.getText());
            double stock = Double.parseDouble(txtProdStock.getText());
            double threshold = Double.parseDouble(txtProdThreshold.getText());
            boolean isPiece = chkIsPiece.isSelected();
            String catName = comboProdCategory.getValue();

            // INTENTIONAL: Hardcoded Category ID mapping for prototype phase.
            // In production, fetch categories from DB via CategoryDao.
            int catId = "Vegetable".equals(catName) ? 1 : 2;

            Product newProd = new Product(0, name, price, stock, catName, threshold, isPiece, null);

            FileInputStream fis = null;
            if (selectedImageFile != null) {
                fis = new FileInputStream(selectedImageFile);
            }

            productDao.addProduct(newProd, catId, fis);
            loadProducts();
            clearProductFields();
        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Please check number fields.");
        } catch (FileNotFoundException e) {
            showAlert("File Error", "Image file not found.");
        }
    }

    // ... existing handleDeleteProduct ...
    @FXML
    private void handleDeleteProduct() {
        Product selected = productTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            productDao.deleteProduct(selected.getId());
            loadProducts();
        } else {
            showAlert("Selection Error", "Please select a product to delete.");
        }
    }

    @FXML
    private void handleUpdateProduct() {
        // Similar to Add, but calls updateProduct with selected ID
        Product selected = productTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Selection Error", "Please select a product to update.");
            return;
        }
        try {
            // Update fields from inputs (assuming user clicked detailed view or re-entered)
            // For simplicity, using text fields
            String name = txtProdName.getText();
            double price = Double.parseDouble(txtProdPrice.getText());
            double stock = Double.parseDouble(txtProdStock.getText());
            double threshold = Double.parseDouble(txtProdThreshold.getText());
            boolean isPiece = chkIsPiece.isSelected();
            String catName = comboProdCategory.getValue();

            // INTENTIONAL: Hardcoded Category ID mapping for prototype phase.
            int catId = "Vegetable".equals(catName) ? 1 : 2;

            Product updateProd = new Product(selected.getId(), name, price, stock, catName, threshold, isPiece, null);

            FileInputStream fis = null;
            if (selectedImageFile != null) {
                fis = new FileInputStream(selectedImageFile);
            }

            productDao.updateProduct(updateProd, catId, fis);
            loadProducts();
        } catch (NumberFormatException | FileNotFoundException e) {
            showAlert("Error", "Invalid input or file.");
        }
    }

    private void clearProductFields() {
        txtProdName.clear();
        txtProdPrice.clear();
        txtProdStock.clear();
        txtProdThreshold.clear();
        lblImageStatus.setText("");
        selectedImageFile = null;
    }

    // --- Carrier Logic ---
    private void setupCarrierTable() {
        colCarrUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
    }

    private void loadCarriers() {
        carrierTable.setItems(FXCollections.observableArrayList(userDao.getCarriers()));
    }

    @FXML
    private void handleAddCarrier() {
        String user = txtCarrUsername.getText();
        String pass = txtCarrPassword.getText();
        if (!user.isEmpty() && !pass.isEmpty()) {
            userDao.addCarrier(user, pass);
            loadCarriers();
            txtCarrUsername.clear();
            txtCarrPassword.clear();
        } else {
            showAlert("Input Error", "Username and Password required.");
        }
    }

    @FXML
    private void handleFireCarrier() {
        User selected = carrierTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            userDao.deleteCarrier(selected.getId());
            loadCarriers();
        }
    }

    @FXML
    private void handleViewCarrierRatings() {
        User selected = carrierTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            com.group25.greengrocer.dao.RatingDao ratingDao = new com.group25.greengrocer.dao.RatingDao();
            double avg = ratingDao.getAverageRating(selected.getId());
            java.util.List<com.group25.greengrocer.model.CarrierRating> ratings = ratingDao
                    .getRatingsByCarrierId(selected.getId());

            StringBuilder content = new StringBuilder();
            content.append(String.format("Average Rating: %.2f / 5.0\n\n", avg));
            content.append("Recent Feedback:\n");
            content.append("----------------\n");

            if (ratings.isEmpty()) {
                content.append("No ratings yet.");
            } else {
                for (com.group25.greengrocer.model.CarrierRating r : ratings) {
                    content.append(String.format("Rating: %d/5\nComment: %s\nDate: %s\n\n",
                            r.getRating(),
                            r.getComment() == null ? "-" : r.getComment(),
                            r.getCreatedAt().toString()));
                }
            }

            TextArea textArea = new TextArea(content.toString());
            textArea.setEditable(false);
            textArea.setWrapText(true);
            textArea.setPrefWidth(400);
            textArea.setPrefHeight(300);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Carrier Ratings: " + selected.getUsername());
            alert.setHeaderText("Performance Report");
            alert.getDialogPane().setContent(textArea);
            alert.showAndWait();
        } else {
            showAlert("Selection Error", "Please select a carrier to view ratings.");
        }
    }

    // --- Order Logic ---
    private void setupOrderTable() {
        colOrderId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colOrderStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colOrderTotal.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        colOrderDate.setCellValueFactory(new PropertyValueFactory<>("orderTime"));
    }

    private void loadOrders() {
        orderTable.setItems(FXCollections.observableArrayList(orderDao.getAllOrders()));
    }

    // --- Message Logic ---
    private void setupMessageTable() {
        colMsgSender.setCellValueFactory(new PropertyValueFactory<>("senderName"));
        colMsgContent.setCellValueFactory(new PropertyValueFactory<>("content"));
        colMsgReply.setCellValueFactory(new PropertyValueFactory<>("reply"));
    }

    private void loadMessages() {
        messageTable.setItems(FXCollections.observableArrayList(messageDao.getAllMessages()));
    }

    @FXML
    private void handleReplyMessage() {
        Message selected = messageTable.getSelectionModel().getSelectedItem();
        String reply = txtReply.getText();
        if (selected != null && !reply.isEmpty()) {
            messageDao.replyToMessage(selected.getId(), reply);
            loadMessages();
            txtReply.clear();
        } else {
            showAlert("Error", "Select a message and enter reply.");
        }
    }

    // --- Reports Logic ---
    private void loadReports() {
        // Top Selling Products
        chartTopProducts.getData().clear();
        XYChart.Series<String, Number> seriesProducts = new XYChart.Series<>();
        seriesProducts.setName("Top Products");
        Map<String, Integer> topProducts = orderDao.getTopSellingProducts();
        for (Map.Entry<String, Integer> entry : topProducts.entrySet()) {
            seriesProducts.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }
        chartTopProducts.getData().add(seriesProducts);

        // Revenue
        chartRevenue.getData().clear();
        XYChart.Series<String, Number> seriesRevenue = new XYChart.Series<>();
        seriesRevenue.setName("Revenue (Last 7 Days)");
        Map<String, Double> revenue = orderDao.getRevenueByDate();
        for (Map.Entry<String, Double> entry : revenue.entrySet()) {
            seriesRevenue.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }
        chartRevenue.getData().add(seriesRevenue);
    }

    @FXML
    private void handleRefresh() {
        loadAllData();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
