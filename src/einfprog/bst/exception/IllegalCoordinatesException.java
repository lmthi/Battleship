package einfprog.bst.exception;

import einfprog.bst.game.BoardType;

public class IllegalCoordinatesException extends BSTException {
    public final int row;
    public final int column;

    private IllegalCoordinatesException(InvalidCoordinatesType type, String message, int row, int column) {
        super(message);
        this.row = row;
        this.column = column;
    }

    public static void validate(BoardType board, int row, int column) {
        if(board == null) {
            throw new IllegalCoordinatesException(InvalidCoordinatesType.BOARD_NULL, "Board is null!", row, column);
        }
        if(!board.validate(row, column)) {
            throw new IllegalCoordinatesException(InvalidCoordinatesType.INVALID_ROW_COLUMN, String.format("Invalid coordinates row %s (%s), column=%s (%s)", board.formatRow(row), row, board.formatColumn(column), column), row, column);
        }
    }

    public enum InvalidCoordinatesType {
        BOARD_NULL,
        INVALID_ROW_COLUMN
    }
}
