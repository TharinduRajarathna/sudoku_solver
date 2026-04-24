package com.sudoku.command;

import java.util.Optional;

/**
 * Represents the result of executing a command.
 * Encapsulates the outcome and any message to display to the user.
 */
public class CommandResult {

    private final boolean shouldContinue;
    private final boolean shouldRenderBoard;
    private final Optional<String> message;

    private CommandResult(boolean shouldContinue, boolean shouldRenderBoard, Optional<String> message) {
        this.shouldContinue = shouldContinue;
        this.shouldRenderBoard = shouldRenderBoard;
        this.message = message;
    }

    /**
     * Creates a result indicating the command was successful and the game should continue.
     *
     * @param message Optional message to display
     * @param shouldRenderBoard Whether to render the board after this command
     * @return A CommandResult
     */
    public static CommandResult success(String message, boolean shouldRenderBoard) {
        return new CommandResult(true, shouldRenderBoard, Optional.ofNullable(message));
    }

    /**
     * Creates a result indicating the command was successful with no message.
     *
     * @param shouldRenderBoard Whether to render the board after this command
     * @return A CommandResult
     */
    public static CommandResult success(boolean shouldRenderBoard) {
        return new CommandResult(true, shouldRenderBoard, Optional.empty());
    }

    /**
     * Creates a result indicating the game should quit.
     *
     * @param message Optional message to display
     * @return A CommandResult
     */
    public static CommandResult quit(String message) {
        return new CommandResult(false, false, Optional.ofNullable(message));
    }

    /**
     * @return true if the game should continue, false if it should quit
     */
    public boolean shouldContinue() {
        return shouldContinue;
    }

    /**
     * @return true if the board should be rendered after this command
     */
    public boolean shouldRenderBoard() {
        return shouldRenderBoard;
    }

    /**
     * @return Optional message to display to the user
     */
    public Optional<String> getMessage() {
        return message;
    }
}
