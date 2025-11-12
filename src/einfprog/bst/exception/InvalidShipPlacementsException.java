package einfprog.bst.exception;

import einfprog.bst.game.BoardType;
import einfprog.bst.game.Coordinates;
import einfprog.bst.game.ShipPlacement;
import einfprog.bst.game.ShipType;

import java.util.Collections;
import java.util.List;

public class InvalidShipPlacementsException extends BSTException {
    public final InvalidShipPlacementsType type;
    public final List<ShipPlacement> shipPlacements;
    public final List<ShipType> ships;

    private InvalidShipPlacementsException(InvalidShipPlacementsType type, String message, List<ShipPlacement> shipPlacements, List<ShipType> ships) {
        super(message);
        this.type = type;
        this.shipPlacements = Collections.unmodifiableList(shipPlacements);
        this.ships = Collections.unmodifiableList(ships);
    }

    public static void validate(List<ShipPlacement> ships, List<ShipType> shipTypes) {
        for(ShipPlacement ship : ships) {
            if(ship == null) {
                throw new InvalidShipPlacementsException(InvalidShipPlacementsType.NULL, "ShipPlacement is null", ships, shipTypes);
            }
        }
        for(int i = 0; i < ships.size(); i++) {
            if(ships.get(i).shipType != shipTypes.get(i)) {
                throw new InvalidShipPlacementsException(InvalidShipPlacementsType.WRONG_SHIP, String.format("Wrong ship at index %d: expected %s but found %s", i, shipTypes.get(i).name, ships.get(i).shipType.name), ships, shipTypes);
            }
        }
        BoardType board =  ships.getFirst().board;
        for(int i = 1; i < ships.size(); i++) {
            if(ships.get(i).board != board) {
                throw new InvalidShipPlacementsException(InvalidShipPlacementsType.DIFFERENT_BOARDS, "Different board types", ships, shipTypes);
            }
        }
        boolean[][] flags = new boolean[board.getHeight()][board.getWidth()];
        for(ShipPlacement ship : ships) {
            for(Coordinates coords : ship.allCoordinates) {
                if(!flags[coords.getRowIndex()][coords.getColumnIndex()]) {
                    flags[coords.getRowIndex()][coords.getColumnIndex()] = true;
                } else {
                    throw new InvalidShipPlacementsException(InvalidShipPlacementsType.OVERLAP, String.format("Ships overlapping at %s", coords.toString()), ships, shipTypes);
                }
            }
        }
    }

    public enum InvalidShipPlacementsType {
        NULL,
        WRONG_SHIP,
        DIFFERENT_BOARDS,
        OVERLAP,
    }
}
