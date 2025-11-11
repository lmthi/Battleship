package einfprog.bst;

import einfprog.bst.game.Coordinates;
import einfprog.bst.game.ShipType;

public class FireResult {
    public final FireResultType type;
    public final Coordinates coords;

    private FireResult(FireResultType type, Coordinates coords) {
        this.type = type;
        this.coords = coords;
    }

    public static final class Miss extends FireResult {
        public Miss(Coordinates coords) {
            super(FireResultType.MISS, coords);
        }
    }

    public static final class Hit extends FireResult {
        public Hit(Coordinates coords) {
            super(FireResultType.HIT, coords);
        }
    }

    public static final class Sunk extends FireResult {
        public final ShipType ship;

        public Sunk(Coordinates coords, ShipType ship) {
            super(FireResultType.SUNK, coords);
            this.ship = ship;
        }
    }

    public static final class Repetition extends FireResult {
        public Repetition(Coordinates coords) {
            super(FireResultType.REPETITION, coords);
        }
    }

    public enum FireResultType {
        MISS, HIT, SUNK, REPETITION;
    }
}
