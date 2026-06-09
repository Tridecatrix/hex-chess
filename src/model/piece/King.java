package model.piece;

import model.Board;
import model.Move;
import model.Position;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;

public class King extends Piece {
    public King(Color color) {
        super(color);
    }

    // TODO: moves that move the king into check are illegal
    @Override
    public Set<Move> getMovesFromPos(Board board, Position fromPos) {
        ArrayList<BiFunction<Position, Integer, Position>> movementDirs = new ArrayList<>(List.of(
                Position::oneStepBackward,
                Position::oneStepForward,
                Position::oneStepLeftAndBackward,
                Position::oneStepLeftAndForward,
                Position::oneStepRightAndBackward,
                Position::oneStepRightAndForward,
                Position::bishopStepLeft,
                Position::bishopStepRight,
                Position::bishopStepBackwardLeft,
                Position::bishopStepBackwardRight,
                Position::bishopStepForwardLeft,
                Position::bishopStepForwardRight));

        List<Move> possibleMoves = new ArrayList<>(12);
        for (BiFunction<Position, Integer, Position> stepInDir : movementDirs) {
            possibleMoves.add(new Move(fromPos, stepInDir.apply(fromPos, board.boarddim)));
        }

        Set<Move> moves = new HashSet<>();
        moves.addAll(possibleMoves.stream().filter(move -> board.isInBounds(move.toPos)
                && (board.getPos(move.toPos) == null
                || board.getPos(move.toPos).color != this.color)).toList());
        return moves;
    }

    @Override
    public char getChar() {
        return 'k';
    }
}
