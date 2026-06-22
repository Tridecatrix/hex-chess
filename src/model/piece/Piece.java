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
        PURPLE // for 6 player
    }

    public static List<Color> allColors = List.of(
            Piece.Color.WHITE,
            Piece.Color.BLACK,
            Piece.Color.RED,
            Piece.Color.BLUE,
            Piece.Color.YELLOW,
            Piece.Color.GREEN,
            Piece.Color.PURPLE
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