package einfprog.bst.exception;

import einfprog.bst.game.Coordinates;
import einfprog.bst.game.ShipPlacement;
import einfprog.bst.game.ShipType;

public class IllegalShipPlacementException extends BSTException {
    public final InvalidShipPlacementType type;
    public final ShipType ship;
    public final Coordinates corner1; //incl
    public final Coordinates corner2; //excl

    private IllegalShipPlacementException(InvalidShipPlacementType type, String message, ShipType ship, Coordinates coordStart, Coordinates coordEnd) {
        super(message);
        this.type = type;
        this.ship = ship;
        this.corner1 = coordStart;
        this.corner2 = coordEnd;
    }

    public static void validate(ShipType ship, Coordinates coordStart, Coordinates coordEnd) {
        if(coordStart.board != coordEnd.board) {
            throw new IllegalShipPlacementException(InvalidShipPlacementType.DIFFERENT_BOARDS, "Different board types", ship, coordStart, coordEnd);
        }
        if(coordStart.row > coordEnd.row || coordStart.column > coordEnd.column) {
            throw new IllegalShipPlacementException(InvalidShipPlacementType.MISALIGNED, String.format("coordStart (%s) must be left and above of coordEnd (%s)", coordStart, coordEnd), ship, coordStart, coordEnd);
        }

        int height = coordEnd.row - coordStart.row + 1;
        int width = coordEnd.column - coordStart.column + 1;
        if((height != ship.length || width != ship.width) && (height != ship.width || width != ship.length)) {
            throw new IllegalShipPlacementException(InvalidShipPlacementType.MISSIZED, String.format("%s ship size (%dx%d) does not fit corner area (%dx%d)", ship.name, ship.length, ship.width, height, width), ship, coordStart, coordEnd);
        }
    }

    public static void validate(ShipPlacement shipPlacement) {
        validate(shipPlacement.shipType, shipPlacement.coordStart, shipPlacement.coordEnd);
    }

    public enum InvalidShipPlacementType {
        DIFFERENT_BOARDS,
        MISALIGNED,
        MISSIZED
    }
}
