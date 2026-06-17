package model.piece;

import model.Board;
import model.Move;
import model.PieceType;
import model.Position;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

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
        GREY
    }

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

    abstract public char getChar();

    abstract public PieceType getPieceType();
}