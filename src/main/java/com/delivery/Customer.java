package com.delivery;

// Inheritance: Customer inherits properties and behaviors from User
public class Customer extends User {
    private String deliveryAddress;

    public Customer(String name, String email, String deliveryAddress) {
        super(name, email);
        this.deliveryAddress = deliveryAddress;
    }

    public String getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }

    // Polymorphism: Overriding the abstract method
    @Override
    public String getRoleDescription() {
        return "Customer: Allowed to view menus, place orders, and track deliveries.";
    }
}
