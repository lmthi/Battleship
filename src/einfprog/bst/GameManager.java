package einfprog.bst;

import einfprog.bst.exception.CallTimedOutException;
import einfprog.bst.exception.InvalidShipPlacementsException;
import einfprog.bst.game.BoardType;
import einfprog.bst.game.Coordinates;
import einfprog.bst.game.ShipPlacement;
import einfprog.bst.game.ShipType;
import einfprog.bst.player.IPlayer;
import einfprog.bst.state.FullBoardView;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;

public class GameManager {
    public final BoardType boardType;
    public final ShipType[] ships;
    public final IPlayer player1;
    public final IPlayer player2;

    protected GameResult gameResult;
    protected boolean playersFlipped;
    protected List<Coordinates> moves;
    protected ShipPlacement[] shipPlacements1;
    protected ShipPlacement[] shipPlacements2;
    protected FullBoardView boardPlayer1;
    protected FullBoardView boardPlayer2;

    public GameManager(BoardType boardType, ShipType[] ships, IPlayer player1, IPlayer player2) {
        this.boardType = boardType;
        this.ships = ships;
        this.player1 = player1;
        this.player2 = player2;
        playersFlipped = true; // we switch at init
    }

    public GameResult playNewRound() {
        initNewRound();

        startRound();
        innerRound();
        endRound();

        return gameResult;
    }

    protected void initNewRound() {
        gameResult = null;
        playersFlipped = !playersFlipped;
        moves = new LinkedList<>();
        shipPlacements1 = null;
        shipPlacements2 = null;
        boardPlayer1 = null;
        boardPlayer2 = null;
    }

    protected IPlayer getPlayer1() {
        if(!playersFlipped) {
            return player1;
        } else {
            return player2;
        }
    }

    protected IPlayer getPlayer2() {
        if(playersFlipped) {
            return player1;
        } else {
            return player2;
        }
    }

    protected void startRound() {
        wrap(PlayerType.PLAYER1, () -> getPlayer1().onGameStart(PlayerType.PLAYER1), true, false);
        wrap(PlayerType.PLAYER2, () -> getPlayer2().onGameStart(PlayerType.PLAYER2), true, false);
    }

    protected void endRound() {
        wrap(PlayerType.PLAYER1, () -> getPlayer1().onGameEnd(gameResult), true, false);
        wrap(PlayerType.PLAYER2, () -> getPlayer2().onGameEnd(gameResult), true, false);
    }

    protected void innerRound() {
        placeShips();
        if(gameResult != null) {
            return;
        }

        playRound();
    }

    protected void placeShips() {
        Callable<ShipPlacement[]> shipsPlayer1Callable = () -> {
            ShipPlacement[] shipPlacements = getPlayer1().placeShips(ships);
            InvalidShipPlacementsException.validate(shipPlacements);
            return shipPlacements;
        };
        shipPlacements1 = wrap(PlayerType.PLAYER1, shipsPlayer1Callable);

        Callable<ShipPlacement[]> shipsPlayer2Callable = () -> {
            ShipPlacement[] shipPlacements = getPlayer2().placeShips(ships);
            InvalidShipPlacementsException.validate(shipPlacements);
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
        PlayerType turn = playersFlipped ? PlayerType.PLAYER2 : PlayerType.PLAYER1;
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
            attacker = getPlayer1();
            defender = getPlayer2();
            board = boardPlayer2;
        } else {
            attacker = getPlayer2();
            defender = getPlayer1();
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
            return doWrap(callable);
        } catch(Exception e) {
            if(writeGameResult && gameResult == null) {
                gameResult = new GameResult.Disqualified(player.getOtherPlayer(), boardType, e);
            }
            return null;
        }
    }

    protected  <T> T doWrap(Callable<T> callable) {
        try {
            return callable.call();
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
