package com.sudoku.command.handlers;

import com.sudoku.command.Command;
import com.sudoku.command.CommandResult;

import com.sudoku.board.Position;
import com.sudoku.game.GameSession;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests for HintCommand.
 */
class HintCommandTest {

    @Test
    void shouldReturnHintWhenAvailable() {
        GameSession gameSession = mock(GameSession.class);
        Position hintPos = new Position(0, 2);
        when(gameSession.requestHint()).thenReturn(Optional.of(hintPos));
        when(gameSession.getHintValue(hintPos)).thenReturn(5);

        HintCommand command = new HintCommand(gameSession);
        CommandResult result = command.execute();

        assertThat(result.shouldContinue()).isTrue();
        assertThat(result.shouldRenderBoard()).isFalse();
        assertThat(result.getMessage()).isPresent();
        assertThat(result.getMessage().get()).contains("A3");
        assertThat(result.getMessage().get()).contains("5");
    }

    @Test
    void shouldReturnNoHintMessageWhenNotAvailable() {
        GameSession gameSession = mock(GameSession.class);
        when(gameSession.requestHint()).thenReturn(Optional.empty());

        HintCommand command = new HintCommand(gameSession);
        CommandResult result = command.execute();

        assertThat(result.shouldContinue()).isTrue();
        assertThat(result.shouldRenderBoard()).isFalse();
        assertThat(result.getMessage()).isPresent();
    }
}
