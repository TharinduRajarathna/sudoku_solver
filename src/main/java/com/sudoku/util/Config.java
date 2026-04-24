package com.sudoku.util;

import java.util.ResourceBundle;

/**
 * Central configuration class for the Sudoku engine.
 * Dynamically loads subgrid dimensions and calculates the total grid size.
 */

/**
 * Central configuration class for the Sudoku engine.
 * Dynamically loads subgrid dimensions and calculates the total grid size.
 *
 * <p>Grid dimensions can be overridden at runtime via JVM system properties:
 * <pre>
 *   mvn compile exec:java -Dsubgrid.rows=4 -Dsubgrid.cols=4
 * </pre>
 * Both dimensions are capped at {@value #MAX_SUBGRID_SIZE} to keep puzzle generation
 * within a reasonable time frame.
 */
public final class Config {

    /**
     * The maximum allowed value for either subgrid dimension.
     * A 5x5 subgrid produces a 25x25 board, which is already the practical limit
     * of the concurrent backtracking generator. Anything beyond this risks
     * taking minutes or longer to generate.
     */
    public static final int MAX_SUBGRID_SIZE = 5;
    /**
     * Resource bundle to load default fallback configurations.
     */
    private static final ResourceBundle bundle = ResourceBundle.getBundle(Constants.CONFIG_BUNDLE_NAME);
    /**
     * The number of rows in each subgrid partition.
     */
    public static final int SUBGRID_ROWS = parseAndValidate("subgrid.rows");

    /**
     * The number of columns in each subgrid partition.
     */
    public static final int SUBGRID_COLS = parseAndValidate("subgrid.cols");

    /**
     * The overall width and height of the grid (Calculated dynamically).
     */
    public static final int SIZE = SUBGRID_ROWS * SUBGRID_COLS;

    /**
     * Private constructor to prevent instantiation of utility class.
     */
    private Config() {
        // Prevent instantiation
    }

    /**
     * Reads a dimension property from JVM system properties (with fallback to config.properties),
     * then validates that it falls within the allowed range of 1 to {@value #MAX_SUBGRID_SIZE}.
     *
     * @param key The property key (e.g. "subgrid.rows").
     * @return The validated integer dimension.
     * @throws IllegalArgumentException if the value is outside [1, MAX_SUBGRID_SIZE].
     */
    private static int parseAndValidate(String key) {
        int value = Integer.parseInt(System.getProperty(key, bundle.getString(key)));
        if (value < 1 || value > MAX_SUBGRID_SIZE) {
            throw new IllegalArgumentException(
                    "Invalid value for " + key + ": " + value + ". " +
                            "Subgrid dimensions must be between 1 and " + MAX_SUBGRID_SIZE + " " +
                            "(max supported board size is " + MAX_SUBGRID_SIZE + "x" + MAX_SUBGRID_SIZE +
                            " = " + (MAX_SUBGRID_SIZE * MAX_SUBGRID_SIZE) + "x" + (MAX_SUBGRID_SIZE * MAX_SUBGRID_SIZE) + " grid)."
            );
        }
        return value;
    }
}
