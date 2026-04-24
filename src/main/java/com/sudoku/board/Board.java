package com.sudoku.board;

import com.sudoku.util.Config;

import java.util.stream.IntStream;

/**
 * Manages the dynamic grid layout of Cell objects for the Sudoku game.
 */
public final class Board {

    /**
     * The 2D grid storing the state of the board's cells.
     */
    private final Cell[][] grid;

    /**
     * Constructs a new Board and initializes it with empty cells.
     */
    public Board() {
        this.grid = new Cell[Config.SIZE][Config.SIZE];
        initializeEmptyGrid();
    }

    /**
     * Initializes the grid with empty cells based on configured dimensions.
     */
    private void initializeEmptyGrid() {
        IntStream.range(0, Config.SIZE).forEach(row ->
                IntStream.range(0, Config.SIZE).forEach(col ->
                        grid[row][col] = Cell.createEmpty()
                )
        );
    }

    /**
     * Clears the entire board back to an empty state.
     */
    public void clearBoard() {
        initializeEmptyGrid();
    }

    /**
     * Places a value at the specified position.
     *
     * @param position The grid coordinates.
     * @param value    The value to place.
     */
    public void executeMove(Position position, int value) {
        grid[position.row()][position.col()].placeNumber(value);
    }

    /**
     * Clears the value at the specified position.
     *
     * @param position The grid coordinates.
     */
    public void executeClear(Position position) {
        grid[position.row()][position.col()].clear();
    }

    /**
     * Overwrites a cell specifically for puzzle generation to lock in the starting numbers.
     *
     * @param position The grid coordinates.
     * @param value    The value of the clue.
     */
    public void setPrefilledCell(Position position, int value) {
        grid[position.row()][position.col()] = Cell.createPrefilled(value);
    }

    /**
     * Retrieves the cell at the given position.
     *
     * @param position The grid coordinates.
     * @return The cell at the specified position.
     */
    public Cell getCellAt(Position position) {
        return grid[position.row()][position.col()];
    }
}