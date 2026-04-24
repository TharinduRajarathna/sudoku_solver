package com.sudoku.command.handlers;

import com.sudoku.board.Position;
import com.sudoku.command.Command;
import com.sudoku.command.CommandResult;
import com.sudoku.game.GameSession;
import com.sudoku.util.Config;
import com.sudoku.util.Messages;

/**
 * Command to automatically solve the puzzle (leaving one empty cell for the user).
 */
public class AutosolveCommand implements Command {

    private final GameSession gameSession;

    /**
     * Creates a new AutosolveCommand.
     *
     * @param gameSession The game session to autosolve
     */
    public AutosolveCommand(GameSession gameSession) {
        this.gameSession = gameSession;
    }

    @Override
    public CommandResult execute() {
        // Count empty cells
        int emptyCount = 0;
        for (int r = 0; r < Config.SIZE; r++) {
            for (int c = 0; c < Config.SIZE; c++) {
                if (gameSession.getBoard().getCellAt(new Position(r, c)).isEmpty()) {
                    emptyCount++;
                }
            }
        }

        // Fill all but one empty cell
        for (int i = 0; i < emptyCount - 1; i++) {
            gameSession.requestHint().ifPresent(pos -> {
                int val = gameSession.getHintValue(pos);
                gameSession.playMove(pos, val);
            });
        }

        return CommandResult.success(Messages.get("msg.autosolved"), true);
    }
}
