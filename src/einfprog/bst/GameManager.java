package einfprog.bst;

import einfprog.bst.exception.InvalidShipPlacementsException;
import einfprog.bst.game.BoardType;
import einfprog.bst.game.Coordinates;
import einfprog.bst.game.ShipPlacement;
import einfprog.bst.game.ShipType;
import einfprog.bst.player.IPlayer;
import einfprog.bst.state.FullBoardView;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;

public class GameManager {
    public final BoardType boardType;
    public final List<ShipType> ships;

    protected IPlayer player1;
    protected IPlayer player2;
    protected GameResult gameResult;
    protected List<Coordinates> moves;
    protected List<ShipPlacement> shipPlacements1;
    protected List<ShipPlacement> shipPlacements2;
    protected FullBoardView boardPlayer1;
    protected FullBoardView boardPlayer2;

    public GameManager(BoardType boardType, List<ShipType> ships) {
        this.boardType = boardType;
        this.ships = Collections.unmodifiableList(ships);
    }

    public GameResult playNewRound(IPlayer player1, IPlayer player2) {
        this.player1 = player1;
        this.player2 = player2;

        initNewRound();

        startRound();
        innerRound();
        endRound();

        return gameResult;
    }

    protected void initNewRound() {
        gameResult = null;
        moves = new LinkedList<>();
        shipPlacements1 = null;
        shipPlacements2 = null;
        boardPlayer1 = null;
        boardPlayer2 = null;
    }
    
    protected void startRound() {
        wrap(PlayerType.PLAYER1, () -> player1.onGameStart(PlayerType.PLAYER1), true, false);
        wrap(PlayerType.PLAYER2, () -> player2.onGameStart(PlayerType.PLAYER2), true, false);
    }

    protected void endRound() {
        wrap(PlayerType.PLAYER1, () -> player1.onGameEnd(gameResult), true, false);
        wrap(PlayerType.PLAYER2, () -> player2.onGameEnd(gameResult), true, false);
    }

    protected void innerRound() {
        placeShips();
        if(gameResult != null) {
            return;
        }

        playRound();
    }

    protected void placeShips() {
        Callable<List<ShipPlacement>> shipsPlayer1Callable = () -> {
            List<ShipPlacement> shipPlacements = player1.placeShips(ships);
            InvalidShipPlacementsException.validate(shipPlacements, ships);
            return shipPlacements;
        };
        shipPlacements1 = wrap(PlayerType.PLAYER1, shipsPlayer1Callable);

        Callable<List<ShipPlacement>> shipsPlayer2Callable = () -> {
            List<ShipPlacement> shipPlacements = player2.placeShips(ships);
            InvalidShipPlacementsException.validate(shipPlacements, ships);
            return shipPlacements;
        };
        shipPlacements2 = wrap(PlayerType.PLAYER2, shipsPlayer2Callable);

        if(gameResult != null) {
            return;
        }

        boardPlayer1 = new FullBoardView(boardType, shipPlacements1);
        boardPlayer2 = new FullBoardView(boardType, shipPlacements2);
    }

    protected void playRound() {
        PlayerType turn = PlayerType.PLAYER1;
        while(gameResult == null) {
            fire(turn);
            turn = turn.getOtherPlayer();
        }
    }

    protected void fire(PlayerType player) {
        PlayerType otherPlayer = player.getOtherPlayer();

        IPlayer attacker;
        IPlayer defender;
        FullBoardView board;
        if(player == PlayerType.PLAYER1) {
            attacker = player1;
            defender = player2;
            board = boardPlayer2;
        } else {
            attacker = player2;
            defender = player1;
            board = boardPlayer1;
        }

        Coordinates coords = wrap(player, attacker::fire);
        if(coords == null) {
            return;
        }

        moves.add(coords);

        FireResult result = board.fireAt(coords);
        wrap(player, () -> attacker.onFireResult(result));
        wrap(otherPlayer, () -> defender.onOpponentFires(coords));

        if(gameResult == null && board.getUnsunkShips().isEmpty()) {
            gameResult = new GameResult.FullyPlayed(player, boardType, shipPlacements1, shipPlacements2, moves);
        }
    }

    protected void wrap(PlayerType player, Runnable runnable) {
        wrap(player, runnable, false, true);
    }

    protected void wrap(PlayerType player, Runnable runnable, boolean ignoreGameResult, boolean writeGameResult) {
        Callable<Void> callable = () -> {
            runnable.run();
            return null;
        };
        wrap(player, callable, ignoreGameResult, writeGameResult);
    }

    protected <T> T wrap(PlayerType player, Callable<T> callable) {
        return wrap(player, callable, false, true);
    }

    protected <T> T wrap(PlayerType player, Callable<T> callable, boolean ignoreGameResult, boolean writeGameResult) {
        if(!ignoreGameResult && gameResult != null) {
            return null;
        }
        try {
            return callable.call();
        } catch(Exception e) {
            if(writeGameResult && gameResult == null) {
                gameResult = new GameResult.Disqualified(player.getOtherPlayer(), boardType, e);
            }
            return null;
        }
    }
}
