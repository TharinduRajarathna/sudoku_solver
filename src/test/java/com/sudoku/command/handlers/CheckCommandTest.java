package com.sudoku.command.handlers;

import com.sudoku.command.Command;
import com.sudoku.command.CommandResult;

import com.sudoku.game.GameSession;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests for CheckCommand.
 */
class CheckCommandTest {

    @Test
    void shouldReturnNoViolationsMessage() {
        GameSession gameSession = mock(GameSession.class);
        when(gameSession.getViolations()).thenReturn(new ArrayList<>());

        CheckCommand command = new CheckCommand(gameSession);
        CommandResult result = command.execute();

        assertThat(result.shouldContinue()).isTrue();
        assertThat(result.shouldRenderBoard()).isFalse();
        assertThat(result.getMessage()).isPresent();
    }

    @Test
    void shouldReturnViolationsMessage() {
        GameSession gameSession = mock(GameSession.class);
        List<String> violations = Arrays.asList("Violation 1", "Violation 2");
        when(gameSession.getViolations()).thenReturn(violations);

        CheckCommand command = new CheckCommand(gameSession);
        CommandResult result = command.execute();

        assertThat(result.shouldContinue()).isTrue();
        assertThat(result.shouldRenderBoard()).isFalse();
        assertThat(result.getMessage()).isPresent();
        assertThat(result.getMessage().get()).contains("Violation 1");
        assertThat(result.getMessage().get()).contains("Violation 2");
    }
}
