package einfprog.bst.player;

import einfprog.bst.FireResult;
import einfprog.bst.GameResult;
import einfprog.bst.MatchResult;
import einfprog.bst.PlayerType;
import einfprog.bst.game.BoardType;
import einfprog.bst.game.Coordinates;
import einfprog.bst.game.ShipPlacement;
import einfprog.bst.game.ShipType;
import einfprog.bst.state.BoardMarkerType;
import einfprog.bst.state.FullBoardView;
import einfprog.bst.state.ShallowBoardView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public abstract class AbstractPlayer implements IPlayer {
    protected Random random;

    protected BoardType boardType;
    protected FullBoardView myBoard;
    protected ShallowBoardView opponentBoard;
    protected List<ShipType> shipsToSink;

    public abstract String getName();

    public abstract ShipPlacement[] placeMyShips(ShipType[] ships);

    public abstract Coordinates selectNextTarget();

    @Override
    public void init(Random random) {
        this.random = random;
    }

    @Override
    public void onMatchStart(String opponent, int rounds, BoardType board) {
        this.boardType = board;
    }

    @Override
    public void onMatchEnd(MatchResult result) {}

    @Override
    public void onGameStart(PlayerType playerType) {}

    @Override
    public void onGameEnd(GameResult result) {}

    @Override
    public ShipPlacement[] placeShips(ShipType[] ships) {
        ShipPlacement[] placements = placeMyShips(ships);
        myBoard = new FullBoardView(boardType, placements);
        opponentBoard = new ShallowBoardView(boardType);
        shipsToSink = new ArrayList<>(Arrays.asList(ships));
        return placements;
    }


    @Override
    public Coordinates fire() {
        return selectNextTarget();
    }

    @Override
    public void onFireResult(FireResult result) {
        BoardMarkerType marker = BoardMarkerType.fromFireResult(result);
        if(marker != BoardMarkerType.NONE) {
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
