package einfprog.bst.player;

import einfprog.bst.FireResult;
import einfprog.bst.PlayerType;
import einfprog.bst.game.BoardType;
import einfprog.bst.game.Coordinates;
import einfprog.bst.game.ShipPlacement;
import einfprog.bst.game.ShipType;
import einfprog.bst.state.BoardMarker;

import java.util.*;
import java.util.stream.IntStream;

public class SearchAndSeekPlayer extends AbstractPlayer {
    public final String name;
    protected Mode mode;

    public SearchAndSeekPlayer(String name) {
        this.name = name;
        mode = null;
    }

    @Override
    public String getName() {
        return name;
    }

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

    @Override
    public Coordinates selectNextTarget() {
        Coordinates coords = mode.getNextTarget();
        if(coords == null) {
            toSearchMode();
            return mode.getNextTarget();
        } else {
            return coords;
        }
    }

    @Override
    public void onGameStart(PlayerType playerType) {
        super.onGameStart(playerType);
        toSearchMode();
    }

    @Override
    public void onFireResult(FireResult result) {
        super.onFireResult(result);

        if(mode.type == ModeType.SEEK) {
            if(mode.onFireResult(result)) {
                toSearchMode();
            }
        } else if(mode.type == ModeType.SEARCH && result.type == FireResult.FireResultType.HIT) {
            toSeekMode(result.coords);
        }
    }

    protected void toSearchMode() {
        List<Coordinates> pattern = findNextPattern();
        Collections.shuffle(pattern, random);
        mode = new SearchMode(boardType, random, pattern);
    }

    protected void toSeekMode(Coordinates coords) {
        mode = new SeekMode(coords, random);
    }

    protected List<Coordinates> findNextPattern() {
        List<Coordinates> pattern = makePattern(1, false, 0);
        int length = shipsToSink.stream().mapToInt(s -> Math.max(s.length, s.width)).min().orElse(1);

        List<Coordinates> nextPattern;
        for(int offset = 0; offset < length; offset++) {
            nextPattern = makePattern(length, true, offset);
            if(nextPattern.size() < pattern.size()) {
                pattern = nextPattern;
            }

            nextPattern = makePattern(length, false, offset);
            if(nextPattern.size() < pattern.size()) {
                pattern = nextPattern;
            }
        }

        return pattern;
    }

    protected List<Coordinates> makePattern(int length, boolean reverse, int offset) {
        if(length == 1) {
            return IntStream.range(0, boardType.height * boardType.width)
                    .mapToObj(i -> new Coordinates(boardType, i / boardType.width, i % boardType.width)).toList();
        }

        List<Coordinates> result = new LinkedList<>();
        for(int r = 0; r < boardType.height; r++) {
            for(int c = offset; c < boardType.width; c+=length) {
                Coordinates coords = new Coordinates(boardType, r, c);
                if(opponentBoard.getMarker(coords).type == BoardMarker.BoardMarkerType.NONE) {
                    result.add(coords);
                }
            }
            offset = (offset + (!reverse ? 1 : length - 1)) % length;
        }
        return result;
    }

    protected abstract class Mode {
        protected final ModeType type;

        protected Mode(ModeType type) {
            this.type = type;
        }

        protected abstract Coordinates getNextTarget();
        protected boolean onFireResult(FireResult result) {
            return false;
        }
    }

    protected class SeekMode extends Mode {
        protected final Coordinates coords;
        protected final boolean clockwise;
        protected Direction direction;
        protected int directionsTried;

        protected SeekMode(Coordinates seekCoords, Random random) {
            super(ModeType.SEEK);
            this.coords = seekCoords;
            clockwise = random.nextBoolean();
            direction = Direction.values()[random.nextInt(Direction.values().length)];
            directionsTried = 0;
        }

        @Override
        protected Coordinates getNextTarget() {
            if(directionsTried >= Direction.values().length) {
                return null;
            }

            int row = coords.row;
            int column = coords.column;
            Coordinates targetCoords = null;

            do {
                row += direction.dRow;
                column += direction.dColumn;

                if(!boardType.validate(row, column)) {
                    // out of bounds
                    goToNextDirection();
                    continue;
                }

                targetCoords = new Coordinates(boardType, row, column);
            } while(opponentBoard.getMarker(targetCoords).type == BoardMarker.BoardMarkerType.HIT);

            return targetCoords;
        }

        @Override
        protected boolean onFireResult(FireResult result) {
            if(directionsTried >= Direction.values().length) {
                return true;
            } else if(result.type == FireResult.FireResultType.MISS) {
                goToNextDirection();
            } else if(result.type == FireResult.FireResultType.SUNK) {
                return true;
            }

            return false;
        }

        protected void goToNextDirection() {
            int length = Direction.values().length;
            int index = direction.ordinal();
            index = clockwise ? index + 1 : index - 1;
            index = (index + length) % length;
            direction = Direction.values()[index];
            directionsTried++;
        }

        protected enum Direction {
            UP(-1, 0), RIGHT(0, +1), DOWN(+1, 0), LEFT(0, -1);

            public final int dRow;
            public final int dColumn;

            Direction(int dRow, int dColumn) {
                this.dRow = dRow;
                this.dColumn = dColumn;
            }
        }
    }

    protected class SearchMode extends Mode {
        protected final BoardType boardType;
        protected final Random random;
        protected final List<Coordinates> pattern;

        protected SearchMode(BoardType boardType, Random random, List<Coordinates> pattern) {
            super(ModeType.SEARCH);
            this.boardType = boardType;
            this.random = random;
            this.pattern = pattern;
        }

        @Override
        protected Coordinates getNextTarget() {
            if(pattern.isEmpty()) {
                // should never happen
                int r = random.nextInt(boardType.height);
                int c = random.nextInt(boardType.width);
                return new Coordinates(boardType, r, c);
            }
            return pattern.removeLast();
        }
    }

    protected enum ModeType {
        SEARCH, SEEK;
    }
}
