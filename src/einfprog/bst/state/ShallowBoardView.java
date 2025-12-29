package einfprog.bst.state;

import einfprog.bst.game.BoardType;
import einfprog.bst.game.Coordinates;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;

public class ShallowBoardView {
    public final BoardType boardType;
    protected final BoardMarker[][] markers;

    public ShallowBoardView(BoardType boardType) {
        this.boardType = boardType;

        markers = new BoardMarker[boardType.height][boardType.width];
        for(int r = 0; r < boardType.height; r++) {
            for(int c = 0; c < boardType.width; c++) {
                markers[r][c] = new BoardMarker.None();
            }
        }
    }

    protected void validateCoords(Coordinates coords) {
        if(coords == null) {
            throw new IllegalArgumentException("Coordinates null");
        }
        if(coords.board != boardType) {
            throw new IllegalArgumentException("Wrong board");
        }
    }

    public BoardMarker getMarker(Coordinates coords) {
        validateCoords(coords);
        return markers[coords.row][coords.column];
    }

    public BoardMarker setMarker(Coordinates coords, BoardMarker marker) {
        BoardMarker prevMarker = getMarker(coords);
        markers[coords.row][coords.column] = marker;
        return prevMarker;
    }

    public String getLineRepresentation() {
        return String.join("\n", getLineRepresentationList());
    }

    public List<String> getLineRepresentationList() {
        //TODO clean this up
        List<String> leftMarkers = IntStream.range(0, boardType.height).mapToObj(boardType::formatRow).toList();
        int leftMarkerWidth = leftMarkers.stream().mapToInt(String::length).max().orElse(0);
        leftMarkers = leftMarkers.stream().map(s -> " ".repeat(leftMarkerWidth - s.length()) + s).toList();

        final String colorBackGround = "\u001B[104m";
        final String colorBackGroundDark = colorBackGround;
        final String colorDefault = "\u001B[0m";
        final String colorBorder = "\u001B[97m" + colorBackGroundDark;
        final String colorNone = "\u001B[94m" + colorBackGround;
        final String colorMiss = "\u001B[97m" + colorBackGround;
        final String colorHit = "\u001B[91m" + colorBackGround;
        final String colorSunk = "\u001B[31m" + colorBackGround;

        final char horizontalDelim = '\u2003';
        final String corner = colorBorder + "\u23FA" + colorDefault;

        final String borderDelim = colorBackGroundDark + horizontalDelim + colorDefault;
        final String fieldDelim = colorBackGround + horizontalDelim + colorDefault;

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
                BoardMarker.BoardMarkerType marker = markers[r][c].type;
                String s = switch(marker) {
                    case NONE -> colorNone + "◼" + colorDefault;
                    case MISS -> colorMiss + "◼" + colorDefault;
                    case HIT -> colorHit + "◼" + colorDefault;
                    case SUNK -> colorSunk + "◼" + colorDefault;
                };
                row.append(fieldDelim).append(s);
            }
            row.append(fieldDelim).append(colorBorder).append(mark).append(colorDefault);
            rows.add(row.toString());
        }
        rows.addAll(topMarkerRows);

        return rows;
    }
}
