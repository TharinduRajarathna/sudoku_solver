package com.sudoku.game;

import com.sudoku.board.Board;
import com.sudoku.board.Position;
import com.sudoku.util.Config;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.lang.reflect.Method;
import java.util.concurrent.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

class PuzzleGeneratorImplTest {

    private PuzzleGeneratorImpl generator;
    private Board board;

    @BeforeEach
    void setUp() {
        generator = new PuzzleGeneratorImpl();
        board = new Board();
    }

    @Test
    void shouldGeneratePuzzleWithCorrectNumberOfPrefilledCells() {
        int prefilledCount = 15;
        generator.generatePuzzle(board, prefilledCount);

        int actualPrefilledCount = 0;
        for (int r = 0; r < Config.SIZE; r++) {
            for (int c = 0; c < Config.SIZE; c++) {
                if (board.getCellAt(new Position(r, c)).isPrefilled()) {
                    actualPrefilledCount++;
                }
            }
        }

        assertThat(actualPrefilledCount).isEqualTo(prefilledCount);
    }

    @Test
    void shouldHandleMorePrefilledCellsThanTotal() {
        int totalCells = Config.SIZE * Config.SIZE;
        generator.generatePuzzle(board, totalCells + 10);
        
        int actualPrefilledCount = 0;
        for (int r = 0; r < Config.SIZE; r++) {
            for (int c = 0; c < Config.SIZE; c++) {
                if (board.getCellAt(new Position(r, c)).isPrefilled()) {
                    actualPrefilledCount++;
                }
            }
        }
        assertThat(actualPrefilledCount).isEqualTo(totalCells);
    }

    @Test
    void shouldStoreValidSolutionAndProvideCorrectHint() {
        generator.generatePuzzle(board, 10);

        // Find an empty cell to request a hint for
        Position emptyPos = null;
        for (int r = 0; r < Config.SIZE; r++) {
            for (int c = 0; c < Config.SIZE; c++) {
                Position p = new Position(r, c);
                if (board.getCellAt(p).isEmpty()) {
                    emptyPos = p;
                    break;
                }
            }
            if (emptyPos != null) break;
        }

        assertThat(emptyPos).isNotNull();

        // Get solution value
        int hintValue = generator.getSolutionValue(emptyPos);

        // Ensure hint value is within bounds
        assertThat(hintValue).isBetween(1, Config.SIZE);

        // Let's place the hint to see if it violates the validator logic
        board.executeMove(emptyPos, hintValue);
        Validator validator = new Validator();
        assertThat(validator.isBoardValid(board)).isTrue();
    }

    @Test
    void shouldProvideHintPosition() {
        generator.generatePuzzle(board, 20);

        Position hintPosition = generator.provideHint(board);

        assertThat(hintPosition).isNotNull();
        assertThat(board.getCellAt(hintPosition).isEmpty()).isTrue();
    }

    @Test
    void shouldReturnNullHintWhenBoardIsFull() {
        // Generate with all cells prefilled
        generator.generatePuzzle(board, Config.SIZE * Config.SIZE);

        Position hintPosition = generator.provideHint(board);
        assertThat(hintPosition).isNull();
    }

    @Test
    void testFillGridWithPreFilledCell() throws Exception {
        Method fillGridMethod = PuzzleGeneratorImpl.class.getDeclaredMethod("fillGrid", int[][].class, int.class, int.class);
        fillGridMethod.setAccessible(true);

        int[][] grid = new int[Config.SIZE][Config.SIZE];
        grid[0][0] = 1; // Pre-fill first cell

        // Should return true if it can fill the rest
        boolean result = (boolean) fillGridMethod.invoke(generator, grid, 0, 0);
        assertThat(result).isTrue();
        assertThat(grid[0][0]).isEqualTo(1); // Should still be 1
    }

    @Test
    void testFillGridInterrupted() throws Exception {
        Method fillGridMethod = PuzzleGeneratorImpl.class.getDeclaredMethod("fillGrid", int[][].class, int.class, int.class);
        fillGridMethod.setAccessible(true);

        int[][] grid = new int[Config.SIZE][Config.SIZE];
        
        Thread.currentThread().interrupt();
        try {
            boolean result = (boolean) fillGridMethod.invoke(generator, grid, 0, 0);
            assertThat(result).isFalse();
        } finally {
            // Clear interrupted status
            Thread.interrupted();
        }
    }

    @Test
    void testGenerateConcurrentException() throws Exception {
        try (var mockedExecutors = mockStatic(Executors.class)) {
            ExecutorService mockService = mock(ExecutorService.class);
            mockedExecutors.when(() -> Executors.newFixedThreadPool(anyInt())).thenReturn(mockService);
            
            // Mock invokeAny to throw InterruptedException to hit the catch block
            when(mockService.invokeAny(anyList())).thenThrow(new InterruptedException("Test"));
            
            assertThatThrownBy(() -> generator.generatePuzzle(board, 10))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Failed to generate puzzle concurrently");
        }
    }
}
