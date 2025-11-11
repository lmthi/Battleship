package einfprog.bst;

public enum PlayerType {
    PLAYER1(true), PLAYER2(!PLAYER1.starting);

    public final boolean starting;

    PlayerType(boolean starting) {
        this.starting = starting;
    }

    public PlayerType getOtherPlayer() {
        if(this == PLAYER1) {
            return PLAYER2;
        } else if(this == PLAYER2) {
            return PLAYER1;
        } else {
            throw new RuntimeException(new IllegalStateException());
        }
    }
}
