package com.sudoku.command.handlers;

import com.sudoku.command.Command;
import com.sudoku.command.CommandResult;

import com.sudoku.board.Position;
import com.sudoku.game.GameSession;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

/**
 * Tests for ClearCommand.
 */
class ClearCommandTest {

    @Test
    void shouldClearCellSuccessfully() {
        GameSession gameSession = mock(GameSession.class);
        Position position = new Position(0, 0);
        doNothing().when(gameSession).clearCell(position);

        ClearCommand command = new ClearCommand(gameSession, position);
        CommandResult result = command.execute();

        assertThat(result.shouldContinue()).isTrue();
        assertThat(result.shouldRenderBoard()).isTrue();
        assertThat(result.getMessage()).isPresent();
    }

    @Test
    void shouldHandlePrefilledCellError() {
        GameSession gameSession = mock(GameSession.class);
        Position position = new Position(0, 0);
        doThrow(new IllegalStateException("Cannot modify prefilled cell"))
                .when(gameSession).clearCell(position);

        ClearCommand command = new ClearCommand(gameSession, position);
        CommandResult result = command.execute();

        assertThat(result.shouldContinue()).isTrue();
        assertThat(result.shouldRenderBoard()).isTrue();
        assertThat(result.getMessage()).isPresent();
    }

    @Test
    void shouldHandleInvalidArgumentError() {
        GameSession gameSession = mock(GameSession.class);
        Position position = new Position(0, 0);
        doThrow(new IllegalArgumentException("Invalid position"))
                .when(gameSession).clearCell(position);

        ClearCommand command = new ClearCommand(gameSession, position);
        CommandResult result = command.execute();

        assertThat(result.shouldContinue()).isTrue();
        assertThat(result.shouldRenderBoard()).isFalse();
        assertThat(result.getMessage()).isPresent();
    }

    @Test
    void shouldHandleIndexOutOfBoundsError() {
        GameSession gameSession = mock(GameSession.class);
        Position position = new Position(0, 0);
        doThrow(new IndexOutOfBoundsException("Out of bounds"))
                .when(gameSession).clearCell(position);

        ClearCommand command = new ClearCommand(gameSession, position);
        CommandResult result = command.execute();

        assertThat(result.shouldContinue()).isTrue();
        assertThat(result.shouldRenderBoard()).isFalse();
        assertThat(result.getMessage()).isPresent();
    }

    @Test
    void shouldHandleGenericError() {
        GameSession gameSession = mock(GameSession.class);
        Position position = new Position(0, 0);
        doThrow(new RuntimeException("Unexpected error"))
                .when(gameSession).clearCell(position);

        ClearCommand command = new ClearCommand(gameSession, position);
        CommandResult result = command.execute();

        assertThat(result.shouldContinue()).isTrue();
        assertThat(result.shouldRenderBoard()).isFalse();
        assertThat(result.getMessage()).isPresent();
    }
}
