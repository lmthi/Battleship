package einfprog.bst.state;

import einfprog.bst.FireResult;

public enum BoardMarkerType {
    NONE, MISS, HIT, SUNK;

    public static BoardMarkerType fromFireResult(FireResult result) {
        return switch (result.type) {
            case MISS -> MISS;
            case HIT -> HIT;
            case SUNK -> SUNK;
            default -> NONE;
        };
    }
}
