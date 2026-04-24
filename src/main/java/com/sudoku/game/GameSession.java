package com.sudoku.game;

import com.sudoku.board.Board;
import com.sudoku.board.Position;
import com.sudoku.util.Config;

import java.util.Optional;

/**
 * The core controller for the Sudoku game state.
 * Orchestrates interaction between the Board, the Generator, and the Validator.
 */
public class GameSession {

    /**
     * The active Sudoku board.
     */
    private final Board board;

    /**
     * Validates rule violations in the grid.
     */
    private final Validator validator;

    /**
     * Generates the puzzle layout and provides solution hints.
     */
    private final PuzzleGenerator puzzleGenerator;

    /**
     * Initializes the game engine with required dependencies.
     *
     * @param validator       The rules engine validator.
     * @param puzzleGenerator The clue generation algorithm.
     */
    public GameSession(Validator validator, PuzzleGenerator puzzleGenerator) {
        this.board = new Board();
        this.validator = validator;
        this.puzzleGenerator = puzzleGenerator;
    }

    /**
     * Wipes the board clean and initializes a new randomized puzzle.
     */
    public void startNewGame() {
        board.clearBoard();
        int prefilled = (Config.SIZE == 9) ? 30 : (Config.SIZE * Config.SIZE) / 3;
        puzzleGenerator.generatePuzzle(board, prefilled);
    }

    /**
     * Retrieves the correct background solution value for a given coordinate.
     *
     * @param position The position to lookup.
     * @return The correct integer value for that position.
     */
    public int getHintValue(Position position) {
        return puzzleGenerator.getSolutionValue(position);
    }

    /**
     * Attempts to place a user-provided value onto the board.
     *
     * @param position The grid coordinates to mutate.
     * @param value    The numeric value to place.
     */
    public void playMove(Position position, int value) {
        board.executeMove(position, value);
    }

    /**
     * Clears a user-provided value from the board.
     *
     * @param position The grid coordinates to clear.
     */
    public void clearCell(Position position) {
        board.executeClear(position);
    }

    /**
     * Performs a strict validation check of the entire board.
     *
     * @return True if there are zero rule violations.
     */
    public boolean checkValidity() {
        return validator.isBoardValid(board);
    }

    /**
     * Retrieves a list of formatted violation strings.
     *
     * @return A list of errors, or empty if valid.
     */
    public java.util.List<String> getViolations() {
        return validator.getViolations(board);
    }

    /**
     * Requests a hint coordinate from the puzzle generator.
     *
     * @return An Optional containing an empty coordinate, or empty if grid is full.
     */
    public Optional<Position> requestHint() {
        return Optional.ofNullable(puzzleGenerator.provideHint(board));
    }

    /**
     * Checks if the user has successfully solved the puzzle without rule violations.
     *
     * @return True if the grid is entirely full and perfectly valid.
     */
    public boolean isGameOver() {
        if (!validator.isBoardValid(board)) return false;

        for (int r = 0; r < Config.SIZE; r++) {
            for (int c = 0; c < Config.SIZE; c++) if (board.getCellAt(new Position(r, c)).isEmpty()) return false;
        }
        return true;
    }

    /**
     * Returns the underlying board structure.
     *
     * @return The Board instance.
     */
    public Board getBoard() {
        return this.board;
    }
}
