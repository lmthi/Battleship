package einfprog.bst;

import einfprog.bst.exception.CallTimedOutException;
import einfprog.bst.game.BoardType;
import einfprog.bst.game.ShipType;
import einfprog.bst.player.IPlayer;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.*;

public class TimeOutGameManager extends GameManager implements Closeable {
    public final long timeoutMillis;
    private final ExecutorService executor;

    public TimeOutGameManager(BoardType boardType, List<ShipType> ships, IPlayer player1, IPlayer player2, long timeoutMillis) {
        super(boardType, ships, player1, player2);
        this.timeoutMillis = timeoutMillis;
        executor = Executors.newSingleThreadExecutor();
    }

    protected <T> T doWrap(Callable<T> callable) {
        try {
            Future<T> f = executor.submit(() -> super.doWrap(callable));
            return f.get(timeoutMillis, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            System.err.println("Interrupted");
            throw new RuntimeException(e);
        } catch (TimeoutException e) {
            throw new CallTimedOutException("Timed out");
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

    @Override
    public void close() throws IOException {
        executor.close();
    }
}
