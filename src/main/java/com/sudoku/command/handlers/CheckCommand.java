package com.sudoku.command.handlers;

import com.sudoku.command.Command;
import com.sudoku.command.CommandResult;
import com.sudoku.game.GameSession;
import com.sudoku.util.Messages;

import java.util.List;

/**
 * Command to check the current board for rule violations.
 */
public class CheckCommand implements Command {

    private final GameSession gameSession;

    /**
     * Creates a new CheckCommand.
     *
     * @param gameSession The game session to check
     */
    public CheckCommand(GameSession gameSession) {
        this.gameSession = gameSession;
    }

    @Override
    public CommandResult execute() {
        List<String> violations = gameSession.getViolations();
        if (violations.isEmpty()) {
            return CommandResult.success(Messages.get("msg.no_violations"), false);
        } else {
            String message = String.join("\n", violations);
            return CommandResult.success(message, false);
        }
    }
}
