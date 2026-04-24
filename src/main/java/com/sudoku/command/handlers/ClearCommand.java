package com.sudoku.command.handlers;

import com.sudoku.board.Position;
import com.sudoku.command.Command;
import com.sudoku.command.CommandResult;
import com.sudoku.game.GameSession;
import com.sudoku.util.Messages;

/**
 * Command to clear a cell at a specified position.
 */
public class ClearCommand implements Command {

    private final GameSession gameSession;
    private final Position position;

    /**
     * Creates a new ClearCommand.
     *
     * @param gameSession The game session
     * @param position The position to clear
     */
    public ClearCommand(GameSession gameSession, Position position) {
        this.gameSession = gameSession;
        this.position = position;
    }

    @Override
    public CommandResult execute() {
        try {
            gameSession.clearCell(position);
            return CommandResult.success(Messages.get("msg.move_accepted"), true);
        } catch (IllegalStateException e) {
            String posStr = "" + (char) ('A' + position.row()) + (position.col() + 1);
            return CommandResult.success(Messages.get("msg.invalid_move_prefilled", posStr), true);
        } catch (IllegalArgumentException | IndexOutOfBoundsException e) {
            return CommandResult.success(Messages.get("msg.invalid_format"), false);
        } catch (Exception e) {
            return CommandResult.success(Messages.get("msg.error", e.getMessage()), false);
        }
    }
}
