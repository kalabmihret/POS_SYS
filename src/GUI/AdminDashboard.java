package GUI;

import Model.Product;
import Model.Sale;
import Service.ProductServiceInterface;
import Service.SaleServiceInterface;
import Service.AuthServiceInterface;
import Model.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.Node;
import java.util.List;

public class AdminDashboard {

    private Main mainApp;
    private AuthServiceInterface authService;
    private ProductServiceInterface productService;
    private SaleServiceInterface saleService;

    public AdminDashboard(Main mainApp, AuthServiceInterface authService, ProductServiceInterface productService, SaleServiceInterface saleService) {
        this.mainApp = mainApp;
        this.authService = authService;
        this.productService = productService;
        this.saleService = saleService;
    }

    public Scene getScene() {
        // Main layout
        BorderPane mainLayout = new BorderPane();
        mainLayout.setStyle("-fx-background-color: #f5f5f5;");

        // Top header
        HBox header = new HBox(20);
        header.setPadding(new Insets(20));
        header.setStyle("-fx-background-color: #2c3e50;");
        header.setAlignment(Pos.CENTER_LEFT);
        // title part
        Label title = new Label("Admin Dashboard");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        title.setTextFill(Color.WHITE);
        // logout button
        Button logoutBtn = new Button("Logout");
        logoutBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");

        HBox headerRight = new HBox(10);
        headerRight.setAlignment(Pos.CENTER_RIGHT);
        headerRight.getChildren().add(logoutBtn);

        HBox.setHgrow(headerRight, Priority.ALWAYS);
        header.getChildren().addAll(title, headerRight);

        // Sidebar
        VBox sidebar = new VBox(10);
        sidebar.setPadding(new Insets(20));
        sidebar.setStyle("-fx-background-color: #34495e;");
        sidebar.setPrefWidth(200);

        Button productsBtn = new Button("Manage Products");
        Button salesBtn = new Button("View Sales");
        Button reportsBtn = new Button("Reports");
        Button usersBtn = new Button("Manage Users");

        // Style sidebar buttons
        for (Button btn : new Button[]{productsBtn, salesBtn, reportsBtn,usersBtn}) {
            btn.setPrefWidth(160);
            btn.setPrefHeight(40);
            btn.setStyle("-fx-background-color: #1abc9c; -fx-text-fill: white; -fx-font-size: 14px;");
            btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: #16a085; -fx-text-fill: white;"));
            btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: #1abc9c; -fx-text-fill: white;"));
        }

        sidebar.getChildren().addAll(productsBtn, salesBtn, reportsBtn, usersBtn);

        // Main content area
        StackPane contentArea = new StackPane();
        contentArea.setPadding(new Insets(20));

        // Default content (Products Management)
        VBox productsContent = createProductsContent();
        contentArea.getChildren().add(productsContent);

        // Button actions
        productsBtn.setOnAction(e -> {
            contentArea.getChildren().clear();
            contentArea.getChildren().add(createProductsContent());
        });

        salesBtn.setOnAction(e -> {
            contentArea.getChildren().clear();
            contentArea.getChildren().add(createSalesContent());
        });

        reportsBtn.setOnAction(e -> {
            contentArea.getChildren().clear();
            contentArea.getChildren().add(createReportsContent());
        });

        usersBtn.setOnAction(e -> {
            contentArea.getChildren().clear();
            contentArea.getChildren().add(createUsersContent());
        });

        logoutBtn.setOnAction(e -> mainApp.showLogin());

        // Assemble layout
        mainLayout.setTop(header);
        mainLayout.setLeft(sidebar);
        mainLayout.setCenter(contentArea);

        return new Scene(mainLayout, 1000, 700);
    }

