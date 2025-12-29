package einfprog.bst.state;

import einfprog.bst.FireResult;
import einfprog.bst.game.BoardType;
import einfprog.bst.game.Coordinates;
import einfprog.bst.game.ShipPlacement;
import einfprog.bst.game.ShipType;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class BoardTracker {
    protected BoardType boardType;
    protected List<ShipType> shipTypes;
    protected FullBoardView myBoard;
    protected ShallowBoardView opponentBoard;
    protected List<ShipType> shipsToSink;

    public BoardTracker() {
        boardType = null;
        shipTypes = null;
        myBoard = null;
        opponentBoard = null;
        shipsToSink = null;
    }

    public void init(BoardType board, List<ShipType> ships) {
        this.boardType = board;
        this.shipTypes = ships;
        myBoard = null;
        opponentBoard = null;
        shipsToSink = null;
    }

    public void newGame() {
        opponentBoard = new ShallowBoardView(boardType);
        shipsToSink = new ArrayList<>(shipTypes);
    }

    public void markShips(List<ShipPlacement> placements) {
        myBoard = new FullBoardView(boardType, placements);
    }

    public void markFireResult(FireResult result) {
        BoardMarker marker = BoardMarker.fromFireResult(result);
        if(marker.type != BoardMarker.BoardMarkerType.NONE) {
            opponentBoard.setMarker(result.coords, marker);
        }
        if(result instanceof FireResult.Sunk sunkResult) {
            shipsToSink.remove(sunkResult.ship);
        }
    }

    public FireResult markOpponentFires(Coordinates coordinates) {
        return myBoard.fireAt(coordinates);
    }

    public List<String> getMyBoardRepresentationList() {
        return myBoard.getLineRepresentationList();
    }

    public List<String> getOpponentBoardRepresentationList() {
        return opponentBoard.getLineRepresentationList();
    }

    public List<ShipType> getShipsToSink() {
        return shipsToSink;
    }

    public BoardMarker getOpponentBoardMarker(Coordinates coords) {
        return opponentBoard.getMarker(coords);
    }
}
