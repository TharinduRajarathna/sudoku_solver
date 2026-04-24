package com.sudoku;

import com.sudoku.game.GameSession;
import com.sudoku.game.PuzzleGenerator;
import com.sudoku.game.Validator;
import com.sudoku.ui.ConsoleRenderer;
import com.sudoku.board.Board;
import com.sudoku.board.Position;
import com.sudoku.util.Config;
import com.sudoku.util.Constants;
import org.mockito.ArgumentMatchers;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.security.Permission;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SudokuApplicationTest {

    private GameSession gameSession;
    private PuzzleGenerator puzzleGenerator;
    private Validator validator;
    private ConsoleRenderer renderer;
    private Board board;
    private SudokuApplication application;
    
    private final InputStream systemIn = System.in;
    private final PrintStream systemOut = System.out;
    private final ByteArrayOutputStream testOut = new ByteArrayOutputStream();

    @BeforeEach
    void setUp() {
        validator = new Validator();
        puzzleGenerator = mock(PuzzleGenerator.class);
        renderer = mock(ConsoleRenderer.class);
        gameSession = new GameSession(validator, puzzleGenerator);
        board = gameSession.getBoard();
        application = new SudokuApplication(gameSession, renderer);
        System.setOut(new PrintStream(testOut));
        System.setErr(new PrintStream(testOut));
    }

    private void provideInput(String data) {
        System.setIn(new ByteArrayInputStream(data.getBytes()));
    }

    @Test
    void shouldHandleAutosolve() {
        provideInput("autosolve\nquit\n");
        Position pos = new Position(0, 0);
        when(puzzleGenerator.provideHint(any())).thenReturn(pos).thenReturn(null);
        when(puzzleGenerator.getSolutionValue(pos)).thenReturn(1);
        application.run();
        assertThat(testOut.toString()).contains("Autosolved");
    }

    @Test
    void shouldHandleHint() {
        provideInput("hint\nquit\n");
        Position pos = new Position(0, 0);
        when(puzzleGenerator.provideHint(any())).thenReturn(pos);
        when(puzzleGenerator.getSolutionValue(pos)).thenReturn(5);
        application.run();
        assertThat(testOut.toString()).contains("Hint: Cell A1 = 5");
    }

    @Test
    void shouldHandleHintNone() {
        provideInput("hint\nquit\n");
        when(puzzleGenerator.provideHint(any())).thenReturn(null);
        application.run();
        assertThat(testOut.toString()).contains("No hint could be generated.");
    }

    @Test
    void shouldHandleCheck() {
        provideInput("check\nquit\n");
        application.run();
        assertThat(testOut.toString()).contains("No rule violations detected.");
    }

    @Test
    void shouldHandleCheckViolations() {
        provideInput("check\nquit\n");
        doAnswer(invocation -> {
            Board b = invocation.getArgument(0);
            b.executeMove(new Position(0,0), 1);
            b.executeMove(new Position(0,1), 1);
            return null;
        }).when(puzzleGenerator).generatePuzzle(any(), anyInt());
        application.run();
        assertThat(testOut.toString()).contains("already exists");
    }

    @Test
    void shouldHandleClearAndInvalid() {
        provideInput("A1 clear\ninvalid\nA1 0\nquit\n");
        doAnswer(invocation -> {
            Board b = invocation.getArgument(0);
            b.setPrefilledCell(new Position(0,0), 5);
            return null;
        }).when(puzzleGenerator).generatePuzzle(any(), anyInt());
        application.run();
        assertThat(testOut.toString()).contains("Invalid move. A1 is pre-filled.");
        assertThat(testOut.toString()).contains("Invalid format");
    }

    @Test
    void shouldHandleWinAndPlayAgain() {
        // Win -> Play again 'n' -> Win -> Quit via catch block
        provideInput("n\nquit\n");
        doAnswer(invocation -> {
            Board b = invocation.getArgument(0);
            for (int r = 0; r < Config.SIZE; r++) {
                for (int c = 0; c < Config.SIZE; c++) {
                    int val = (r * Config.SUBGRID_ROWS + r / Config.SUBGRID_ROWS + c) % Config.SIZE + 1;
                    b.setPrefilledCell(new Position(r, c), val);
                }
            }
            return null;
        }).when(puzzleGenerator).generatePuzzle(any(), anyInt());
        application.run();
        assertThat(testOut.toString()).contains("successfully completed");
    }

    @Test
    void shouldHandleWinAndImmediateQuit() {
        provideInput("q\n");
        doAnswer(invocation -> {
            Board b = invocation.getArgument(0);
            for (int r = 0; r < Config.SIZE; r++) {
                for (int c = 0; c < Config.SIZE; c++) {
                    int val = (r * Config.SUBGRID_ROWS + r / Config.SUBGRID_ROWS + c) % Config.SIZE + 1;
                    b.setPrefilledCell(new Position(r, c), val);
                }
            }
            return null;
        }).when(puzzleGenerator).generatePuzzle(any(), anyInt());
        application.run();
        assertThat(testOut.toString()).contains("Exiting game. Goodbye!");
    }

    @Test
    void shouldHandleFullButInvalid() {
        provideInput("A1 1\nquit\n");
        doAnswer(invocation -> {
            Board b = invocation.getArgument(0);
            for (int r = 0; r < Config.SIZE; r++) {
                for (int c = 0; c < Config.SIZE; c++) {
                    if (r == 0 && c == 0) continue;
                    b.setPrefilledCell(new Position(r, c), 1);
                }
            }
            return null;
        }).when(puzzleGenerator).generatePuzzle(any(), anyInt());
        application.run();
        assertThat(testOut.toString()).contains("already exists");
    }

    @Test
    void shouldHandleGenericException() {
        provideInput("A1 1\nquit\n");
        // We need a spy to throw from playMove
        gameSession = spy(gameSession);
        application = new SudokuApplication(gameSession, renderer);
        doThrow(new RuntimeException("Oops")).when(gameSession).playMove(any(), anyInt());
        application.run();
        assertThat(testOut.toString()).contains("Error: Oops");
    }

    @Test
    void shouldHandleClearGenericException() {
        provideInput("A1 clear\nquit\n");
        gameSession = spy(gameSession);
        application = new SudokuApplication(gameSession, renderer);
        doThrow(new RuntimeException("Clear error")).when(gameSession).clearCell(any());
        application.run();
        assertThat(testOut.toString()).contains("Error: Clear error");
    }

    @Test
    void shouldHandleClearInvalidFormat() {
        provideInput("A clear\nquit\n"); // IndexOutOfBoundsException in Position.fromCliInput
        application.run();
        assertThat(testOut.toString()).contains("Invalid format");
    }

    @Test
    void shouldHandleMoveInvalidFormat() {
        provideInput("A1\nquit\n"); // parts.length != 2
        application.run();
        assertThat(testOut.toString()).contains("Invalid format");
    }

    @Test
    void shouldHandleMoveInvalidNumberFormat() {
        provideInput("A1 X\nquit\n"); // NumberFormatException
        application.run();
        assertThat(testOut.toString()).contains("Invalid format");
    }

    @Test
    void shouldHandleWinAndContinue() throws Exception {
        // Mock Runtime to return a successful process
        try (var runtimeMockedStatic = mockStatic(Runtime.class)) {
            Runtime runtime = mock(Runtime.class);
            runtimeMockedStatic.when(Runtime::getRuntime).thenReturn(runtime);
            Process process = mock(Process.class);
            when(runtime.exec(any(String[].class))).thenReturn(process);
            when(process.waitFor()).thenReturn(0);

            // System.in.read() needs to return something that is NOT 'q'
            // We provide "y\nquit\n". System.in.read() will take 'y' (121)
            provideInput("y\nquit\n");

            doAnswer(invocation -> {
                Board b = invocation.getArgument(0);
                for (int r = 0; r < Config.SIZE; r++) {
                    for (int c = 0; c < Config.SIZE; c++) {
                        int val = (r * Config.SUBGRID_ROWS + r / Config.SUBGRID_ROWS + c) % Config.SIZE + 1;
                        b.setPrefilledCell(new Position(r, c), val);
                    }
                }
                return null;
            }).when(puzzleGenerator).generatePuzzle(any(), anyInt());

            // We need to use a real gameSession or a carefully prepared one
            // because startNewGame will be called twice.
            gameSession = new GameSession(validator, puzzleGenerator);
            application = new SudokuApplication(gameSession, renderer);

            application.run();
            assertThat(testOut.toString()).contains("successfully completed");
            assertThat(testOut.toString()).contains("Press any key to play again...");
        }
    }

    @Test
    void shouldHandleWinAndQuitWithQ() throws Exception {
        try (var runtimeMockedStatic = mockStatic(Runtime.class)) {
            Runtime runtime = mock(Runtime.class);
            runtimeMockedStatic.when(Runtime::getRuntime).thenReturn(runtime);
            Process process = mock(Process.class);
            when(runtime.exec(any(String[].class))).thenReturn(process);
            when(process.waitFor()).thenReturn(0);

            provideInput("Q\n");

            doAnswer(invocation -> {
                Board b = invocation.getArgument(0);
                for (int r = 0; r < Config.SIZE; r++) {
                    for (int c = 0; c < Config.SIZE; c++) {
                        int val = (r * Config.SUBGRID_ROWS + r / Config.SUBGRID_ROWS + c) % Config.SIZE + 1;
                        b.setPrefilledCell(new Position(r, c), val);
                    }
                }
                return null;
            }).when(puzzleGenerator).generatePuzzle(any(), anyInt());

            gameSession = new GameSession(validator, puzzleGenerator);
            application = new SudokuApplication(gameSession, renderer);

            application.run();
            assertThat(testOut.toString()).contains("successfully completed");
        }
    }

    @Test
    void shouldHandleWinAndQuitWithCtrlC() throws Exception {
        try (var runtimeMockedStatic = mockStatic(Runtime.class)) {
            Runtime runtime = mock(Runtime.class);
            runtimeMockedStatic.when(Runtime::getRuntime).thenReturn(runtime);
            Process process = mock(Process.class);
            when(runtime.exec(any(String[].class))).thenReturn(process);
            when(process.waitFor()).thenReturn(0);

            provideInput(new String(new byte[]{3})); // Ctrl+C

            doAnswer(invocation -> {
                Board b = invocation.getArgument(0);
                for (int r = 0; r < Config.SIZE; r++) {
                    for (int c = 0; c < Config.SIZE; c++) {
                        int val = (r * Config.SUBGRID_ROWS + r / Config.SUBGRID_ROWS + c) % Config.SIZE + 1;
                        b.setPrefilledCell(new Position(r, c), val);
                    }
                }
                return null;
            }).when(puzzleGenerator).generatePuzzle(any(), anyInt());

            gameSession = new GameSession(validator, puzzleGenerator);
            application = new SudokuApplication(gameSession, renderer);

            application.run();
            assertThat(testOut.toString()).contains("successfully completed");
        }
    }

    @Test
    void testTerminateLogic() {
        try (var runtimeMockedStatic = mockStatic(Runtime.class)) {
            Runtime runtime = mock(Runtime.class);
            runtimeMockedStatic.when(Runtime::getRuntime).thenReturn(runtime);

            application.terminate(42);
            verify(runtime).exit(42);
        }
    }

    @Test
    void testMain() {
        provideInput("quit\n");
        SudokuApplication.main(new String[]{});
        assertThat(testOut.toString()).contains("Exiting game. Goodbye!");
    }

    @Test
    void testExecuteIllegalArgumentException() {
        application = spy(application);
        doNothing().when(application).terminate(anyInt());
        doThrow(new IllegalArgumentException("Forced IAE")).when(application).run();

        application.execute();
        assertThat(testOut.toString()).contains("[Configuration Error] Forced IAE");
        verify(application).terminate(1);
    }

    @Test
    void testExecuteExceptionInInitializerError() {
        application = spy(application);
        doNothing().when(application).terminate(anyInt());
        doThrow(new ExceptionInInitializerError("Static fail")).when(application).run();

        application.execute();
        assertThat(testOut.toString()).contains("[Configuration Error] Static fail");
        verify(application).terminate(1);
    }

    @Test
    void testExecuteExceptionInInitializerErrorWithCause() {
        application = spy(application);
        doNothing().when(application).terminate(anyInt());
        doThrow(new ExceptionInInitializerError(new RuntimeException("Nested fail"))).when(application).run();

        application.execute();
        assertThat(testOut.toString()).contains("[Configuration Error] Nested fail");
        verify(application).terminate(1);
    }

    @Test
    void shouldHandleAutosolvePartial() {
        provideInput("autosolve\nquit\n");
        // emptyCount = 2
        Position pos1 = new Position(0, 0);
        when(puzzleGenerator.provideHint(any())).thenReturn(pos1).thenReturn(null);
        when(puzzleGenerator.getSolutionValue(pos1)).thenReturn(1);
        
        doAnswer(invocation -> {
            Board b = invocation.getArgument(0);
            b.executeMove(new Position(0, 1), 2); // One cell empty at (0,0)
            return null;
        }).when(puzzleGenerator).generatePuzzle(any(), anyInt());
        
        gameSession = new GameSession(validator, puzzleGenerator);
        application = new SudokuApplication(gameSession, renderer);
        application.run();
        assertThat(testOut.toString()).contains("Autosolved");
    }

    @Test
    void shouldHandleAutosolveOneEmpty() {
        provideInput("autosolve\nquit\n");
        // emptyCount = 1 -> loop doesn't run
        doAnswer(invocation -> {
            Board b = invocation.getArgument(0);
            for (int r = 0; r < Config.SIZE; r++) {
                for (int c = 0; c < Config.SIZE; c++) {
                    if (r == 0 && c == 0) continue;
                    b.setPrefilledCell(new Position(r, c), 1);
                }
            }
            return null;
        }).when(puzzleGenerator).generatePuzzle(any(), anyInt());

        gameSession = new GameSession(validator, puzzleGenerator);
        application = new SudokuApplication(gameSession, renderer);
        application.run();
        assertThat(testOut.toString()).contains("Autosolved");
    }

    @Test
    void testMainExceptionInInitializerError() {
        try (MockedConstruction<SudokuApplication> mocked = mockConstruction(SudokuApplication.class, (mock, context) -> {
            doThrow(new ExceptionInInitializerError("Static fail in main")).when(mock).execute();
        })) {
            SudokuApplication.main(new String[]{});
            assertThat(testOut.toString()).contains("[Configuration Error] Static fail in main");
        }
    }

    @Test
    void testMainIllegalArgumentException() {
        try (MockedConstruction<SudokuApplication> mocked = mockConstruction(SudokuApplication.class, (mock, context) -> {
            doThrow(new IllegalArgumentException("Config fail in main")).when(mock).execute();
        })) {
            SudokuApplication.main(new String[]{});
            assertThat(testOut.toString()).contains("[Configuration Error] Config fail in main");
        }
    }

    @Test
    void shouldHandleWinAndQuitWithSmallQ() throws Exception {
        try (var runtimeMockedStatic = mockStatic(Runtime.class)) {
            Runtime runtime = mock(Runtime.class);
            runtimeMockedStatic.when(Runtime::getRuntime).thenReturn(runtime);
            Process process = mock(Process.class);
            when(runtime.exec(any(String[].class))).thenReturn(process);
            when(process.waitFor()).thenReturn(0);

            provideInput("q\n");

            doAnswer(invocation -> {
                Board b = invocation.getArgument(0);
                for (int r = 0; r < Config.SIZE; r++) {
                    for (int c = 0; c < Config.SIZE; c++) {
                        int val = (r * Config.SUBGRID_ROWS + r / Config.SUBGRID_ROWS + c) % Config.SIZE + 1;
                        b.setPrefilledCell(new Position(r, c), val);
                    }
                }
                return null;
            }).when(puzzleGenerator).generatePuzzle(any(), anyInt());

            gameSession = new GameSession(validator, puzzleGenerator);
            application = new SudokuApplication(gameSession, renderer);

            application.run();
            assertThat(testOut.toString()).contains("successfully completed");
        }
    }

    @Test
    void shouldHandleClearSuccess() {
        provideInput("A1 clear\nquit\n");
        // Board is empty, A1 is not pre-filled
        application.run();
        assertThat(testOut.toString()).contains("Move accepted");
    }

    @Test
    void shouldHandleNormalMoveNotFull() {
        provideInput("A1 5\nquit\n");
        // Board is mostly empty
        application.run();
        assertThat(testOut.toString()).contains("Move accepted");
    }

    @Test
    void shouldHandleFullAndValidButNotGameOver() {
        // This case covers if (isFull) { if (!isGameOver()) { ... } }
        // when isFull=true and isGameOver=true (inner if branch missed)
        provideInput("A1 1\nquit\n");
        doAnswer(invocation -> {
            Board b = invocation.getArgument(0);
            for (int r = 0; r < Config.SIZE; r++) {
                for (int c = 0; c < Config.SIZE; c++) {
                    int val = (r * Config.SUBGRID_ROWS + r / Config.SUBGRID_ROWS + c) % Config.SIZE + 1;
                    if (r == 0 && c == 0) continue;
                    b.setPrefilledCell(new Position(r, c), val);
                }
            }
            return null;
        }).when(puzzleGenerator).generatePuzzle(any(), anyInt());

        when(puzzleGenerator.getSolutionValue(new Position(0, 0))).thenReturn(1);
        
        gameSession = new GameSession(validator, puzzleGenerator);
        application = new SudokuApplication(gameSession, renderer);
        application.run();
        // A1 1 completes the board and makes it valid.
        // isFull=true, isGameOver=true. Inner if (!isGameOver()) is skipped.
        assertThat(testOut.toString()).contains("Move accepted");
    }

    @Test
    void shouldHandleWinAndQuitViaException() throws Exception {
        try (var runtimeMockedStatic = mockStatic(Runtime.class)) {
            Runtime runtime = mock(Runtime.class);
            runtimeMockedStatic.when(Runtime::getRuntime).thenReturn(runtime);
            when(runtime.exec(any(String[].class))).thenThrow(new RuntimeException("Forced stty fail"));

            provideInput("quit\n");
            doAnswer(invocation -> {
                Board b = invocation.getArgument(0);
                for (int r = 0; r < Config.SIZE; r++) {
                    for (int c = 0; c < Config.SIZE; c++) {
                        int val = (r * Config.SUBGRID_ROWS + r / Config.SUBGRID_ROWS + c) % Config.SIZE + 1;
                        b.setPrefilledCell(new Position(r, c), val);
                    }
                }
                return null;
            }).when(puzzleGenerator).generatePuzzle(any(), anyInt());

            gameSession = new GameSession(validator, puzzleGenerator);
            application = new SudokuApplication(gameSession, renderer);

            application.run();
            assertThat(testOut.toString()).contains("successfully completed");
            assertThat(testOut.toString()).contains("Exiting game. Goodbye!");
        }
    }

    @Test
    void shouldHandleWinAndContinueViaException() throws Exception {
        try (var runtimeMockedStatic = mockStatic(Runtime.class)) {
            Runtime runtime = mock(Runtime.class);
            runtimeMockedStatic.when(Runtime::getRuntime).thenReturn(runtime);
            when(runtime.exec(any(String[].class))).thenThrow(new RuntimeException("Forced stty fail"));

            // First win: say 'y' (not 'quit'), Second win: say 'quit'
            provideInput("y\nquit\n");
            
            doAnswer(invocation -> {
                Board b = invocation.getArgument(0);
                for (int r = 0; r < Config.SIZE; r++) {
                    for (int c = 0; c < Config.SIZE; c++) {
                        int val = (r * Config.SUBGRID_ROWS + r / Config.SUBGRID_ROWS + c) % Config.SIZE + 1;
                        b.setPrefilledCell(new Position(r, c), val);
                    }
                }
                return null;
            }).when(puzzleGenerator).generatePuzzle(any(), anyInt());

            gameSession = new GameSession(validator, puzzleGenerator);
            application = new SudokuApplication(gameSession, renderer);

            application.run();
            assertThat(testOut.toString()).contains("successfully completed");
            assertThat(testOut.toString()).contains("Exiting game. Goodbye!");
        }
    }
}
