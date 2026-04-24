package com.sudoku.ui;

import com.sudoku.board.Board;
import com.sudoku.board.Position;
import com.sudoku.util.Config;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.assertj.core.api.Assertions.assertThat;

class ConsoleRendererTest {

    private final PrintStream standardOut = System.out;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
    private ConsoleRenderer renderer;

    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outputStreamCaptor));
        renderer = new ConsoleRenderer();
    }

    @AfterEach
    void tearDown() {
        System.setOut(standardOut);
    }

    @Test
    void shouldRenderEmptyBoard() {
        Board board = new Board();
        renderer.render(board);

        String output = outputStreamCaptor.toString();
        String[] lines = output.split("\r?\n");

        // Header + Top Border + 9 Rows + 2 Inter-subgrid Borders + Bottom Border = 14 lines
        // Wait, printColumnHeader prints a leading \n
        // 64: System.out.print(ANSI_GREEN + "\n   ");
        // So the first line might be empty if we split by \n
        
        assertThat(lines).hasSizeGreaterThanOrEqualTo(14);

        // Check for row labels (A, B, C...)
        for (int i = 0; i < Config.SIZE; i++) {
            char rowLabel = (char) ('A' + i);
            assertThat(output).contains(String.valueOf(rowLabel));
        }

        // Check for column headers (1, 2, 3...)
        for (int i = 1; i <= Config.SIZE; i++) {
            assertThat(output).contains(String.valueOf(i));
        }

        // Check for border characters
        assertThat(output).contains("╔").contains("╗").contains("╚").contains("╝").contains("║").contains("═");
        assertThat(output).contains("╠").contains("╣").contains("╬").contains("╦").contains("╩");

        // Verify border line length for 9x9
        // 2 spaces + 1 corner + 3 subgrids * 7 chars + 2 intersections + 1 corner = 27
        // But ANSI codes are present, so we should strip them or check lines carefully.
        String firstBorderLine = null;
        for (String line : lines) {
            if (line.contains("╔")) {
                firstBorderLine = line.replaceAll("\u001B\\[[;\\d]*m", "");
                break;
            }
        }
        assertThat(firstBorderLine).isNotNull();
        assertThat(firstBorderLine.trim()).hasSize(25); // 27 - 2 leading spaces = 25
        
        // Check for empty cell markers
        assertThat(output).contains("_");
    }

    @Test
    void shouldRenderBoardWithValuesAndColors() {
        Board board = new Board();
        Position userPos = new Position(0, 0);
        Position prefilledPos = new Position(1, 1);
        
        board.executeMove(userPos, 5); // User filled
        board.setPrefilledCell(prefilledPos, 3); // Prefilled

        renderer.render(board);

        String output = outputStreamCaptor.toString();

        // Verify values are present
        assertThat(output).contains("5");
        assertThat(output).contains("3");

        // ANSI escape codes
        String green = "\u001B[32m";
        String reset = "\u001B[0m";

        // Prefilled cell (3) should be green
        assertThat(output).contains(green + " 3" + reset);

        // User filled cell (5) should NOT be green (at least not in the same way)
        assertThat(output).doesNotContain(green + " 5" + reset);
        assertThat(output).contains(" 5");
    }
}
