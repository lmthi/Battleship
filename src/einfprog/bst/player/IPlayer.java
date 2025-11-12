package einfprog.bst.player;

import einfprog.bst.FireResult;
import einfprog.bst.GameResult;
import einfprog.bst.MatchResult;
import einfprog.bst.PlayerType;
import einfprog.bst.game.BoardType;
import einfprog.bst.game.Coordinates;
import einfprog.bst.game.ShipPlacement;
import einfprog.bst.game.ShipType;

import java.util.List;
import java.util.Random;

/**
 * <p>A player interface to play battleship.</p>
 * </br>
 * <p>The whole <b>player lifecycle</b> is as follows:</p>
 * <ul>
 *     <li>{@link IPlayer#init(Random)} (once)</li>
 *     <li><b>Match lifecycle</b> (any amount of repetitions)</li>
 * </ul>
 * </br>
 * <p>The <b>match lifecycle</b> is as follows and in this order:
 * <ul>
 *     <li>{@link IPlayer#onMatchStart(String, int, BoardType)} (once)</li>
 *     <li><b>Game lifecycle</b> (at least once)</li>
 *     <li>{@link IPlayer#onMatchEnd(MatchResult)} (once)</li>
 * </ul>
 * </p>
 * </br>
 * <p>The <b>game lifecycle</b> is as follows and in this order:
 * <ul>
 *     <li>{@link IPlayer#onGameStart(PlayerType)} (once)</li>
 *     <li>{@link IPlayer#placeShips(List)} (once)</li>
 *     <li><b>Firing lifecycle</b> (at least once)</li>
 *     <li>{@link IPlayer#onGameEnd(GameResult)}</li>
 * </ul>
 * This lifecycle can be broken any time in which case only {@link IPlayer#onGameStart(PlayerType)} and {@link IPlayer#onGameEnd(GameResult)} are guaranteed to be called.
 * </p></br>
 * </br>
 * <p>The <b>firing lifecycle</b> is one or both of the following in any order (the order is consistent within a game based on who started):
 * <ul>
 *     <li>Both of the following in this order:
 *         <ul>
 *             <li>{@link IPlayer#fire()} (once)</li>
 *             <li>{@link IPlayer#onFireResult(FireResult)} (once)</li>
 *         </ul>
 *     </li>
 *     <li>{@link IPlayer#onOpponentFires(Coordinates)} (once)</li>
 * </ul>
 * </p>
 */
public interface IPlayer {
    String getName();

    /**
     * Player is initiated.
     * @param random Random access to use or keep.
     */
    void init(Random random);

    /**
     * A match is started and a best-of-X (with possible overtime) is played.
     * @param opponent The unique name of the opponent for this match.
     * @param rounds The match is a best-of-X where X is this parameter.
     * @param board The board type the match will be played on
     */
    void onMatchStart(String opponent, int rounds, BoardType board);

    /**
     * A match ended with a clear winner.
     * @param result Information about the result of the match.
     */
    void onMatchEnd(MatchResult result);

    /**
     * The next game is initiated.
     * @param playerType Whether this player goes first or second.
     */
    void onGameStart(PlayerType playerType);

    /**
     * The current game is ended.
     * @param result Information about the result of the game.
     */
    void onGameEnd(GameResult result);

    /**
     * Called at game start (once per ship) to place each ship on to the board.
     *
     * @param ships The ship types to place.
     * @return The ship placements where their order must be the same as the order of the ships parameter..
     */
    List<ShipPlacement> placeShips(List<ShipType> ships);

    /**
     * Called to get the next target announcement (i.e. where the player fires). The result is then given in {@link IPlayer#onFireResult(FireResult)}.
     * @return The coordinates to fire at.
     */
    Coordinates fire();

    /**
     * Information about the last {@link IPlayer#fire()} (hit, miss, sunk).
     * @param result The information.
     */
    void onFireResult(FireResult result);

    /**
     * Called whenever the opponent fires to allow for recording of the opponent's behaviour.
     * @param coordinates The coordinates the opponent fired at.
     */
    void onOpponentFires(Coordinates coordinates);
}
