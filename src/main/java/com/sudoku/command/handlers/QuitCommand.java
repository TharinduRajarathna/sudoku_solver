package com.sudoku.command.handlers;

import com.sudoku.command.Command;
import com.sudoku.command.CommandResult;
import com.sudoku.util.Messages;

/**
 * Command to quit the game.
 */
public class QuitCommand implements Command {

    @Override
    public CommandResult execute() {
        return CommandResult.quit(Messages.get("msg.exit"));
    }
}
