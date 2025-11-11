package einfprog.bst.exception;

import einfprog.bst.game.BoardType;
import einfprog.bst.game.Coordinates;
import einfprog.bst.game.ShipPlacement;
import einfprog.bst.game.ShipType;

public class InvalidShipPlacementsException extends BSTException {
    public final InvalidShipPlacementsType type;
    public final ShipPlacement[] ships;

    private InvalidShipPlacementsException(InvalidShipPlacementsType type, String message, ShipPlacement[] ships) {
        super(message);
        this.type = type;
        this.ships = ships;
    }

    public static void validate(ShipPlacement[] ships) {
        for(ShipPlacement ship : ships) {
            if(ship == null) {
                throw new InvalidShipPlacementsException(InvalidShipPlacementsType.NULL, "ShipPlacement is null", ships);
            }
        }
        BoardType board =  ships[0].board;
        for(int i = 1; i < ships.length; i++) {
            if(ships[i].board != board) {
                throw new InvalidShipPlacementsException(InvalidShipPlacementsType.DIFFERENT_BOARDS, "Different board types", ships);
            }
        }
        boolean[][] flags = new boolean[board.getHeight()][board.getWidth()];
        for(ShipPlacement ship : ships) {
            for(Coordinates coords : ship.allCoordinates) {
                if(!flags[coords.getRowIndex()][coords.getColumnIndex()]) {
                    flags[coords.getRowIndex()][coords.getColumnIndex()] = true;
                } else {
                    throw new InvalidShipPlacementsException(InvalidShipPlacementsType.OVERLAP, String.format("Ships overlapping at %s", coords.toString()), ships);
                }
            }
        }
    }

    public enum InvalidShipPlacementsType {
        NULL,
        DIFFERENT_BOARDS,
        OVERLAP,
    }
}
