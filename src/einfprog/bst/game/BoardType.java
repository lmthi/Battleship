package einfprog.bst.game;

public final class BoardType {
    private static final int STANDARD_BOARD_SIZE = 10;

    private static char CIRCLED_A = '\u24B6';
    private static char CIRCLED_1 = '\u2460';

    public static final BoardType STANDARD_BOARD = new BoardType(STANDARD_BOARD_SIZE);

    public final int height; //incl
    public final int width; //incl

    public BoardType(int height, int width) {
        this.height = height;
        this.width = width;
    }

    public BoardType(int size) {
        this(size, size);
    }

    public boolean validate(int row, int column) {
        return row >= 0 && row < height && column >= 0 && column < width;
    }

    public String formatCoordinates(Coordinates coordinates) {
        return String.format("%s%s", formatRow(coordinates.row), formatColumn(coordinates.column));
    }

    public String formatRow(int row) {
        return String.valueOf((char)(row+CIRCLED_A));
    }

    public String formatColumn(int column) {
        return String.valueOf((char)(column+CIRCLED_1));
    }

    public int readRow(char row) {
        return row - 'A';
    }

    public int readColumn(char column) {
        return column - '1';
    }
}
