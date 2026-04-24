package com.sudoku.command.handlers;

import com.sudoku.command.Command;
import com.sudoku.command.CommandResult;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for QuitCommand.
 */
class QuitCommandTest {

    @Test
    void shouldReturnQuitResult() {
        QuitCommand command = new QuitCommand();
        CommandResult result = command.execute();

        assertThat(result.shouldContinue()).isFalse();
        assertThat(result.getMessage()).isPresent();
    }
}
