package com.delivery;
public class MenuItem {
    private String id;
    private String name;
    private double price;
    private String category;

    public MenuItem(String id, String name, double price, String category) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.category = category;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public String getCategory() { return category; }

    // Polymorphism: Overriding toString() for smooth UI listing rendering
    @Override
    public String toString() {
        return String.format("%s (%s) - $%.2f", name, category, price);
    }
}
