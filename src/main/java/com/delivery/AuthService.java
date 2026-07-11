package com.delivery;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Lightweight authentication service backed entirely by an in-memory data
 * store (no database or file persistence). Credentials only live for the
 * lifetime of the running application; restarting the app resets the store
 * back to the seeded demo accounts.
 *
 * This class is intentionally simple (plain-text password comparison) to
 * match the scope of the rest of this demo project. It is NOT meant to be a
 * production-grade authentication implementation.
 */
public final class AuthService {

    // The "in-memory data store": username -> password
    private static final Map<String, String> credentialStore = new ConcurrentHashMap<>();

    static {
        // Seed a handful of demo accounts so the app is usable immediately.
        credentialStore.put("admin", "admin123");
        credentialStore.put("customer", "customer123");
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

    /**
     * Registers a brand-new account in the in-memory store.
     *
     * @return true if registration succeeded; false if the username is
     *         blank, the password is blank, or the username is already taken.
     */
    public static boolean register(String username, String password) {
        if (username == null || password == null) {
            return false;
        }
        String key = username.trim();
        if (key.isEmpty() || password.isEmpty()) {
            return false;
        }
        // putIfAbsent returns the *previous* value; null means it was new.
        return credentialStore.putIfAbsent(key, password) == null;
    }

    /** True if an account with this username is already present in the store. */
    public static boolean userExists(String username) {
        return username != null && credentialStore.containsKey(username.trim());
    }
}
