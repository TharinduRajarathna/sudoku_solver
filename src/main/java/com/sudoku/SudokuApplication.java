package com.sudoku;

import com.sudoku.command.Command;
import com.sudoku.command.CommandParser;
import com.sudoku.command.CommandResult;
import com.sudoku.game.GameSession;
import com.sudoku.game.PuzzleGeneratorImpl;
import com.sudoku.game.Validator;
import com.sudoku.ui.ConsoleRenderer;
import com.sudoku.util.Constants;
import com.sudoku.util.Messages;

import java.util.Scanner;

/**
 * Main entry point and CLI runner for the Sudoku application.
 * Handles the game loop, user input parsing, and delegating commands to the GameSession.
 */
public class SudokuApplication {

    /**
     * The core game logic controller.
     */
    private final GameSession gameSession;

    /**
     * The UI component responsible for drawing the board.
     */
    private final ConsoleRenderer renderer;

    /**
     * Initializes the application with required engine dependencies.
     *
     * @param gameSession The game engine to manage state.
     * @param renderer    The console renderer to manage UI.
     */
    public SudokuApplication(GameSession gameSession, ConsoleRenderer renderer) {
        this.gameSession = gameSession;
        this.renderer = renderer;
    }

    /**
     * The main entry point to run the application from the command line.
     *
     * @param args Command line arguments (not used).
     */
    static void main(String[] args) {
        try {
            GameSession session = new GameSession(new Validator(), new PuzzleGeneratorImpl());
            ConsoleRenderer renderer = new ConsoleRenderer();
            new SudokuApplication(session, renderer).execute();
        } catch (ExceptionInInitializerError error) {
            new SudokuApplication(null, null).handleStaticError(error);
        } catch (IllegalArgumentException error) {
            new SudokuApplication(null, null).handleConfigError(error);
        }
    }

    /**
     * Internal entry point that can be spied on in tests.
     */
    void execute() {
        try {
            run();
        } catch (ExceptionInInitializerError error) {
            handleStaticError(error);
        } catch (IllegalArgumentException error) {
            handleConfigError(error);
        }
    }

    private void handleStaticError(ExceptionInInitializerError error) {
        Throwable cause = error.getCause();
        System.err.println(Constants.ERROR_PREFIX_CONFIG + (cause != null ? cause.getMessage() : error.getMessage()));
        System.err.println(Constants.USAGE_MESSAGE);
        terminate(1);
    }

    private void handleConfigError(IllegalArgumentException error) {
        System.err.println(Constants.ERROR_PREFIX_CONFIG + error.getMessage());
        terminate(1);
    }

    /**
     * Terminate the JVM with the given status code.
     * Externalized to allow mocking in unit tests.
     *
     * @param status Exit status code.
     */
    void terminate(int status) {
        System.exit(status);
    }

    /**
     * Starts the main interactive game loop.
     * Continuously prompts the user for commands until the puzzle is solved or the user quits.
     */
    public void run() {
        Scanner scanner = new Scanner(System.in);
        CommandParser commandParser = new CommandParser(gameSession);

        while (true) {
            gameSession.startNewGame();
            System.out.println(Messages.get("msg.welcome"));
            renderer.render(gameSession.getBoard());

            while (!gameSession.isGameOver()) {
                System.out.println(Messages.get("msg.enter_command"));
                String input = scanner.nextLine();

                Command command = commandParser.parse(input);
                CommandResult result = command.execute();

                // Display any message from the command
                result.getMessage().ifPresent(System.out::println);

                // Render board if requested
                if (result.shouldRenderBoard()) {
                    renderer.render(gameSession.getBoard());
                }

                // Check if we should quit
                if (!result.shouldContinue()) {
                    scanner.close();
                    return;
                }
            }

            System.out.println(Messages.get("msg.success"));
            System.out.println(Messages.get("msg.play_again"));
            try {
                String[] cmd = {Constants.SHELL_BIN, Constants.SHELL_FLAG_C, Constants.TTY_CMD_RAW};
                Runtime.getRuntime().exec(cmd).waitFor();
                int inputChar = System.in.read();
                String[] cmd2 = {Constants.SHELL_BIN, Constants.SHELL_FLAG_C, Constants.TTY_CMD_SANE};
                Runtime.getRuntime().exec(cmd2).waitFor();

                if (inputChar == Constants.CTRL_C || inputChar == Constants.CHAR_QUIT_LOWER || inputChar == Constants.CHAR_QUIT_UPPER) {
                    break;
                }
            } catch (Exception e) {
                String choice = scanner.nextLine().trim().toLowerCase();
                if (choice.equals(Constants.CMD_QUIT)) {
                    break;
                }
            }
        }
        System.out.println(Messages.get("msg.exit"));
        scanner.close();
    }
}
