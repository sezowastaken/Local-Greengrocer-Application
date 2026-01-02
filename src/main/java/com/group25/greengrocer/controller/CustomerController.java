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
import javafx.scene.layout.StackPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;

import javafx.scene.control.Dialog;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Slider;
import javafx.scene.layout.GridPane;

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

import javafx.collections.ObservableList;
import com.group25.greengrocer.dao.UserDao;
import com.group25.greengrocer.service.LoyaltyService;
import com.group25.greengrocer.model.Coupon;
import com.group25.greengrocer.dao.CouponDao;

/**
 * Controller for the customer dashboard screen.
 * 
 * This controller manages the main customer interface, including:
 * - Product browsing (vegetables and fruits with category toggle)
 * - Shopping cart management (add, remove, update quantities)
 * - Order placement and history viewing
 * - Coupon management and application
 * - Product rating and reviews
 * - Messaging with the owner
 * - Profile viewing and management
 * - Loyalty discount tracking
 * 
 * The dashboard features:
 * - Animated UI elements with hover effects
 * - Category-based product filtering
 * - Real-time cart updates
 * - Order tracking with status updates
 * - Invoice download functionality
 * - Profile image upload and display
 * 
 * @see ProductDao
 * @see OrderDao
 * @see CouponDao
 * @see MessageDao
 * @see UserDao
 */
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

    /**
     * Initializes the customer dashboard.
     * 
     * Sets up the UI components including:
     * - Category toggle buttons (vegetables and fruits) with SVG icons
     * - Product tables and message tables
     * - Hover animations for interactive elements
     * - Category switching listeners
     * - Initial product card display
     */
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
        setupOrderTable();
        setupMessageListView();

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

    @FXML
    private BorderPane paymentView;
    @FXML
    private VBox paymentItemsContainer;
    @FXML
    private TextArea paymentNoteArea;
    @FXML
    private Label paymentTotalLabel;

    @FXML
    private Label paymentSubtotalLabel;
    @FXML
    private Label paymentLoyaltyLabel;
    @FXML
    private Label paymentCouponLabel;
    @FXML
    private TextField couponCodeField;
    @FXML
    private Label couponMessageLabel;

    private Coupon appliedCoupon = null;
    private final CouponDao couponDao = new CouponDao();

    // --- Order History Views ---

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
            return product.getDynamicPrice() * quantity;
        }
    }

    private long customerId;
    private String customerUsername;

    /**
     * Sets the customer session information.
     * 
     * Called by LoginController after successful authentication to initialize
     * the customer's session data and load their profile information.
     * 
     * @param id The customer's user ID
     * @param name The customer's username
     */
    public void setCustomerSession(long id, String name) {
        this.customerId = id;
        this.customerUsername = name;
        welcomeText.setText("Welcome, " + name);
    }

    @FXML
    /**
     * Handles the "Proceed to Payment" action.
     * 
     * Validates that the cart is not empty, calculates the final price including
     * discounts and loyalty rates, and displays the payment confirmation dialog.
     */
    private void handleProceedToPayment() {
        if (cart.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Cart is Empty", "Please add items to your cart before checking out.");
            return;
        }

        hideAllViews();
        paymentView.setVisible(true);

        // Reset Coupon
        appliedCoupon = null;
        couponCodeField.clear();
        couponMessageLabel.setText("");

        // Populate Payment Items List (Left Column)
        paymentItemsContainer.getChildren().clear();
        for (CartItem item : cart) {
            HBox row = new HBox(10);
            row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
            row.setStyle("-fx-padding: 5; -fx-border-color: #eee; -fx-border-width: 0 0 1 0;");

            Label name = new Label(item.product.getName());
            name.setStyle("-fx-font-weight: bold;");
            name.setWrapText(true);
            name.setPrefWidth(Region.USE_COMPUTED_SIZE);

            // Name should grow to fill space
            HBox.setHgrow(name, javafx.scene.layout.Priority.ALWAYS);

            Label qty = new Label(String.format("x %.2f %s", item.quantity, item.product.isPiece() ? "qty" : "kg"));
            qty.setMinWidth(80); // Ensure min width for alignment
            qty.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
            qty.setStyle("-fx-text-fill: #666;");

            Label price = new Label(String.format("$%.2f", item.getTotalPrice()));
            price.setMinWidth(70); // Fixed width for price alignment
            price.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
            price.setStyle("-fx-font-weight: bold; -fx-text-fill: #2e7d32;");

            // Removed spacer, now Name grows. Structure: [Name (grows)] [Qty] [Price]
            row.getChildren().addAll(name, qty, price);
            paymentItemsContainer.getChildren().add(row);
        }

        recalculatePaymentTotal();
    }

    private void recalculatePaymentTotal() {
        double subtotal = 0;
        for (CartItem ci : cart)
            subtotal += ci.getTotalPrice();

        // Loyalty
        java.math.BigDecimal individualRate = userDao.getIndividualLoyaltyRate(customerId);
        double discountRate = (individualRate != null) ? individualRate.doubleValue()
                : loyaltyService.getLoyaltyDiscountRate();
        double loyaltyDiscount = subtotal * discountRate;

        // Coupon
        double couponDiscount = 0;
        if (appliedCoupon != null) {
            if ("PERCENTAGE".equalsIgnoreCase(appliedCoupon.getDiscountType())) {
                couponDiscount = subtotal * (appliedCoupon.getDiscountValue() / 100.0);
            } else {
                couponDiscount = appliedCoupon.getDiscountValue();
            }
        }

        double total = subtotal - loyaltyDiscount - couponDiscount;
        if (total < 0)
            total = 0;

        // Update Labels
        paymentSubtotalLabel.setText(String.format("$%.2f", subtotal));
        paymentLoyaltyLabel.setText(String.format("-$%.2f", loyaltyDiscount));
        paymentCouponLabel.setText(String.format("-$%.2f", couponDiscount));
        paymentTotalLabel.setText(String.format("$%.2f", total));
    }

    @FXML
    /**
     * Handles coupon code application.
     * 
     * Validates the coupon code, checks if it's already used by the customer,
     * and applies the discount if valid. Updates the cart total accordingly.
     */
    private void handleApplyCoupon() {
        String code = couponCodeField.getText().trim();
        if (code.isEmpty())
            return;

        try {
            Coupon coupon = couponDao.findByCode(code);
            if (coupon == null) {
                couponMessageLabel.setText("Invalid coupon code.");
                couponMessageLabel.setStyle("-fx-text-fill: red;");
                appliedCoupon = null;
            } else {
                double subtotal = 0;
                for (CartItem ci : cart)
                    subtotal += ci.getTotalPrice();

                if (subtotal < coupon.getMinOrderTotal()) {
                    couponMessageLabel.setText("Min order total: $" + coupon.getMinOrderTotal());
                    couponMessageLabel.setStyle("-fx-text-fill: red;");
                    appliedCoupon = null;
                } else {
                    appliedCoupon = coupon;
                    couponMessageLabel.setText("Coupon applied!");
                    couponMessageLabel.setStyle("-fx-text-fill: green;");
                }
            }
            recalculatePaymentTotal();
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            couponMessageLabel.setText("Error checking coupon.");
        }
    }

    @FXML
    /**
     * Handles order confirmation and placement.
     * 
     * Creates a new order in the database with all cart items, applies discounts,
     * generates an invoice, updates product stock, and clears the cart.
     * Uses OrderService to ensure transactional integrity.
     */
    private void handleConfirmPayment() {
        if (customerId == 0) {
            showAlert(Alert.AlertType.ERROR, "Session Error", "Please logout and login again.");
            return;
        }

        try {
            // Get Note from Payment Screen
            String orderNote = paymentNoteArea.getText();

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
            double discountRate = (individualRate != null) ? individualRate.doubleValue()
                    : loyaltyService.getLoyaltyDiscountRate();
            double loyaltyDiscount = subtotal * discountRate;

            // Coupon Logic
            double couponDiscount = 0;
            if (appliedCoupon != null) {
                if ("PERCENTAGE".equalsIgnoreCase(appliedCoupon.getDiscountType())) {
                    couponDiscount = subtotal * (appliedCoupon.getDiscountValue() / 100.0);
                } else {
                    couponDiscount = appliedCoupon.getDiscountValue();
                }
                order.setAppliedCouponId(appliedCoupon.getId());
            }

            double totalDiscount = loyaltyDiscount + couponDiscount;
            double discountedSubtotal = subtotal - totalDiscount;
            if (discountedSubtotal < 0)
                discountedSubtotal = 0;

            order.setSubtotal(subtotal);
            order.setLoyaltyDiscountRate(discountRate); // Save rate
            order.setDiscountTotal(totalDiscount);

            order.setVatRate(0.18); // Default 18%
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
                        ci.product.getDynamicPrice(),
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

    private void updateProductCardUI(VBox card, Product product) {
        // Store ID for reference (optional but good practice)
        card.setUserData(product.getId());

        // Clear children to rebuild content (this does NOT trigger mouse exit on the
        // card itself)
        card.getChildren().clear();

        StackPane imageContainer = new StackPane();
        imageContainer.setPrefSize(100, 100);
        imageContainer.setMinSize(100, 100);
        imageContainer.setMaxSize(100, 100);
        imageContainer.getStyleClass().add("product-image-container");

        if (product.getProductImage() != null) {
            ImageView imgView = new ImageView(product.getProductImage());
            imgView.setFitWidth(100);
            imgView.setFitHeight(100);
            imgView.setPreserveRatio(true);
            imgView.getStyleClass().add("product-image");
            imageContainer.getChildren().add(imgView);
        }
        card.getChildren().add(imageContainer);

        Label nameLabel = new Label(product.getName());
        nameLabel.getStyleClass().add("product-name");

        Label priceLabel = new Label("$" + String.format("%.2f", product.getDynamicPrice()) + " per "
                + (product.isPiece() ? "piece" : "kg"));
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

    /**
     * Adds a product to the shopping cart.
     * 
     * Checks stock availability, updates cart quantities, and refreshes the cart display.
     * Handles both piece-based and weight-based products with appropriate quantity steps.
     * 
     * @param product The product to add to the cart
     */
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
    /**
     * Navigates to the cart view.
     * 
     * Hides all other views and displays the shopping cart with current items.
     */
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

            StackPane cartImageContainer = new StackPane();
            cartImageContainer.setPrefSize(50, 50);
            cartImageContainer.setMinSize(50, 50);
            cartImageContainer.setMaxSize(50, 50);
            cartImageContainer.setAlignment(javafx.geometry.Pos.CENTER);

            if (item.product.getProductImage() != null) {
                ImageView imgView = new ImageView(item.product.getProductImage());
                imgView.setFitWidth(50);
                imgView.setFitHeight(50);
                imgView.setPreserveRatio(true);
                imgView.getStyleClass().add("cart-image");
                cartImageContainer.getChildren().add(imgView);
            }
            row.getChildren().add(cartImageContainer);

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
    /**
     * Displays the customer profile in a dialog.
     * 
     * Loads the profile.fxml and passes the customer ID to the ProfileController
     * for displaying and editing profile information.
     */
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
    /**
     * Handles user logout.
     * 
     * Clears the current session and navigates back to the login screen.
     */
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
        paymentView.setVisible(false);
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
    private ListView<com.group25.greengrocer.model.Message> messagesListView;
    // Removed TableColumns as we are now using ListView

    @FXML
    private TextField txtMsgSubject;
    @FXML
    private TextArea txtMsgContent;

    private final OrderDao orderDao = new OrderDao();
    private final MessageDao messageDao = new MessageDao();
    private final com.group25.greengrocer.dao.CarrierRatingDao carrierRatingDao = new com.group25.greengrocer.dao.CarrierRatingDao();

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
            private final javafx.scene.control.Button btnRate = new javafx.scene.control.Button("Rate"); // New Rate
                                                                                                         // Button
            private final javafx.scene.layout.HBox pane = new javafx.scene.layout.HBox(5, btnInvoice, btnCancel,
                    btnRate);

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

                btnRate.getStyleClass().add("button-primary");
                btnRate.setOnAction(event -> {
                    Order order = getTableView().getItems().get(getIndex());
                    handleRateOrder(order);
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

                    // Rating Logic
                    if (order.getStatus() == com.group25.greengrocer.model.OrderStatus.DELIVERED
                            && !carrierRatingDao.hasRated(order.getId())) {
                        pane.getChildren().add(btnRate);
                    }

                    setGraphic(pane);
                }
            }
        });
    }

    private void handleRateOrder(Order order) {
        // Create custom dialog
        Dialog<com.group25.greengrocer.model.CarrierRating> dialog = new Dialog<>();
        dialog.setTitle("Rate Order #" + order.getId());
        dialog.setHeaderText("Rate your delivery experience");

        ButtonType submitButtonType = new ButtonType("Submit", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(submitButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

        Slider ratingSlider = new Slider(1, 5, 5);
        ratingSlider.setMajorTickUnit(1);
        ratingSlider.setMinorTickCount(0);
        ratingSlider.setShowTickLabels(true);
        ratingSlider.setSnapToTicks(true);

        grid.add(new Label("Rating (1-5):"), 0, 0);
        grid.add(ratingSlider, 1, 0);

        dialog.getDialogPane().setContent(grid);

        // Convert result
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == submitButtonType) {
                int rating = (int) ratingSlider.getValue();
                // carrierId might be null checking needed? Order status is DELIVERED so
                // carrierId SHOULD be set.
                int carrierId = (order.getCarrierId() != null) ? order.getCarrierId().intValue() : 0;

                return new com.group25.greengrocer.model.CarrierRating(0, (int) order.getId(), (int) customerId,
                        carrierId, rating, null, null);
            }
            return null;
        });

        java.util.Optional<com.group25.greengrocer.model.CarrierRating> result = dialog.showAndWait();

        result.ifPresent(rating -> {
            try {
                if (rating.getCarrierId() == 0) {
                    showAlert(Alert.AlertType.ERROR, "Error",
                            "Cannot rate: No carrier linked to this delivered order (Data Error).");
                    return;
                }
                carrierRatingDao.addRating(rating);
                showAlert(Alert.AlertType.INFORMATION, "Success", "Thank you for your feedback!");
                refreshOrders(); // Refresh to hide the button
            } catch (Exception e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to submit rating: " + e.getMessage());
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

            // Create a temporary file
            File tempFile = File.createTempFile("invoice_" + order.getId() + "_", ".pdf");
            tempFile.deleteOnExit(); // File will be deleted when the VM exits (optional, maybe user wants to keep it
                                     // open)

            try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                fos.write(pdfData);
            }

            // Open the file
            if (java.awt.Desktop.isDesktopSupported()) {
                java.awt.Desktop.getDesktop().open(tempFile);
            } else {
                showAlert(Alert.AlertType.WARNING, "Not Supported", "Opening files is not supported on this platform.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to open invoice: " + e.getMessage());
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
            List<Message> messages = messageDao.getMessagesForUser(customerId);
            messagesListView.setItems(FXCollections.observableArrayList(messages));
            messagesListView.scrollTo(messages.size() - 1);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Could not load messages.");
        }
    }

    private void setupMessageListView() {
        messagesListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Message message, boolean empty) {
                super.updateItem(message, empty);
                if (empty || message == null) {
                    setGraphic(null);
                    setText(null);
                    setStyle("-fx-background-color: transparent;");
                } else {
                    // Create Bubble
                    VBox bubble = new VBox(5);
                    bubble.getStyleClass().add("chat-bubble");

                    Text content = new Text(message.getContent());
                    content.getStyleClass().add("text");
                    content.wrappingWidthProperty().bind(messagesListView.widthProperty().multiply(0.6)); // Max width
                                                                                                          // 60%

                    Label date = new Label(message.getSentTime().toString());
                    date.getStyleClass().add("date-label");

                    bubble.getChildren().addAll(content, date);

                    // Container for alignment
                    HBox container = new HBox();
                    container.setFillHeight(false); // Do not stretch bubble vertically

                    // Determine sender (Me vs Them)
                    if (message.getSenderId() == customerId) {
                        // Me (Right)
                        bubble.getStyleClass().add("chat-bubble-me");
                        container.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
                        container.getChildren().add(bubble);
                    } else {
                        // Them (Left)
                        bubble.getStyleClass().add("chat-bubble-them");
                        container.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
                        // Add sender name for them
                        Label senderName = new Label(message.getSenderName());
                        senderName.setStyle("-fx-font-size: 10px; -fx-text-fill: #555; -fx-padding: 0 0 2 5;");

                        VBox messangeWithHeader = new VBox(2, senderName, bubble);
                        container.getChildren().add(messangeWithHeader);
                    }

                    setGraphic(container);
                    setText(null);
                    setStyle("-fx-background-color: transparent;");
                }
            }
        });
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
