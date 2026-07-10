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

public class Knight extends Piece {
    public Knight(Color color) {
        super(color);
    }

    @Override
    public Set<Move> getMovesFromPos(Board board, Position fromPos) {
        List<Move> possibleMoves = new ArrayList<>(12);

        // list of all possible L shaped step sequences, i.e. step twice in one of 6 directions, then step once in
        // one of two adjacent directions
        List<List<BiFunction<Position, Integer, Position>>> stepsAll = List.of(
                List.of(Position::oneStepForward, Position::oneStepForward, Position::oneStepLeftAndForward),
                List.of(Position::oneStepForward, Position::oneStepForward, Position::oneStepRightAndForward),
                List.of(Position::oneStepLeftAndForward, Position::oneStepLeftAndForward, Position::oneStepLeftAndBackward),
                List.of(Position::oneStepLeftAndForward, Position::oneStepLeftAndForward, Position::oneStepForward),
                List.of(Position::oneStepRightAndForward, Position::oneStepRightAndForward, Position::oneStepRightAndBackward),
                List.of(Position::oneStepRightAndForward, Position::oneStepRightAndForward, Position::oneStepForward),
                List.of(Position::oneStepLeftAndBackward, Position::oneStepLeftAndBackward, Position::oneStepLeftAndForward),
                List.of(Position::oneStepLeftAndBackward, Position::oneStepLeftAndBackward, Position::oneStepBackward),
                List.of(Position::oneStepRightAndBackward, Position::oneStepRightAndBackward, Position::oneStepRightAndForward),
                List.of(Position::oneStepRightAndBackward, Position::oneStepRightAndBackward, Position::oneStepBackward),
                List.of(Position::oneStepBackward, Position::oneStepBackward, Position::oneStepLeftAndBackward),
                List.of(Position::oneStepBackward, Position::oneStepBackward, Position::oneStepRightAndBackward)
        );

        for (List<BiFunction<Position, Integer, Position>> steps : stepsAll) {
            Position toPos = fromPos;
            for (BiFunction<Position, Integer, Position> step : steps) {
                toPos = step.apply(toPos, board.getBoardDiameter());
            }
            possibleMoves.add(new Move(fromPos, toPos));
        }

        Set<Move> moves = new HashSet<>();
        moves.addAll(possibleMoves.stream().filter(move -> board.isInBounds(move.toPos)
                && (board.getPos(move.toPos) == null
                || board.getPos(move.toPos).color != this.color)).toList());
        return moves;
    }

    @Override
    public char getCharBase() {
        return 'n';
    }

    @Override
    public PieceType getPieceType() {
        return PieceType.KNIGHT;
    }
}
