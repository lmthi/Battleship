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
            System.out.println("What is Your Name? Please Enter:");
            name = SavitchIn.readLineWord();
        }

        printLine();
    }

    @Override
    public List<ShipPlacement> placeMyShips(List<ShipType> ships) {
        List<ShipPlacement> placedShips = new ArrayList<>(ships.size());
        FullBoardView curBoard = null;

        for(ShipType ship : ships) {
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
        System.out.println("Which Coordinates to Fire at?");
        return readCoordinates();
    }

    @Override
    public void onFireResult(FireResult result) {
        super.onFireResult(result);

        if(result.type == FireResult.FireResultType.MISS) {
            System.out.println("Miss at " + result.coords + "!");
        } else if(result.type == FireResult.FireResultType.HIT) {
            System.out.println("Hit at " + result.coords + "!");
        } else if(result instanceof FireResult.Sunk sunk) {
            System.out.println("You Sunk their " + sunk.ship.name + " at " + result.coords + "!");
        }

        printLine();
        sleep();
    }

    @Override
    public void onOpponentFires(Coordinates coordinates) {
        super.onOpponentFires(coordinates);

        System.out.println("Opponent Fired at: " + coordinates);
        System.out.println("Your Board:");
        System.out.println(myBoard.getLineRepresentation());
        printLine();
        sleep();
    }

    protected Coordinates readCoordinates() {
        Coordinates coords = null;
        while(coords == null) {
            System.out.println("Enter Coordinates:");

            String sr = null;
            String sc = null;

            String in = SavitchIn.readLine();
            if(in.length() == 2) {
                sr = in.substring(0, 1);
                sc = in.substring(1, 2);
            }

            String[] split = in.split(" ");
            if(split.length == 2) {
                sr = split[0];
                sc = split[1];
            }

            if(sr != null && sc != null) {
                int r = 0;
                int c = 0;
                boolean retry = false;

                if(sr.length() == 1) {
                    r = sr.charAt(0);
                } else {
                    try {
                        r = boardType.rowStart + Integer.parseInt(sr) - 1;
                    } catch (NumberFormatException _) {
                        retry = true;
                    }
                }

                if(sc.length() == 1) {
                    c = sc.charAt(0);
                } else {
                    try {
                        c = boardType.colStart + Integer.parseInt(sc) - 1;
                    } catch (NumberFormatException _) {
                        retry = true;
                    }
                }

                if(!retry) {
                    try {
                        return coords = new Coordinates(boardType, r, c);
                    } catch (BSTException e) {
                        System.out.println("Invalid coordinates input:");
                        System.out.println(e.getMessage());
                    }
                }
            }

            System.out.println("Either:");
            System.out.println("- Type coordinates together if each axis requires only 1 character (eg. \"E7\").");
            System.out.println("- Type coordinates with a space (' ') in between, if any axis requires more than 1 digits (eg. \"A 10\").");
        }

        return coords;
    }

    protected void printBoards() {
        List<String> myBoardPrint = myBoard.getLineRepresentationList();
        List<String> opponentBoardPrint = opponentBoard.getLineRepresentationList();

        if(myBoardPrint.size() != opponentBoardPrint.size()) {
            System.err.println("Can not print boards of different sizes next to each other.");
            return;
        }

        List<String> lines = new ArrayList<>(myBoardPrint.size());
        final String space = " ".repeat(5);
        for(int i = 0; i < myBoardPrint.size(); i++) {
            lines.add(myBoardPrint.get(i) + space + opponentBoardPrint.get(i));
        }

        System.out.println("Your Board:" + " ".repeat(25) + "Opponent's Board:");
        for(String line : lines) {
            System.out.println(line);
        }
    }

    protected void printLine() {
        System.out.println("-".repeat(50));
    }

    protected void sleep() {
        try {
            Thread.sleep(TimeUnit.SECONDS.toMillis(2));
        } catch (InterruptedException e) {}
    }
}
