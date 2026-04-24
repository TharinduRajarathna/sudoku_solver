package com.sudoku.command;

import com.sudoku.command.handlers.*;
import com.sudoku.game.GameSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Tests for CommandParser.
 */
class CommandParserTest {

    private CommandParser parser;
    private GameSession gameSession;

    @BeforeEach
    void setUp() {
        gameSession = mock(GameSession.class);
        parser = new CommandParser(gameSession);
    }

    @Test
    void shouldParseQuitCommand() {
        Command command = parser.parse("quit");
        assertThat(command).isInstanceOf(QuitCommand.class);
    }

    @Test
    void shouldParseQuitCommandWithWhitespace() {
        Command command = parser.parse("  quit  ");
        assertThat(command).isInstanceOf(QuitCommand.class);
    }

    @Test
    void shouldParseQuitCommandCaseInsensitive() {
        Command command = parser.parse("QUIT");
        assertThat(command).isInstanceOf(QuitCommand.class);
    }

    @Test
    void shouldParseHintCommand() {
        Command command = parser.parse("hint");
        assertThat(command).isInstanceOf(HintCommand.class);
    }

    @Test
    void shouldParseCheckCommand() {
        Command command = parser.parse("check");
        assertThat(command).isInstanceOf(CheckCommand.class);
    }

    @Test
    void shouldParseAutosolveCommand() {
        Command command = parser.parse("autosolve");
        assertThat(command).isInstanceOf(AutosolveCommand.class);
    }

    @Test
    void shouldParseClearCommand() {
        Command command = parser.parse("A1 clear");
        assertThat(command).isInstanceOf(ClearCommand.class);
    }

    @Test
    void shouldParseClearCommandWithUpperCase() {
        Command command = parser.parse("B5 CLEAR");
        assertThat(command).isInstanceOf(ClearCommand.class);
    }

    @Test
    void shouldParsePlaceNumberCommand() {
        Command command = parser.parse("A3 7");
        assertThat(command).isInstanceOf(PlaceNumberCommand.class);
    }

    @Test
    void shouldParsePlaceNumberCommandWithLowerCase() {
        Command command = parser.parse("b4 5");
        assertThat(command).isInstanceOf(PlaceNumberCommand.class);
    }

    @Test
    void shouldReturnInvalidCommandForUnknownSingleWord() {
        Command command = parser.parse("unknown");
        assertThat(command).isInstanceOf(InvalidCommand.class);
    }

    @Test
    void shouldReturnInvalidCommandForTooManyWords() {
        Command command = parser.parse("A3 7 extra");
        assertThat(command).isInstanceOf(InvalidCommand.class);
    }

    @Test
    void shouldReturnInvalidCommandForInvalidNumber() {
        Command command = parser.parse("A3 abc");
        assertThat(command).isInstanceOf(InvalidCommand.class);
    }

    @Test
    void shouldReturnInvalidCommandForInvalidPosition() {
        Command command = parser.parse("ZZ 5");
        assertThat(command).isInstanceOf(InvalidCommand.class);
    }

    @Test
    void shouldReturnInvalidCommandForEmptyString() {
        Command command = parser.parse("");
        assertThat(command).isInstanceOf(InvalidCommand.class);
    }

    @Test
    void shouldHandleExceptionDuringParsing() {
        // Test with invalid position that would throw exception
        Command command = parser.parse("! 5");
        assertThat(command).isInstanceOf(InvalidCommand.class);
    }
}
