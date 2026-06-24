package model.piece;

import model.Board;
import model.Move;
import model.PieceType;
import model.Position;

import java.util.*;

public abstract class Piece {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Piece piece)) return false;
        return color == piece.color && this.getPieceType() == ((Piece) o).getPieceType();
    }

    @Override
    public int hashCode() {
        return Objects.hash(color, this.getPieceType());
    }

    public enum Color {
        WHITE,
        BLACK,
        RED, // for 3 player
        BLUE, // for 3 player
        YELLOW, // for 6 player
        GREEN, // for 6 player
        PURPLE; // for 6 player

        public String toStringCapitalised() {
            return this.toString().substring(0, 1).toUpperCase() + this.toString().substring(1).toLowerCase();
        }

        public String getPieceColorAsHex() {
            return switch (this) {
                case WHITE -> "0x000000"; // white pieces have their outlines in black
                case BLACK -> "0x000000";
                case RED -> "0xFF0000";
                case BLUE -> "0x0000FF";
                case YELLOW -> "0xCCCC00";
                case GREEN -> "0x00CC00";
                case PURPLE -> "0xB056FF";
            };
        }
    }

    public static List<Color> allColors = List.of(
            Color.WHITE,
            Color.BLACK,
            Color.RED,
            Color.BLUE,
            Color.YELLOW,
            Color.GREEN,
            Color.PURPLE
    );

    public Color color;

    public Piece(Color color) {
        this.color = color;
    }

    abstract public Set<Move> getMovesFromPos(Board board, Position fromPos);

    // default implementation: just get all moves, as most pieces capture with the same movements
    // that they move to empty spaces with. exceptions are pawn and king
    public Set<Move> getPotentialCapturingMovesFromPos(Board board, Position fromPos) {
        return getMovesFromPos(board, fromPos);
    }

    public char getChar() {
        switch (color) {
            case WHITE, RED, BLUE -> { return Character.toUpperCase(this.getCharBase()); }
            case BLACK, YELLOW, GREEN, PURPLE -> { return Character.toLowerCase(this.getCharBase()); }
            default -> { return ' '; }
        }
    }

    abstract char getCharBase();

    abstract public PieceType getPieceType();

    public Color getColor() {
        return color;
    }

    public int compareTo(Piece other) {
        if (this.color.equals(other.color)) {
            return this.getPieceType().compareTo(other.getPieceType());
        } else {
            return this.color.compareTo(other.color);
        }
    }

    public char getPieceIcon() {
        if (this.color != Color.BLACK) {
            switch (this.getPieceType()) {
                case PAWN -> {
                    return '♙';
                }
                case KNIGHT -> {
                    return '♘';
                }
                case BISHOP -> {
                    return '♗';
                }
                case ROOK -> {
                    return '♖';
                }
                case QUEEN -> {
                    return '♕';
                }
                case KING -> {
                    return '♔';
                }
                case NIGHTRIDER -> {
                    return 'm';
                }
            }
        } else {
            switch (this.getPieceType()) {
                case PAWN -> {
                    return '♟';
                }
                case KNIGHT -> {
                    return '♞';
                }
                case BISHOP -> {
                    return '♝';
                }
                case ROOK -> {
                    return '♜';
                }
                case QUEEN -> {
                    return '♛';
                }
                case KING -> {
                    return '♚';
                }
                case NIGHTRIDER -> {
                    return 'm';
                }
            }
        }

        return ' ';
    }
}