package einfprog.bst;

import einfprog.bst.exception.CallTimedOutException;
import einfprog.bst.exception.InvalidShipPlacementsException;
import einfprog.bst.game.BoardType;
import einfprog.bst.game.Coordinates;
import einfprog.bst.game.ShipPlacement;
import einfprog.bst.game.ShipType;
import einfprog.bst.player.IPlayer;
import einfprog.bst.state.FullBoardView;

import java.io.Closeable;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;

public final class GameManager implements Closeable {
    public static final long TIME_SHORT = 2000;
    public static final long TIME_LONG = 4000;

    public final BoardType boardType;
    public final ShipType[] ships;
    public final IPlayer player1;
    public final IPlayer player2;

    private final ExecutorService executor;

    private GameResult gameResult;
    private boolean playersFlipped;
    private List<Coordinates> moves;
    private ShipPlacement[] shipPlacements1;
    private ShipPlacement[] shipPlacements2;
    private FullBoardView boardPlayer1;
    private FullBoardView boardPlayer2;

    public GameManager(BoardType boardType, ShipType[] ships, IPlayer player1, IPlayer player2) {
        this.boardType = boardType;
        this.ships = ships;
        this.player1 = player1;
        this.player2 = player2;
        executor = Executors.newSingleThreadExecutor();
        playersFlipped = true; // we switch at init
    }

    public GameResult playNewRound() {
        initNewRound();

        startRound();
        innerRound();
        endRound();

        return gameResult;
    }

    private void initNewRound() {
        gameResult = null;
        playersFlipped = !playersFlipped;
        moves = new LinkedList<>();
        shipPlacements1 = null;
        shipPlacements2 = null;
        boardPlayer1 = null;
        boardPlayer2 = null;
    }

    private IPlayer getPlayer1() {
        if(!playersFlipped) {
            return player1;
        } else {
            return player2;
        }
    }

    private IPlayer getPlayer2() {
        if(playersFlipped) {
            return player1;
        } else {
            return player2;
        }
    }

    private void startRound() {
        wrap(PlayerType.PLAYER1, () -> getPlayer1().onGameStart(PlayerType.PLAYER1), TIME_LONG, true, false);
        wrap(PlayerType.PLAYER2, () -> getPlayer2().onGameStart(PlayerType.PLAYER2), TIME_LONG, true, false);
    }

    private void endRound() {
        wrap(PlayerType.PLAYER1, () -> getPlayer1().onGameEnd(gameResult), TIME_LONG, true, false);
        wrap(PlayerType.PLAYER2, () -> getPlayer2().onGameEnd(gameResult), TIME_LONG, true, false);
    }

    private void innerRound() {
        placeShips();
        if(gameResult != null) {
            return;
        }

        playRound();
    }

    private void placeShips() {
        Callable<ShipPlacement[]> shipsPlayer1Callable = () -> {
            ShipPlacement[] shipPlacements = getPlayer1().placeShips(ships);
            InvalidShipPlacementsException.validate(shipPlacements);
            return shipPlacements;
        };
        shipPlacements1 = wrap(PlayerType.PLAYER1, shipsPlayer1Callable, TIME_SHORT);

        Callable<ShipPlacement[]> shipsPlayer2Callable = () -> {
            ShipPlacement[] shipPlacements = getPlayer2().placeShips(ships);
            InvalidShipPlacementsException.validate(shipPlacements);
            return shipPlacements;
        };
        shipPlacements2 = wrap(PlayerType.PLAYER2, shipsPlayer2Callable, TIME_SHORT);

        if(gameResult != null) {
            return;
        }

        boardPlayer1 = new FullBoardView(boardType, shipPlacements1);
        boardPlayer2 = new FullBoardView(boardType, shipPlacements2);
    }

    private void playRound() {
        PlayerType turn = playersFlipped ? PlayerType.PLAYER2 : PlayerType.PLAYER1;
        while(gameResult == null) {
            fire(turn);
            turn = turn.getOtherPlayer();
        }
    }

    private void fire(PlayerType player) {
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

        Coordinates coords = wrap(player, attacker::fire, TIME_SHORT);
        if(coords == null) {
            return;
        }

        moves.add(coords);

        FireResult result = board.fireAt(coords);
        wrap(player, () -> attacker.onFireResult(result), TIME_SHORT);
        wrap(otherPlayer, () -> defender.onOpponentFires(coords), TIME_SHORT);

        if(gameResult == null && board.getUnsunkShips().isEmpty()) {
            gameResult = new GameResult.FullyPlayed(player, boardType, shipPlacements1, shipPlacements2, moves);
        }
    }

    private void wrap(PlayerType player, Runnable runnable, long timeoutSeconds) {
        wrap(player, runnable, timeoutSeconds, false, true);
    }

    private void wrap(PlayerType player, Runnable runnable, long timeoutSeconds, boolean ignoreGameResult, boolean writeGameResult) {
        Callable<Void> callable = () -> {
            runnable.run();
            return null;
        };
        wrap(player, callable, timeoutSeconds, ignoreGameResult, writeGameResult);
    }

    private <T> T wrap(PlayerType player, Callable<T> callable, long timeoutSeconds) {
        return wrap(player, callable, timeoutSeconds, false, true);
    }

    private <T> T wrap(PlayerType player, Callable<T> callable, long timeoutSeconds, boolean ignoreGameResult, boolean writeGameResult) {
        if(!ignoreGameResult && gameResult != null) {
            return null;
        }
        try {
            return doWrap(callable, timeoutSeconds);
        } catch(Exception e) {
            if(writeGameResult && gameResult == null) {
                gameResult = new GameResult.Disqualified(player.getOtherPlayer(), boardType, e);
            }
            return null;
        }
    }

    private <T> T doWrap(Callable<T> callable, long timeoutSeconds) {
        Future<T> f = executor.submit(callable);
        try {
            return f.get(timeoutSeconds, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            System.err.println("Interrupted");
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            if(e.getCause() instanceof RuntimeException e1) {
                throw e1;
            } else {
                throw new RuntimeException(e.getCause());
            }
        } catch (TimeoutException e) {
            throw new CallTimedOutException("Timed out");
        }
    }

    @Override
    public void close() throws IOException {
        executor.close();
    }
}
