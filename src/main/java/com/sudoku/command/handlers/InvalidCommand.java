package com.sudoku.command.handlers;

import com.sudoku.command.Command;
import com.sudoku.command.CommandResult;
import com.sudoku.util.Messages;

/**
 * Command representing an invalid or unrecognized user input.
 */
public class InvalidCommand implements Command {

    @Override
    public CommandResult execute() {
        return CommandResult.success(Messages.get("msg.invalid_format"), false);
    }
}
