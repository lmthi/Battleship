package einfprog.bst.game;

import einfprog.bst.exception.IllegalShipPlacementException;

import java.util.ArrayList;
import java.util.List;

public final class ShipPlacement {
    public final ShipType shipType;
    public final BoardType board;
    public final Coordinates coordStart; //incl
    public final Coordinates coordEnd; //incl
    public final List<Coordinates> allCoordinates;

    public ShipPlacement(ShipType shipType, Coordinates coordStart, Coordinates coordEnd) {
        IllegalShipPlacementException.validate(shipType, coordStart, coordEnd);
        this.shipType = shipType;
        this.board = coordStart.board;
        this.coordStart = coordStart;
        this.coordEnd = coordEnd;
        this.allCoordinates = new ArrayList<>(shipType.length * shipType.width);
        for(int r = coordStart.row; r <= coordEnd.row; r++) {
            for(int c = coordStart.column; c <= coordEnd.column; c++) {
                allCoordinates.add(new Coordinates(board, r, c));
            }
        }
    }

    public static boolean overlaps(ShipPlacement ship1, ShipPlacement ship2) {
        return ship1.allCoordinates.stream().anyMatch(coords1 ->
                ship2.allCoordinates.stream().anyMatch(coords1::equals)
        );
    }

    public static boolean overlaps(BoardType board, ShipPlacement[] ships) {
        boolean[][] flags = new boolean[board.height][board.width];
        for(ShipPlacement ship : ships) {
            for(Coordinates coords : ship.allCoordinates) {
                if(!flags[coords.row][coords.column]) {
                    flags[coords.row][coords.column] = true;
                } else {
                    return true;
                }
            }
        }
        return false;
    }
}
