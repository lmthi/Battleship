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

public class ManualPlayer extends AbstractPlayer {
    protected String name;

    public ManualPlayer() {
        name = "";
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void init(Random random) {
        super.init(random);

        while(name.isEmpty()){
            System.out.print("What is Your Name? Please Enter: ");
            name = SavitchIn.readLineWord();
        }

        InteractionUtil.printLine();
    }

    @Override
    public List<ShipPlacement> placeMyShips() {
        List<ShipPlacement> placedShips = new ArrayList<>(shipTypes.size());
        FullBoardView curBoard = null;

        for(ShipType ship : shipTypes) {
            curBoard = new FullBoardView(boardType, placedShips);

            System.out.println("Current Board:");
            System.out.println(curBoard.getLineRepresentation());
            System.out.println("Next to place: " + ship.name + ", Length: " + ship.length + (ship.width == 1 ? "" : ", Width: " + ship.width));

            ShipPlacement placement = null;
            while(placement == null){
                System.out.println("Where to place the first end of the ship (i.e. the top and/or left coordinate of the ship)?");
                Coordinates coords1 = readCoordinates();
                System.out.println("Where to place the second end of the ship (i.e. the bottom and/or right coordinate of the ship)?");
                Coordinates coords2 = readCoordinates();

                try {
                    placement = new ShipPlacement(ship, coords1, coords2);
                } catch (BSTException e) {
                    System.out.println("Incorrect placement input:");
                    System.out.println(e.getMessage());
                }
            }

            placedShips.add(placement);
        }

        return placedShips;
    }

    @Override
    public Coordinates selectNextTarget() {
        printBoards();
        System.out.print("Which Coordinates to Fire at? ");
        return readCoordinates();
    }

    @Override
    public void onFireResult(FireResult result) {
        super.onFireResult(result);

        if(result.type == FireResult.FireResultType.MISS) {
            System.out.println("-> Miss at " + result.coords + "!");
        } else if(result.type == FireResult.FireResultType.HIT) {
            System.out.println("-> Hit at " + result.coords + "!");
        } else if(result instanceof FireResult.Sunk sunk) {
            System.out.println("-> You Sunk their " + sunk.ship.name + " at " + result.coords + "!");
        }

        InteractionUtil.waitForInput();
        InteractionUtil.printLine();
    }

    @Override
    public void onOpponentFires(Coordinates coordinates) {
        super.onOpponentFires(coordinates);

        System.out.println("Your Board:");
        System.out.println(myBoard.getLineRepresentation());
        System.out.println("-> Opponent Fired at: " + coordinates);
        InteractionUtil.waitForInput();
        InteractionUtil.printLine();
    }

    protected Coordinates readCoordinates() {
        return InteractionUtil.readCoordinates(this, boardType);
    }

    protected void printBoards() {
        InteractionUtil.printBoards(this, myBoard, opponentBoard);
    }
}
