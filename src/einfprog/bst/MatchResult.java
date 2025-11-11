package einfprog.bst;

public final class MatchResult {
    public final MatchResultType type;

    public final String player1;
    public final String player2;

    public final int totalRounds;
    public final int scorePlayer1;
    public final int scorePlayer2;

    public MatchResult(MatchResultType type, String player1, String player2,
                       int totalRounds, int scorePlayer1, int scorePlayer2) {
        this.type = type;
        this.player1 = player1;
        this.player2 = player2;
        this.totalRounds = totalRounds;
        this.scorePlayer1 = scorePlayer1;
        this.scorePlayer2 = scorePlayer2;
    }

    public String getWinner() {
        return scorePlayer1 > scorePlayer2 ? player1 : player2;
    }

    public String getLoser() {
        return scorePlayer1 < scorePlayer2 ? player1 : player2;
    }

    public enum MatchResultType {
        FULLY_PLAYED,
        DISQUALIFIED;
    }
}
