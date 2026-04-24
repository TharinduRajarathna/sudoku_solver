package com.sudoku.command;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for CommandResult class.
 */
class CommandResultTest {

    @Test
    void shouldCreateSuccessResultWithMessage() {
        CommandResult result = CommandResult.success("Test message", true);

        assertThat(result.shouldContinue()).isTrue();
        assertThat(result.shouldRenderBoard()).isTrue();
        assertThat(result.getMessage()).isPresent();
        assertThat(result.getMessage().get()).isEqualTo("Test message");
    }

    @Test
    void shouldCreateSuccessResultWithoutMessage() {
        CommandResult result = CommandResult.success(false);

        assertThat(result.shouldContinue()).isTrue();
        assertThat(result.shouldRenderBoard()).isFalse();
        assertThat(result.getMessage()).isEmpty();
    }

    @Test
    void shouldCreateSuccessResultWithNullMessage() {
        CommandResult result = CommandResult.success(null, true);

        assertThat(result.shouldContinue()).isTrue();
        assertThat(result.shouldRenderBoard()).isTrue();
        assertThat(result.getMessage()).isEmpty();
    }

    @Test
    void shouldCreateQuitResult() {
        CommandResult result = CommandResult.quit("Goodbye");

        assertThat(result.shouldContinue()).isFalse();
        assertThat(result.shouldRenderBoard()).isFalse();
        assertThat(result.getMessage()).isPresent();
        assertThat(result.getMessage().get()).isEqualTo("Goodbye");
    }

    @Test
    void shouldCreateQuitResultWithNullMessage() {
        CommandResult result = CommandResult.quit(null);

        assertThat(result.shouldContinue()).isFalse();
        assertThat(result.shouldRenderBoard()).isFalse();
        assertThat(result.getMessage()).isEmpty();
    }
}
