package einfprog.bst.game;

public final class BoardType {
    private static final int STANDARD_BOARD_SIZE = 10;

    private static char CIRCLED_A = '\u24B6';
    private static char CIRCLED_1 = '\u2460';

    public static final BoardType STANDARD_BOARD = new BoardType('A', '1', STANDARD_BOARD_SIZE);

    public final int rowStart; //incl
    public final int rowEnd; //excl
    public final int colStart; //incl
    public final int colEnd; //excl

    public BoardType(int rowStart, int rowEnd, int colStart, int colEnd) {
        this.rowStart = rowStart;
        this.rowEnd = rowEnd;
        this.colStart = colStart;
        this.colEnd = colEnd;
    }

    public BoardType(int rowStart, int colStart, int size) {
        this(rowStart, rowStart + size, colStart, colStart + size);
    }

    public int getHeight() {
        return rowEnd - rowStart;
    }

    public int getWidth() {
        return colEnd - colStart;
    }

    public boolean validate(int row, int column) {
        return row >= rowStart && row < rowEnd && column >= colStart && column < colEnd;
    }

    public String formatCoordinates(Coordinates coordinates) {
        return String.format("%s%s", formatRow(coordinates.row), formatColumn(coordinates.column));
    }

    public String formatRow(int row) {
        return String.valueOf((char)(row-rowStart+CIRCLED_A));
    }

    public String formatColumn(int column) {
        return String.valueOf((char)(column-colStart+CIRCLED_1));
    }
}
