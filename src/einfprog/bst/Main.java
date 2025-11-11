package einfprog.bst;

import einfprog.bst.game.BoardType;
import einfprog.bst.game.ShipType;
import einfprog.bst.player.IPlayer;
import einfprog.bst.player.ManualPlayer;
import einfprog.bst.player.RandomPlayer;

import java.io.IOException;
import java.util.Random;

public class Main {
    public static void main(String[] args) throws IOException {
        Random random = new Random(100);

        IPlayer player1 = new RandomPlayer("P1", 3);
        player1.init(new Random(random.nextLong()));

        IPlayer player2 = new RandomPlayer("P2", 3);
        player2.init(new Random(random.nextLong()));

        IPlayer human = new ManualPlayer();
        human.init(new Random(random.nextLong()));

        player1.onMatchStart(human.getName(), 1, BoardType.STANDARD_BOARD);
        //player2.onMatchStart(player1.getName(), 1, BoardType.STANDARD_BOARD);
        human.onMatchStart(player1.getName(), 1, BoardType.STANDARD_BOARD);

        try(GameManager m = new GameManager(BoardType.STANDARD_BOARD, ShipType.STANDARD_SHIPS, player1, human)) {
            GameResult res = m.playNewRound();
            System.out.println(res.toString());

            if(res instanceof GameResult.Disqualified d) {
                d.exception.printStackTrace();
            }
        }
    }
}