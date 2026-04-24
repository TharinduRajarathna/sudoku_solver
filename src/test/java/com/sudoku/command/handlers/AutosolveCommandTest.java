package com.sudoku.command.handlers;

import com.sudoku.command.Command;
import com.sudoku.command.CommandResult;

import com.sudoku.board.Board;
import com.sudoku.board.Cell;
import com.sudoku.board.Position;
import com.sudoku.game.GameSession;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for AutosolveCommand.
 */
class AutosolveCommandTest {

    @Test
    void shouldAutosolvePuzzleLeavingOneCell() {
        GameSession gameSession = mock(GameSession.class);
        Board board = mock(Board.class);
        when(gameSession.getBoard()).thenReturn(board);

        // Mock 3 empty cells
        when(board.getCellAt(any(Position.class)))
                .thenReturn(Cell.createEmpty())
                .thenReturn(Cell.createEmpty())
                .thenReturn(Cell.createEmpty())
                .thenReturn(Cell.createPrefilled(5))
                .thenReturn(Cell.createPrefilled(3))
                .thenReturn(Cell.createPrefilled(7))
                .thenReturn(Cell.createPrefilled(2))
                .thenReturn(Cell.createPrefilled(8))
                .thenReturn(Cell.createPrefilled(1));

        Position hintPos1 = new Position(0, 0);
        Position hintPos2 = new Position(0, 1);
        when(gameSession.requestHint())
                .thenReturn(Optional.of(hintPos1))
                .thenReturn(Optional.of(hintPos2));
        when(gameSession.getHintValue(any(Position.class))).thenReturn(5);

        AutosolveCommand command = new AutosolveCommand(gameSession);
        CommandResult result = command.execute();

        assertThat(result.shouldContinue()).isTrue();
        assertThat(result.shouldRenderBoard()).isTrue();
        assertThat(result.getMessage()).isPresent();

        // Should fill all but one cell (3 empty - 1 = 2 fills)
        verify(gameSession, times(2)).playMove(any(Position.class), any(Integer.class));
    }
}
