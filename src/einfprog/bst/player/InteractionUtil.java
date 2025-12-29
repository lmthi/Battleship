package einfprog.bst.player;

import einfprog.SavitchIn;
import einfprog.bst.exception.BSTException;
import einfprog.bst.game.BoardType;
import einfprog.bst.game.Coordinates;
import einfprog.bst.state.BoardTracker;

import java.util.ArrayList;
import java.util.List;

public class InteractionUtil {
    public static void waitForInput() {
        System.out.print("Press any key to continue...");
        try
        {
            System.in.read();
        }
        catch(Exception e) {}
    }

    public static void printBoards(BoardTracker boardTracker) {
        List<String> myBoardPrint = boardTracker.getMyBoardRepresentationList();
        List<String> opponentBoardPrint = boardTracker.getOpponentBoardRepresentationList();

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

    public static void printLine() {
        System.out.println("-".repeat(50));
    }

    public static Coordinates readCoordinates(ManualPlayer manualPlayer, BoardType boardType) {
        Coordinates coords = null;
        while(coords == null) {
            System.out.print("Enter Coordinates: ");

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
                    r = boardType.readRow(sr.charAt(0));
                } else {
                    try {
                        r = Integer.parseInt(sr) - 1;
                    } catch (NumberFormatException _) {
                        retry = true;
                    }
                }

                if(sc.length() == 1) {
                    c = boardType.readColumn(sc.charAt(0));
                } else {
                    try {
                        c = Integer.parseInt(sc) - 1;
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
}
