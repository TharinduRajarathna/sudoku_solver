package com.sudoku.board;

import com.sudoku.util.Config;
import com.sudoku.util.Constants;
import com.sudoku.util.Messages;

/**
 * Represents a single square cell within the Sudoku board.
 * Tracks both the numerical value and whether it is a prefilled starting clue.
 */
public final class Cell {

    /**
     * Internal representation of an empty cell.
     */
    private static final int EMPTY_VALUE = 0;
    private final boolean isPrefilled;
    private int value;

    /**
     * Private constructor to enforce creation via static factory methods.
     *
     * @param value       The numerical value of the cell.
     * @param isPrefilled True if this cell is a generated clue that cannot be modified.
     */
    private Cell(int value, boolean isPrefilled) {
        this.value = value;
        this.isPrefilled = isPrefilled;
    }

    /**
     * Creates an empty, modifiable cell.
     *
     * @return A new empty cell.
     */
    public static Cell createEmpty() {
        return new Cell(EMPTY_VALUE, false);
    }

    /**
     * Creates a prefilled, immutable clue cell.
     *
     * @param value The value of the clue.
     * @return A new prefilled cell.
     * @throws IllegalArgumentException if the value is out of bounds.
     */
    public static Cell createPrefilled(int value) {
        if (value < 1 || value > Config.SIZE)
            throw new IllegalArgumentException(Messages.get("err.number_range", Config.SIZE));
        return new Cell(value, true);
    }

    /**
     * Updates the value of the cell.
     *
     * @param newValue The new number to place.
     * @throws IllegalStateException    if the cell is prefilled.
     * @throws IllegalArgumentException if the number is out of bounds.
     */
    public void placeNumber(int newValue) {
        if (this.isPrefilled) throw new IllegalStateException(Messages.get("err.modify_prefilled"));
        if (newValue < 1 || newValue > Config.SIZE)
            throw new IllegalArgumentException(Messages.get("err.number_range", Config.SIZE));
        this.value = newValue;
    }

    /**
     * Clears the value of the cell, resetting it to empty.
     *
     * @throws IllegalStateException if the cell is prefilled.
     */
    public void clear() {
        if (this.isPrefilled) throw new IllegalStateException(Messages.get("err.clear_prefilled"));
        this.value = EMPTY_VALUE;
    }

    /**
     * Checks if the cell is currently empty.
     *
     * @return True if the cell is empty, false otherwise.
     */
    public boolean isEmpty() {
        return this.value == EMPTY_VALUE;
    }

    /**
     * Retrieves the current numerical value of the cell.
     *
     * @return The cell's value.
     */
    public int getValue() {
        return this.value;
    }

    /**
     * Checks if the cell is a prefilled starting clue.
     *
     * @return True if the cell is prefilled, false otherwise.
     */
    public boolean isPrefilled() {
        return this.isPrefilled;
    }

    /**
     * Returns a string representation of the cell.
     *
     * @return The string representation of the cell's value, or "_" if empty.
     */
    @Override
    public String toString() {
        return isEmpty() ? Constants.EMPTY_CELL_DISPLAY : String.valueOf(value);
    }
}
