package einfprog.bst;

import einfprog.bst.game.BoardType;
import einfprog.bst.game.Coordinates;
import einfprog.bst.game.ShipPlacement;

import java.util.Collections;
import java.util.List;

public class GameResult {
    public final GameResultType type;
    public final BoardType board;
    public final PlayerType winner;

    private GameResult(GameResultType type, BoardType board, PlayerType winner) {
        this.type = type;
        this.board = board;
        this.winner = winner;
    }

    @Override
    public String toString() {
        return String.format("type: %s, board: %s, winner: %s", type.name(), String.format("%dx%d", board.height, board.width), winner.name());
    }

    public static final class FullyPlayed extends GameResult {
        public final List<ShipPlacement> player1Ships;
        public final List<ShipPlacement> player2Ships;
        public final List<Coordinates> moves;

        public FullyPlayed(PlayerType winner, BoardType board, List<ShipPlacement> player1Ships, List<ShipPlacement> player2Ships, List<Coordinates> moves) {
            super(GameResultType.FULLY_PLAYED, board, winner);
            this.player1Ships = Collections.unmodifiableList(player1Ships);
            this.player2Ships = Collections.unmodifiableList(player2Ships);
            this.moves = Collections.unmodifiableList(moves);
        }
    }

    public static final class Disqualified extends GameResult {
        public final Exception exception;

        public Disqualified(PlayerType winner, BoardType board, Exception exception) {
            super(GameResultType.DISQUALIFICATION, board, winner);
            this.exception = exception;
        }

        @Override
        public String toString() {
            return super.toString() + " exception: " + exception;
        }
    }

    public enum GameResultType {
        FULLY_PLAYED, DISQUALIFICATION;
    }
}
