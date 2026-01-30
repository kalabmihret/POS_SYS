package GUI;

import Service.AuthServiceInterface;
import Util.ValidationUtil;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class LoginView {
    private AuthServiceInterface authService;
    private Main mainApp;

    public LoginView(Main mainApp, AuthServiceInterface authService) {
        this.mainApp = mainApp;
        this.authService = authService;
    }

    public Scene getScene() {
        // Create layout
        VBox mainLayout = new VBox(20);
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setPadding(new Insets(40));
        mainLayout.setStyle("-fx-background-color: linear-gradient(to bottom right, #667eea, #764ba2);");

        // Title
        Label titleLabel = new Label("🛒 POS SYSTEM LOGIN");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        titleLabel.setTextFill(Color.WHITE);

        // Login panel
        VBox loginPanel = new VBox(15);
        loginPanel.setAlignment(Pos.CENTER);
        loginPanel.setPadding(new Insets(30));
        loginPanel.setStyle("-fx-background-color: white; -fx-background-radius: 10;");
        loginPanel.setMaxWidth(400);

        Label loginLabel = new Label("Login to System");
        loginLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));

        // Username field
        Label userLabel = new Label("Username:");
        userLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter username");
        usernameField.setPrefHeight(35);
        usernameField.setStyle("-fx-font-size: 14px;");

        // Password field
        Label passLabel = new Label("Password:");
        passLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter password");
        passwordField.setPrefHeight(35);
        passwordField.setStyle("-fx-font-size: 14px;");

        // Login button
        Button loginButton = new Button("Login");
        loginButton.setStyle("-fx-background-color: #667eea; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");
        loginButton.setPrefWidth(200);
        loginButton.setPrefHeight(40);

        // Status label
        Label statusLabel = new Label();
        statusLabel.setTextFill(Color.RED);

        // Default credentials hint
        Label hintLabel = new Label("Default: admin/admin123 or cashier/1234");
        hintLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
        hintLabel.setTextFill(Color.GRAY);

        // Add components to login panel
        loginPanel.getChildren().addAll(
                loginLabel, userLabel, usernameField, passLabel,
                passwordField, loginButton, statusLabel, hintLabel
        );

        // Add to main layout
        mainLayout.getChildren().addAll(titleLabel, loginPanel);

        // Login button action
        loginButton.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText().trim();

            if (!ValidationUtil.isValidUsername(username)) {
                statusLabel.setText("Username must be at least 3 characters!");
                return;
            }

            if (!ValidationUtil.isValidPassword(password)) {
                statusLabel.setText("Password must be at least 4 characters!");
                return;
            }

            if (authService.login(username, password)) {
                statusLabel.setText("Login successful!");
                statusLabel.setTextFill(Color.GREEN);

                // Navigate to appropriate dashboard
                if (authService.isAdmin()) {
                    mainApp.showAdminDashboard();
                } else {
                    mainApp.showCashierDashboard();
                }
            } else {
                statusLabel.setText("Invalid username or password!");
                statusLabel.setTextFill(Color.RED);
            }
        });

        Platform.runLater(() -> usernameField.requestFocus());

        // Username field: Press Enter → jump to password field
        usernameField.setOnAction(e -> passwordField.requestFocus());

        // Password field: Press Enter → trigger login
        passwordField.setOnAction(e -> loginButton.fire());

        // Create scene with global Enter key support
        Scene scene = new Scene(mainLayout, 800, 600);

        // Press Enter
        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                loginButton.fire();
                e.consume(); // Prevent double action
            }
        });

        return scene;
    }
}