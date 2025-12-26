package einfprog.bst.game;

import einfprog.bst.exception.IllegalCoordinatesException;

public final class Coordinates {
    public final BoardType board;

    public final int row;
    public final int column;

    public Coordinates(BoardType board, int row, int column) {
        IllegalCoordinatesException.validate(board, row, column);
        this.board = board;
        this.row = row;
        this.column = column;
    }

    public Coordinates move(int dRow, int dColumn) {
        return new Coordinates(board, row + dRow, column + dColumn);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Coordinates coords &&
                coords.board == board &&
                coords.row == row && coords.column == column;
    }

    @Override
    public String toString() {
        return board.formatCoordinates(this);
    }
}
