package einfprog.bst.player;

import einfprog.SavitchIn;
import einfprog.bst.FireResult;
import einfprog.bst.exception.BSTException;
import einfprog.bst.game.Coordinates;
import einfprog.bst.game.ShipPlacement;
import einfprog.bst.game.ShipType;
import einfprog.bst.state.FullBoardView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class AutoPlacementManualPlayer extends ManualPlayer {
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
}
