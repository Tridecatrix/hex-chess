package model.piece;

import model.Board;
import model.Move;
import model.PieceType;
import model.Position;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;

public class Bishop extends Piece {
    public Bishop(Color color) {
        super(color);
    }

    @Override
    public Set<Move> getMovesFromPos(Board board, Position fromPos) {
        ArrayList<BiFunction<Position, Integer, Position>> movementDirs = new ArrayList<>(List.of(
                Position::bishopStepLeft,
                Position::bishopStepRight,
                Position::bishopStepBackwardLeft,
                Position::bishopStepBackwardRight,
                Position::bishopStepForwardLeft,
                Position::bishopStepForwardRight));

        Set<Move> moves = new HashSet<>();

        for (BiFunction<Position, Integer, Position> stepInDir : movementDirs) {
            Position nextPos = stepInDir.apply(fromPos, board.boardDiameter);

            while (board.isInBounds(nextPos) && board.getPos(nextPos) == null) {
                moves.add(new Move(fromPos, nextPos));
                nextPos = stepInDir.apply(nextPos, board.boardDiameter); // continue moving in direction until we
                // hit a piece or edge of board
            }

            if (board.isInBounds(nextPos) && board.getPos(nextPos) != null && board.getPos(nextPos).color != this.color) {
                moves.add(new Move(fromPos, nextPos)); // add the capturing move for opponent piece
            }
        }

        return moves;
    }

    @Override
    public char getCharBase() {
        return 'b';
    }

    @Override
    public PieceType getPieceType() {
        return PieceType.BISHOP;
    }
}
