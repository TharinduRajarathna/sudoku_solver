package com.sudoku.ui;

import com.sudoku.board.Board;
import com.sudoku.board.Cell;
import com.sudoku.board.Position;
import com.sudoku.util.Config;
import com.sudoku.util.Constants;

/**
 * Responsible for rendering the Sudoku board to the standard console output.
 * It dynamically sizes the printed grid and formats all elements using ANSI color codes.
 *
 * <p>The {@link #render(Board)} method acts as the top-level coordinator, delegating
 * each distinct visual section (header, rows, borders) to a focused private method.
 * This keeps cognitive complexity low and each method easy to understand in isolation.
 */
public class ConsoleRenderer {

    /**
     * Renders the current state of the provided Board to the console.
     * Delegates each visual section to a focused private helper method.
     *
     * @param board The Sudoku board to render.
     */
    public void render(Board board) {
        int gridSize = Config.SIZE;
        int subgridColumnCount = Config.SUBGRID_COLS;
        int subgridRowCount = Config.SUBGRID_ROWS;

        // Calculate how many characters wide each cell value must be printed.
        // For a 9x9 grid, single digit numbers need 1 char.
        // For a 25x25 grid, two-digit numbers need 2 chars.
        int cellWidth = String.valueOf(gridSize).length();

        printColumnHeader(gridSize, subgridColumnCount, cellWidth);
        printHorizontalBorder(subgridRowCount, subgridColumnCount, cellWidth, Constants.BOX_TOP_LEFT, Constants.BOX_TOP_INTERSECTION, Constants.BOX_TOP_RIGHT);

        for (int rowIndex = 0; rowIndex < gridSize; rowIndex++) {
            printGridRow(board, rowIndex, gridSize, subgridRowCount, subgridColumnCount, cellWidth);
        }

        printHorizontalBorder(subgridRowCount, subgridColumnCount, cellWidth, Constants.BOX_BOTTOM_LEFT, Constants.BOX_BOTTOM_INTERSECTION, Constants.BOX_BOTTOM_RIGHT);
    }

    /**
     * Prints the top column-number header row (e.g. "  1 2 3   4 5 6   7 8 9").
     * Extra spacing is added at each subgrid boundary to align with vertical borders.
     *
     * @param gridSize           Total number of columns in the grid.
     * @param subgridColumnCount Number of columns per subgrid block.
     * @param cellWidth          Character width of each cell value.
     */
    private void printColumnHeader(int gridSize, int subgridColumnCount, int cellWidth) {
        String cellFormat = Constants.SPACING_CELL + "%" + cellWidth + "s";
        System.out.print(Constants.ANSI_GREEN + Constants.SPACING_COLUMN_HEADER);
        for (int columnIndex = 0; columnIndex < gridSize; columnIndex++) {
            boolean isAtSubgridBoundary = columnIndex > 0 && columnIndex % subgridColumnCount == 0;
            if (isAtSubgridBoundary) {
                // Extra two spaces to visually align with the "═╦═" border characters
                System.out.print(Constants.SPACING_SUBGRID_BOUNDARY);
            }
            System.out.printf(cellFormat, columnIndex + 1);
        }
        System.out.println(Constants.ANSI_RESET);
    }

    /**
     * Prints a single data row of the grid, including the row label (A, B, C...),
     * all cell values, and vertical subgrid dividers.
     * After the row is printed, checks whether a horizontal subgrid divider is needed.
     *
     * @param board              The board to read cell values from.
     * @param rowIndex           The zero-based row index being rendered.
     * @param gridSize           Total number of rows/columns in the grid.
     * @param subgridRowCount    Number of rows per subgrid block.
     * @param subgridColumnCount Number of columns per subgrid block.
     * @param cellWidth          Character width of each cell value.
     */
    private void printGridRow(Board board, int rowIndex, int gridSize,
                              int subgridRowCount, int subgridColumnCount, int cellWidth) {
        // Row label uses standard English alphabet: A=0, B=1, C=2...
        // This supports up to 26 rows (Z) before label collisions occur.
        char rowLabel = (char) (Constants.ROW_LABEL_BASE + rowIndex);
        System.out.print(Constants.ANSI_GREEN + rowLabel + Constants.BORDER_VERTICAL_SPACED + Constants.ANSI_RESET);

        printRowCells(board, rowIndex, gridSize, subgridColumnCount, cellWidth);

        // Close the right edge of the row
        System.out.println(Constants.ANSI_GREEN + Constants.BORDER_VERTICAL_SPACED + Constants.ANSI_RESET);

        boolean isLastRow = rowIndex == gridSize - 1;
        boolean isAtSubgridBoundary = (rowIndex + 1) % subgridRowCount == 0;
        if (isAtSubgridBoundary && !isLastRow) {
            printHorizontalBorder(subgridRowCount, subgridColumnCount, cellWidth, Constants.BOX_MID_LEFT, Constants.BOX_MID_INTERSECTION, Constants.BOX_MID_RIGHT);
        }
    }

