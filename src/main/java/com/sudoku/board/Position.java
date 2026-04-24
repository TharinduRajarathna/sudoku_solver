package com.sudoku.board;

import com.sudoku.util.Config;
import com.sudoku.util.Constants;
import com.sudoku.util.Messages;

/**
 * An immutable record representing a specific 2D coordinate on the Sudoku board.
 *
 * @param row The 0-indexed row coordinate.
 * @param col The 0-indexed column coordinate.
 */
public record Position(int row, int col) {

    /**
     * Compact constructor that validates the bounds of the coordinate.
     *
     * @throws IllegalArgumentException if the row or col exceeds the grid size boundaries.
     */
    public Position {
        if (row < 0 || row > Config.SIZE - 1 || col < 0 || col > Config.SIZE - 1) {
            throw new IllegalArgumentException(Messages.get("err.out_of_bounds", Config.SIZE));
        }
    }

    /**
     * Parses CLI input like "B3" or "D16" into a valid Position coordinate.
     *
     * @param input The raw string input from the CLI.
     * @return A valid Position object.
     */
    public static Position fromCliInput(String input) {
        var rowChar = input.toUpperCase().charAt(0);
        var colStr = input.substring(1);
        var row = rowChar - Constants.ROW_LABEL_BASE;
        var col = Integer.parseInt(colStr) - 1;
        return new Position(row, col);
    }
}
