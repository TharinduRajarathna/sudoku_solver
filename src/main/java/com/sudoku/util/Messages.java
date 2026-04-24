package com.sudoku.util;

import java.text.MessageFormat;
import java.util.ResourceBundle;

/**
 * Utility class to retrieve and format externalized string resources.
 * Supports localization and prevents hardcoded strings in the application logic.
 */
public final class Messages {

    /**
     * The loaded resource bundle containing externalized strings.
     */
    private static final ResourceBundle bundle = ResourceBundle.getBundle("messages");

    /**
     * Private constructor to prevent instantiation.
     */
    private Messages() {
        // Prevent instantiation
    }

    /**
     * Retrieves a message string from the resources.
     *
     * @param key The property key.
     * @return The unformatted string.
     */
    public static String get(String key) {
        return bundle.getString(key);
    }

    /**
     * Retrieves a formatted message string from the resources.
     *
     * @param key  The property key.
     * @param args Dynamic arguments to interpolate into the string (e.g., {0}, {1}).
     * @return The fully formatted string.
     */
    public static String get(String key, Object... args) {
        return MessageFormat.format(bundle.getString(key), args);
    }
}
