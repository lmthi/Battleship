package einfprog.bst;

public final class MatchResult {
    public final MatchResultType type;

    public MatchResult(MatchResultType type) {
        this.type = type;
    }

    public enum MatchResultType {
        FULLY_PLAYED,
    }
}
