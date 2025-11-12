package einfprog.bst.game;

import java.util.List;

public final class ShipType {
    public static final ShipType CARRIER = new ShipType("Carrier", 5, 1);
    public static final ShipType BATTLESHIP = new ShipType("Battleship", 4, 1);
    public static final ShipType CRUISER = new ShipType("Cruiser", 3, 1);
    public static final ShipType SUBMARINE = new ShipType("Submarine", 3, 1);
    public static final ShipType DESTROYER = new ShipType("Destroyer", 2, 1);

    public static final List<ShipType> STANDARD_SHIPS = List.of(
            CARRIER,
            BATTLESHIP,
            CRUISER,
            SUBMARINE,
            DESTROYER
    );

    public final String name;
    public final int length;
    public final int width;

    public ShipType(String name, int length, int width) {
        this.name = name;
        this.length = length;
        this.width = width;
    }
}
