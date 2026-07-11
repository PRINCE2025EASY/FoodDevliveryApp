package com.delivery;

    import java.util.UUID;

// Abstraction: Abstract class defining the core structure of a user
public abstract class User {
    private final String userId; // Encapsulation: private field
    private String name;
    private String email;

    public User(String name, String email) {
        this.userId = UUID.randomUUID().toString().substring(0, 8);
        this.name = name;
        this.email = email;
    }

    // Encapsulation: Public getters and setters
    public String getUserId() { return userId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    // Abstract method to be overridden by subclasses
    public abstract String getRoleDescription();
}
