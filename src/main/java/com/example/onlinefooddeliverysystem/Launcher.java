package com.example.onlinefooddeliverysystem;

import com.delivery.LoginScreen;
import javafx.application.Application;

public class Launcher {
    public static void main(String[] args) {
        // App now starts at the login screen; a successful login hands the
        // Stage off to FoodDeliveryApp (see LoginScreen.openFoodDeliveryApp).
        Application.launch(LoginScreen.class, args);
    }
}
