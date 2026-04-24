package com.sudoku.util;

/**
 * Central repository for all static application constants and command strings.
 */
public final class Constants {

    /**
     * Command to exit the application.
     */
    public static final String CMD_QUIT = "quit";

    /**
     * Command to request a hint for a random cell.
     */
    public static final String CMD_HINT = "hint";

    /**
     * Secret command to auto-fill the board up to the last remaining cell for testing.
     */
    public static final String CMD_AUTOSOLVE = "autosolve";

    /**
     * Command to check the current board for any rule violations.
     */
    public static final String CMD_CHECK = "check";

    /**
     * Command to clear a specific cell (e.g. "A3 clear").
     */
    public static final String CMD_CLEAR = "clear";

    /**
     * Separator constant used for splitting commands.
     */
    public static final String SPACE = " ";

    // === Error Message Prefixes ===

    /**
     * Prefix for configuration error messages.
     */
    public static final String ERROR_PREFIX_CONFIG = "\n[Configuration Error] ";

    // === Shell Commands ===

    /**
     * Shell binary path.
     */
    public static final String SHELL_BIN = "/bin/sh";

    /**
     * Shell command flag.
     */
    public static final String SHELL_FLAG_C = "-c";

    /**
     * TTY command to set raw mode for single key input.
     */
    public static final String TTY_CMD_RAW = "stty raw -echo < /dev/tty";

    /**
     * TTY command to restore sane mode after raw input.
     */
    public static final String TTY_CMD_SANE = "stty sane < /dev/tty";

    // === Control Characters ===

    /**
     * ASCII code for Ctrl+C (ETX - End of Text).
     */
    public static final int CTRL_C = 3;

    /**
     * Lowercase 'q' character for quit command.
     */
    public static final char CHAR_QUIT_LOWER = 'q';

    /**
     * Uppercase 'Q' character for quit command.
     */
    public static final char CHAR_QUIT_UPPER = 'Q';

    // === Display Characters ===

    /**
     * Empty cell display character.
     */
    public static final String EMPTY_CELL_DISPLAY = "_";

    /**
     * Base character for row labels (A, B, C, ...).
     */
    public static final char ROW_LABEL_BASE = 'A';

    // === Configuration ===

    /**
     * Resource bundle name for configuration properties.
     */
    public static final String CONFIG_BUNDLE_NAME = "config";

    /**
     * Usage instruction message for command-line execution.
     */
    public static final String USAGE_MESSAGE = "Usage: mvn compile exec:java -Dexec.mainClass=\"com.sudoku.SudokuApplication\" -Dsubgrid.rows=<1-5> -Dsubgrid.cols=<1-5>";

    // === ANSI Color Codes ===

    /**
     * ANSI escape code to set text color to green.
     */
    public static final String ANSI_GREEN = "\u001B[32m";

    /**
     * ANSI escape code to reset text color to the terminal default.
     */
    public static final String ANSI_RESET = "\u001B[0m";

    // === Rendering Spacing ===

    /**
     * Spacing for column header prefix.
     */
    public static final String SPACING_COLUMN_HEADER = "\n   ";

    /**
     * Spacing for subgrid boundary alignment.
     */
    public static final String SPACING_SUBGRID_BOUNDARY = "  ";

    /**
     * Spacing for cell separator.
     */
    public static final String SPACING_CELL = " ";

    /**
     * Vertical border with spacing.
     */
    public static final String BORDER_VERTICAL_SPACED = " ║";

    // === Box Drawing Characters ===

    /**
     * Top-left corner box drawing character.
     */
    public static final String BOX_TOP_LEFT = "╔";

    /**
     * Top intersection box drawing character.
     */
    public static final String BOX_TOP_INTERSECTION = "╦";

    /**
     * Top-right corner box drawing character.
     */
    public static final String BOX_TOP_RIGHT = "╗";

    /**
     * Middle-left intersection box drawing character.
     */
    public static final String BOX_MID_LEFT = "╠";

    /**
     * Middle intersection box drawing character.
     */
    public static final String BOX_MID_INTERSECTION = "╬";

    /**
     * Middle-right intersection box drawing character.
     */
    public static final String BOX_MID_RIGHT = "╣";

    /**
     * Bottom-left corner box drawing character.
     */
    public static final String BOX_BOTTOM_LEFT = "╚";

    /**
     * Bottom intersection box drawing character.
     */
    public static final String BOX_BOTTOM_INTERSECTION = "╩";

    /**
     * Bottom-right corner box drawing character.
     */
    public static final String BOX_BOTTOM_RIGHT = "╝";

    /**
     * Horizontal line box drawing character.
     */
    public static final String BOX_HORIZONTAL = "═";

    /**
     * Private constructor to prevent instantiation.
     */
    private Constants() {
        // Prevent instantiation
    }
}
