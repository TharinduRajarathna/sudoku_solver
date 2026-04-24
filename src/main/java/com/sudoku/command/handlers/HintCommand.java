package com.sudoku.command.handlers;

import com.sudoku.board.Position;
import com.sudoku.command.Command;
import com.sudoku.command.CommandResult;
import com.sudoku.game.GameSession;
import com.sudoku.util.Messages;

import java.util.Optional;

/**
 * Command to request a hint for the next move.
 */
public class HintCommand implements Command {

    private final GameSession gameSession;

    /**
     * Creates a new HintCommand.
     *
     * @param gameSession The game session to provide hints from
     */
    public HintCommand(GameSession gameSession) {
        this.gameSession = gameSession;
    }

    @Override
    public CommandResult execute() {
        Optional<Position> hintPos = gameSession.requestHint();
        if (hintPos.isPresent()) {
            Position pos = hintPos.get();
            int val = gameSession.getHintValue(pos);
            String posStr = "" + (char) ('A' + pos.row()) + (pos.col() + 1);
            return CommandResult.success(Messages.get("msg.hint", posStr, val), false);
        } else {
            return CommandResult.success(Messages.get("msg.no_hint"), false);
        }
    }
}
