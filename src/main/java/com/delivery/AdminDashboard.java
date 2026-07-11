package com.delivery;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

/**
 * Admin-only screen showing every order placed by every customer,
 * Reached only when a login through {@link LoginScreen} resolves to an
 * {@link AuthService.Role#ADMIN} account (e.g. the seeded "prince" / "prince123").
 */
public class AdminDashboard extends Application {

    private final OrderStore orderStore = OrderStore.getInstance();

    private TableView<Order> orderTable;
    private TextArea txtInvoiceDetail;
    private Label lblSummary;
    private TextField txtSearch;
    private String loggedInUsername = "Admin";

    public AdminDashboard() {
        // Default no-arg constructor, used when launched directly (no login).
    }

    public AdminDashboard(String loggedInUsername) {
        if (loggedInUsername != null && !loggedInUsername.isEmpty()) {
            this.loggedInUsername = loggedInUsername;
        }
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Online Food Delivery System - Admin Dashboard (" + loggedInUsername + ")");

        // Pick up anything persisted by earlier sessions/customers before rendering.
        orderStore.reload();

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(15));
        root.setStyle("-fx-background-color: #f4f6f9;");

        // --- TOP: Title banner + logout ---
        VBox topBanner = new VBox(5);
        topBanner.setAlignment(Pos.CENTER);
        topBanner.setPadding(new Insets(0, 0, 15, 0));

        Button btnLogout = new Button("Logout");
        btnLogout.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold;");
        btnLogout.setOnAction(e -> handleLogout(primaryStage));
        HBox topRow = new HBox();
        topRow.setAlignment(Pos.CENTER_RIGHT);
        topRow.getChildren().add(btnLogout);

        Label lblTitle = new Label("PIO ONLINE FOOD DELIVERY SYSTEM \u2014 ADMIN CONTROL PANEL");
        lblTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        Label lblWelcome = new Label("Logged in as: " + loggedInUsername + " (Admin)");
        lblWelcome.setStyle("-fx-font-size: 12px; -fx-text-fill: #7f8c8d;");

        topBanner.getChildren().addAll(topRow, lblTitle, lblWelcome);
        root.setTop(topBanner);

        // --- CENTER: split pane -> orders table (left) + invoice detail (right) ---
        SplitPane splitPane = new SplitPane();
        splitPane.setDividerPositions(0.58);

        VBox leftBox = new VBox(10);
        leftBox.setPadding(new Insets(10, 10, 0, 0));

        Label lblOrdersHeader = new Label("All Customer Orders");
        lblOrdersHeader.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        HBox searchRow = new HBox(8);
        searchRow.setAlignment(Pos.CENTER_LEFT);
        txtSearch = new TextField();
        txtSearch.setPromptText("Filter by order ID, customer name, or email...");
        HBox.setHgrow(txtSearch, Priority.ALWAYS);
        Button btnRefresh = new Button("Refresh");
        btnRefresh.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold;");
        btnRefresh.setOnAction(e -> handleRefresh());
        searchRow.getChildren().addAll(txtSearch, btnRefresh);

        orderTable = new TableView<>();
        setupTableColumns();

        FilteredList<Order> filteredOrders = new FilteredList<>(orderStore.getOrders(), o -> true);
        txtSearch.textProperty().addListener((obs, oldVal, newVal) -> {
            String lower = newVal == null ? "" : newVal.toLowerCase().trim();
            filteredOrders.setPredicate(order -> {
                if (lower.isEmpty()) {
                    return true;
                }
                return order.getOrderId().toLowerCase().contains(lower)
                        || order.getCustomerName().toLowerCase().contains(lower)
                        || order.getCustomerEmail().toLowerCase().contains(lower)
                        || order.getPlacedByUsername().toLowerCase().contains(lower);
            });
        });
        orderTable.setItems(filteredOrders);
        orderTable.getSelectionModel().selectedItemProperty()
                .addListener((obs, oldSel, newSel) -> showInvoiceDetail(newSel));

        // Keep the revenue/order-count summary in sync with the live order list.
        orderStore.getOrders().addListener((ListChangeListener<Order>) c -> updateSummaryLabel());

        lblSummary = new Label();
        lblSummary.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: #27ae60;");
        updateSummaryLabel();

        VBox.setVgrow(orderTable, Priority.ALWAYS);
        leftBox.getChildren().addAll(lblOrdersHeader, searchRow, orderTable, lblSummary);

        VBox rightBox = new VBox(10);
        rightBox.setPadding(new Insets(10, 0, 0, 10));
        Label lblDetailHeader = new Label("Selected Order Invoice");
        lblDetailHeader.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        txtInvoiceDetail = new TextArea();
        txtInvoiceDetail.setEditable(false);
        txtInvoiceDetail.setWrapText(true);
        txtInvoiceDetail.setPromptText("Select an order from the list on the left to view its full generated invoice...");
        VBox.setVgrow(txtInvoiceDetail, Priority.ALWAYS);

        rightBox.getChildren().addAll(lblDetailHeader, txtInvoiceDetail);

        splitPane.getItems().addAll(leftBox, rightBox);
        root.setCenter(splitPane);

        Scene scene = new Scene(root, 980, 640);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void setupTableColumns() {
        TableColumn<Order, String> colOrderId = new TableColumn<>("Order ID");
        colOrderId.setCellValueFactory(new PropertyValueFactory<>("orderId"));

        TableColumn<Order, String> colCustomer = new TableColumn<>("Customer");
        colCustomer.setCellValueFactory(new PropertyValueFactory<>("customerName"));

        TableColumn<Order, String> colEmail = new TableColumn<>("Email");
        colEmail.setCellValueFactory(new PropertyValueFactory<>("customerEmail"));

        TableColumn<Order, String> colAddress = new TableColumn<>("Delivery Address");
        colAddress.setCellValueFactory(new PropertyValueFactory<>("deliveryAddress"));

        TableColumn<Order, String> colTotal = new TableColumn<>("Total (\u20B5)");
        colTotal.setCellValueFactory(cell ->
                new SimpleStringProperty(String.format("%.2f", cell.getValue().getTotalAmount())));

        TableColumn<Order, String> colTime = new TableColumn<>("Placed At");
        colTime.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getFormattedTimestamp()));

        orderTable.getColumns().addAll(colOrderId, colCustomer, colEmail, colAddress, colTotal, colTime);
        orderTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        orderTable.setPlaceholder(new Label("No orders have been placed by any customer yet."));
    }

    private void showInvoiceDetail(Order order) {
        txtInvoiceDetail.setText(order == null ? "" : order.toInvoiceString());
    }

    private void handleRefresh() {
        orderStore.reload();
        updateSummaryLabel();
    }

    private void updateSummaryLabel() {
        int count = orderStore.getOrders().size();
        double revenue = orderStore.getTotalRevenue();
        lblSummary.setText(String.format("Total Orders: %d   |   Total Revenue: \u20B5%.2f", count, revenue));
    }

    private void handleLogout(Stage primaryStage) {
        LoginScreen loginScreen = new LoginScreen();
        loginScreen.start(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
