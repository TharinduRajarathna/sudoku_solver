package com.sudoku.command.handlers;

import com.sudoku.command.Command;
import com.sudoku.command.CommandResult;

import com.sudoku.board.Board;
import com.sudoku.board.Cell;
import com.sudoku.board.Position;
import com.sudoku.game.GameSession;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests for PlaceNumberCommand.
 */
class PlaceNumberCommandTest {

    @Test
    void shouldPlaceNumberSuccessfully() {
        GameSession gameSession = mock(GameSession.class);
        Board board = mock(Board.class);
        Position position = new Position(0, 0);
        int number = 5;

        when(gameSession.getBoard()).thenReturn(board);
        when(board.getCellAt(any(Position.class))).thenReturn(Cell.createPrefilled(1));
        doNothing().when(gameSession).playMove(position, number);

        PlaceNumberCommand command = new PlaceNumberCommand(gameSession, position, number);
        CommandResult result = command.execute();

        assertThat(result.shouldContinue()).isTrue();
        assertThat(result.shouldRenderBoard()).isTrue();
        assertThat(result.getMessage()).isPresent();
    }

    @Test
    void shouldShowViolationsWhenBoardIsFull() {
        GameSession gameSession = mock(GameSession.class);
        Board board = mock(Board.class);
        Position position = new Position(0, 0);
        int number = 5;

        when(gameSession.getBoard()).thenReturn(board);
        // All cells filled
        when(board.getCellAt(any(Position.class))).thenReturn(Cell.createPrefilled(1));
        when(gameSession.isGameOver()).thenReturn(false);
        when(gameSession.getViolations()).thenReturn(Arrays.asList("Violation 1", "Violation 2"));
        doNothing().when(gameSession).playMove(position, number);

        PlaceNumberCommand command = new PlaceNumberCommand(gameSession, position, number);
        CommandResult result = command.execute();

        assertThat(result.shouldContinue()).isTrue();
        assertThat(result.shouldRenderBoard()).isTrue();
        assertThat(result.getMessage()).isPresent();
        assertThat(result.getMessage().get()).contains("Violation 1");
        assertThat(result.getMessage().get()).contains("Violation 2");
    }

    @Test
    void shouldHandlePrefilledCellError() {
        GameSession gameSession = mock(GameSession.class);
        Position position = new Position(0, 0);
        int number = 5;

        doThrow(new IllegalStateException("Cannot modify prefilled cell"))
                .when(gameSession).playMove(position, number);

        PlaceNumberCommand command = new PlaceNumberCommand(gameSession, position, number);
        CommandResult result = command.execute();

        assertThat(result.shouldContinue()).isTrue();
        assertThat(result.shouldRenderBoard()).isTrue();
        assertThat(result.getMessage()).isPresent();
    }

    @Test
    void shouldHandleInvalidArgumentError() {
        GameSession gameSession = mock(GameSession.class);
        Position position = new Position(0, 0);
        int number = 10; // Invalid number

        doThrow(new IllegalArgumentException("Invalid number"))
                .when(gameSession).playMove(position, number);

        PlaceNumberCommand command = new PlaceNumberCommand(gameSession, position, number);
        CommandResult result = command.execute();

        assertThat(result.shouldContinue()).isTrue();
        assertThat(result.shouldRenderBoard()).isFalse();
        assertThat(result.getMessage()).isPresent();
    }

    @Test
    void shouldHandleIndexOutOfBoundsError() {
        GameSession gameSession = mock(GameSession.class);
        Position position = new Position(0, 0);
        int number = 5;

        doThrow(new IndexOutOfBoundsException("Out of bounds"))
                .when(gameSession).playMove(position, number);

        PlaceNumberCommand command = new PlaceNumberCommand(gameSession, position, number);
        CommandResult result = command.execute();

        assertThat(result.shouldContinue()).isTrue();
        assertThat(result.shouldRenderBoard()).isFalse();
        assertThat(result.getMessage()).isPresent();
    }

    @Test
    void shouldHandleGenericError() {
        GameSession gameSession = mock(GameSession.class);
        Position position = new Position(0, 0);
        int number = 5;

        doThrow(new RuntimeException("Unexpected error"))
                .when(gameSession).playMove(position, number);

        PlaceNumberCommand command = new PlaceNumberCommand(gameSession, position, number);
        CommandResult result = command.execute();

        assertThat(result.shouldContinue()).isTrue();
        assertThat(result.shouldRenderBoard()).isFalse();
        assertThat(result.getMessage()).isPresent();
    }

    @Test
    void shouldNotShowViolationsWhenGameIsOver() {
        GameSession gameSession = mock(GameSession.class);
        Board board = mock(Board.class);
        Position position = new Position(0, 0);
        int number = 5;

        when(gameSession.getBoard()).thenReturn(board);
        // All cells filled
        when(board.getCellAt(any(Position.class))).thenReturn(Cell.createPrefilled(1));
        when(gameSession.isGameOver()).thenReturn(true); // Game is over, no violations
        doNothing().when(gameSession).playMove(position, number);

        PlaceNumberCommand command = new PlaceNumberCommand(gameSession, position, number);
        CommandResult result = command.execute();

        assertThat(result.shouldContinue()).isTrue();
        assertThat(result.shouldRenderBoard()).isTrue();
        assertThat(result.getMessage()).isPresent();
        assertThat(result.getMessage().get()).doesNotContain("Violation");
    }
}
