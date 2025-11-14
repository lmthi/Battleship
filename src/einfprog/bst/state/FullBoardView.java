package einfprog.bst.state;

import einfprog.bst.FireResult;
import einfprog.bst.game.BoardType;
import einfprog.bst.game.Coordinates;
import einfprog.bst.game.ShipPlacement;

import java.util.*;
import java.util.stream.IntStream;

public class FullBoardView {
    protected final ShallowBoardView boardView;

    protected final List<ShipPlacement> ships;
    protected final ShipPlacement[][] placement;

    protected final List<ShipPlacement> unsunkShips;

    public FullBoardView(BoardType boardType, List<ShipPlacement> ships) {
        boardView = new ShallowBoardView(boardType);

        this.ships = ships;

        placement = new ShipPlacement[boardType.height][boardType.width];
        for(int r = 0; r < boardType.height; r++) {
            for(int c = 0; c < boardType.width; c++) {
                placement[r][c] = null;
            }
        }

        for(ShipPlacement ship : ships) {
            for(Coordinates coords : ship.allCoordinates) {
                placement[coords.row][coords.column] = ship;
            }
        }

        unsunkShips = new ArrayList<>(ships.size());
        for(ShipPlacement ship : ships) {
            unsunkShips.add(ship);
        }
    }

    public FireResult fireAt(Coordinates coords) {
        BoardMarker.BoardMarkerType prevMarker = boardView.getMarker(coords).type;

        if(prevMarker != BoardMarker.BoardMarkerType.NONE) {
            return new FireResult.Repetition(coords);
        }

        ShipPlacement hit = placement[coords.row][coords.column];
        if(hit == null) {
            boardView.setMarker(coords, new BoardMarker.Miss());
            return new FireResult.Miss(coords);
        }

        // hit a ship and shot is not a repetition
        for(Coordinates shipCoords : hit.allCoordinates) {
            if(coords.equals(shipCoords)) {
                continue;
            }

            if(boardView.getMarker(shipCoords).type == BoardMarker.BoardMarkerType.NONE) {
                // one coord was not hit
                boardView.setMarker(coords, new BoardMarker.Hit());
                return new FireResult.Hit(coords);
            }
        }

        unsunkShips.remove(hit);
        boardView.setMarker(coords, new BoardMarker.Sunk(hit.shipType));
        return new FireResult.Sunk(coords, hit.shipType);
    }

    public List<ShipPlacement> getUnsunkShips() {
        return Collections.unmodifiableList(unsunkShips);
    }

    public List<ShipPlacement> getSunkShips() {
        return ships.stream().filter(s -> !unsunkShips.contains(s)).toList();
    }


    public String getLineRepresentation() {
        return String.join("\n", getLineRepresentationList());
    }

    public List<String> getLineRepresentationList() {
        final BoardType boardType = boardView.boardType;

        List<String> leftMarkers = IntStream.range(0, boardType.height).mapToObj(boardType::formatRow).toList();
        int leftMarkerWidth = leftMarkers.stream().mapToInt(String::length).max().orElse(0);
        leftMarkers = leftMarkers.stream().map(s -> " ".repeat(leftMarkerWidth - s.length()) + s).toList();

        final String colorBackGround = "\u001B[104m";
        final String colorBackGroundShip = "\u001B[44m";
        final String colorBackGroundDark = colorBackGround;
        final String colorDefault = "\u001B[0m";
        final String colorBorder = "\u001B[97m" + colorBackGroundDark;
        final String colorNone = "\u001B[94m";
        final String colorMiss = "\u001B[97m";
        final String colorHit = "\u001B[91m";
        final String colorSunk = "\u001B[31m";

        final char horizontalDelim = '\u2003';
        final String corner = colorBorder + "\u23FA" + colorDefault;

        final String borderDelim = colorBackGroundDark + horizontalDelim + colorDefault;
        final String fieldDelim = colorBackGround + horizontalDelim + colorDefault;
        final String fieldDelimShip = colorBackGroundShip + horizontalDelim + colorDefault;

        List<String> topMarkerRows = new LinkedList<>();
        boolean flag = true;
        for(int l = 0; flag; l++) {
            flag = false;

            StringBuilder row = new StringBuilder();
            row.append(corner).append(" ".repeat(leftMarkerWidth-1));
            for (int i = 0; i < boardType.width; i++) {
                String s = boardType.formatColumn(i);
                if (s.length() <= l) {
                    row.append(" ");
                    continue;
                } else if (s.length() > l + 1) {
                    flag = true;
                }
                row.append(borderDelim).append(colorBorder).append(s.charAt(l)).append(colorDefault);
            }
            row.append(borderDelim).append(corner);
            topMarkerRows.add(row.toString());
        }

        List<String> rows = new ArrayList<>(boardType.height + 2 * topMarkerRows.size());
        rows.addAll(topMarkerRows);
        for(int r = 0; r < boardType.height; r++) {
            StringBuilder row = new StringBuilder();
            String mark = leftMarkers.get(r);
            row.append(colorBorder).append(mark).append(colorDefault);
            for(int c = 0; c < boardType.width; c++) {
                BoardMarker.BoardMarkerType marker = boardView.markers[r][c].type;
                String s = switch(marker) {
                    case NONE -> colorNone + "◼" + colorDefault;
                    case MISS -> colorMiss + "◼" + colorDefault;
                    case HIT -> colorHit + "◼" + colorDefault;
                    case SUNK -> colorSunk + "◼" + colorDefault;
                };
                if(c > 0 && placement[r][c] != null && placement[r][c-1] != null && placement[r][c] == placement[r][c-1]) {
                    row.append(fieldDelimShip);
                } else {
                    row.append(fieldDelim);
                }
                if(placement[r][c] != null) {
                    row.append(colorBackGroundShip);
                } else {
                    row.append(colorBackGround);
                }
                row.append(s);
            }
            row.append(fieldDelim).append(colorBorder).append(mark).append(colorDefault);
            rows.add(row.toString());
        }
        rows.addAll(topMarkerRows);

        return rows;
    }
}
