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

    protected List<Coordinates> moves;

    public RandomPlayer(String name) {
        this.name = name;
        moves = null;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<ShipPlacement> placeMyShips() {
        List<ShipPlacement> result = new ArrayList<>(shipTypes.size());
        for(int i = 0; i < shipTypes.size(); i++) {
            ShipType shipType = shipTypes.get(i);
            while(result.size() <= i) {
                ShipPlacement placement;
                if(random.nextBoolean()) {
                    //horizontal
                    int r = random.nextInt(boardType.height - shipType.width + 1);
                    int c = random.nextInt(boardType.width - shipType.length + 1);
                    placement = new ShipPlacement(shipType, new Coordinates(boardType, r, c), new Coordinates(boardType, r + shipType.width - 1, c + shipType.length - 1));
                } else {
                    //vertical
                    int r = random.nextInt(boardType.height - shipType.length + 1);
                    int c = random.nextInt(boardType.width - shipType.width + 1);
                    placement = new ShipPlacement(shipType, new Coordinates(boardType, r, c), new Coordinates(boardType, r + shipType.length - 1, c + shipType.width - 1));
                }

                boolean overlap = false;
                for(int j = 0; j < i && !overlap; j++) {
                    ShipPlacement otherPlacement = result.get(j);
                    if(ShipPlacement.overlaps(otherPlacement, placement)) {
                        overlap = true;
                    }
                }

                if(!overlap) {
                    result.add(placement);
                }
            }

        }
        return result;
    }

    @Override
    public Coordinates selectNextTarget() {
        if(moves.isEmpty()) {
            // should never happen
            return new Coordinates(boardType, random.nextInt(boardType.height), random.nextInt(boardType.width));
        }
        return moves.removeLast();
    }

    @Override
    public void onGameStart(PlayerType playerType) {
        super.onGameStart(playerType);

        moves = new ArrayList<>(boardType.height * boardType.width);
        for(int r = 0; r < boardType.height; r++) {
            for(int c = 0; c < boardType.width; c++) {
                moves.add(new Coordinates(boardType, r, c));
            }
        }
        Collections.shuffle(moves, random);
    }
}
