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
import javafx.stage.Stage;

public class login_interface extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Online Food Delivery System");

        // Create a GridPane layout
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        // Add a Title Header
        Text sceneTitle = new Text("LOGIN");
        sceneTitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(sceneTitle, 0, 0, 2, 1);

        // Username Field
        Label userName = new Label("Username:");
        grid.add(userName, 0, 1);
        TextField userTextField = new TextField();
        userTextField.setPromptText("Enter your username");
        grid.add(userTextField, 1, 1);

        // Password Field
        Label pw = new Label("Password:");
        grid.add(pw, 0, 2);
        PasswordField pwBox = new PasswordField();
        pwBox.setPromptText("Enter your password");
        grid.add(pwBox, 1, 2);
        CheckBox chkRemember = new CheckBox("Remember Me");

        // Buttons
        Button loginBtn = new Button("Login");
        loginBtn.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold;");
        Button signUpBtn = new Button("Sign Up");
        signUpBtn.setStyle("-fx-background-color: #0096FF; -fx-text-fill: white; -fx-font-weight: bold;");

        // Layout container for the buttons to place them side-by-side
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().addAll(loginBtn, signUpBtn);
        grid.add(hbBtn, 1, 4);

        // Optional: Simple Action Handlers for demo purposes
        loginBtn.setOnAction(e -> System.out.println("Login button clicked! Username: " + userTextField.getText()));
        signUpBtn.setOnAction(e -> System.out.println("Sign Up button clicked!"));

        // Setup and show the Scene
        Scene scene = new Scene(grid, 350, 250);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}