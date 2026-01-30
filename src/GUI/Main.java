package GUI;

import Model.*;
import Service.AuthService;
import Service.ProductService;
import Service.SaleService;
import Service.AuthServiceInterface;
import Service.ProductServiceInterface;
import Service.SaleServiceInterface;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    private Stage primaryStage;
    private LoginView loginView;
    private AdminDashboard adminDashboard;
    private CashierDashboard cashierDashboard;
    private AuthServiceInterface authService;
    private ProductServiceInterface productService;
    private SaleServiceInterface saleService;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

        // Initialize services (shared)
        authService = new AuthService();
        productService = new ProductService();
        saleService = new SaleService();

        // Initialize views with shared services
        loginView = new LoginView(this, authService);
        adminDashboard = new AdminDashboard(this, authService, productService, saleService);
        cashierDashboard = new CashierDashboard(this, productService, saleService);

        // Show login screen first
        showLogin();

        primaryStage.setTitle("🛒 POS System");
        primaryStage.show();
    }