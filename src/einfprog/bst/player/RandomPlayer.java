package einfprog.bst.player;

import einfprog.bst.PlayerType;
import einfprog.bst.game.Coordinates;
import einfprog.bst.game.ShipPlacement;
import einfprog.bst.game.ShipType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RandomPlayer extends AbstractPlayer {
    public final String name;
    public final int retries;

    protected List<Coordinates> moves;

    public RandomPlayer(String name, int retries) {
        this.name = name;
        this.retries = retries;
        moves = null;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ShipPlacement[] placeMyShips(ShipType[] ships) {
        ShipPlacement[] result = new ShipPlacement[ships.length];
        for(int i = 0; i < ships.length; i++) {
            ShipType shipType = ships[i];
            while(result[i] == null) {
                ShipPlacement placement;
                if(random.nextBoolean()) {
                    //horizontal
                    int r = random.nextInt(boardType.getHeight() - shipType.width + 1) + boardType.rowStart;
                    int c = random.nextInt(boardType.getWidth() - shipType.length + 1) + boardType.colStart;
                    placement = new ShipPlacement(shipType, new Coordinates(boardType, r, c), new Coordinates(boardType, r + shipType.width - 1, c + shipType.length - 1));
                } else {
                    //vertical
                    int r = random.nextInt(boardType.getHeight() - shipType.length + 1) + boardType.rowStart;
                    int c = random.nextInt(boardType.getWidth() - shipType.width + 1) + boardType.colStart;
                    placement = new ShipPlacement(shipType, new Coordinates(boardType, r, c), new Coordinates(boardType, r + shipType.length - 1, c + shipType.width - 1));
                }

                boolean overlap = false;
                for(int j = 0; j < i && !overlap; j++) {
                    ShipPlacement otherPlacement = result[j];
                    if(ShipPlacement.overlaps(otherPlacement, placement)) {
                        overlap = true;
                    }
                }

                if(!overlap) {
                    result[i] = placement;
                }
            }

        }
        return result;
    }

    @Override
    public Coordinates selectNextTarget() {
        if(moves.isEmpty()) {
            // should never happen
            return new Coordinates(boardType, random.nextInt(boardType.getHeight()) + boardType.rowStart, random.nextInt(boardType.getWidth()) + boardType.colStart);
        }
        return moves.removeLast();
    }

    @Override
    public void onGameStart(PlayerType playerType) {
        super.onGameStart(playerType);

        moves = new ArrayList<>(boardType.getHeight() * boardType.getWidth());
        for(int r = 0; r < boardType.getHeight(); r++) {
            for(int c = 0; c < boardType.getWidth(); c++) {
                moves.add(new Coordinates(boardType, r, c));
            }
        }
        Collections.shuffle(moves, random);
    }
}
