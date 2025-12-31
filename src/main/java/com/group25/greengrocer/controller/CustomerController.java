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
import javafx.scene.text.Text;

import java.io.IOException;
import java.util.List;
import com.group25.greengrocer.dao.UserDao;
import com.group25.greengrocer.service.LoyaltyService;

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
    private VBox cartView;
    @FXML
    private VBox cartItemsContainer;
    @FXML
    private Label cartTotalLabel;

    private final ProductDao productDao = new ProductDao();
    private final UserDao userDao = new UserDao();
    private final LoyaltyService loyaltyService = new LoyaltyService();
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
    private String customerUsername;

    public void setCustomerSession(long id, String name) {
        this.customerId = id;
        this.customerUsername = name;
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

            // Loyalty Logic
            java.math.BigDecimal individualRate = userDao.getIndividualLoyaltyRate(customerId);
            double discountRate = 0.0;
            if (individualRate != null) {
                discountRate = individualRate.doubleValue();
            } else {
                discountRate = loyaltyService.getLoyaltyDiscountRate();
            }

            double discountAmount = subtotal * discountRate;
            double discountedSubtotal = subtotal - discountAmount;

            order.setSubtotal(subtotal);
            order.setDiscountTotal(discountAmount);

            order.setVatRate(18.0);
            order.setVatTotal(discountedSubtotal * 0.18);
            order.setTotal(discountedSubtotal + order.getVatTotal());

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

    @FXML
    public void initialize() {
        refreshProductCards();
    }

    private void refreshProductCards() {
        loadProducts("Vegetable", vegPane);
        loadProducts("Fruit", fruitPane);
    }

    private void loadProducts(String category, FlowPane pane) {
        pane.getChildren().clear();
        List<Product> products = productDao.getProductsByCategory(category);

        for (Product product : products) {
            // Update local map with fresh DB values initially
            localStockMap.put(product.getId(), product.getStock());
            pane.getChildren().add(createProductCard(product));
        }
    }

    private VBox createProductCard(Product product) {
        VBox card = new VBox(5);
        card.getStyleClass().add("product-card");
        card.setPrefWidth(150);

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
        stockLabel.setStyle("-fx-text-fill: #aaa; -fx-font-size: 12px;");

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

        return card;
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
        productTabPane.setVisible(false);
        ordersView.setVisible(false);
        messagesView.setVisible(false);
        cartView.setVisible(true);
        updateCartView();
    }

    @FXML
    private void handleBackToShop() {
        cartView.setVisible(false);
        ordersView.setVisible(false);
        messagesView.setVisible(false);
        productTabPane.setVisible(true);
        refreshProductCards(); // Refresh to ensure sync
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
    private void handleShowProfile() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/profile.fxml"));
            Parent root = loader.load();

            ProfileController profileController = loader.getController();
            profileController.setUserSession(customerId, customerUsername, "customer");

            welcomeText.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load profile page: " + e.getMessage());
        }
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

    @FXML
    private VBox ordersView;
    @FXML
    private TableView<com.group25.greengrocer.model.Order> ordersTable;
    @FXML
    private TableColumn<com.group25.greengrocer.model.Order, Long> colOrderId;
    @FXML
    private TableColumn<com.group25.greengrocer.model.Order, String> colOrderDate;
    @FXML
    private TableColumn<com.group25.greengrocer.model.Order, Double> colOrderTotal;
    @FXML
    private TableColumn<com.group25.greengrocer.model.Order, String> colOrderStatus;
    @FXML
    private TableColumn<com.group25.greengrocer.model.Order, Void> colOrderAction;

    @FXML
    private VBox messagesView;
    @FXML
    private TableView<com.group25.greengrocer.model.Message> messagesTable;
    @FXML
    private TableColumn<com.group25.greengrocer.model.Message, String> colMsgSender;
    @FXML
    private TableColumn<com.group25.greengrocer.model.Message, String> colMsgSubject;
    @FXML
    private TableColumn<com.group25.greengrocer.model.Message, String> colMsgContent;
    @FXML
    private TableColumn<com.group25.greengrocer.model.Message, String> colMsgDate;
    @FXML
    private TextField txtMsgSubject;
    @FXML
    private TextArea txtMsgContent;

    private final com.group25.greengrocer.dao.OrderDao orderDao = new com.group25.greengrocer.dao.OrderDao();
    private final com.group25.greengrocer.dao.MessageDao messageDao = new com.group25.greengrocer.dao.MessageDao();
    // private final com.group25.greengrocer.dao.RatingDao ratingDao = new
    // com.group25.greengrocer.dao.RatingDao();

    @FXML
    private void handleViewOrders() {
        productTabPane.setVisible(false);
        cartView.setVisible(false);
        messagesView.setVisible(false);
        ordersView.setVisible(true);
        refreshOrders();
    }

    @FXML
    private void handleRefreshOrders() {
        refreshOrders();
    }

    private void refreshOrders() {
        setupOrderTable();
        try {
            List<com.group25.greengrocer.model.Order> orders = orderDao.findByCustomerId(customerId);
            ordersTable.setItems(javafx.collections.FXCollections.observableArrayList(orders));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupOrderTable() {
        if (colOrderId.getCellValueFactory() == null) {
            colOrderId.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("id"));
            colOrderDate.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData
                    .getValue().getOrderTime().format(java.time.format.DateTimeFormatter.ofPattern("M/d/yyyy"))));
            colOrderTotal.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("total"));
            colOrderStatus.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("status"));

            colOrderAction.setCellFactory(param -> new TableCell<>() {
                private final Button btnRate = new Button("Rate");
                {
                    btnRate.getStyleClass().add("button-secondary");
                    btnRate.setOnAction(event -> {
                        com.group25.greengrocer.model.Order order = getTableView().getItems().get(getIndex());
                        handleRateCarrier(order);
                    });
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        com.group25.greengrocer.model.Order order = getTableView().getItems().get(getIndex());
                        if (order.getStatus() == com.group25.greengrocer.model.OrderStatus.DELIVERED
                                && order.getCarrierId() != null) {
                            setGraphic(btnRate);
                        } else {
                            setGraphic(null);
                        }
                    }
                }
            });
        }
    }

    private void handleRateCarrier(com.group25.greengrocer.model.Order order) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Rate Carrier");
        dialog.setHeaderText("Rate for Order #" + order.getId());

        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        VBox content = new VBox(10);
        content.setPadding(new javafx.geometry.Insets(20));

        Label lblRating = new Label("Rating (1-5):");
        ChoiceBox<Integer> ratingBox = new ChoiceBox<>();
        ratingBox.getItems().addAll(1, 2, 3, 4, 5);
        ratingBox.setValue(5);

        Label lblComment = new Label("Comment:");
        TextArea txtComment = new TextArea();
        txtComment.setPrefRowCount(3);
        txtComment.setWrapText(true);

        content.getChildren().addAll(lblRating, ratingBox, lblComment, txtComment);
        dialogPane.setContent(content);
        try {
            dialogPane.getStylesheets().add(getClass().getResource("/css/app.css").toExternalForm());
        } catch (Exception e) {
        }

        java.util.Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                // Use RatingDao
                com.group25.greengrocer.model.CarrierRating rating = new com.group25.greengrocer.model.CarrierRating(
                        0, // ID auto-inc
                        (int) order.getId(),
                        (int) customerId,
                        order.getCarrierId().intValue(),
                        ratingBox.getValue(),
                        txtComment.getText(),
                        null // Date handled by DB NOW()
                );

                // We need to uncomment the dao field first or use local instance
                com.group25.greengrocer.dao.RatingDao rDao = new com.group25.greengrocer.dao.RatingDao();
                rDao.addRating(rating);

                showAlert(Alert.AlertType.INFORMATION, "Success", "Rating submitted!");
            } catch (Exception e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to submit rating.");
            }
        }
    }

    @FXML
    private void handleViewMessages() {
        productTabPane.setVisible(false);
        cartView.setVisible(false);
        ordersView.setVisible(false);
        messagesView.setVisible(true);
        refreshMessages();
    }

    @FXML
    private void handleRefreshMessages() {
        refreshMessages();
    }

    private void refreshMessages() {
        setupMessageTable();
        List<com.group25.greengrocer.model.Message> messages = messageDao.getMessagesForUser(customerId);
        messagesTable.setItems(javafx.collections.FXCollections.observableArrayList(messages));
    }

    private void setupMessageTable() {
        if (colMsgSender.getCellValueFactory() == null) {
            colMsgSender.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("senderName"));
            colMsgSubject.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("subject"));
            colMsgContent.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("content"));
            colMsgDate.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                    cellData.getValue().getSentTime().toString()));
        }
    }

    @FXML
    private void handleSendMessage() {
        String subject = txtMsgSubject.getText();
        String content = txtMsgContent.getText();

        if (subject.isEmpty() || content.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please enter subject and content.");
            return;
        }

        long ownerId = 1;

        messageDao.sendMessage(customerId, ownerId, subject, content);
        txtMsgSubject.clear();
        txtMsgContent.clear();
        refreshMessages();
        showAlert(Alert.AlertType.INFORMATION, "Sent", "Message sent to Owner.");
    }
}
