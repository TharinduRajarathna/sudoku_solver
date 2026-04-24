package com.sudoku.board;

import com.sudoku.util.Config;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CellTest {

    @Test
    void shouldCreateEmptyCell() {
        Cell cell = Cell.createEmpty();
        assertThat(cell.isEmpty()).isTrue();
        assertThat(cell.getValue()).isZero();
        assertThat(cell.isPrefilled()).isFalse();
        assertThat(cell).hasToString("_");
    }

    @Test
    void shouldCreatePrefilledCell() {
        Cell cell = Cell.createPrefilled(5);
        assertThat(cell.isEmpty()).isFalse();
        assertThat(cell.getValue()).isEqualTo(5);
        assertThat(cell.isPrefilled()).isTrue();
        assertThat(cell).hasToString("5");
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, 100})
    void shouldThrowExceptionForInvalidPrefilledValue(int invalidValue) {
        if (invalidValue > Config.SIZE || invalidValue < 1) {
            assertThatThrownBy(() -> Cell.createPrefilled(invalidValue))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Number must be 1-");
        }
    }

    @Test
    void shouldPlaceNumberInEmptyCell() {
        Cell cell = Cell.createEmpty();
        cell.placeNumber(3);
        assertThat(cell.getValue()).isEqualTo(3);
        assertThat(cell.isEmpty()).isFalse();
        assertThat(cell).hasToString("3");
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, 100})
    void shouldThrowExceptionWhenPlacingInvalidNumber(int invalidValue) {
        Cell cell = Cell.createEmpty();
        if (invalidValue > Config.SIZE || invalidValue < 1) {
            assertThatThrownBy(() -> cell.placeNumber(invalidValue))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Number must be 1-");
        }
    }

    @Test
    void shouldClearCell() {
        Cell cell = Cell.createEmpty();
        cell.placeNumber(5);
        cell.clear();
        assertThat(cell.isEmpty()).isTrue();
        assertThat(cell.getValue()).isZero();
        assertThat(cell).hasToString("_");
    }

    @Test
    void shouldThrowExceptionWhenModifyingPrefilledCell() {
        Cell cell = Cell.createPrefilled(5);
        assertThatThrownBy(() -> cell.placeNumber(3))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("You cannot modify pre-filled cells.");
    }

    @Test
    void shouldThrowExceptionWhenClearingPrefilledCell() {
        Cell cell = Cell.createPrefilled(5);
        assertThatThrownBy(cell::clear)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("You cannot clear pre-filled cells.");
    }
}
