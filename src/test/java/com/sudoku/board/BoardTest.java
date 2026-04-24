package com.sudoku.board;

import com.sudoku.util.Config;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BoardTest {

    private Board board;

    @BeforeEach
    void setUp() {
        board = new Board();
    }

    @Test
    void shouldInitializeWithEmptyCells() {
        for (int row = 0; row < Config.SIZE; row++) {
            for (int col = 0; col < Config.SIZE; col++) {
                Cell cell = board.getCellAt(new Position(row, col));
                assertThat(cell.isEmpty()).isTrue();
                assertThat(cell.isPrefilled()).isFalse();
            }
        }
    }

    @Test
    void shouldExecuteMove() {
        Position position = new Position(1, 2);
        board.executeMove(position, 5);

        Cell cell = board.getCellAt(position);
        assertThat(cell.getValue()).isEqualTo(5);
        assertThat(cell.isEmpty()).isFalse();
    }

    @Test
    void shouldExecuteClear() {
        Position position = new Position(1, 2);
        board.executeMove(position, 5);

        board.executeClear(position);

        Cell cell = board.getCellAt(position);
        assertThat(cell.isEmpty()).isTrue();
        assertThat(cell.getValue()).isZero();
    }

    @Test
    void shouldSetPrefilledCell() {
        Position position = new Position(3, 4);
        board.setPrefilledCell(position, 7);

        Cell cell = board.getCellAt(position);
        assertThat(cell.getValue()).isEqualTo(7);
        assertThat(cell.isPrefilled()).isTrue();
    }

    @Test
    void shouldClearBoard() {
        Position pos1 = new Position(0, 0);
        Position pos2 = new Position(1, 1);

        board.executeMove(pos1, 3);
        board.setPrefilledCell(pos2, 5);

        board.clearBoard();

        Cell cell1 = board.getCellAt(pos1);
        Cell cell2 = board.getCellAt(pos2);

        assertThat(cell1.isEmpty()).isTrue();
        assertThat(cell2.isEmpty()).isTrue();
        assertThat(cell2.isPrefilled()).isFalse();
    }
}
