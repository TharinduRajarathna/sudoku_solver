package com.sudoku.board;

import com.sudoku.util.Config;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PositionTest {

    @Test
    void shouldCreateValidPosition() {
        Position position = new Position(0, 0);
        assertThat(position.row()).isZero();
        assertThat(position.col()).isZero();

        position = new Position(Config.SIZE - 1, Config.SIZE - 1);
        assertThat(position.row()).isEqualTo(Config.SIZE - 1);
        assertThat(position.col()).isEqualTo(Config.SIZE - 1);
    }

    @ParameterizedTest
    @CsvSource({
            "-1, 0",
            "0, -1",
            "-1, -1",
            "100, 0",
            "0, 100",
            "100, 100"
    })
    void shouldThrowExceptionForInvalidPosition(int row, int col) {
        if (row >= Config.SIZE || col >= Config.SIZE || row < 0 || col < 0) {
            assertThatThrownBy(() -> new Position(row, col))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Position must be strictly within the");
        }
    }

    @Test
    void shouldParseValidCliInput() {
        Position position = Position.fromCliInput("A1");
        assertThat(position.row()).isZero();
        assertThat(position.col()).isZero();

        position = Position.fromCliInput("b3");
        assertThat(position.row()).isEqualTo(1);
        assertThat(position.col()).isEqualTo(2);

        // Max typical grid string
        char lastRow = (char) ('A' + Config.SIZE - 1);
        String lastCell = lastRow + String.valueOf(Config.SIZE);
        position = Position.fromCliInput(lastCell);
        assertThat(position.row()).isEqualTo(Config.SIZE - 1);
        assertThat(position.col()).isEqualTo(Config.SIZE - 1);
    }
}
