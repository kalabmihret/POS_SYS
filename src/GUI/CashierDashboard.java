package GUI;

import Model.Product;
import Model.Sale;
import Service.ProductServiceInterface;
import Service.SaleServiceInterface;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.util.HashMap;
import java.util.Map;

public class CashierDashboard {

    private Main mainApp;
    private ProductServiceInterface productService;
    private SaleServiceInterface saleService;
    private Sale currentSale;
    private Map<Product, Integer> cartItems;
    private ObservableList<String> cartObservableList;

    public CashierDashboard(Main mainApp, ProductServiceInterface productService, SaleServiceInterface saleService) {
        this.mainApp = mainApp;
        this.productService = productService;
        this.saleService = saleService;
        this.cartItems = new HashMap<>();
        this.cartObservableList = FXCollections.observableArrayList();
    }

    public Scene getScene() {
        // Main layout
        BorderPane mainLayout = new BorderPane();
        mainLayout.setStyle("-fx-background-color: #f5f5f5;");

        // Top header
        HBox header = new HBox(20);
        header.setPadding(new Insets(20));
        header.setStyle("-fx-background-color: #27ae60;");
        header.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Cashier Dashboard");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        title.setTextFill(Color.WHITE);

        Button newSaleBtn = new Button("Clear Sale");
        Button logoutBtn = new Button("Logout");

        newSaleBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
        logoutBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");

        HBox headerRight = new HBox(10);
        headerRight.setAlignment(Pos.CENTER_RIGHT);
        headerRight.getChildren().addAll(newSaleBtn, logoutBtn);

        HBox.setHgrow(headerRight, Priority.ALWAYS);
        header.getChildren().addAll(title, headerRight);

        // Main content (split left-right)
        SplitPane contentPane = new SplitPane();

        // Left panel - Products
        VBox productsPanel = new VBox(10);
        productsPanel.setPadding(new Insets(15));
        productsPanel.setStyle("-fx-background-color: white;");

        Label productsLabel = new Label("Available Products");
        productsLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        TextField searchField = new TextField();
        searchField.setPromptText("Search products...");

        ListView<Product> productsList = new ListView<>();
        productsList.setPrefHeight(400);

        // Load products
        refreshProductsList(productsList, "");

        // Register to receive updates when products change
        productService.addChangeListener(() -> {
            // Run on JavaFX thread
            javafx.application.Platform.runLater(() -> refreshProductsList(productsList, searchField.getText()));
        });

        // Search functionality
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            refreshProductsList(productsList, newValue);
        });

        productsPanel.getChildren().addAll(productsLabel, searchField, productsList);

        // Right panel - Cart
        VBox cartPanel = new VBox(10);
        cartPanel.setPadding(new Insets(15));
        cartPanel.setStyle("-fx-background-color: white;");

        Label cartLabel = new Label("Shopping Cart");
        cartLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        ListView<String> cartList = new ListView<>();
        cartList.setItems(cartObservableList);
        cartList.setPrefHeight(300);

        Label totalLabel = new Label("Total: $0.00");
        totalLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        totalLabel.setTextFill(Color.GREEN);

        HBox quantityPanel = new HBox(10);
        Label quantityLabel = new Label("Quantity:");
        TextField quantityField = new TextField("1");

        quantityField.setPrefWidth(80);
        quantityPanel.getChildren().addAll(quantityLabel, quantityField);

        HBox cartButtons = new HBox(10);
        Button addToCartBtn = new Button("Add to Cart");
        Button removeFromCartBtn = new Button("Remove");
        Button checkoutBtn = new Button("Checkout");

        addToCartBtn.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white;");
        removeFromCartBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
        checkoutBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");

        cartButtons.getChildren().addAll(addToCartBtn, removeFromCartBtn, checkoutBtn);

        cartPanel.getChildren().addAll(cartLabel, cartList, totalLabel, quantityPanel, cartButtons);

        // Add panels to split pane
        contentPane.getItems().addAll(productsPanel, cartPanel);
        contentPane.setDividerPositions(0.6);

        // Assemble main layout
        mainLayout.setTop(header);
        mainLayout.setCenter(contentPane);

        // Button actions
        newSaleBtn.setOnAction(e -> startNewSale(cartList, totalLabel));

        addToCartBtn.setOnAction(e -> {
            Product selectedProduct = productsList.getSelectionModel().getSelectedItem();
            if (selectedProduct != null) {
                try {
                    int quantity = Integer.parseInt(quantityField.getText());
                    if (quantity > 0 && quantity <= selectedProduct.getQuantity()) {
                        addToCart(selectedProduct, quantity);
                        updateCartDisplay(cartList, totalLabel);
                        quantityField.setText("1");
                    } else {
                        showAlert("Invalid Quantity", "Quantity exceeds available stock!");
                    }
                } catch (NumberFormatException ex) {
                    showAlert("Invalid Input", "Please enter a valid number!");
                }
            }
        });

        removeFromCartBtn.setOnAction(e -> {
            int selectedIndex = cartList.getSelectionModel().getSelectedIndex();
            if (selectedIndex >= 0) {
                removeFromCart(selectedIndex);
                updateCartDisplay(cartList, totalLabel);
            }
        });

        checkoutBtn.setOnAction(e -> processCheckout(cartList, totalLabel));

        logoutBtn.setOnAction(e -> mainApp.showLogin());

        // Initialize new sale
        startNewSale(cartList, totalLabel);

        return new Scene(mainLayout, 1000, 700);
    }

    private void startNewSale(ListView<String> cartList, Label totalLabel) {
        currentSale = saleService.createNewSale("CASHIER001");// you see
        cartItems.clear();
        cartObservableList.clear();
        cartList.refresh();
        totalLabel.setText("Total: $0.00");
    }

    private void refreshProductsList(ListView<Product> listView, String searchTerm) {
        listView.getItems().clear();

        if (searchTerm.isEmpty()) {
            listView.getItems().addAll(productService.getAllProducts());
        } else {
            listView.getItems().addAll(productService.searchProducts(searchTerm));
        }

        listView.setCellFactory(lv -> new ListCell<Product>() {
            @Override
            protected void updateItem(Product item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("%s - %s ($%.2f) [%d in stock]",
                            item.getId(), item.getName(), item.getPrice(), item.getQuantity()));
                }
            }
        });
    }

    private void addToCart(Product product, int quantity) {
        cartItems.put(product, cartItems.getOrDefault(product, 0) + quantity);
    }

    private void removeFromCart(int index) {
        int i = 0;
        Product toRemove = null;

        for (Map.Entry<Product, Integer> entry : cartItems.entrySet()) {
            if (i == index) {
                toRemove = entry.getKey();
                break;
            }
            i++;
        }
        if (toRemove != null) {
            cartItems.remove(toRemove);
        }
    }

    private void updateCartDisplay(ListView<String> cartList, Label totalLabel) {
        cartObservableList.clear();
        double total = 0;

        for (Map.Entry<Product, Integer> entry : cartItems.entrySet()) {
            Product product = entry.getKey();
            int quantity = entry.getValue();
            double subtotal = product.getPrice() * quantity;
            total += subtotal;

            cartObservableList.add(String.format("%s x%d @ $%.2f = $%.2f",
                    product.getName(), quantity, product.getPrice(), subtotal));
        }

        cartList.refresh();
        totalLabel.setText(String.format("Total: $%.2f", total));
    }

    private void processCheckout(ListView<String> cartList, Label totalLabel) {
        if (cartItems.isEmpty()) {
            showAlert("Empty Cart", "Add items to cart before checkout!");
            return;
        }

        // Add items to sale
        for (Map.Entry<Product, Integer> entry : cartItems.entrySet()) {
            currentSale.addProduct(entry.getKey(), entry.getValue());
        }

        // Save sale
        saleService.saveSale(currentSale);

        // Show receipt
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Checkout Complete");
        alert.setHeaderText("Sale Processed Successfully!");

        StringBuilder receipt = new StringBuilder();
        receipt.append("=== RECEIPT ===\n");
        receipt.append("Sale ID: ").append(currentSale.getSaleId()).append("\n");
        receipt.append("Date: ").append(currentSale.getDateTime()).append("\n\n");

        double total = 0;
        for (Map.Entry<Product, Integer> entry : cartItems.entrySet()) {
            Product p = entry.getKey();
            int qty = entry.getValue();
            double subtotal = p.getPrice() * qty;
            total += subtotal;
            receipt.append(String.format("%s x%d @ $%.2f = $%.2f\n",
                    p.getName(), qty, p.getPrice(), subtotal));
        }

        receipt.append("\nTotal: $").append(String.format("%.2f", total)).append("\n");
        receipt.append("Thank you!");

        TextArea receiptArea = new TextArea(receipt.toString());
        receiptArea.setEditable(false);
        receiptArea.setPrefSize(300, 200);

        alert.getDialogPane().setContent(receiptArea);
        alert.showAndWait();

        // Start new sale
        startNewSale(cartList, totalLabel);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}