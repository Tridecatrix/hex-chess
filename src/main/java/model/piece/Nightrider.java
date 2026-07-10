package model.piece;

import model.Board;
import model.Move;
import model.Position;
import model.PieceType;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;

public class Nightrider extends Piece {
    public Nightrider(Color color) {
        super(color);
    }

    @Override
    public Set<Move> getMovesFromPos(Board board, Position fromPos) {
        Set<Move> moves = new HashSet<>();

        // list of all possible L shaped step sequences, i.e. step twice in one of 6 directions, then step once in
        // one of two adjacent directions
        List<List<BiFunction<Position, Integer, Position>>> movementDirs = List.of(
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

        for (List<BiFunction<Position, Integer, Position>> stepsInDir : movementDirs) {
            Position nextPos = fromPos;
            for (BiFunction<Position, Integer, Position> step : stepsInDir) {
                nextPos = step.apply(nextPos, board.getBoardDiameter());
            }

            while (board.isInBounds(nextPos) && board.getPos(nextPos) == null) {
                moves.add(new Move(fromPos, nextPos));
                
                for (BiFunction<Position, Integer, Position> step : stepsInDir) {
                    nextPos = step.apply(nextPos, board.getBoardDiameter());
                }
            }

            if (board.isInBounds(nextPos) && board.getPos(nextPos) != null && board.getPos(nextPos).color != this.color) {
                moves.add(new Move(fromPos, nextPos)); // add the capturing move for opponent piece
            }
        }

        return moves;
    }

    @Override
    public char getCharBase() {
        return 'm';
    }

    @Override
    public PieceType getPieceType() {
        return PieceType.NIGHTRIDER;
    }
}
