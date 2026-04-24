package com.sudoku.command;

/**
 * Represents a user command in the Sudoku game.
 * Implementations of this interface encapsulate specific game actions.
 * This follows the Command Pattern to decouple command parsing from execution.
 */
public interface Command {

    /**
     * Executes the command.
     *
     * @return CommandResult indicating the outcome of the command execution
     */
    CommandResult execute();
}
