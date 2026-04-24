package com.sudoku.game;

import com.sudoku.board.Board;
import com.sudoku.board.Position;
import com.sudoku.util.Config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

/**
 * Generates a randomized Sudoku puzzle using a concurrent backtracking algorithm.
 */
public class PuzzleGeneratorImpl implements PuzzleGenerator {

    /**
     * Stores the complete solution grid.
     */
    private final int[][] solutionGrid = new int[Config.SIZE][Config.SIZE];

    /**
     * Generates a puzzle and applies it to the board.
     */
    @Override
    public void generatePuzzle(Board board, int prefilledCellsCount) {
        int[][] completedGrid = generateCompletedGridConcurrent();
        saveSolutionGrid(completedGrid);

        int[][] puzzleGrid = punchHoles(completedGrid, prefilledCellsCount);
        applyGridToBoard(puzzleGrid, board);
    }

    /**
     * Concurrently generates a valid Sudoku grid.
     */
    private int[][] generateCompletedGridConcurrent() {
        int threadCount = Runtime.getRuntime().availableProcessors();

        List<Callable<int[][]>> tasks = new ArrayList<>();
        for (int i = 0; i < threadCount; i++) {
            tasks.add(() -> {
                int[][] localGrid = new int[Config.SIZE][Config.SIZE];
                if (fillGrid(localGrid, 0, 0)) {
                    return localGrid;
                }
                throw new IllegalStateException("Failed to generate grid.");
            });
        }

        try (ExecutorService executor = Executors.newFixedThreadPool(threadCount)) {
            return executor.invokeAny(tasks);
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to generate puzzle concurrently", e);
        }
    }

    /**
     * Saves the generated grid to the solutionGrid array.
     */
    private void saveSolutionGrid(int[][] generatedGrid) {
        for (int row = 0; row < Config.SIZE; row++) {
            System.arraycopy(generatedGrid[row], 0, solutionGrid[row], 0, Config.SIZE);
        }
    }

    /**
     * Creates a playable puzzle by erasing cells from the completed grid.
     */
    private int[][] punchHoles(int[][] completedGrid, int prefilledCellsCount) {
        int totalCells = Config.SIZE * Config.SIZE;
        int cellsToKeep = Math.min(prefilledCellsCount, totalCells);
        int cellsToRemove = totalCells - cellsToKeep;

        int[][] puzzleGrid = new int[Config.SIZE][Config.SIZE];
        for (int row = 0; row < Config.SIZE; row++) {
            System.arraycopy(completedGrid[row], 0, puzzleGrid[row], 0, Config.SIZE);
        }

        while (cellsToRemove > 0) {
            int row = ThreadLocalRandom.current().nextInt(Config.SIZE);
            int column = ThreadLocalRandom.current().nextInt(Config.SIZE);

            // Only remove cells that haven't been removed yet
            if (puzzleGrid[row][column] != 0) {
                puzzleGrid[row][column] = 0;
                cellsToRemove--;
            }
        }
        return puzzleGrid;
    }

    /**
     * Transfers the puzzle grid onto the game Board.
     */
    private void applyGridToBoard(int[][] puzzleGrid, Board board) {
        for (int row = 0; row < Config.SIZE; row++) {
            for (int column = 0; column < Config.SIZE; column++) {
                int cellValue = puzzleGrid[row][column];
                if (cellValue != 0) {
                    // Use setPrefilledCell so these clues are locked (green) and cannot be cleared
                    board.setPrefilledCell(new Position(row, column), cellValue);
                }
            }
        }
    }

    /**
     * Retrieves the solution value for a specific cell.
     */
    @Override
    public int getSolutionValue(Position position) {
        return solutionGrid[position.row()][position.col()];
    }

    /**
     * Finds the first empty cell on the board.
     */
    @Override
    public Position provideHint(Board board) {
        for (int row = 0; row < Config.SIZE; row++) {
            for (int column = 0; column < Config.SIZE; column++) {
                Position position = new Position(row, column);
                if (board.getCellAt(position).isEmpty()) {
                    return position;
                }
            }
        }
        return null;
    }

    /**
     * Recursive backtracking function to fill the grid.
     */
    private boolean fillGrid(int[][] grid, int row, int column) {
        if (Thread.currentThread().isInterrupted()) {
            return false;
        }

        if (row == Config.SIZE - 1 && column == Config.SIZE) {
            return true;
        }

        if (column == Config.SIZE) {
            row++;
            column = 0;
        }

        if (grid[row][column] != 0) {
            return fillGrid(grid, row, column + 1);
        }

        return tryPlacingNumbers(grid, row, column);
    }

    /**
     * Tries placing valid numbers in randomized order.
     */
    private boolean tryPlacingNumbers(int[][] grid, int row, int column) {
        List<Integer> numbers = generateShuffledNumbers();
        for (int number : numbers) {
            if (isSafe(grid, row, column, number)) {
                grid[row][column] = number;
                if (fillGrid(grid, row, column + 1)) {
                    return true;
                }
                grid[row][column] = 0;
            }
        }
        return false;
    }

    /**
     * Generates a shuffled list of numbers.
     */
    private List<Integer> generateShuffledNumbers() {
        List<Integer> numbers = new ArrayList<>(Config.SIZE);
        for (int index = 1; index <= Config.SIZE; index++) {
            numbers.add(index);
        }
        Collections.shuffle(numbers, ThreadLocalRandom.current());
        return numbers;
    }

    /**
     * Validates if a number can be placed at the given coordinates.
     */
    private boolean isSafe(int[][] grid, int row, int column, int number) {
        return isSafeInRow(grid, row, number)
                && isSafeInColumn(grid, column, number)
                && isSafeInSubgrid(grid, row, column, number);
    }

    private boolean isSafeInRow(int[][] grid, int row, int number) {
        for (int index = 0; index < Config.SIZE; index++) {
            if (grid[row][index] == number) return false;
        }
        return true;
    }

    private boolean isSafeInColumn(int[][] grid, int column, int number) {
        for (int index = 0; index < Config.SIZE; index++) {
            if (grid[index][column] == number) return false;
        }
        return true;
    }

    /**
     * Checks if a number exists in the local subgrid.
     */
    private boolean isSafeInSubgrid(int[][] grid, int row, int column, int number) {
        int startRow = row - row % Config.SUBGRID_ROWS;
        int startColumn = column - column % Config.SUBGRID_COLS;
        for (int subRow = 0; subRow < Config.SUBGRID_ROWS; subRow++) {
            for (int subColumn = 0; subColumn < Config.SUBGRID_COLS; subColumn++) {
                if (grid[subRow + startRow][subColumn + startColumn] == number) {
                    return false;
                }
            }
        }
        return true;
    }
}