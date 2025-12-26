package einfprog.bst;

import einfprog.bst.game.BoardType;
import einfprog.bst.game.Coordinates;
import einfprog.bst.game.ShipType;
import einfprog.bst.player.*;
import einfprog.bst.state.BoardMarker;
import einfprog.bst.state.ShallowBoardView;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;

public class Main {
    public static void main(String[] args) throws IOException {
        Random random = new Random(100);

        /*
        testTourney(random);
        /*/
        testPlayer(random, new SearchAndSeekPlayer("search and seek player"));
        //*/
    }

    private static void testPlayer(Random random, IPlayer player) {
        List<IPlayer> players = new LinkedList<>();
        players.add(new AutoPlacementManualPlayer());
        players.add(player);

        TournamentManager tm = new TournamentManager(BoardType.STANDARD_BOARD, ShipType.STANDARD_SHIPS, players, random);

        try(ExecutorService executor =  Executors.newSingleThreadExecutor()) {
            IPlayer winner = tm.freeForAll(players, 1);
            System.out.println("WINNER: " + winner.getName());
        }
    }

    private static void testTourney(Random random) {
        List<IPlayer> players = new LinkedList<>();

        for(int i = 1; i <= 3; i++) {
            players.add(new RandomPlayer("P".repeat(i) + i));
        }
        //players.add(new ManualPlayer());

        TournamentManager tm = new TournamentManager(BoardType.STANDARD_BOARD, ShipType.STANDARD_SHIPS, players, random);

        try(ExecutorService executor =  Executors.newSingleThreadExecutor()) {
            //List<IPlayer> wrappedPlayers = TimeoutPlayerWrapper.wrap(players, executor, TimeUnit.SECONDS.toMillis(2));
            //IPlayer winner = tm.freeForAll(wrappedPlayers, 10);
            IPlayer winner = tm.freeForAll(players, 4);
            System.out.println("WINNER: " + winner.getName());
        }
    }
}