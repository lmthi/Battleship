package einfprog.bst;

import einfprog.bst.game.BoardType;
import einfprog.bst.game.ShipType;
import einfprog.bst.player.IPlayer;

import java.util.*;
import java.util.function.BiConsumer;

public class TournamentManager {
    public final BoardType boardType;
    public final List<ShipType> ships;
    protected final GameManager gameManager;
    protected final Random random;

    public TournamentManager(BoardType boardType, List<ShipType> ships, List<IPlayer> players, Random random) {
        this.boardType = boardType;
        this.ships = ships;
        this.gameManager = new GameManager(boardType, ships);
        this.random = random;
    }

    public IPlayer freeForAll(List<IPlayer> players, int rounds) {
        for(IPlayer player : players) {
            player.init(new Random(random.nextLong()));
        }

        Map<IPlayer, Integer> scores = new HashMap<>();

        while(players.size() > 1) {
            BiConsumer<IPlayer, IPlayer> playMatchConsumer = (player1, player2) -> {
                int result = playMatch(rounds, player1, player2);
                while(result == 0) {
                    result = playMatch(2, player1, player2);
                }
                if(result < 0) {
                    System.out.printf("%s won against %s\n", player1.getName(), player2.getName());
                    scores.put(player1, scores.getOrDefault(player1, 0) + 1);
                } else /*if(result > 0)*/ {
                    System.out.printf("%s won against %s\n", player2.getName(), player1.getName());
                    scores.put(player2, scores.getOrDefault(player2, 0) + 1);
                }
            };

            forAllPairs(players, playMatchConsumer);

            int highestScore = scores.values().stream().mapToInt(Integer::intValue).max().orElse(0);
            players = players.stream().filter(p -> scores.getOrDefault(p, 0) >= highestScore).toList();
        }

        return players.getFirst();
    }

    protected int playMatch(int rounds, IPlayer player1, IPlayer player2) {
        player1.onMatchStart(player2.getName(), rounds, boardType, ships);
        player2.onMatchStart(player1.getName(), rounds, boardType, ships);

        boolean flip = false;
        int wins1 = 0;
        int wins2 = 0;
        for(int round = 0; round < rounds; round++) {
            PlayerType winner;
            GameResult result;
            if(flip) {
                result = gameManager.playNewRound(player1, player2);
                winner = result.winner;
            } else {
                result = gameManager.playNewRound(player2, player1);
                winner = result.winner.getOtherPlayer();
            }

            if(winner == PlayerType.PLAYER1) {
                System.out.printf("    %s // %s won against %s\n", result.toString(), player1.getName(), player2.getName());
                wins1++;
            } else {
                System.out.printf("    %s // %s won against %s\n", result.toString(), player2.getName(), player1.getName());
                wins2++;
            }

            if(result instanceof GameResult.Disqualified d) {
                d.exception.printStackTrace();
            }

            flip = !flip;
        }

        MatchResult result = new MatchResult(MatchResult.MatchResultType.FULLY_PLAYED);
        player1.onMatchEnd(result);
        player2.onMatchEnd(result);

        return wins1 - wins2;
    }

    protected void forAllPairs(List<IPlayer> players, BiConsumer<IPlayer, IPlayer> consumer) {
        /* doing it like this ensures that adding a new player
         * to the list does not change the games of the others
         */

        if(players.size() == 2) {
            consumer.accept(players.getFirst(), players.getLast());
            return;
        }

        if(players.size() < 2) {
            return;
        }

        IPlayer last = players.getLast();
        players = players.subList(0, players.size() - 1);
        forAllPairs(players, consumer);

        for(IPlayer player : players) {
            consumer.accept(player, last);
        }
    }
}
