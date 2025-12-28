package com.group25.greengrocer.controller;

import com.group25.greengrocer.dao.MessageDao;
import com.group25.greengrocer.dao.OrderDao;
import com.group25.greengrocer.dao.ProductDao;
import com.group25.greengrocer.dao.UserDao;
import com.group25.greengrocer.model.Message;
import com.group25.greengrocer.model.Order;
import com.group25.greengrocer.model.Product;
import com.group25.greengrocer.model.User;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.scene.layout.AnchorPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.scene.control.Slider;
import javafx.scene.paint.ImagePattern;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import javafx.embed.swing.SwingFXUtils;
import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Files;
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

    // --- Panel References for Sidebar Navigation ---
    @FXML
    private javafx.scene.layout.AnchorPane pnlOverview;
    @FXML
    private javafx.scene.layout.AnchorPane pnlProducts;
    @FXML
    private javafx.scene.layout.AnchorPane pnlCarriers;
    @FXML
    private javafx.scene.layout.AnchorPane pnlOrders;
    @FXML
    private javafx.scene.layout.AnchorPane pnlMessages;
    @FXML
    private javafx.scene.layout.AnchorPane pnlReports;
    @FXML
    private javafx.scene.layout.AnchorPane pnlMarketing;

    // --- Navigation Buttons ---
    @FXML
    private Button btnOverview;
    @FXML
    private Button btnProducts;
    @FXML
    private Button btnCarriers;
    @FXML
    private Button btnOrders;
    @FXML
    private Button btnMessages;
    @FXML
    private Button btnReports;
    @FXML
    private Button btnMarketing;

    // --- Overview Dashboard Stats Labels ---
    @FXML
    private Label lblTotalUsers;
    @FXML
    private Label lblTotalCarriers;
    @FXML
    private Label lblTotalOrders;
    @FXML
    private Label lblDeliveredOrders;
    @FXML
    private Label lblRecentActivity;

    // --- Dashboard Table and Chart ---
    @FXML
    private TableView<Order> dashboardOrderTable;
    @FXML
    private TableColumn<Order, Long> colDashOrderId;
    @FXML
    private TableColumn<Order, String> colDashCustomer;
    @FXML
    private TableColumn<Order, String> colDashStatus;
    @FXML
    private TableColumn<Order, Double> colDashTotal;
    @FXML
    private javafx.scene.chart.PieChart userPieChart;

    // --- Profile Panel Components ---
    @FXML
    private AnchorPane pnlProfile;
    @FXML
    private TextField txtProfileUsername;
    @FXML
    private TextField txtProfileRole;
    @FXML
    private PasswordField txtProfileNewPass;
    @FXML
    private PasswordField txtProfileConfirmPass;
    @FXML
    private PasswordField txtProfileCurrentPass;
    @FXML
    private Label lblProfileMessage;
    @FXML
    private StackPane cropContainer;
    @FXML
    private ImageView imgProfileView;
    @FXML
    private Slider sliderZoom;
    @FXML
    private Circle imgSidebarProfile;
    @FXML
    private Label lblSidebarPlaceholder;
    @FXML
    private Label lblProfilePlaceholder;

    private byte[] profilePictureBytes; // Store current profile picture
    private java.util.Map<String, Integer> categoryMap; // Store category name-to-id mapping

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
        categoryMap = productDao.getCategories();
        if (categoryMap != null && !categoryMap.isEmpty()) {
            comboProdCategory.setItems(FXCollections.observableArrayList(categoryMap.keySet()));
        } else {
            // Fallback if DB is empty, though technically should be handled
            comboProdCategory.setItems(FXCollections.observableArrayList());
        }
        comboDiscountType.setItems(FXCollections.observableArrayList("PERCENT", "AMOUNT"));

        // Initialize Panel Navigation - Show Products by default
        setupPanelNavigation();

        loadAllData();

        // Initialize dragging for profile picture
        initProfileImageDrag();

        // Initialize Zoom Slider
        sliderZoom.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (imgProfileView != null) {
                imgProfileView.setScaleX(newVal.doubleValue());
                imgProfileView.setScaleY(newVal.doubleValue());
            }
        });

        // Circular clip for crop container
        Circle clip = new Circle(100);
        clip.setCenterX(100);
        clip.setCenterY(100);
        cropContainer.setClip(clip);

        // Sidebar Profile Image Update
        updateSidebarProfile();
    }

    private double startX, startY;

    private void initProfileImageDrag() {
        imgProfileView.setOnMousePressed(e -> {
            startX = e.getSceneX() - imgProfileView.getTranslateX();
            startY = e.getSceneY() - imgProfileView.getTranslateY();
        });

        imgProfileView.setOnMouseDragged(e -> {
            imgProfileView.setTranslateX(e.getSceneX() - startX);
            imgProfileView.setTranslateY(e.getSceneY() - startY);
        });
    }

    private void updateSidebarProfile() {
        if (com.group25.greengrocer.util.Session.getCurrentUser() == null)
            return;

        byte[] userId = userDao.getProfilePicture(com.group25.greengrocer.util.Session.getCurrentUser().getId());
        if (userId != null && userId.length > 0) {
            try {
                javafx.scene.image.Image image = new javafx.scene.image.Image(new java.io.ByteArrayInputStream(userId));
                imgSidebarProfile.setFill(new ImagePattern(image));
                if (lblSidebarPlaceholder != null)
                    lblSidebarPlaceholder.setVisible(false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            imgSidebarProfile.setFill(javafx.scene.paint.Color.web("#e0e0e0"));
            if (lblSidebarPlaceholder != null)
                lblSidebarPlaceholder.setVisible(true);
        }
    }

    /**
     * Setup Panel Navigation - Initialize sidebar panel switching
     */
    private void setupPanelNavigation() {
        // Hide all panels initially
        pnlOverview.setVisible(false);
        pnlProducts.setVisible(false);
        pnlCarriers.setVisible(false);
        pnlOrders.setVisible(false);
        pnlMessages.setVisible(false);
        pnlReports.setVisible(false);
        pnlMarketing.setVisible(false);

        // Show Overview panel by default
        pnlOverview.setVisible(true);
        pnlOverview.toFront();
        setActiveButton(btnOverview);

        // Load dashboard stats
        loadDashboardStats();
    }

    /**
     * Handle Navigation Button Clicks - Switch between panels
     */
    @FXML
    private void handleNavClick(javafx.event.ActionEvent event) {
        hideAllPanels();

        // Show selected panel and set active state
        if (event.getSource() == btnOverview) {
            pnlOverview.setVisible(true);
            pnlOverview.toFront();
            setActiveButton(btnOverview);
            loadDashboardStats();
        } else if (event.getSource() == btnProducts) {
            pnlProducts.setVisible(true);
            pnlProducts.toFront();
            setActiveButton(btnProducts);
        } else if (event.getSource() == btnCarriers) {
            pnlCarriers.setVisible(true);
            pnlCarriers.toFront();
            setActiveButton(btnCarriers);
        } else if (event.getSource() == btnOrders) {
            pnlOrders.setVisible(true);
            pnlOrders.toFront();
            setActiveButton(btnOrders);
        } else if (event.getSource() == btnMessages) {
            pnlMessages.setVisible(true);
            pnlMessages.toFront();
            setActiveButton(btnMessages);
        } else if (event.getSource() == btnReports) {
            pnlReports.setVisible(true);
            pnlReports.toFront();
            setActiveButton(btnReports);
        } else if (event.getSource() == btnMarketing) {
            pnlMarketing.setVisible(true);
            pnlMarketing.toFront();
            setActiveButton(btnMarketing);
        }
    }

    /**
     * Hide all panels
     */
    private void hideAllPanels() {
        pnlOverview.setVisible(false);
        pnlProducts.setVisible(false);
        pnlCarriers.setVisible(false);
        pnlOrders.setVisible(false);
        pnlMessages.setVisible(false);
        pnlReports.setVisible(false);
        pnlMarketing.setVisible(false);
        if (pnlProfile != null) {
            pnlProfile.setVisible(false);
            pnlProfile.setManaged(false);
        }
    }

    /**
     * Set active button styling - highlight current page
     */
    private void setActiveButton(Button activeBtn) {
        // Reset all buttons
        btnOverview.setStyle("-fx-background-color: #05071F;");
        btnProducts.setStyle("-fx-background-color: #05071F;");
        btnCarriers.setStyle("-fx-background-color: #05071F;");
        btnOrders.setStyle("-fx-background-color: #05071F;");
        btnMessages.setStyle("-fx-background-color: #05071F;");
        btnReports.setStyle("-fx-background-color: #05071F;");
        btnMarketing.setStyle("-fx-background-color: #05071F;");

        // Highlight active button
        activeBtn.setStyle("-fx-background-color: #1620A1; -fx-text-fill: white;");
    }

    /**
     * Load Dashboard Statistics from Database
     */
    private void loadDashboardStats() {
        try {
            java.util.List<Order> allOrders = orderDao.getAllOrders();

            // Total Users (customers) - fetch from DB
            long totalUsers = userDao.getCustomerCount();
            lblTotalUsers.setText(String.valueOf(totalUsers));

            // Total Carriers
            int totalCarriers = userDao.getCarriers().size();
            lblTotalCarriers.setText(String.valueOf(totalCarriers));

            // Total Orders
            int totalOrders = allOrders.size();
            lblTotalOrders.setText(String.valueOf(totalOrders));

            // Delivered Orders
            long deliveredOrders = allOrders.stream()
                    .filter(order -> order.getStatus() != null &&
                            order.getStatus().toString().equalsIgnoreCase("DELIVERED"))
                    .count();
            lblDeliveredOrders.setText(String.valueOf(deliveredOrders));

            // Recent Activity Summary
            String activityText = "System Overview: No active users or orders.";
            if (totalUsers > 0 || totalOrders > 0) {
                activityText = String.format(
                        "System Overview: %d active users, %d carriers managing deliveries. " +
                                "%.1f%% order completion rate with %d total orders processed.",
                        totalUsers,
                        totalCarriers,
                        totalOrders > 0 ? (deliveredOrders * 100.0 / totalOrders) : 0.0,
                        totalOrders);
            }
            lblRecentActivity.setText(activityText);

            // ===== LOAD DASHBOARD TABLE =====
            // Get recent orders (last 10)
            java.util.List<Order> recentOrders = allOrders.stream()
                    .sorted((o1, o2) -> Long.compare(o2.getId(), o1.getId()))
                    .limit(10)
                    .collect(java.util.stream.Collectors.toList());

            dashboardOrderTable.setItems(FXCollections.observableArrayList(recentOrders));

            // Setup table columns
            colDashOrderId.setCellValueFactory(
                    cellData -> new javafx.beans.property.SimpleLongProperty(cellData.getValue().getId()).asObject());
            colDashCustomer.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                    "Customer #" + cellData.getValue().getCustomerId()));
            colDashStatus.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                    cellData.getValue().getStatus() != null ? cellData.getValue().getStatus().toString() : "N/A"));
            colDashTotal.setCellValueFactory(
                    cellData -> new javafx.beans.property.SimpleDoubleProperty(cellData.getValue().getTotal())
                            .asObject());

            // ===== LOAD PIE CHART =====
            // Get all users and count by role
            java.util.List<User> allUsers = userDao.getCarriers(); // Get carriers
            int ownerCount = userDao.getOwnerCount(); // Fetch actual owner count from DB
            int customerCount = (int) totalUsers;
            int carrierCount = allUsers.size();

            javafx.scene.chart.PieChart.Data ownerData = new javafx.scene.chart.PieChart.Data(
                    "Owners (" + ownerCount + ")", ownerCount);
            javafx.scene.chart.PieChart.Data customerData = new javafx.scene.chart.PieChart.Data(
                    "Customers (" + customerCount + ")", customerCount);
            javafx.scene.chart.PieChart.Data carrierData = new javafx.scene.chart.PieChart.Data(
                    "Carriers (" + carrierCount + ")", carrierCount);

            userPieChart.setData(FXCollections.observableArrayList(ownerData, customerData, carrierData));
            userPieChart.setLegendVisible(true);

        } catch (Exception e) {
            lblTotalUsers.setText("N/A");
            lblTotalCarriers.setText("N/A");
            lblTotalOrders.setText("N/A");
            lblDeliveredOrders.setText("N/A");
            lblRecentActivity.setText("Error loading statistics: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Set owner session data
     */
    public void setOwnerSession(long id, String username) {
        this.ownerId = id;
        this.ownerUsername = username;
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

            // Fetch Category ID from Map
            int catId = (categoryMap != null && categoryMap.containsKey(catName)) ? categoryMap.get(catName) : 1;

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

            // Fetch Category ID from Map
            int catId = (categoryMap != null && categoryMap.containsKey(catName)) ? categoryMap.get(catName) : 1;

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

    // ================== PROFILE PANEL METHODS ==================

    @FXML
    private void handleShowProfile() {
        // Hide all panels
        hideAllPanels();

        // Show profile panel
        if (pnlProfile != null) {
            pnlProfile.setVisible(true);
            pnlProfile.setManaged(true);
            loadProfileData();
        }
    }

    @FXML
    private void handleBackFromProfile() {
        // Go back to Overview
        pnlProfile.setVisible(false);
        pnlProfile.setManaged(false);
        pnlOverview.setVisible(true);
        pnlOverview.setManaged(true);
        setActiveButton(btnOverview);
    }

    private void loadProfileData() {
        User currentUser = com.group25.greengrocer.util.Session.getCurrentUser();
        if (currentUser != null) {
            txtProfileUsername.setText(currentUser.getUsername());
            txtProfileRole.setText(currentUser.getRole());

            // Load profile picture from database
            byte[] profilePictureData = userDao.getProfilePicture(currentUser.getId());

            if (profilePictureData != null) {
                profilePictureBytes = profilePictureData;
                javafx.scene.image.Image image = new javafx.scene.image.Image(
                        new java.io.ByteArrayInputStream(profilePictureBytes));

                // Set to view
                imgProfileView.setImage(image);
                imgProfileView.setTranslateX(0);
                imgProfileView.setTranslateY(0);
                imgProfileView.setScaleX(1);
                imgProfileView.setScaleY(1);
                sliderZoom.setValue(1);
                sliderZoom.setVisible(true);

                lblProfilePlaceholder.setVisible(false);
            } else {
                imgProfileView.setImage(null);
                sliderZoom.setVisible(false);
                lblProfilePlaceholder.setVisible(true);
            }

            // Clear password fields
            txtProfileCurrentPass.clear();
            txtProfileNewPass.clear();
            txtProfileConfirmPass.clear();

            lblProfileMessage.setText("");
            lblProfileMessage.setStyle("-fx-background-color: transparent;");
        }
    }

    @FXML
    private void handleUploadPhoto() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Profile Picture");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));

        File selectedFile = fileChooser.showOpenDialog(imgProfileView.getScene().getWindow());

        if (selectedFile != null) {
            try {
                // Check file size (max 2MB)
                long fileSize = selectedFile.length();
                if (fileSize > 2 * 1024 * 1024) {
                    showProfileError("File size exceeds 2MB limit!");
                    return;
                }

                // Read file to byte array
                profilePictureBytes = Files.readAllBytes(selectedFile.toPath());

                // Display image
                Image image = new Image(new ByteArrayInputStream(profilePictureBytes));
                imgProfileView.setImage(image);
                imgProfileView.setVisible(true);
                lblProfilePlaceholder.setVisible(false);
                sliderZoom.setVisible(true); // Show slider when image is loaded

                // Reset zoom and position
                imgProfileView.setTranslateX(0);
                imgProfileView.setTranslateY(0);
                imgProfileView.setScaleX(1);
                imgProfileView.setScaleY(1);
                sliderZoom.setValue(1);

                showProfileSuccess("Photo uploaded! Click 'Save Changes' to update your profile.");

            } catch (Exception e) {
                showProfileError("Error loading image: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleRemovePhoto() {
        imgProfileView.setImage(null);
        sliderZoom.setVisible(false);
        lblProfilePlaceholder.setVisible(true);
        profilePictureBytes = null; // Mark for deletion logic if needed, or just set to null

        // But wait, if we want to delete from DB on save:
        // We need a flag or check if imgProfileView.getImage() is null
        showProfileSuccess("Photo removed! Click 'Save Changes' to update.");
    }

    @FXML
    private void handleSaveProfile() {
        User currentUser = com.group25.greengrocer.util.Session.getCurrentUser();
        if (currentUser == null) {
            showProfileError("Session expired. Please login again.");
            return;
        }

        String currentPassword = txtProfileCurrentPass.getText();
        String newPassword = txtProfileNewPass.getText();
        String confirmPassword = txtProfileConfirmPass.getText();

        // Check if user wants to change password
        boolean isChangingPassword = !newPassword.isEmpty() || !confirmPassword.isEmpty();
        StringBuilder successMsg = new StringBuilder();

        // Validate current password only if changing password
        if (isChangingPassword && currentPassword.isEmpty()) {
            showProfileError("Please enter your current password to change password.");
            return;
        }

        // Verify current password only if changing password
        if (isChangingPassword) {
            String hashedCurrentPassword = hashPassword(currentPassword);
            if (!hashedCurrentPassword.equals(currentUser.getPassword())) {
                showProfileError("Current password is incorrect!");
                return;
            }
        }

        // If changing password
        if (!newPassword.isEmpty() || !confirmPassword.isEmpty()) {
            if (newPassword.length() < 3) {
                showProfileError("New password must be at least 3 characters long.");
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                showProfileError("New passwords do not match!");
                return;
            }

            // Update password
            String hashedNewPassword = hashPassword(newPassword);
            boolean passwordUpdated = userDao.updatePassword(currentUser.getId(), hashedNewPassword);

            if (!passwordUpdated) {
                showProfileError("Failed to update password!");
                return;
            }
            successMsg.append("Password updated successfully!");
        }

        // Capture the cropped image from the UI
        if (imgProfileView.getImage() != null) {
            try {
                WritableImage snapshot = cropContainer.snapshot(new SnapshotParameters(), null);
                BufferedImage bufferedImage = SwingFXUtils.fromFXImage(snapshot, null);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(bufferedImage, "png", baos);
                profilePictureBytes = baos.toByteArray();
            } catch (Exception e) {
                e.printStackTrace();
                showProfileError("Error processing image!");
                return;
            }
        } else {
            profilePictureBytes = null; // Image removed
        }

        // Save profile picture to database
        if (profilePictureBytes != null) {
            boolean pictureSaved = userDao.saveProfilePicture(
                    currentUser.getId(),
                    profilePictureBytes);

            if (!pictureSaved) {
                showProfileError("Failed to save profile picture!");
                return;
            }
            if (successMsg.length() > 0)
                successMsg.append(" and ");
            successMsg.append("profile picture updated successfully!");
        } else {
            // If null, user might have removed it. Let's delete it.
            userDao.deleteProfilePicture(currentUser.getId());
            if (successMsg.length() > 0)
                successMsg.append(" and ");
            successMsg.append("profile picture removed successfully!");
        }

        // Update sidebar
        updateSidebarProfile();

        if (successMsg.length() == 0) {
            showProfileSuccess("No changes to save.");
        } else {
            showProfileSuccess(successMsg.toString());
        }

        // Clear password fields
        txtProfileCurrentPass.clear();
        txtProfileNewPass.clear();
        txtProfileConfirmPass.clear();
    }

    @FXML
    private void handleResetProfile() {
        loadProfileData();
        showProfileSuccess("Profile form reset.");
    }

    private void showProfileSuccess(String message) {
        lblProfileMessage.setText("✓ " + message);
        lblProfileMessage.setStyle(
                "-fx-background-color: #d4edda; -fx-text-fill: #155724; -fx-border-color: #c3e6cb; -fx-border-width: 1;");
    }

    private void showProfileError(String message) {
        lblProfileMessage.setText("✗ " + message);
        lblProfileMessage.setStyle(
                "-fx-background-color: #f8d7da; -fx-text-fill: #721c24; -fx-border-color: #f5c6cb; -fx-border-width: 1;");
    }

    /**
     * Hash password using SHA-256
     */
    private String hashPassword(String password) {
        try {
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(password.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder(2 * encodedhash.length);
            for (byte b : encodedhash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1)
                    hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
}
