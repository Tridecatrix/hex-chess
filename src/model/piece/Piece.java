package model.piece;

import model.Board;
import model.Move;
import model.Position;

import java.util.HashSet;
import java.util.Set;

public abstract class Piece {
    public enum Color {
        WHITE,
        BLACK
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
}