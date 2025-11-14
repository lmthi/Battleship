package einfprog.bst.state;

import einfprog.bst.FireResult;
import einfprog.bst.game.ShipType;

public class BoardMarker {
    public final BoardMarkerType type;

    private BoardMarker(BoardMarkerType type) {
        this.type = type;
    }

    public static class None extends BoardMarker {
        public None() {
            super(BoardMarkerType.NONE);
        }
    }

    public static class Miss extends BoardMarker {
        public Miss() {
            super(BoardMarkerType.MISS);
        }
    }

    public static class Hit extends BoardMarker {
        public Hit() {
            super(BoardMarkerType.HIT);
        }
    }

    public static class Sunk extends BoardMarker {
        public final ShipType ship;

        public Sunk(ShipType ship) {
            super(BoardMarkerType.SUNK);
            this.ship = ship;
        }
    }

    public enum BoardMarkerType {
        NONE, MISS, HIT, SUNK;
    }

    public static BoardMarker fromFireResult(FireResult result) {
        return switch (result.type) {
            case MISS -> new BoardMarker.Miss();
            case HIT -> new BoardMarker.Hit();
            case SUNK -> new BoardMarker.Sunk(((FireResult.Sunk)result).ship);
            default -> new BoardMarker.None();
        };
    }
}