    private VBox createUsersContent() {
        VBox content = new VBox(12);
        content.setPadding(new Insets(16));

        Label title = new Label("User Management");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 20));

        // Observable list + filtered list for search
        ObservableList<User> usersObs = FXCollections.observableArrayList(authService.getAllUsers());
        FilteredList<User> filtered = new FilteredList<>(usersObs, p -> true);

        TableView<User> table = new TableView<>();
        table.setPrefHeight(360);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<User, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setMaxWidth(1f * Integer.MAX_VALUE * 10); // weight-based

        TableColumn<User, String> usernameCol = new TableColumn<>("Username");
        usernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        usernameCol.setMaxWidth(1f * Integer.MAX_VALUE * 18);

        TableColumn<User, String> passCol = new TableColumn<>("Password");
        passCol.setCellValueFactory(new PropertyValueFactory<>("password"));
        passCol.setMaxWidth(1f * Integer.MAX_VALUE * 18);

        TableColumn<User, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setMaxWidth(1f * Integer.MAX_VALUE * 26);

        TableColumn<User, String> roleCol = new TableColumn<>("Role");
        roleCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getRole()));
        roleCol.setMaxWidth(1f * Integer.MAX_VALUE * 15);

        TableColumn<User, String> empCol = new TableColumn<>("Employee ID");
        empCol.setCellValueFactory(data -> {
            if (data.getValue() instanceof Model.Cashier) {
                return new javafx.beans.property.SimpleStringProperty(((Model.Cashier) data.getValue()).getEmployeeId());
            }
            return new javafx.beans.property.SimpleStringProperty("");
        });
        empCol.setMaxWidth(1f * Integer.MAX_VALUE * 25);

        table.getColumns().addAll(idCol, usernameCol, passCol, nameCol, roleCol, empCol);
        table.setItems(filtered);

        Button addCashierBtn = new Button("Add Cashier");
        Button deleteBtn = new Button("Delete cashier");

        // Uniform button sizing and styling
        for (Button b : new Button[]{addCashierBtn, deleteBtn}) {
            b.setPrefWidth(100);
            b.setPrefHeight(30);
            b.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white; -fx-font-weight: 600;");
        }
        // Differentiate delete with a warning color
        deleteBtn.setStyle("-fx-background-color: #c0392b; -fx-text-fill: white; -fx-font-weight: 600;");

        HBox toolbar = new HBox(8);
        toolbar.setAlignment(Pos.CENTER_LEFT);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        toolbar.getChildren().addAll(spacer, addCashierBtn, deleteBtn);

        // Disable delete unless a row is selected
        deleteBtn.disableProperty().bind(table.getSelectionModel().selectedItemProperty().isNull());

        // Tooltips for clarity
        addCashierBtn.setTooltip(new Tooltip("Create a new cashier account"));
        deleteBtn.setTooltip(new Tooltip("Remove selected user from the system"));

        addCashierBtn.setOnAction(e -> {
            showAddCashierDialog(table, usersObs);
        });



        deleteBtn.setOnAction(e -> {
            User sel = table.getSelectionModel().getSelectedItem();
            if (sel != null) {
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Delete user '" + sel.getUsername() + "'?");
                confirm.showAndWait().ifPresent(resp -> {
                    if (resp == ButtonType.OK) {
                        authService.getAllUsers().removeIf(u -> u.getUsername().equals(sel.getUsername()));
                        Util.FileUtil.saveUsers("Data/Users/users.csv", authService.getAllUsers());
                        usersObs.setAll(authService.getAllUsers());
                        new Alert(Alert.AlertType.INFORMATION, "User deleted.").showAndWait();
                    }
                });
            } else {
                new Alert(Alert.AlertType.INFORMATION, "Select a user to delete.").showAndWait();
            }
        });

        VBox.setVgrow(table, Priority.ALWAYS);
        content.getChildren().addAll(title, toolbar, table);
        return content;
    }

    private void showAddCashierDialog(TableView<User> table, ObservableList<User> usersObs) {
        Dialog<Boolean> dialog = new Dialog<>();
        dialog.setTitle("Add New Cashier");
        dialog.setHeaderText("Enter cashier details:");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField usernameField = new TextField();
        PasswordField passwordField = new PasswordField();
        TextField nameField = new TextField();

        grid.add(new Label("Username:"), 0, 0);
        grid.add(usernameField, 1, 0);
        grid.add(new Label("Password:"), 0, 1);
        grid.add(passwordField, 1, 1);
        grid.add(new Label("Name:"), 0, 2);
        grid.add(nameField, 1, 2);

        dialog.getDialogPane().setContent(grid);

        ButtonType addButton = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButton, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> dialogButton == addButton);

        dialog.showAndWait().ifPresent(ok -> {
            if (ok) {
                String username = usernameField.getText().trim();
                String password = passwordField.getText();
                String name = nameField.getText().trim();
                if (username.isEmpty() || password.isEmpty() || name.isEmpty()) {
                    new Alert(Alert.AlertType.ERROR, "All fields are required.").showAndWait();
                    return;
                }
                boolean created = authService.addCashier(username, password, name);
                if (created) {
                    usersObs.setAll(authService.getAllUsers());
                    // select the newly added user
                    table.getSelectionModel().selectLast();
                    new Alert(Alert.AlertType.INFORMATION, "Cashier added successfully.").showAndWait();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Username already exists.").showAndWait();
                }
            }
        });
    }


    private VBox createProductsContent() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(20));

        Label title = new Label("Product Management");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 20));

        // Product list
        ListView<Product> productList = new ListView<>();
        productList.setPrefHeight(300);

        // Buttons
        HBox buttonPanel = new HBox(10);
        Button addBtn = new Button("Add Product");
        Button editBtn = new Button("Edit Product");
        Button deleteBtn = new Button("Delete Product");
        Button refreshBtn = new Button("Refresh");

        buttonPanel.getChildren().addAll(addBtn, editBtn, deleteBtn, refreshBtn);

        // Load products
        refreshProductList(productList);

        // Button actions

        addBtn.setOnAction(e -> showAddProductDialog(productList));

        editBtn.setOnAction(e -> {
            Product selected = productList.getSelectionModel().getSelectedItem();
            if (selected != null) {
                showEditProductDialog(selected, productList);
            }
        });

        deleteBtn.setOnAction(e -> {
            Product selected = productList.getSelectionModel().getSelectedItem();
            if (selected != null) {
                productService.deleteProduct(selected.getId());
                refreshProductList(productList);
            }
        });

        content.getChildren().addAll(title, productList, buttonPanel);
        return content;
    }

    private VBox createSalesContent() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(20));

        Label title = new Label("Sales Overview");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 20));

        // Create table
        TableView<String[]> salesTable = new TableView<>();

        TableColumn<String[], String> idCol = new TableColumn<>("Sale ID");
        TableColumn<String[], String> cashierCol = new TableColumn<>("Cashier");
        TableColumn<String[], String> totalCol = new TableColumn<>("Total");
        TableColumn<String[], String> dateCol = new TableColumn<>("Date");

        idCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()[0]));
        cashierCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()[1]));
        totalCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()[2]));
        dateCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()[3]));

        salesTable.getColumns().addAll(idCol, cashierCol, totalCol, dateCol);

        // Load sales data
        List<Sale> sales = saleService.getAllSales();
        for (Sale sale : sales) {
            salesTable.getItems().add(new String[]{
                    sale.getSaleId(),
                    sale.getCashierId(),
                    String.format("$%.2f", sale.getTotalAmount()),
                    sale.getDateTime().toString()
            });
        }

        Label totalLabel = new Label(String.format("Total Revenue: $%.2f", saleService.getTotalRevenue()));
        totalLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        content.getChildren().addAll(title, salesTable, totalLabel);
        return content;
    }

    private VBox createReportsContent() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(20));

        Label title = new Label("Reports");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 20));

        Label reportLabel = new Label("Sales Summary:");
        reportLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));

        TextArea reportArea = new TextArea();
        reportArea.setPrefHeight(200);
        reportArea.setEditable(false);

        // Generate report
        List<Sale> sales = saleService.getAllSales();
        StringBuilder report = new StringBuilder();
        report.append("=== SALES REPORT ===\n\n");
        report.append(String.format("Total Sales: %d\n", sales.size()));
        report.append(String.format("Total Revenue: $%.2f\n\n", saleService.getTotalRevenue()));
        report.append("Recent Sales:\n");

        int count = 0;
        for (int i = sales.size() - 1; i >= 0 && count < 50; i--, count++) {
            Sale sale = sales.get(i);
            report.append(String.format("%s - %s - $%.2f\n",
                    sale.getSaleId(), sale.getCashierId(), sale.getTotalAmount()));
        }

        reportArea.setText(report.toString());

        Button exportBtn = new Button("Export Report");
        exportBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");

        // FIX: Add action handler to make export button work
        exportBtn.setOnAction(event -> {
            try {
                // Create FileChooser
                javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
                fileChooser.setTitle("Save Report");
                fileChooser.getExtensionFilters().add(
                        new javafx.stage.FileChooser.ExtensionFilter("Text Files", "*.txt")
                );
                fileChooser.setInitialFileName("sales_report.txt");

                // Show save dialog
                java.io.File file = fileChooser.showSaveDialog(exportBtn.getScene().getWindow());

                if (file != null) {
                    // Write report to file
                    java.nio.file.Files.write(
                            file.toPath(),
                            reportArea.getText().getBytes()
                    );

                    // Show success message
                    javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                            javafx.scene.control.Alert.AlertType.INFORMATION
                    );
                    alert.setTitle("Success");
                    alert.setHeaderText(null);
                    alert.setContentText("Report exported successfully to:\n" + file.getAbsolutePath());
                    alert.showAndWait();
                }
            } catch (Exception e) {
                // Show error message
                javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                        javafx.scene.control.Alert.AlertType.ERROR
                );
                alert.setTitle("Error");
                alert.setHeaderText("Export Failed");
                alert.setContentText("Could not save report: " + e.getMessage());
                alert.showAndWait();
            }
        });

        content.getChildren().addAll(title, reportLabel, reportArea, exportBtn);
        return content;
    }

    private void refreshProductList(ListView<Product> listView) {
        listView.getItems().clear();
        List<Product> products = productService.getAllProducts();
        listView.getItems().addAll(products);
        listView.setCellFactory(lv -> new ListCell<Product>() {
            @Override
            protected void updateItem(Product item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item.toDisplayString());
            }
        });
    }

    private void showAddProductDialog(ListView<Product> listView) {
        Dialog<Product> dialog = new Dialog<>();
        dialog.setTitle("Add New Product");
        dialog.setHeaderText("Enter product details:");

        // Create form
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField idField = new TextField();
        TextField nameField = new TextField();
        TextField priceField = new TextField();
        TextField quantityField = new TextField();
        TextField categoryField = new TextField();

        grid.add(new Label("ID:"), 0, 0);
        grid.add(idField, 1, 0);
        grid.add(new Label("Name:"), 0, 1);
        grid.add(nameField, 1, 1);
        grid.add(new Label("Price:"), 0, 2);
        grid.add(priceField, 1, 2);
        grid.add(new Label("Quantity:"), 0, 3);
        grid.add(quantityField, 1, 3);
        grid.add(new Label("Category:"), 0, 4);
        grid.add(categoryField, 1, 4);

        dialog.getDialogPane().setContent(grid);

        // Add buttons
        ButtonType addButton = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButton, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButton) {
                try {
                    String id = idField.getText();
                    String name = nameField.getText();
                    double price = Double.parseDouble(priceField.getText());
                    int quantity = Integer.parseInt(quantityField.getText());
                    String category = categoryField.getText();

                    return new Product(id, name, price, quantity, category);
                } catch (Exception e) {
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(product -> {
            productService.addProduct(product);
            refreshProductList(listView);
        });
    }

    private void showEditProductDialog(Product product, ListView<Product> listView) {
        Dialog<Product> dialog = new Dialog<>();
        dialog.setTitle("Edit Product");
        dialog.setHeaderText("Edit product details:");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField nameField = new TextField(product.getName());
        TextField priceField = new TextField(String.valueOf(product.getPrice()));
        TextField quantityField = new TextField(String.valueOf(product.getQuantity()));

        TextField categoryField = new TextField(product.getCategory());

        grid.add(new Label("ID:"), 0, 0);
        grid.add(new Label(product.getId()), 1, 0);
        grid.add(new Label("Name:"), 0, 1);
        grid.add(nameField, 1, 1);
        grid.add(new Label("Price:"), 0, 2);
        grid.add(priceField, 1, 2);
        grid.add(new Label("Quantity:"), 0, 3);
        grid.add(quantityField, 1, 3);
        grid.add(new Label("Category:"), 0, 4);
        grid.add(categoryField, 1, 4);

        dialog.getDialogPane().setContent(grid);

        ButtonType saveButton = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButton, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButton) {
                try {
                    product.setName(nameField.getText());
                    product.setPrice(Double.parseDouble(priceField.getText()));
                    product.setQuantity(Integer.parseInt(quantityField.getText()));
                    product.setCategory(categoryField.getText());
                    return product;
                } catch (Exception e) {
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(updatedProduct -> {
            productService.updateProduct(updatedProduct);
            refreshProductList(listView);
        });
    }
}
