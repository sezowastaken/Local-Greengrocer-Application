package com.group25.greengrocer.controller;

import com.group25.greengrocer.dao.ProductDao;
import com.group25.greengrocer.model.Product;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.io.IOException;
import java.util.List;

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

        Button plusBtn = new Button("+");
        plusBtn.getStyleClass().add("qty-btn");
        plusBtn.setOnAction(e -> handleIncrement(item));

        qtyBox.getChildren().addAll(minusBtn, qtyLabel, plusBtn);
        return qtyBox;
    }

    private CartItem findInCart(Product product) {
        for (CartItem item : cart) {
            if (item.product.getId() == product.getId()) {
                return item;
            }
        }
        return null;
    }

    private void handleAddToCart(Product product) {
        if (product.getStock() < 1) { // Basic check, better logic needed for strict stock
            showAlert(Alert.AlertType.ERROR, "Stock Error", "Out of stock!");
            return;
        }
        cart.add(new CartItem(product, 1.0));
        refreshProductCards();
    }

    private void handleIncrement(CartItem item) {
        double step = item.product.isPiece() ? 1.0 : 0.25;
        double newQty = item.quantity + step;

        if (newQty > item.product.getStock()) {
            showAlert(Alert.AlertType.ERROR, "Stock Error", "Not enough stock available.");
            return;
        }

        item.quantity = newQty;
        refreshProductCards();
        if (cartView.isVisible()) {
            updateCartView();
        }
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
        cartView.setVisible(true);
        updateCartView();
    }

    @FXML
    private void handleBackToShop() {
        cartView.setVisible(false);
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
}
