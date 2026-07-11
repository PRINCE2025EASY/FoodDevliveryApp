package com.delivery;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
        credentialStore.put("prince", "prince123");
        roleStore.put("prince", Role.ADMIN);

        credentialStore.put("linda", "linda123");
        roleStore.put("linda123", Role.CUSTOMER);
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
