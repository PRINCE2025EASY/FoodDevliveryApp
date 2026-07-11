package com.delivery;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Application entry screen: prompts for a username/password, validates the
 * credentials against {@link AuthService}'s in-memory data store, and — on
 * success — hands the primary Stage off to {@link FoodDeliveryApp}.
 */
public class LoginScreen extends Application {

    private Label lblStatus;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Online Food Delivery System - Login");

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        // Title Header
        Text sceneTitle = new Text("LOGIN");
        sceneTitle.setFont(Font.font("Tahoma", FontWeight.BOLD, 20));
        grid.add(sceneTitle, 0, 0, 2, 1);

        // Username Field
        Label userLabel = new Label("Username:");
        grid.add(userLabel, 0, 1);
        TextField userTextField = new TextField();
        userTextField.setPromptText("Enter your username");
        grid.add(userTextField, 1, 1);

        // Password Field
        Label pwLabel = new Label("Password:");
        grid.add(pwLabel, 0, 2);
        PasswordField pwField = new PasswordField();
        pwField.setPromptText("Enter your password");
        grid.add(pwField, 1, 2);

        // Inline validation/status message
        lblStatus = new Label();
        lblStatus.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 11px;");
        grid.add(lblStatus, 0, 3, 2, 1);

        // Buttons
        Button loginBtn = new Button("Login");
        loginBtn.setDefaultButton(true);
        loginBtn.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold;");
        Button signUpBtn = new Button("Sign Up");
        signUpBtn.setStyle("-fx-background-color: #0096FF; -fx-text-fill: white; -fx-font-weight: bold;");

        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().addAll(loginBtn, signUpBtn);
        grid.add(hbBtn, 1, 4);

        // Login: validate input, then check credentials against the in-memory store
        loginBtn.setOnAction(e -> attemptLogin(primaryStage, userTextField.getText(), pwField.getText()));

        // Sign Up: open a small modal to register a brand-new account
        signUpBtn.setOnAction(e -> showSignUpDialog(primaryStage));

        Scene scene = new Scene(grid, 360, 260);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    /**
     * Validates the submitted fields, then checks them against the in-memory
     * credential store via {@link AuthService#authenticate(String, String)}.
     */
    private void attemptLogin(Stage primaryStage, String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            lblStatus.setText("Username cannot be empty.");
            return;
        }
        if (password == null || password.isEmpty()) {
            lblStatus.setText("Password cannot be empty.");
            return;
        }

        if (AuthService.authenticate(username, password)) {
            lblStatus.setText("");
            openFoodDeliveryApp(primaryStage, username.trim());
        } else {
            lblStatus.setText("Invalid username or password.");
        }
    }

    /** Hands the current Stage off to the main food delivery application. */
    private void openFoodDeliveryApp(Stage primaryStage, String username) {
        FoodDeliveryApp app = new FoodDeliveryApp(username);
        app.start(primaryStage);
    }

    /**
     * Small modal dialog for registering a brand-new account directly into
     * the same in-memory data store used for login validation.
     */
    private void showSignUpDialog(Stage owner) {
        Stage dialog = new Stage();
        dialog.initOwner(owner);
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.setTitle("Create Account");
        dialog.setResizable(false);

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        Label newUserLabel = new Label("Username:");
        TextField newUserField = new TextField();
        newUserField.setPromptText("Choose a username");

        Label newPwLabel = new Label("Password:");
        PasswordField newPwField = new PasswordField();
        newPwField.setPromptText("Choose a password");

        Label confirmPwLabel = new Label("Confirm Password:");
        PasswordField confirmPwField = new PasswordField();
        confirmPwField.setPromptText("Re-enter password");

        Label statusLabel = new Label();
        statusLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 11px;");

        Button registerBtn = new Button("Register");
        registerBtn.setDefaultButton(true);
        registerBtn.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold;");

        registerBtn.setOnAction(e -> {
            String newUser = newUserField.getText() == null ? "" : newUserField.getText().trim();
            String newPw = newPwField.getText() == null ? "" : newPwField.getText();
            String confirmPw = confirmPwField.getText() == null ? "" : confirmPwField.getText();

            if (newUser.isEmpty() || newPw.isEmpty() || confirmPw.isEmpty()) {
                statusLabel.setText("All fields are required.");
                return;
            }
            if (!newPw.equals(confirmPw)) {
                statusLabel.setText("Passwords do not match.");
                return;
            }
            if (AuthService.userExists(newUser)) {
                statusLabel.setText("That username is already taken.");
                return;
            }
            if (AuthService.register(newUser, newPw)) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.initOwner(dialog);
                alert.setTitle("Account Created");
                alert.setHeaderText(null);
                alert.setContentText("Account '" + newUser + "' created successfully. You can now log in.");
                alert.showAndWait();
                dialog.close();
            } else {
                statusLabel.setText("Registration failed. Please try again.");
            }
        });

        grid.add(newUserLabel, 0, 0);
        grid.add(newUserField, 1, 0);
        grid.add(newPwLabel, 0, 1);
        grid.add(newPwField, 1, 1);
        grid.add(confirmPwLabel, 0, 2);
        grid.add(confirmPwField, 1, 2);
        grid.add(statusLabel, 0, 3, 2, 1);
        grid.add(registerBtn, 1, 4);

        Scene scene = new Scene(grid, 320, 240);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
