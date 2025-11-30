package einfprog.bst;

import einfprog.bst.exception.TimeoutException;
import einfprog.bst.game.BoardType;
import einfprog.bst.game.Coordinates;
import einfprog.bst.game.ShipPlacement;
import einfprog.bst.game.ShipType;
import einfprog.bst.player.IPlayer;

import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

public class TimeoutPlayerWrapper implements IPlayer {
    protected final IPlayer player;
    protected final ExecutorService executor;
    protected final long timeoutMillis;

    public TimeoutPlayerWrapper(IPlayer player, ExecutorService executor, long timeoutMillis) {
        this.player = player;
        this.executor = executor;
        this.timeoutMillis = timeoutMillis;
    }

    @Override
    public String getName() {
        return wrap(() -> player.getName());
    }

    @Override
    public void init(Random random) {
        wrap(() -> player.init(random));
    }

    @Override
    public void onMatchStart(String opponent, int rounds, BoardType board, List<ShipType> ships) {
        wrap(() -> player.onMatchStart(opponent, rounds, board, ships));
    }

    @Override
    public void onMatchEnd(MatchResult result) {
        wrap(() -> player.onMatchEnd(result));
    }

    @Override
    public void onGameStart(PlayerType playerType) {
        wrap(() -> player.onGameStart(playerType));
    }

    @Override
    public void onGameEnd(GameResult result) {
        wrap(() -> player.onGameEnd(result));
    }

    @Override
    public List<ShipPlacement> placeShips() {
        return wrap(() -> player.placeShips());
    }

    @Override
    public Coordinates fire() {
        return wrap(() -> player.fire());
    }

    @Override
    public void onFireResult(FireResult result) {
        wrap(() -> player.onFireResult(result));
    }

    @Override
    public void onOpponentFires(Coordinates coordinates) {
        wrap(() -> player.onOpponentFires(coordinates));
    }

    protected void wrap(Runnable runnable) {
        wrap(() -> {
            runnable.run();
            return null;
        });
    }

    protected <T> T wrap(Callable<T> callable) {
        try {
            Future<T> f = executor.submit(callable);
            return f.get(timeoutMillis, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            System.err.println("Interrupted");
            throw new RuntimeException(e);
        } catch (java.util.concurrent.TimeoutException e) {
            throw new TimeoutException("Timed out");
        } catch (ExecutionException e) {
            if(e.getCause() instanceof RuntimeException e1) {
                throw e1;
            } else {
                throw new RuntimeException(e.getCause());
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static List<IPlayer> wrap(List<IPlayer> players, ExecutorService executor, long timeoutMillis) {
        return players.stream().map(p -> (IPlayer)new TimeoutPlayerWrapper(p, executor, timeoutMillis)).toList();
    }
}
