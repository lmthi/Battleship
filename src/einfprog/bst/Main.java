package einfprog.bst;

import einfprog.bst.game.BoardType;
import einfprog.bst.game.ShipType;
import einfprog.bst.player.HeatmapPlayer;
import einfprog.bst.player.IPlayer;
import einfprog.bst.player.ManualPlayer;
import einfprog.bst.player.RandomPlayer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) throws IOException {
        Random random = new Random(100);

        List<IPlayer> players = new LinkedList<>();

        for(int i = 1; i <= 1; i++) {
            players.add(new RandomPlayer("P".repeat(i) + i, 3));
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