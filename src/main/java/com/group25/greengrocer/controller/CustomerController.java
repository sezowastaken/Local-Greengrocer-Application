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

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import com.group25.greengrocer.model.Order;
import com.group25.greengrocer.model.Message;
import com.group25.greengrocer.dao.MessageDao;
import com.group25.greengrocer.dao.OrderDao;
import java.io.File;
import java.io.FileOutputStream;
import javafx.stage.FileChooser;

import javafx.collections.ObservableList;
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

        // Setup tables
        setupOrderTable();
        setupMessageTable();

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

    // --- Order History Views ---

    private final ProductDao productDao = new ProductDao();
    private final UserDao userDao = new UserDao();
    private final LoyaltyService loyaltyService = new LoyaltyService();
    private final List<CartItem> cart = new java.util.ArrayList<>();

    private class CartItem {
        Product product;
        double quantity;

        CartItem(Product product, double quantity) {
            this.product = product;
            this.quantity = quantity;
        }

        double getEffectivePrice() {
            double currentStock = localStockMap.getOrDefault(product.getId(), product.getStock());
            return (currentStock < product.getThreshold()) ? product.getPrice() * 2 : product.getPrice();
        }

        double getTotalPrice() {
            return getEffectivePrice() * quantity;
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
            // Ask for Order Note
            TextInputDialog noteDialog = new TextInputDialog();
            noteDialog.setTitle("Order Note");
            noteDialog.setHeaderText("Add a note for your order (Optional)");
            noteDialog.setContentText("Note:");
            styleAlert(noteDialog); // Reuse existing style method if possible, otherwise remove this line

            String orderNote = "";
            java.util.Optional<String> noteResult = noteDialog.showAndWait();
            if (noteResult.isPresent()) {
                orderNote = noteResult.get();
            }

            // Build Order Object
            com.group25.greengrocer.model.Order order = new com.group25.greengrocer.model.Order();
            order.setNote(orderNote); // Set the note
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
                        ci.getEffectivePrice(),
                        ci.getTotalPrice());
                oi.setProductName(ci.product.getName());
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

    private double getDynamicPrice(Product product) {
        double currentStock = localStockMap.getOrDefault(product.getId(), product.getStock());
        return (currentStock < product.getThreshold()) ? product.getPrice() * 2 : product.getPrice();
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

        double currentPrice = getDynamicPrice(product);
        Label priceLabel = new Label("$" + currentPrice + " per " + (product.isPiece() ? "piece" : "kg"));
        priceLabel.getStyleClass().add("product-price");

        // Check if item is in cart
        CartItem cartItem = findInCart(product);

        if (cartItem == null) {
            Button addToCartBtn = new Button("Add to Cart");
            addToCartBtn.getStyleClass().add("button-primary");
            addToCartBtn.setMaxWidth(Double.MAX_VALUE);
            addToCartBtn.setOnAction(e -> handleAddToCart(product));
            card.getChildren().addAll(nameLabel, priceLabel, addToCartBtn);
        } else {
            card.getChildren().addAll(nameLabel, priceLabel, createQtyBox(cartItem));
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

    @FXML
    private ScrollPane profileView;
    @FXML
    private TextField profileNameField;
    @FXML
    private TextArea profileAddressField;
    @FXML
    private TextField profilePhoneField;
    @FXML
    private ComboBox<String> profileCityCombo;

    // Error Labels
    @FXML
    private Label usernameErrorLabel;
    @FXML
    private Label addressErrorLabel;
    @FXML
    private Label cityErrorLabel;
    @FXML
    private Label phoneErrorLabel;
    @FXML
    private Label passwordErrorLabel;

    // Password Change Fields
    @FXML
    private PasswordField currentPasswordField;
    @FXML
    private PasswordField newPasswordField;
    @FXML
    private PasswordField confirmPasswordField;

    // Snapshot for change detection
    private String originalUsername;
    private String originalAddress;
    private String originalCity;
    private String originalPhone;

    private static final String[] CITIES_OF_TURKEY = {
            "Adana", "Adıyaman", "Afyonkarahisar", "Ağrı", "Amasya", "Ankara", "Antalya", "Artvin", "Aydın",
            "Balıkesir",
            "Bilecik", "Bingöl", "Bitlis", "Bolu", "Burdur", "Bursa", "Çanakkale", "Çankırı", "Çorum", "Denizli",
            "Diyarbakır", "Edirne", "Elazığ", "Erzincan", "Erzurum", "Eskişehir", "Gaziantep", "Giresun", "Gümüşhane",
            "Hakkari",
            "Hatay", "Isparta", "Mersin", "İstanbul", "İzmir", "Kars", "Kastamonu", "Kayseri", "Kırklareli", "Kırşehir",
            "Kocaeli", "Konya", "Kütahya", "Malatya", "Manisa", "Kahramanmaraş", "Mardin", "Muğla", "Muş", "Nevşehir",
            "Niğde", "Ordu", "Rize", "Sakarya", "Samsun", "Siirt", "Sinop", "Sivas", "Tekirdağ", "Tokat",
            "Trabzon", "Tunceli", "Şanlıurfa", "Uşak", "Van", "Yozgat", "Zonguldak", "Aksaray", "Bayburt", "Karaman",
            "Kırıkkale", "Batman", "Şırnak", "Bartın", "Ardahan", "Iğdır", "Yalova", "Karabük", "Kilis", "Osmaniye",
            "Düzce"
    };

    @FXML
    private void handleViewProfile() {
        hideAllViews();
        profileView.setVisible(true);
        loadProfileData();
    }

    @FXML
    private void handleViewOrders() {
        hideAllViews();
        ordersView.setVisible(true);
        refreshOrders();
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
        ordersView.setVisible(false);
        messagesView.setVisible(false);
    }

    @FXML
    private void handleSaveProfile() {
        // Clear previous errors
        clearError(usernameErrorLabel);
        clearError(addressErrorLabel);
        clearError(cityErrorLabel);
        clearError(phoneErrorLabel);

        // 1. Get Values
        String username = profileNameField.getText().trim();
        String address = profileAddressField.getText().trim();
        String city = profileCityCombo.getValue();
        if (city == null)
            city = profileCityCombo.getEditor().getText(); // Handle editable combo
        city = city.trim();
        String phone = profilePhoneField.getText().trim();

        // 2. Validate
        boolean isValid = true;

        // Username Validation
        if (username.isEmpty()) {
            showError(usernameErrorLabel, "Username cannot be empty.");
            isValid = false;
        } else if (username.length() < 3) {
            showError(usernameErrorLabel, "Username must be at least 3 characters.");
            isValid = false;
        } else if (username.contains(" ")) {
            showError(usernameErrorLabel, "Username cannot contain spaces.");
            isValid = false;
        }

        // Address Validation
        if (address.isEmpty() || address.length() < 5) {
            showError(addressErrorLabel, "Please enter a valid address (min 5 chars).");
            isValid = false;
        }

        // City Validation
        if (city.isEmpty()) {
            showError(cityErrorLabel, "Please select or type a city.");
            isValid = false;
        }

        // Phone Validation (10 digits)
        if (!phone.matches("^\\d{10}$")) {
            // Try to be helpful: if they added 0 or +90, strip it?
            // Actually, requirements say: normalize if user starts with +90 or 0
            String normalized = normalizePhone(phone);
            if (normalized != null) {
                phone = normalized;
                profilePhoneField.setText(phone); // Update UI
            } else {
                showError(phoneErrorLabel, "Phone must be 10 digits (e.g. 5321234567).");
                isValid = false;
            }
        }

        if (!isValid)
            return;

        // 3. Check for Changes
        if (username.equals(originalUsername) &&
                address.equals(originalAddress) &&
                city.equals(originalCity) &&
                phone.equals(originalPhone)) {

            showAlert(Alert.AlertType.INFORMATION, "No Changes", "You haven't made any changes to your profile.");
            return;
        }

        // 4. Update DB
        String sql = "UPDATE users SET username = ?, address_line = ?, city = ?, phone = ? WHERE id = ?";

        try (java.sql.Connection conn = com.group25.greengrocer.util.DbAdapter.getConnection();
                java.sql.PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, address);
            stmt.setString(3, city);
            stmt.setString(4, phone);
            stmt.setLong(5, customerId);

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                // Update snapshot
                originalUsername = username;
                originalAddress = address;
                originalCity = city;
                originalPhone = phone;

                // Update Welcome Text if username changed
                welcomeText.setText("Welcome, " + username);

                showAlert(Alert.AlertType.INFORMATION, "Success", "Profile updated successfully!");
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Could not update profile.");
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            if (e.getMessage().contains("Duplicate")) {
                showError(usernameErrorLabel, "Username already taken.");
            } else {
                showAlert(Alert.AlertType.ERROR, "Database Error", "Update failed: " + e.getMessage());
            }
        }
    }

    private String normalizePhone(String input) {
        // Strip non-digits
        String digits = input.replaceAll("\\D", "");
        if (digits.length() == 10)
            return digits;
        if (digits.length() == 11 && digits.startsWith("0"))
            return digits.substring(1);
        if (digits.length() == 12 && digits.startsWith("90"))
            return digits.substring(2);
        return null; // Invalid
    }

    @FXML
    private void handleChangePassword() {
        clearError(passwordErrorLabel);

        String current = currentPasswordField.getText();
        String newVal = newPasswordField.getText();
        String confirm = confirmPasswordField.getText();

        if (current.isEmpty() || newVal.isEmpty() || confirm.isEmpty()) {
            showError(passwordErrorLabel, "All password fields are required.");
            return;
        }

        if (!newVal.equals(confirm)) {
            showError(passwordErrorLabel, "New passwords do not match.");
            return;
        }

        if (newVal.length() < 4) {
            showError(passwordErrorLabel, "New password must be at least 4 characters.");
            return;
        }

        // 1. Verify Current Password (Plaintext check as per current system)
        String verifySql = "SELECT id FROM users WHERE id = ? AND password_hash = ?";
        try (java.sql.Connection conn = com.group25.greengrocer.util.DbAdapter.getConnection();
                java.sql.PreparedStatement verifyStmt = conn.prepareStatement(verifySql)) {

            verifyStmt.setLong(1, customerId);
            verifyStmt.setString(2, current);

            try (java.sql.ResultSet rs = verifyStmt.executeQuery()) {
                if (!rs.next()) {
                    showError(passwordErrorLabel, "Incorrect current password.");
                    return;
                }
            }

            // 2. Update Password
            String updateSql = "UPDATE users SET password_hash = ? WHERE id = ?";
            try (java.sql.PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                updateStmt.setString(1, newVal);
                updateStmt.setLong(2, customerId);

                int rows = updateStmt.executeUpdate();
                if (rows > 0) {
                    currentPasswordField.clear();
                    newPasswordField.clear();
                    confirmPasswordField.clear();
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Password changed successfully!");
                } else {
                    showError(passwordErrorLabel, "Failed to update password.");
                }
            }

        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            showError(passwordErrorLabel, "Database error: " + e.getMessage());
        }
    }

    private void loadProfileData() {
        // Initialize Combo if empty
        if (profileCityCombo.getItems().isEmpty()) {
            profileCityCombo.getItems().addAll(CITIES_OF_TURKEY);
        }

        // Reset errors
        clearError(usernameErrorLabel);
        clearError(addressErrorLabel);
        clearError(cityErrorLabel);
        clearError(phoneErrorLabel);
        clearError(passwordErrorLabel);

        // Populate Data
        profileNameField.setText(welcomeText.getText().replace("Welcome, ", ""));

        String sql = "SELECT username, address_line, city, phone FROM users WHERE id = ?";
        try (java.sql.Connection conn = com.group25.greengrocer.util.DbAdapter.getConnection();
                java.sql.PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, customerId);
            try (java.sql.ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Fetch and set
                    String user = rs.getString("username");
                    String addr = rs.getString("address_line");
                    String city = rs.getString("city");
                    String phone = rs.getString("phone");

                    if (user == null)
                        user = "";
                    if (addr == null)
                        addr = "";
                    if (city == null)
                        city = "";
                    if (phone == null)
                        phone = "";

                    profileNameField.setText(user);
                    profileAddressField.setText(addr);
                    profileCityCombo.setValue(city);
                    profilePhoneField.setText(phone);

                    // Allow editable checks if city not in list
                    if (!profileCityCombo.getItems().contains(city)) {
                        profileCityCombo.getEditor().setText(city);
                    }

                    // Save snapshot
                    originalUsername = user;
                    originalAddress = addr;
                    originalCity = city;
                    originalPhone = phone;
                }
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }

        // Add listener for Phone Validation (Typing)
        // Remove old listener if exists to avoid duplicates? Actually initialize() is
        // better but here ensures it's attached to the field.
        // We'll trust JavaFX to handle it or just set it once in initialize if
        // possible.
        // But profilePhoneField is injected.
        // Let's protect against multi-adding by setting OnKeyTyped directly instead of
        // addListener if possible, or just add a simple TextFormatter

        profilePhoneField.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.matches("[0-9]*")) {
                return change;
            }
            return null; // Reject non-digit
        }));
    }

    private void showError(Label label, String message) {
        if (label != null) {
            label.setText(message);
            label.setVisible(true);
            label.setManaged(true);
        }
    }

    private void clearError(Label label) {
        if (label != null) {
            label.setText("");
            label.setVisible(false);
            label.setManaged(false);
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
                refreshOrders();
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

    private void styleAlert(Dialog<?> alert) {
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

    private final OrderDao orderDao = new OrderDao();
    private final MessageDao messageDao = new MessageDao();

    @FXML
    private void handleRefreshOrders() {
        refreshOrders();
    }

    private void refreshOrders() {
        try {
            System.out.println("Refreshing orders for customer ID: " + customerId);
            List<Order> orders = orderDao.findByCustomerId(customerId);
            System.out.println("Found " + orders.size() + " orders.");
            if (!orders.isEmpty()) {
                Order first = orders.get(0);
                System.out.println("First Order - ID: " + first.getId() + ", Total: " + first.getTotal() + ", Status: "
                        + first.getStatus());
            }
            ordersTable.setItems(FXCollections.observableArrayList(orders));
            ordersTable.refresh();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load orders: " + e.getMessage());
        }
    }

    private void setupOrderTable() {
        colOrderId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colOrderDate.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getOrderTime().toLocalDate().toString()));
        colOrderTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
        colOrderStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Action Column: Download Invoice & Cancel
        colOrderAction.setCellFactory(param -> new TableCell<>() {
            private final javafx.scene.control.Button btnInvoice = new javafx.scene.control.Button("Invoice");
            private final javafx.scene.control.Button btnCancel = new javafx.scene.control.Button("Cancel");
            private final javafx.scene.layout.HBox pane = new javafx.scene.layout.HBox(5, btnInvoice, btnCancel);

            {
                btnInvoice.getStyleClass().add("button-secondary");
                btnInvoice.setOnAction(event -> {
                    Order order = getTableView().getItems().get(getIndex());
                    handleDownloadInvoice(order);
                });

                btnCancel.getStyleClass().add("button-danger");
                btnCancel.setOnAction(event -> {
                    Order order = getTableView().getItems().get(getIndex());
                    handleCancelOrder(order.getId());
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Order order = getTableView().getItems().get(getIndex());
                    pane.getChildren().clear();
                    pane.getChildren().add(btnInvoice);

                    if (order.getStatus() == com.group25.greengrocer.model.OrderStatus.PLACED) {
                        pane.getChildren().add(btnCancel);
                    }
                    setGraphic(pane);
                }
            }
        });
    }

    private void handleDownloadInvoice(Order order) {
        try {
            byte[] pdfData = orderDao.getInvoice(order.getId());
            if (pdfData == null || pdfData.length == 0) {
                showAlert(Alert.AlertType.WARNING, "Unavailable", "No invoice found for this order.");
                return;
            }

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Invoice");
            fileChooser.setInitialFileName("invoice_" + order.getId() + ".pdf");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
            File file = fileChooser.showSaveDialog(ordersView.getScene().getWindow());

            if (file != null) {
                try (FileOutputStream fos = new FileOutputStream(file)) {
                    fos.write(pdfData);
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Invoice saved successfully.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to download invoice.");
        }
    }

    // --- Messages Logic ---

    @FXML
    private void handleViewMessages() {
        hideAllViews();
        messagesView.setVisible(true);
        handleRefreshMessages();
    }

    @FXML
    private void handleRefreshMessages() {
        try {
            List<Message> messages = messageDao.getMessagesForUser(customerId); // Assuming this gets sent & received
            messagesTable.setItems(FXCollections.observableArrayList(messages));
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Could not load messages.");
        }
    }

    private void setupMessageTable() {
        colMsgSender.setCellValueFactory(new PropertyValueFactory<>("senderName"));
        colMsgSubject.setCellValueFactory(new PropertyValueFactory<>("subject"));
        colMsgContent.setCellValueFactory(new PropertyValueFactory<>("content"));
        colMsgDate.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getSentTime() != null ? cellData.getValue().getSentTime().toString() : ""));
    }

    @FXML
    private void handleSendMessage() {
        String subject = txtMsgSubject.getText();
        String content = txtMsgContent.getText();

        if (subject == null || subject.trim().isEmpty() || content == null || content.trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Subject and Content cannot be empty.");
            return;
        }

        Integer ownerId = userDao.getPrimaryOwnerId();
        if (ownerId == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "No store owner found to receive message.");
            return;
        }

        try {
            messageDao.sendMessage(customerId, ownerId, subject, content);
            showAlert(Alert.AlertType.INFORMATION, "Success", "Message sent to store owner.");
            txtMsgSubject.clear();
            txtMsgContent.clear();
            handleRefreshMessages();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to send message.");
        }
    }
}
