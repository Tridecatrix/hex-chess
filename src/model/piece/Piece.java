package model.piece;

import model.Board;
import model.Move;
import model.Position;

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

    abstract public char getChar();
}