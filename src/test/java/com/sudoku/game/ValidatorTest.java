package com.sudoku.game;

import com.sudoku.board.Board;
import com.sudoku.board.Position;
import com.sudoku.util.Config;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ValidatorTest {

    private Validator validator;
    private Board board;

    @BeforeEach
    void setUp() {
        validator = new Validator();
        board = new Board();
    }

    @Test
    void shouldReturnNoViolationsForEmptyBoard() {
        assertThat(validator.isBoardValid(board)).isTrue();
        assertThat(validator.getViolations(board)).isEmpty();
    }

    @Test
    void shouldDetectRowViolation() {
        board.executeMove(new Position(0, 0), 5);
        board.executeMove(new Position(0, 1), 5);

        assertThat(validator.isBoardValid(board)).isFalse();

        List<String> violations = validator.getViolations(board);
        assertThat(violations).anySatisfy(v -> assertThat(v).contains("Number 5 already exists in Row A"));
    }

    @Test
    void shouldDetectColumnViolation() {
        board.executeMove(new Position(0, 2), 7);
        board.executeMove(new Position(1, 2), 7);

        assertThat(validator.isBoardValid(board)).isFalse();

        List<String> violations = validator.getViolations(board);
        assertThat(violations).anySatisfy(v -> assertThat(v).contains("Number 7 already exists in Column 3"));
    }

    @Test
    void shouldDetectSubgridViolation() {
        // Place two identical numbers in the same 3x3 subgrid, but different rows and columns
        // e.g. top-left subgrid
        board.executeMove(new Position(0, 0), 3);
        board.executeMove(new Position(1, 1), 3);

        assertThat(validator.isBoardValid(board)).isFalse();

        List<String> violations = validator.getViolations(board);
        String subgridStr = Config.SUBGRID_ROWS + "x" + Config.SUBGRID_COLS;
        assertThat(violations).anySatisfy(v -> assertThat(v).contains("Number 3 already exists in the same " + subgridStr + " subgrid"));
    }

    @Test
    void shouldReturnNoViolationsForValidMoves() {
        board.executeMove(new Position(0, 0), 1);
        board.executeMove(new Position(0, 1), 2);
        board.executeMove(new Position(1, 0), 3);
        board.executeMove(new Position(1, 1), 4);

        // These don't violate row, col, or the top-left subgrid
        assertThat(validator.isBoardValid(board)).isTrue();
    }
}
