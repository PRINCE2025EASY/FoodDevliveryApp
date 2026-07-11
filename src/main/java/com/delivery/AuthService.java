package com.delivery;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Lightweight authentication service backed entirely by an in-memory data
 * store (no database or file persistence). Credentials only live for the
 * lifetime of the running application; restarting the app resets the store
 * back to the seeded demo accounts.
 *
 * Each account also carries a {@link Role}, which {@link LoginScreen} uses to
 * decide whether a successful login should open the customer ordering screen
 * ({@link FoodDeliveryApp}) or the {@link AdminDashboard}.
 *
 * This class is intentionally simple (plain-text password comparison) to
 * match the scope of the rest of this demo project. It is NOT meant to be a
 * production-grade authentication implementation.
 */
public final class AuthService {

    /** Determines which screen a successful login is routed to. */
    public enum Role {
        ADMIN,
        CUSTOMER
    }

    // The "in-memory data store": username -> password / username -> role
    private static final Map<String, String> credentialStore = new ConcurrentHashMap<>();
    private static final Map<String, Role> roleStore = new ConcurrentHashMap<>();

    static {
        // Seed a handful of demo accounts so the app is usable immediately.
        credentialStore.put("admin", "admin123");
        roleStore.put("admin", Role.ADMIN);

        credentialStore.put("customer", "customer123");
        roleStore.put("customer", Role.CUSTOMER);
    }

    private AuthService() {
        // Static utility class; prevent instantiation.
    }

    /**
     * Validates a username/password pair against the in-memory store.
     *
     * @return true if the username exists and the password matches exactly.
     */
    public static boolean authenticate(String username, String password) {
        if (username == null || password == null) {
            return false;
        }
        String key = username.trim();
        String storedPassword = credentialStore.get(key);
        return storedPassword != null && storedPassword.equals(password);
    }

    /** Registers a brand-new account with the default CUSTOMER role. */
    public static boolean register(String username, String password) {
        return register(username, password, Role.CUSTOMER);
    }

    /**
     * Registers a brand-new account in the in-memory store with an explicit role.
     *
     * @return true if registration succeeded; false if the username is
     *         blank, the password is blank, or the username is already taken.
     */
    public static boolean register(String username, String password, Role role) {
        if (username == null || password == null) {
            return false;
        }
        String key = username.trim();
        if (key.isEmpty() || password.isEmpty()) {
            return false;
        }
        // putIfAbsent returns the *previous* value; null means it was new.
        if (credentialStore.putIfAbsent(key, password) == null) {
            roleStore.put(key, role == null ? Role.CUSTOMER : role);
            return true;
        }
        return false;
    }

    /** True if an account with this username is already present in the store. */
    public static boolean userExists(String username) {
        return username != null && credentialStore.containsKey(username.trim());
    }

    /** Returns the role for a known username, defaulting to CUSTOMER if unrecognized. */
    public static Role getRole(String username) {
        if (username == null) {
            return Role.CUSTOMER;
        }
        return roleStore.getOrDefault(username.trim(), Role.CUSTOMER);
    }
}
