package einfprog.bst.player;

import einfprog.bst.FireResult;
import einfprog.bst.GameResult;
import einfprog.bst.MatchResult;
import einfprog.bst.PlayerType;
import einfprog.bst.game.BoardType;
import einfprog.bst.game.Coordinates;
import einfprog.bst.game.ShipPlacement;
import einfprog.bst.game.ShipType;
import einfprog.bst.state.BoardTracker;

import java.util.List;
import java.util.Random;

public abstract class AbstractPlayer implements IPlayer {
    protected Random random;

    protected BoardType boardType;
    protected List<ShipType> shipTypes;
    protected BoardTracker boardTracker;

    public AbstractPlayer() {
        this.boardTracker = new BoardTracker();
    }

    public abstract String getName();

    public abstract List<ShipPlacement> placeMyShips();

    public abstract Coordinates selectNextTarget();

    @Override
    public void init(Random random) {
        this.random = random;
    }

    @Override
    public void onMatchStart(String opponent, int rounds, BoardType board, List<ShipType> ships) {
        this.boardType = board;
        this.shipTypes = ships;
        boardTracker.init(board, ships);
    }

    @Override
    public void onMatchEnd(MatchResult result) {}

    @Override
    public void onGameStart(PlayerType playerType) {
        boardTracker.newGame(playerType.starting);
    }

    @Override
    public void onGameEnd(GameResult result) {}

    @Override
    public List<ShipPlacement> placeShips() {
        List<ShipPlacement> placements = placeMyShips();
        boardTracker.markShips(placements);
        return placements;
    }

    @Override
    public Coordinates fire() {
        return selectNextTarget();
    }

    @Override
    public void onFireResult(FireResult result) {
        boardTracker.markFireResult(result);
    }

    @Override
    public void onOpponentFires(Coordinates coordinates) {
        boardTracker.markOpponentFires(coordinates);
    }
}
