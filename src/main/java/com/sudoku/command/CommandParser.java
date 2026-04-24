package com.sudoku.command;

import com.sudoku.board.Position;
import com.sudoku.command.handlers.*;
import com.sudoku.game.GameSession;
import com.sudoku.util.Constants;

/**
 * Parses user input and returns the appropriate Command.
 * This class encapsulates the logic for interpreting user commands.
 */
public class CommandParser {

    private final GameSession gameSession;

    /**
     * Creates a new CommandParser.
     *
     * @param gameSession The game session that commands will operate on
     */
    public CommandParser(GameSession gameSession) {
        this.gameSession = gameSession;
    }

    /**
     * Parses the user input and returns the corresponding Command.
     *
     * @param input The user's input string
     * @return A Command to execute
     */
    public Command parse(String input) {
        String normalizedInput = input.trim().toLowerCase();
        String[] parts = normalizedInput.split(Constants.SPACE);

        // Handle single-word commands
        if (parts.length == 1) {
            return parseSingleWordCommand(normalizedInput);
        }

        // Handle two-word commands (position + action/number)
        if (parts.length == 2) {
            return parseTwoWordCommand(parts);
        }

        // Invalid format
        return new InvalidCommand();
    }

    /**
     * Parses single-word commands (quit, hint, check, autosolve).
     *
     * @param command The command word
     * @return The corresponding Command
     */
    private Command parseSingleWordCommand(String command) {
        switch (command) {
            case Constants.CMD_QUIT:
                return new QuitCommand();
            case Constants.CMD_HINT:
                return new HintCommand(gameSession);
            case Constants.CMD_CHECK:
                return new CheckCommand(gameSession);
            case Constants.CMD_AUTOSOLVE:
                return new AutosolveCommand(gameSession);
            default:
                return new InvalidCommand();
        }
    }

    /**
     * Parses two-word commands (position clear OR position number).
     *
     * @param parts The split input parts
     * @return The corresponding Command
     */
    private Command parseTwoWordCommand(String[] parts) {
        String positionStr = parts[0].toUpperCase();
        String action = parts[1];

        try {
            Position position = Position.fromCliInput(positionStr);

            // Check if it's a clear command
            if (action.equals(Constants.CMD_CLEAR)) {
                return new ClearCommand(gameSession, position);
            }

            // Try to parse as a number placement
            try {
                int number = Integer.parseInt(action);
                return new PlaceNumberCommand(gameSession, position, number);
            } catch (NumberFormatException e) {
                return new InvalidCommand();
            }
        } catch (Exception e) {
            return new InvalidCommand();
        }
    }
}
