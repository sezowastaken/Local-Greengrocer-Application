package com.group25.greengrocer.controller;

import com.group25.greengrocer.dao.ProductDao;
import com.group25.greengrocer.model.Product;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;

import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;

import java.io.IOException;
import java.util.List;
import javafx.collections.ObservableList;

public class CustomerController {

    @FXML
    private Text welcomeText;
    @FXML
    private FlowPane vegPane;
    @FXML
    private FlowPane fruitPane;
    @FXML
    private TabPane productTabPane;
    @FXML
    private ToggleButton vegetablesToggle;
    @FXML
    private ToggleButton fruitsToggle;
    @FXML
    private ToggleGroup categoryToggleGroup;
    @FXML
    private HBox selectorBar;
    @FXML
    private ScrollPane vegetableView;
    @FXML
    private ScrollPane fruitView;

    // Header Buttons for Animation
    @FXML
    private Button shopButton;
    @FXML
    private Button cartButton;
    @FXML
    private MenuButton menuButton;

    @FXML
    public void initialize() {
        // Create Vegetables Toggle Content: Icon + Label in VBox
        javafx.scene.shape.SVGPath vegIcon = new javafx.scene.shape.SVGPath();
        vegIcon.setContent(
                "M17,8C8,10 5.9,16.17 3.82,21.34L5.71,22L6.66,19.7C7.14,19.87 7.64,20 8,20C19,20 22,3 22,3C21,5 14,5.25 9,6.25C4,7.25 2,11.5 2,13.5C2,15.5 3.75,17.25 3.75,17.25C7,8 17,8 17,8Z");
        vegIcon.getStyleClass().add("selector-icon");
        vegIcon.setScaleX(0.9);
        vegIcon.setScaleY(0.9);

        Label vegLabel = new Label("Vegetables");
        vegLabel.getStyleClass().add("selector-label");

        VBox vegBox = new VBox(6, vegIcon, vegLabel);
        vegBox.setAlignment(javafx.geometry.Pos.CENTER);
        vegetablesToggle.setGraphic(vegBox);

        // Create Fruits Toggle Content: Icon + Label in VBox
        javafx.scene.shape.SVGPath fruitIcon = new javafx.scene.shape.SVGPath();
        fruitIcon.setContent(
                "M20,10C22,13 17,22 15,22C13,22 13,21 12,21C11,21 11,22 9,22C7,22 2,13 4,10C6,7 9,7 11,8V5H13V8C15,7 18,7 20,10Z");
        fruitIcon.getStyleClass().add("selector-icon");
        fruitIcon.setScaleX(0.9);
        fruitIcon.setScaleY(0.9);

        Label fruitLabel = new Label("Fruits");
        fruitLabel.getStyleClass().add("selector-label");

        VBox fruitBox = new VBox(6, fruitIcon, fruitLabel);
        fruitBox.setAlignment(javafx.geometry.Pos.CENTER);
        fruitsToggle.setGraphic(fruitBox);

        // Set default selection
        vegetablesToggle.setSelected(true);

        // Add listener to toggle views
        categoryToggleGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            if (newToggle == vegetablesToggle) {
                vegetableView.setVisible(true);
                vegetableView.setManaged(true);
                fruitView.setVisible(false);
                fruitView.setManaged(false);
            } else if (newToggle == fruitsToggle) {
                vegetableView.setVisible(false);
                vegetableView.setManaged(false);
                fruitView.setVisible(true);
                fruitView.setManaged(true);
            }
        });

        // Apply smooth hover animations
        applyHoverScale(shopButton, 1.05);
        applyHoverScale(cartButton, 1.05);
        applyHoverScale(menuButton, 1.05);
        applyHoverScale(vegetablesToggle, 1.05);
        applyHoverScale(fruitsToggle, 1.05);

        refreshProductCards();
    }

    private void applyHoverScale(javafx.scene.Node node, double scaleFactor) {
        if (node == null)
            return;

        // Scale Transition Up
        javafx.animation.ScaleTransition scaleUp = new javafx.animation.ScaleTransition(
                javafx.util.Duration.millis(200), node);
        scaleUp.setToX(scaleFactor);
        scaleUp.setToY(scaleFactor);
        scaleUp.setInterpolator(javafx.animation.Interpolator.EASE_OUT);

        // Scale Transition Down
        javafx.animation.ScaleTransition scaleDown = new javafx.animation.ScaleTransition(
                javafx.util.Duration.millis(200), node);
        scaleDown.setToX(1.0);
        scaleDown.setToY(1.0);
        scaleDown.setInterpolator(javafx.animation.Interpolator.EASE_OUT);

        node.setOnMouseEntered(e -> {
            scaleDown.stop();
            scaleUp.playFromStart();
        });

        node.setOnMouseExited(e -> {
            scaleUp.stop();
            scaleDown.playFromStart();
        });
    }

    @FXML
    private VBox cartView;
    @FXML
    private VBox cartItemsContainer;
    @FXML
    private Label cartTotalLabel;

    private final ProductDao productDao = new ProductDao();
    private final List<CartItem> cart = new java.util.ArrayList<>();

    private static class CartItem {
        Product product;
        double quantity;

        CartItem(Product product, double quantity) {
            this.product = product;
            this.quantity = quantity;
        }

        double getTotalPrice() {
            return product.getPrice() * quantity;
        }
    }

    private long customerId;

    public void setCustomerSession(long id, String name) {
        this.customerId = id;
        welcomeText.setText("Welcome, " + name);
    }

    @FXML
    private void handlePlaceOrder() {
        if (cart.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Cart is Empty", "Please add items to your cart before checking out.");
            return;
        }

        if (customerId == 0) {
            // Ideally should not happen if login flow is correct, but safer to check
            showAlert(Alert.AlertType.ERROR, "Session Error", "Please logout and login again.");
            return;
        }

        try {
            // Build Order Object
            com.group25.greengrocer.model.Order order = new com.group25.greengrocer.model.Order();
            order.setCustomerId(customerId);
            order.setStatus(com.group25.greengrocer.model.OrderStatus.PLACED);
            order.setOrderTime(java.time.LocalDateTime.now());
            order.setRequestedDeliveryTime(java.time.LocalDateTime.now().plusDays(1)); // Default next day

            // Calculate totals
            double subtotal = 0;
            for (CartItem ci : cart)
                subtotal += ci.getTotalPrice();

            order.setSubtotal(subtotal);
            order.setVatRate(18.0); // Example VAT
            order.setVatTotal(subtotal * 0.18);
            order.setTotal(subtotal + order.getVatTotal());
            order.setDiscountTotal(0); // If coupons implemented later

            // Build Items
            java.util.List<com.group25.greengrocer.model.OrderItem> orderItems = new java.util.ArrayList<>();
            for (CartItem ci : cart) {
                com.group25.greengrocer.model.OrderItem oi = new com.group25.greengrocer.model.OrderItem(
                        0, 0,
                        ci.product.getId(),
                        ci.product.isPiece() ? com.group25.greengrocer.model.UnitType.PCS
                                : com.group25.greengrocer.model.UnitType.KG,
                        ci.quantity,
                        ci.product.getPrice(),
                        ci.getTotalPrice());
                orderItems.add(oi);
            }

            // Call Service
            com.group25.greengrocer.service.OrderService orderService = new com.group25.greengrocer.service.OrderService();
            orderService.placeOrder(order, orderItems);

            // Success
            cart.clear();
            localStockMap.clear(); // Clear local cache to force refresh from DB on next load
            refreshProductCards();
            updateCartView();

            showAlert(Alert.AlertType.INFORMATION, "Order Successful",
                    "Your order has been placed successfully! Order ID: " + order.getId());

            // Navigate back to shop or stay? Usually stay or show empty cart.
            handleBackToShop();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Order Failed", "Could not place order: " + e.getMessage());
        }
    }

    private void refreshProductCards() {
        loadProducts("Vegetable", vegPane);
        loadProducts("Fruit", fruitPane);
    }

    // Optimized to reuse nodes and prevent animation reset
    private void loadProducts(String category, FlowPane pane) {
        List<Product> products = productDao.getProductsByCategory(category);
        ObservableList<javafx.scene.Node> children = pane.getChildren();

        // 1. Update existing nodes or create new ones
        for (int i = 0; i < products.size(); i++) {
            Product product = products.get(i);

            // Update local stock map initially
            localStockMap.put(product.getId(), product.getStock());

            VBox card;
            if (i < children.size() && children.get(i) instanceof VBox) {
                // Reuse existing node
                card = (VBox) children.get(i);
            } else {
                // Create new node
                card = createBaseProductCard();
                if (i < children.size()) {
                    children.set(i, card); // Replace wrong node if any
                } else {
                    children.add(card); // Append new
                }
            }

            // Update UI content on the reused/new card
            updateProductCardUI(card, product);
        }

        // 2. Remove excess nodes if any
        if (pane.getChildren().size() > products.size()) {
            pane.getChildren().remove(products.size(), pane.getChildren().size());
        }
    }

    private VBox createBaseProductCard() {
        VBox card = new VBox(5);
        card.getStyleClass().add("product-card");
        card.setPrefWidth(150);

        // Apply smooth scale animation to the ROOT node of the card
        applyHoverScale(card, 1.05); // 1.05 scale as per request

        return card;
    }

    private void updateProductCardUI(VBox card, Product product) {
        // Store ID for reference (optional but good practice)
        card.setUserData(product.getId());

        // Clear children to rebuild content (this does NOT trigger mouse exit on the
        // card itself)
        card.getChildren().clear();

        if (product.getProductImage() != null) {
            ImageView imgView = new ImageView(product.getProductImage());
            imgView.setFitWidth(100);
            imgView.setFitHeight(100);
            imgView.setPreserveRatio(true);
            imgView.getStyleClass().add("product-image");
            card.getChildren().add(imgView);
        }

        Label nameLabel = new Label(product.getName());
        nameLabel.getStyleClass().add("product-name");

        Label priceLabel = new Label("$" + product.getPrice() + " per " + (product.isPiece() ? "piece" : "kg"));
        priceLabel.getStyleClass().add("product-price");

        Label stockLabel = new Label("Stock: " + product.getStock() + (product.isPiece() ? "" : " kg"));
        stockLabel.getStyleClass().add("product-stock-label");

        // Check if item is in cart
        CartItem cartItem = findInCart(product);

        if (cartItem == null) {
            Button addToCartBtn = new Button("Add to Cart");
            addToCartBtn.getStyleClass().add("button-primary");
            addToCartBtn.setMaxWidth(Double.MAX_VALUE);
            addToCartBtn.setOnAction(e -> handleAddToCart(product));
            card.getChildren().addAll(nameLabel, priceLabel, stockLabel, addToCartBtn);
        } else {
            card.getChildren().addAll(nameLabel, priceLabel, stockLabel, createQtyBox(cartItem));
        }
    }

    private HBox createQtyBox(CartItem item) {
        HBox qtyBox = new HBox();
        qtyBox.getStyleClass().add("qty-box");

        Button minusBtn = new Button("-");
        minusBtn.getStyleClass().add("qty-btn");
        minusBtn.setOnAction(e -> handleDecrement(item));

        Label qtyLabel = new Label(String.format(item.product.isPiece() ? "%.0f" : "%.2f", item.quantity));
        qtyLabel.getStyleClass().add("qty-text");

        // Make the label itself interactive to save space as requested
        qtyLabel.setTooltip(new Tooltip("Click to edit manually"));
        qtyLabel.setOnMouseClicked(e -> handleManualQuantity(item));
        qtyLabel.getStyleClass().add("qty-label-interactive");

        Button plusBtn = new Button("+");
        plusBtn.getStyleClass().add("qty-btn");
        plusBtn.setOnAction(e -> handleIncrement(item));

        qtyBox.getChildren().addAll(minusBtn, qtyLabel, plusBtn);

        return qtyBox;
    }

    private void handleManualQuantity(CartItem item) {
        TextInputDialog dialog = new TextInputDialog(String.valueOf(item.quantity));
        dialog.setTitle("Set Quantity");
        dialog.setHeaderText("Enter quantity for " + item.product.getName());
        dialog.setContentText("Quantity:");

        // Style the dialog
        DialogPane dialogPane = dialog.getDialogPane();
        try {
            dialogPane.getStylesheets().add(getClass().getResource("/css/app.css").toExternalForm());
            dialogPane.getStyleClass().add("custom-alert");
        } catch (Exception e) {
            // ignore
        }

        java.util.Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String input = result.get();

            // 1. Check for letters/symbols (allow digits and one dot)
            if (!input.matches("\\d*\\.?\\d+")) {
                showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please enter a valid positive number.");
                return;
            }

            try {
                double qty = Double.parseDouble(input);

                // 2. Check for negative or zero
                if (qty <= 0) {
                    showAlert(Alert.AlertType.ERROR, "Invalid Input", "Quantity must be greater than 0.");
                    return;
                }

                // 3. Check integer constraint for Piece items
                if (item.product.isPiece()) {
                    if (qty % 1 != 0) {
                        showAlert(Alert.AlertType.ERROR, "Invalid Input",
                                "This item is sold by piece. Please enter a whole number.");
                        return;
                    }
                }

                // 4. Check Stock
                int productId = item.product.getId();
                double maxStock = localStockMap.getOrDefault(productId, item.product.getStock());

                if (qty > maxStock) {
                    showAlert(Alert.AlertType.ERROR, "Stock Error", "Not enough stock available! Max: " + maxStock);
                    return;
                }

                // Apply change
                item.quantity = qty;
                refreshProductCards();
                if (cartView.isVisible()) {
                    updateCartView();
                }

            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please enter a valid number.");
            }
        }
    }

    private CartItem findInCart(Product product) {
        for (CartItem item : cart) {
            if (item.product.getId() == product.getId()) {
                return item;
            }
        }
        return null;
    }

    // Local map to track stock without querying DB on every click
    private final java.util.Map<Integer, Double> localStockMap = new java.util.HashMap<>();

    private void handleIncrement(CartItem item) {
        double step = item.product.isPiece() ? 1.0 : 0.25;
        double newQty = item.quantity + step;

        // Check local stock map
        int productId = item.product.getId();
        double maxStock = localStockMap.getOrDefault(productId, item.product.getStock());

        if (newQty > maxStock) {
            showAlert(Alert.AlertType.ERROR, "Stock Error", "Not enough stock available! Max: " + maxStock);
            return;
        }

        item.quantity = newQty;
        // Optionally update the map if we want to track "remaining" stock vs "total"
        // stock.
        // But for validation "newQty > maxStock" is correct if maxStock is the Total DB
        // stock.

        refreshProductCards();
        if (cartView.isVisible()) {
            updateCartView();
        }
    }

    private void handleAddToCart(Product product) {
        // Initialize/Update local stock map if not present
        localStockMap.putIfAbsent(product.getId(), product.getStock());
        double maxStock = localStockMap.get(product.getId());

        if (maxStock < 1) {
            showAlert(Alert.AlertType.ERROR, "Stock Error", "Out of stock!");
            return;
        }
        cart.add(new CartItem(product, 1.0));
        refreshProductCards();
    }

    private void handleDecrement(CartItem item) {
        double step = item.product.isPiece() ? 1.0 : 0.25;
        double newQty = item.quantity - step;

        if (newQty <= 0) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Remove Item");
            alert.setHeaderText(null);
            alert.setContentText("Do you want to remove " + item.product.getName() + " from your cart?");
            styleAlert(alert);

            java.util.Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                cart.remove(item);
            }
            // If No, do nothing (keep at current qty) or logic could be to define min as
            // step.
            // Request said: "eğer sayı 0'a düşerse... hayır derse pop ekranı kapanacak ve
            // hiçbir şey değişmeyecek"
            // So we don't update qty if they say no.
        } else {
            item.quantity = newQty;
        }
        refreshProductCards();
        if (cartView.isVisible()) {
            updateCartView();
        }
    }

    @FXML
    private void handleViewCart() {
        hideAllViews();
        cartView.setVisible(true);
        updateCartView();
    }

    @FXML
    private void handleBackToShop() {
        hideAllViews();
        // Show selector bar
        selectorBar.setVisible(true);
        selectorBar.setManaged(true);
        // Show the currently selected view (vegetable or fruit)
        if (vegetablesToggle.isSelected()) {
            vegetableView.setVisible(true);
            vegetableView.setManaged(true);
        } else {
            fruitView.setVisible(true);
            fruitView.setManaged(true);
        }
        refreshProductCards();
    }

    private void updateCartView() {
        cartItemsContainer.getChildren().clear();
        double total = 0;

        for (CartItem item : cart) {
            HBox row = new HBox(20);
            row.getStyleClass().add("cart-item");
            row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
            row.setPadding(new javafx.geometry.Insets(10));

            if (item.product.getProductImage() != null) {
                ImageView imgView = new ImageView(item.product.getProductImage());
                imgView.setFitWidth(50);
                imgView.setFitHeight(50);
                imgView.setPreserveRatio(true);
                imgView.getStyleClass().add("cart-image");
                row.getChildren().add(imgView);
            }

            Label name = new Label(item.product.getName());
            name.setPrefWidth(150);
            name.setStyle("-fx-font-weight: bold;");

            // Reuse the QtyBox here!
            HBox qtyBox = createQtyBox(item);

            Label lineTotal = new Label(String.format("$%.2f", item.getTotalPrice()));
            lineTotal.setPrefWidth(80);
            lineTotal.setStyle("-fx-font-weight: bold; -fx-text-fill: #2e7d32;");

            row.getChildren().addAll(name, qtyBox, lineTotal);
            cartItemsContainer.getChildren().add(row);

            total += item.getTotalPrice();
        }

        cartTotalLabel.setText(String.format("$%.2f", total));
    }

    @FXML
    private void handleLogout() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
            welcomeText.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private VBox profileView;
    @FXML
    private TextField profileNameField;
    @FXML
    private TextArea profileAddressField;
    @FXML
    private TextField profilePhoneField;
    @FXML
    private TextField profileCityField;

    @FXML
    private VBox orderHistoryView;
    @FXML
    private VBox ordersContainer;

    @FXML
    private void handleViewProfile() {
        hideAllViews();
        profileView.setVisible(true);
        loadProfileData();
    }

    @FXML
    private void handleViewOrders() {
        hideAllViews();
        orderHistoryView.setVisible(true);
        loadOrderHistory();
    }

    private void hideAllViews() {
        selectorBar.setVisible(false);
        selectorBar.setManaged(false);
        vegetableView.setVisible(false);
        vegetableView.setManaged(false);
        fruitView.setVisible(false);
        fruitView.setManaged(false);
        cartView.setVisible(false);
        profileView.setVisible(false);
        orderHistoryView.setVisible(false);
    }

    @FXML
    private void handleSaveProfile() {
        String address = profileAddressField.getText();
        String city = profileCityField.getText();
        String phone = profilePhoneField.getText();

        String sql = "UPDATE users SET address_line = ?, city = ?, phone = ? WHERE id = ?";

        try (java.sql.Connection conn = com.group25.greengrocer.util.DbAdapter.getConnection();
                java.sql.PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, address);
            stmt.setString(2, city);
            stmt.setString(3, phone);
            stmt.setLong(4, customerId);

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Profile updated successfully!");
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Could not update profile.");
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Update failed: " + e.getMessage());
        }
    }

    private void loadProfileData() {
        profileNameField.setText(welcomeText.getText().replace("Welcome, ", ""));

        String sql = "SELECT address_line, city, phone FROM users WHERE id = ?";
        try (java.sql.Connection conn = com.group25.greengrocer.util.DbAdapter.getConnection();
                java.sql.PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, customerId);
            try (java.sql.ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    profileAddressField.setText(rs.getString("address_line"));
                    profileCityField.setText(rs.getString("city"));
                    try {
                        profilePhoneField.setText(rs.getString("phone"));
                    } catch (Exception e) {
                    }
                }
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadOrderHistory() {
        ordersContainer.getChildren().clear();
        com.group25.greengrocer.dao.OrderDao orderDao = new com.group25.greengrocer.dao.OrderDao();

        try {
            List<com.group25.greengrocer.model.Order> orders = orderDao.findByCustomerId(customerId);

            if (orders.isEmpty()) {
                ordersContainer.getChildren().add(new Label("No past orders found."));
                return;
            }

            for (com.group25.greengrocer.model.Order order : orders) {
                VBox card = new VBox(5);
                card.getStyleClass().add("order-card");

                HBox header = new HBox(10);
                header.setAlignment(javafx.geometry.Pos.CENTER_LEFT); // Align items vertically center

                Label dateLabel = new Label("Date: " + order.getOrderTime().toLocalDate());
                dateLabel.getStyleClass().add("order-date-label");

                Label statusLabel = new Label(order.getStatus().name());
                statusLabel.getStyleClass().add("status-label");

                switch (order.getStatus()) {
                    case PLACED:
                        statusLabel.getStyleClass().add("status-placed");
                        break;
                    case DELIVERED:
                        statusLabel.getStyleClass().add("status-delivered");
                        break;
                    case CANCELLED:
                        statusLabel.getStyleClass().add("status-cancelled");
                        break;
                    default:
                        break;
                }

                Region spacer = new Region();
                HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

                Label totalLabel = new Label("Total: $" + String.format("%.2f", order.getTotal()));
                totalLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #333;");

                header.getChildren().addAll(dateLabel, spacer, statusLabel, new Label("|"), totalLabel);

                if (order.getStatus() == com.group25.greengrocer.model.OrderStatus.PLACED) {
                    Button cancelButton = new Button("Cancel");
                    cancelButton.getStyleClass().add("button-danger");
                    cancelButton.setOnAction(e -> handleCancelOrder(order.getId()));
                    header.getChildren().add(cancelButton);
                }

                Label itemsLabel = new Label("Order ID: #" + order.getId());
                itemsLabel.getStyleClass().add("order-id-label");

                card.getChildren().addAll(header, itemsLabel);
                ordersContainer.getChildren().add(card);
            }

        } catch (Exception e) {
            e.printStackTrace();
            ordersContainer.getChildren().add(new Label("Error loading orders."));
        }
    }

    private void handleCancelOrder(long orderId) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Cancel Order");
        alert.setHeaderText("Confirm Cancellation");
        alert.setContentText("Are you sure you want to cancel this order?");
        styleAlert(alert);

        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            com.group25.greengrocer.dao.OrderDao dao = new com.group25.greengrocer.dao.OrderDao();
            try {
                dao.cancelOrder(orderId);
                showAlert(Alert.AlertType.INFORMATION, "Cancelled", "Order has been cancelled.");
                loadOrderHistory();
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Cancelling Failed", e.getMessage());
            }
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        styleAlert(alert);
        alert.showAndWait();
    }

    private void styleAlert(Alert alert) {
        DialogPane dialogPane = alert.getDialogPane();
        try {
            dialogPane.getStylesheets().add(getClass().getResource("/css/app.css").toExternalForm());
            dialogPane.getStyleClass().add("custom-alert");
        } catch (Exception e) {
            System.err.println("Could not load CSS for alert: " + e.getMessage());
        }
    }
}
