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

public class InteractionPlayerWrapper extends AbstractPlayer {
    protected final IPlayer player;

    public InteractionPlayerWrapper(IPlayer player) {
        this.player = player;
    }

    @Override
    public String getName() {
        return player.getName();
    }

    @Override
    public List<ShipPlacement> placeMyShips() {
        return player.placeShips();
    }

    @Override
    public Coordinates selectNextTarget() {
        InteractionUtil.printBoards(boardTracker);
        return player.fire();
    }

    @Override
    public void init(Random random) {
        player.init(random);
        super.init(random);
    }

    @Override
    public void onMatchStart(String opponent, int rounds, BoardType board, List<ShipType> ships) {
        player.onMatchStart(opponent, rounds, board, ships);
        super.onMatchStart(opponent, rounds, board, ships);
    }

    @Override
    public void onMatchEnd(MatchResult result) {
        player.onMatchEnd(result);
        super.onMatchEnd(result);
    }

    @Override
    public void onGameStart(PlayerType playerType) {
        player.onGameStart(playerType);
        super.onGameStart(playerType);
    }

    @Override
    public void onGameEnd(GameResult result) {
        player.onGameEnd(result);
        super.onGameEnd(result);
    }

    @Override
    public void onFireResult(FireResult result) {
        player.onFireResult(result);
        super.onFireResult(result);

        if(!boardTracker.getGoingFirst()) {
            requestInteraction();
        }
    }

    @Override
    public void onOpponentFires(Coordinates coordinates) {
        player.onOpponentFires(coordinates);
        super.onOpponentFires(coordinates);

        if(boardTracker.getGoingFirst()) {
            requestInteraction();
        }
    }

    protected void requestInteraction() {
        InteractionUtil.printBoards(boardTracker);
        InteractionUtil.waitForInput();
    }
}
