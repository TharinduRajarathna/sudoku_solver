package com.sudoku.game;

import com.sudoku.board.Board;
import com.sudoku.board.Position;
import com.sudoku.util.Config;
import com.sudoku.util.Constants;
import com.sudoku.util.Messages;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Validates the game grid to ensure there are no duplicates in rows, columns, or subgrids.
 */
public final class Validator {

    /**
     * Checks if the current board state is valid and has no duplicate rules violations.
     *
     * @param board The Sudoku board to check.
     * @return True if the board contains no violations.
     */
    public boolean isBoardValid(Board board) {
        return getViolations(board).isEmpty();
    }

    /**
     * Scans the entire board for rule violations across all rows, columns, and subgrids.
     *
     * @param board The Sudoku board to analyze.
     * @return A list of descriptive violation strings. Empty if no violations exist.
     */
    public List<String> getViolations(Board board) {
        List<String> violations = new ArrayList<>();

        for (int i = 0; i < Config.SIZE; i++) {
            Set<Integer> gridDupes = findDuplicates(getSubgrid(board, i));
            for (int dup : gridDupes) {
                violations.add(Messages.get("violation.subgrid", dup, Config.SUBGRID_ROWS, Config.SUBGRID_COLS));
            }
        }

        for (int i = 0; i < Config.SIZE; i++) {
            Set<Integer> rowDupes = findDuplicates(getRow(board, i));
            for (int dup : rowDupes) {
                violations.add(Messages.get("violation.row", dup, (char) (Constants.ROW_LABEL_BASE + i)));
            }
        }

        for (int i = 0; i < Config.SIZE; i++) {
            Set<Integer> colDupes = findDuplicates(getColumn(board, i));
            for (int dup : colDupes) {
                violations.add(Messages.get("violation.col", dup, (i + 1)));
            }
        }

        return violations;
    }

    /**
     * Helper method to find any numbers that appear more than once in an array of cells.
     * Zeros (empty cells) are ignored since multiple empty cells are perfectly valid.
     *
     * @param cells An array of integers representing a row, column, or subgrid.
     * @return A Set containing the specific numbers that were duplicated.
     */
    private Set<Integer> findDuplicates(int[] cells) {
        Set<Integer> seen = new HashSet<>();
        Set<Integer> duplicates = new HashSet<>();
        for (int value : cells) {
            if (value != 0 && !seen.add(value)) {
                duplicates.add(value);
            }
        }
        return duplicates;
    }

    /**
     * Extracts all the values from a specific horizontal row into a simple array.
     *
     * @param board  The game board.
     * @param rowIdx The 0-based index of the row to extract.
     * @return An array containing the numbers in that row.
     */
    private int[] getRow(Board board, int rowIdx) {
        int[] row = new int[Config.SIZE];
        for (int col = 0; col < Config.SIZE; col++) {
            row[col] = board.getCellAt(new Position(rowIdx, col)).getValue();
        }
        return row;
    }

    /**
     * Extracts all the values from a specific vertical column into a simple array.
     *
     * @param board  The game board.
     * @param colIdx The 0-based index of the column to extract.
     * @return An array containing the numbers in that column.
     */
    private int[] getColumn(Board board, int colIdx) {
        int[] col = new int[Config.SIZE];
        for (int row = 0; row < Config.SIZE; row++) {
            col[row] = board.getCellAt(new Position(row, colIdx)).getValue();
        }
        return col;
    }

    /**
     * Extracts all the values from a specific subgrid block (e.g., a 3x3 square) into a flat array.
     * The subgrids are indexed left-to-right, top-to-bottom.
     * For example, on a 9x9 board, index 0 is top-left, index 1 is top-middle, index 8 is bottom-right.
     *
     * @param board      The game board.
     * @param subgridIdx The 0-based index of the subgrid block.
     * @return A flat array containing all the numbers within that subgrid block.
     */
    private int[] getSubgrid(Board board, int subgridIdx) {
        int[] subgrid = new int[Config.SIZE];
        int subgridsPerRow = Config.SIZE / Config.SUBGRID_COLS;
        int rStart = (subgridIdx / subgridsPerRow) * Config.SUBGRID_ROWS;
        int cStart = (subgridIdx % subgridsPerRow) * Config.SUBGRID_COLS;
        int idx = 0;

        for (int r = 0; r < Config.SUBGRID_ROWS; r++) {
            for (int c = 0; c < Config.SUBGRID_COLS; c++) {
                subgrid[idx++] = board.getCellAt(new Position(rStart + r, cStart + c)).getValue();
            }
        }
        return subgrid;
    }
}