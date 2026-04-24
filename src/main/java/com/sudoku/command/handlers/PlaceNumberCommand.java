package com.sudoku.command.handlers;

import com.sudoku.board.Position;
import com.sudoku.command.Command;
import com.sudoku.command.CommandResult;
import com.sudoku.game.GameSession;
import com.sudoku.util.Config;
import com.sudoku.util.Messages;

import java.util.List;

/**
 * Command to place a number at a specified position.
 */
public class PlaceNumberCommand implements Command {

    private final GameSession gameSession;
    private final Position position;
    private final int number;

    /**
     * Creates a new PlaceNumberCommand.
     *
     * @param gameSession The game session
     * @param position The position to place the number
     * @param number The number to place
     */
    public PlaceNumberCommand(GameSession gameSession, Position position, int number) {
        this.gameSession = gameSession;
        this.position = position;
        this.number = number;
    }

    @Override
    public CommandResult execute() {
        try {
            gameSession.playMove(position, number);

            StringBuilder messageBuilder = new StringBuilder();
            messageBuilder.append(Messages.get("msg.move_accepted"));

            // Check if board is full and show violations if any
            if (isBoardFull()) {
                if (!gameSession.isGameOver()) {
                    messageBuilder.append("\n");
                    List<String> violations = gameSession.getViolations();
                    for (String v : violations) {
                        messageBuilder.append("\n").append(v);
                    }
                }
            }

            return CommandResult.success(messageBuilder.toString(), true);
        } catch (IllegalStateException e) {
            String posStr = "" + (char) ('A' + position.row()) + (position.col() + 1);
            return CommandResult.success(Messages.get("msg.invalid_move_prefilled", posStr), true);
        } catch (IllegalArgumentException | IndexOutOfBoundsException e) {
            return CommandResult.success(Messages.get("msg.invalid_format"), false);
        } catch (Exception e) {
            return CommandResult.success(Messages.get("msg.error", e.getMessage()), false);
        }
    }

    /**
     * Checks if the board is completely filled.
     *
     * @return true if all cells are filled, false otherwise
     */
    private boolean isBoardFull() {
        for (int r = 0; r < Config.SIZE; r++) {
            for (int c = 0; c < Config.SIZE; c++) {
                if (gameSession.getBoard().getCellAt(new Position(r, c)).isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }
}