    /**
     * Iterates over every cell in a given row, printing each value with appropriate
     * coloring and inserting vertical subgrid dividers where needed.
     *
     * @param board              The board to read cell values from.
     * @param rowIndex           The zero-based row index being rendered.
     * @param gridSize           Total number of columns in the grid.
     * @param subgridColumnCount Number of columns per subgrid block.
     * @param cellWidth          Character width of each cell value.
     */
    private void printRowCells(Board board, int rowIndex, int gridSize,
                               int subgridColumnCount, int cellWidth) {
        String cellFormat = "%" + cellWidth + "s";
        for (int columnIndex = 0; columnIndex < gridSize; columnIndex++) {
            Cell cell = board.getCellAt(new Position(rowIndex, columnIndex));
            String formattedValue = String.format(cellFormat, cell.toString());

            printCellValue(cell, formattedValue);

            boolean isAtSubgridBoundary = (columnIndex + 1) % subgridColumnCount == 0;
            boolean isLastColumn = columnIndex == gridSize - 1;
            if (isAtSubgridBoundary && !isLastColumn) {
                printVerticalSubgridDivider();
            }
        }
    }

    /**
     * Prints a single cell's value with the correct ANSI color.
     * Pre-filled clue cells are rendered in green to visually distinguish them
     * from cells that the player has filled in themselves.
     *
     * @param cell           The cell to print.
     * @param formattedValue The pre-formatted, padded string value of the cell.
     */
    private void printCellValue(Cell cell, String formattedValue) {
        if (cell.isPrefilled()) {
            System.out.print(Constants.ANSI_GREEN + Constants.SPACING_CELL + formattedValue + Constants.ANSI_RESET);
        } else {
            System.out.print(Constants.SPACING_CELL + formattedValue);
        }
    }

    /**
     * Prints a vertical subgrid divider character (║) in green.
     * Called between subgrid column blocks within a data row.
     */
    private void printVerticalSubgridDivider() {
        System.out.print(Constants.ANSI_GREEN + Constants.BORDER_VERTICAL_SPACED + Constants.ANSI_RESET);
    }

    /**
     * Dynamically generates and prints a complete horizontal border line.
     * Used for the top border (╔╦╗), internal subgrid separators (╠╬╣),
     * and the bottom border (╚╩╝).
     *
     * @param subgridRowCount    Number of subgrid blocks across the width of the border.
     * @param subgridColumnCount Number of columns per subgrid block (determines segment width).
     * @param cellWidth          Character width of each cell (affects segment width).
     * @param leftEdge           Character for the far-left corner (e.g. "╔", "╠", "╚").
     * @param midIntersection    Character for mid-border intersections (e.g. "╦", "╬", "╩").
     * @param rightEdge          Character for the far-right corner (e.g. "╗", "╣", "╝").
     */
    private void printHorizontalBorder(int subgridRowCount, int subgridColumnCount, int cellWidth,
                                       String leftEdge, String midIntersection, String rightEdge) {
        // Each subgrid segment is: (cellWidth + 1 space per cell) + 1 leading space for the ║
        int segmentWidth = subgridColumnCount * (1 + cellWidth) + 1;

        System.out.print(Constants.ANSI_GREEN + Constants.SPACING_SUBGRID_BOUNDARY + leftEdge);
        for (int blockIndex = 0; blockIndex < subgridRowCount; blockIndex++) {
            // Fill the segment with horizontal line characters
            System.out.print(Constants.BOX_HORIZONTAL.repeat(segmentWidth));

            boolean isLastBlock = blockIndex == subgridRowCount - 1;
            if (!isLastBlock) {
                System.out.print(midIntersection);
            }
        }
        System.out.println(rightEdge + Constants.ANSI_RESET);
    }
}