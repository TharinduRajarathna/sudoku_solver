package com.sudoku.game;

import com.sudoku.board.Board;
import com.sudoku.board.Position;

/**
 * Responsible for generating boards and providing hints.
 */
public interface PuzzleGenerator {

    /**
     * Generates a puzzle on the board with a specified number of pre-filled cells.
     * The generator must ensure that the puzzle has at least one valid solution.
     *
     * @param board          The Sudoku board to populate.
     * @param prefilledCount The number of starting clues to leave on the board.
     */
    void generatePuzzle(Board board, int prefilledCount);

    /**
     * Scans the current board and finds an empty cell, returning its position
     * so that a hint can be provided to the user.
     *
     * @param board The active Sudoku board.
     * @return The position of an empty cell, or null if the board is completely full.
     */
    Position provideHint(Board board);

    /**
     * Retrieves the correct background solution value for a given position.
     * This is used to determine what number *should* go in a specific cell
     * according to the initially generated completed grid.
     *
     * @param position The grid coordinates to check.
     * @return The correct integer solution for that cell.
     */
    int getSolutionValue(Position position);
}