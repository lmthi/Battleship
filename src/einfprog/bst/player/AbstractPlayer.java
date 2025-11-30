package einfprog.bst.player;

import einfprog.bst.FireResult;
import einfprog.bst.GameResult;
import einfprog.bst.MatchResult;
import einfprog.bst.PlayerType;
import einfprog.bst.game.BoardType;
import einfprog.bst.game.Coordinates;
import einfprog.bst.game.ShipPlacement;
import einfprog.bst.game.ShipType;
import einfprog.bst.state.BoardMarker;
import einfprog.bst.state.FullBoardView;
import einfprog.bst.state.ShallowBoardView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class AbstractPlayer implements IPlayer {
    protected Random random;

    protected BoardType boardType;
    protected List<ShipType> shipTypes;
    protected FullBoardView myBoard;
    protected ShallowBoardView opponentBoard;
    protected List<ShipType> shipsToSink;

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
    }

    @Override
    public void onMatchEnd(MatchResult result) {}

    @Override
    public void onGameStart(PlayerType playerType) {
        opponentBoard = new ShallowBoardView(boardType);
        shipsToSink = new ArrayList<>(shipTypes);
    }

    @Override
    public void onGameEnd(GameResult result) {}

    @Override
    public List<ShipPlacement> placeShips() {
        List<ShipPlacement> placements = placeMyShips();
        myBoard = new FullBoardView(boardType, placements);
        return placements;
    }


    @Override
    public Coordinates fire() {
        return selectNextTarget();
    }

    @Override
    public void onFireResult(FireResult result) {
        BoardMarker marker = BoardMarker.fromFireResult(result);
        if(marker.type != BoardMarker.BoardMarkerType.NONE) {
            opponentBoard.setMarker(result.coords, marker);
        }
        if(result instanceof FireResult.Sunk sunkResult) {
            shipsToSink.remove(sunkResult.ship);
        }
    }

    @Override
    public void onOpponentFires(Coordinates coordinates) {
        myBoard.fireAt(coordinates);
    }
}
