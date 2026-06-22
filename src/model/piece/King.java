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

public class King extends Piece {
    public King(Color color) {
        super(color);
    }

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

        List<Position> possibleDestinations = new ArrayList<>(12);
        for (BiFunction<Position, Integer, Position> stepInDir : movementDirs) {
            possibleDestinations.add(stepInDir.apply(fromPos, board.boardDiameter));
        }

        List<Position> destinationsWithChecks = new ArrayList<>(12);
        destinationsWithChecks.addAll(possibleDestinations.stream().filter(pos -> board.isInBounds(pos)
                && (board.getPos(pos) == null
                || board.getPos(pos).color != this.color)).toList());

        // EDIT: don't bother filtering out positions with checks here, this is already done by Board's getLegalMoves method
        // for all pieces
        Set<Move> moves = new HashSet<>();
        for (Position toPos : destinationsWithChecks) {
            moves.add(new Move(fromPos, toPos));
        }

//        List<Boolean> isSpaceUnderCheck = board.areSpacesUnderThreat(this.color, destinationsWithChecks);
//        for (int i = 0; i < destinationsWithChecks.size(); i++) {
//            if (!isSpaceUnderCheck.get(i)) {
//                moves.add(new Move(fromPos, destinationsWithChecks.get(i)));
//            }
//        }

        return moves;
    }

    @Override
    public Set<Move> getPotentialCapturingMovesFromPos(Board board, Position fromPos) {
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

        List<Position> possibleDestinations = new ArrayList<>(12);
        for (BiFunction<Position, Integer, Position> stepInDir : movementDirs) {
            possibleDestinations.add(stepInDir.apply(fromPos, board.boardDiameter));
        }

        List<Position> destinationsWithChecks = new ArrayList<>(12);
        destinationsWithChecks.addAll(possibleDestinations.stream().filter(pos -> board.isInBounds(pos)
                && (board.getPos(pos) == null
                || board.getPos(pos).color != this.color)).toList());

        // skip removing locations that are in check

        return new HashSet<>(destinationsWithChecks.stream().map(p -> new Move(fromPos, p)).toList());
    }

    @Override
    public char getCharBase() {
        return 'k';
    }

    @Override
    public PieceType getPieceType() {
        return PieceType.KING;
    }
}
