package com.delivery;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class FoodDeliveryApp extends Application {

    // Collections to manage operational data dynamically
    private final ObservableList<MenuItem> menuData = FXCollections.observableArrayList();
    private final List<MenuItem> currentCart = new ArrayList<>();
    private final ObservableList<String> cartDisplayList = FXCollections.observableArrayList();

    private Customer currentCustomer;
    private double cartTotal = 0.0;

    // Username of the account that logged in via LoginScreen (defaults to
    // "Guest" when this class is launched standalone, e.g. for testing).
    private String loggedInUsername = "Guest";

    // UI Nodes
    private Label lblTotal;
    private ListView<MenuItem> menuListView;
    private ListView<String> cartListView;
    private TextArea txtOrderSummary;

    public FoodDeliveryApp() {
        // Default no-arg constructor, used when launched directly (no login).
    }

    public FoodDeliveryApp(String loggedInUsername) {
        if (loggedInUsername != null && !loggedInUsername.isEmpty()) {
            this.loggedInUsername = loggedInUsername;
        }
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Online Food Delivery System - Logged in as " + loggedInUsername);

        // Seed initial menu data
        loadMockMenuData();

        // Root Layout
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(15));
        root.setStyle("-fx-background-color: #f4f6f9;");

        // Top Section: App Title Banner
        VBox topBanner = new VBox(5);
        topBanner.setAlignment(Pos.CENTER);
        topBanner.setPadding(new Insets(0, 0, 15, 0));

        Button btnLogout = new Button("Logout");
        btnLogout.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold;");
        btnLogout.setOnAction(e -> handleLogout(primaryStage));
        HBox topRow = new HBox();
        topRow.setAlignment(Pos.CENTER_RIGHT);
        topRow.getChildren().add(btnLogout);

        Label lblTitle = new Label("PIO ONLINE FOOD DELIVERY SYSTEM");
        lblTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        Label lblWelcome = new Label("Welcome, " + loggedInUsername + "!");
        lblWelcome.setStyle("-fx-font-size: 12px; -fx-text-fill: #7f8c8d;");
        topBanner.getChildren().addAll(topRow, lblTitle, lblWelcome);
        root.setTop(topBanner);

        // Center Split Section: Left Menu Selection, Right Shopping Cart Checkout
        GridPane mainGrid = new GridPane();
        mainGrid.setHgap(15);
        mainGrid.setVgap(10);
        mainGrid.setMaxWidth(Double.MAX_VALUE);

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(50);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(50);
        mainGrid.getColumnConstraints().addAll(col1, col2);

        // --- LEFT COLUMN: MENU VIEW ---
        VBox menuBox = new VBox(10);
        Label lblMenuHeader = new Label("Available Restaurant Menu Options");
        lblMenuHeader.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        menuListView = new ListView<>(menuData);
        menuListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        Button btnAddToCart = new Button("Add Selected Item to Cart");
        btnAddToCart.setMaxWidth(Double.MAX_VALUE);
        btnAddToCart.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold;");

        // Event Handling: Add item to cart selection
        btnAddToCart.setOnAction(e -> handleAddToCart());

        menuBox.getChildren().addAll(lblMenuHeader, menuListView, btnAddToCart);
        mainGrid.add(menuBox, 0, 0);

        // --- RIGHT COLUMN: CART & CUSTOMER VERIFICATION ---
        VBox cartBox = new VBox(10);
        Label lblCartHeader = new Label("Your Active Shopping Cart");
        lblCartHeader.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        cartListView = new ListView<>(cartDisplayList);

        HBox totalBox = new HBox();
        totalBox.setAlignment(Pos.CENTER_RIGHT);
        lblTotal = new Label("Total Bill: GH\u20B50.00");
        lblTotal.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #e74c3c;");
        totalBox.getChildren().add(lblTotal);

        // Checkout Customer Validation Form
        VBox customerForm = new VBox(5);
        customerForm.setPadding(new Insets(10, 0, 0, 0));

        TextField txtName = new TextField();
        txtName.setPromptText("Enter Full Customer Name");
        TextField txtEmail = new TextField();
        txtEmail.setPromptText("Enter Contact Email Address");
        TextField txtAddress = new TextField();
        txtAddress.setPromptText("Enter Detailed Delivery Address");

        Button btnPlaceOrder = new Button("Finalize & Process Delivery Order");
        btnPlaceOrder.setMaxWidth(Double.MAX_VALUE);
        btnPlaceOrder.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold;");

        // Event Handling: Place Order button click
        btnPlaceOrder.setOnAction(e -> handlePlaceOrder(txtName.getText(), txtEmail.getText(), txtAddress.getText()));

        customerForm.getChildren().addAll(
                new Label("Checkout Validation Details:"),
                txtName, txtEmail, txtAddress, btnPlaceOrder
        );

        cartBox.getChildren().addAll(lblCartHeader, cartListView, totalBox, customerForm);
        mainGrid.add(cartBox, 1, 0);

        root.setCenter(mainGrid);

        // Bottom Section: Output Order Execution Status Console
        VBox bottomBox = new VBox(5);
        bottomBox.setPadding(new Insets(15, 0, 0, 0));
        Label lblConsoleHeader = new Label("System Order Logs / Invoicing Ledger Output:");
        lblConsoleHeader.setStyle("-fx-font-weight: bold;");

        txtOrderSummary = new TextArea();
        txtOrderSummary.setEditable(false);
        txtOrderSummary.setPrefHeight(120);
        txtOrderSummary.setPromptText("Awaiting transactional orders execution logs...");

        bottomBox.getChildren().addAll(lblConsoleHeader, txtOrderSummary);
        root.setBottom(bottomBox);

        Scene scene = new Scene(root, 750, 620);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Seeds reference collection with sample operational metrics menu items.
     */
    private void loadMockMenuData() {
        menuData.add(new MenuItem("M01", "Jollof Rice", 15.50, "Local Entrees"));
        menuData.add(new MenuItem("M02", "Goat Light Soup", 12.00, "Appetizers"));
        menuData.add(new MenuItem("M03", "Fufu with Palmnut Soup", 18.00, "Traditional"));
        menuData.add(new MenuItem("M04", "Konkonte with Grandnut Soup", 9.50, "Traditional"));
        menuData.add(new MenuItem("M05", "Rice Ball with Grandnut Soup", 4.50, "Foreing"));
    }
    /**
     * Handles adding item selections securely to memory and updating layout trackers.
     */
    private void handleAddToCart() {
        MenuItem selectedItem = menuListView.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            currentCart.add(selectedItem);
            cartDisplayList.add(selectedItem.getName() + " - \u20B5" + String.format("%.2f", selectedItem.getPrice()));
            cartTotal += selectedItem.getPrice();
            lblTotal.setText("Total Bill: \u20B5" + String.format("%.2f", cartTotal));
        } else {
            showAlert(Alert.AlertType.WARNING, "Item Selection Error", "Please pick an item from the product catalog matrix to add to the cart selection.");
        }
    }

    /**
     * Robust Input Validation and Error-Handling routines for processing order submission.
     */
    private void handlePlaceOrder(String name, String email, String address) {
        try {
            // Robust Input Validation Handling
            if (currentCart.isEmpty()) {
                throw new IllegalStateException("Operational checkout halted: Shopping cart must contain elements prior to order finalization.");
            }
            if (name.trim().isEmpty() || email.trim().isEmpty() || address.trim().isEmpty()) {
                throw new IllegalArgumentException("System data verification error: Customer validation input forms cannot be submitted empty.");
            }
            if (!email.contains("@") || !email.contains(".")) {
                throw new IllegalArgumentException("Transactional verification failed: Provided email address format configuration is incorrect.");
            }

            // Polymorphism & Object Instantiation
            currentCustomer = new Customer(name, email, address);

            // Persist the order to the shared store so the Admin Dashboard can see it,
            // then render the same invoice text into the local ledger output.
            Order savedOrder = OrderStore.getInstance()
                    .recordOrder(loggedInUsername, currentCustomer, currentCart, cartTotal);

            txtOrderSummary.setText(savedOrder.toInvoiceString());
            showAlert(Alert.AlertType.INFORMATION, "Order Placed Successfully",
                    "Your delivery process execution pipeline has started successfully.\nOrder ID: " + savedOrder.getOrderId());

            // Clear checkout configuration stack cleanly
            clearCartSession();

        } catch (IllegalArgumentException | IllegalStateException ex) {
            // Catching standard logical system workflow structural boundary issues
            showAlert(Alert.AlertType.ERROR, "Data Entry Exception Enforced", ex.getMessage());
        } catch (Exception ex) {
            // General structural global unexpected backup safety handler logic
            showAlert(Alert.AlertType.ERROR, "Fatal System Framework Crash Averted", "An unforeseen execution exception occurred: " + ex.getMessage());
        }
    }

    private void clearCartSession() {
        currentCart.clear();
        cartDisplayList.clear();
        cartTotal = 0.0;
        lblTotal.setText("Total Bill: \u20B50.00");
    }

    private void handleLogout(Stage primaryStage) {
        LoginScreen loginScreen = new LoginScreen();
        loginScreen.start(primaryStage);
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
