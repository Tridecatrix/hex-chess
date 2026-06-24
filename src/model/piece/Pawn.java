package model.piece;

import model.Board;
import model.Move;
import model.PieceType;
import model.Position;

import java.util.*;
import java.util.Set;
import java.util.function.BiFunction;

public class Pawn extends Piece {
    public Pawn(Color color) {
        super(color);
    }

    static List<BiFunction<Position, Integer, Position>> movementDirections = List.of(
            Position::oneStepForward,
            Position::oneStepRightAndForward,
            Position::oneStepRightAndBackward,
            Position::oneStepBackward,
            Position::oneStepLeftAndBackward,
            Position::oneStepLeftAndForward
    );

    public enum Direction {
        FORWARD,
        CAPTURE_LEFT,
        CAPTURE_RIGHT,
        BACK_LEFT,
        BACK_RIGHT,
        BACKWARD
    }

    // helper for getLegalMoves; returns a map of functions corresponding to each direction relevant to the pawn
    public static Map<Direction, BiFunction<Position, Integer, Position>> getDirections(Color color) {
        int forwardDirIndex = switch(color) {
            case WHITE -> 0;
            case GREEN -> 1;
            case RED -> 2;
            case BLACK, YELLOW -> 3;
            case BLUE -> 4;
            case PURPLE -> 5;
        };

        Map<Direction, BiFunction<Position, Integer, Position>> result = new HashMap<>();

        for (int offset = -2; offset <= 3; offset++) {
            int index = (forwardDirIndex + offset + 6) % 6;

            Direction dir = switch(offset) {
                case -2 -> Direction.BACK_LEFT;
                case -1 -> Direction.CAPTURE_LEFT;
                case 0 -> Direction.FORWARD;
                case 1 -> Direction.CAPTURE_RIGHT;
                case 2 -> Direction.BACK_RIGHT;
                case 3 -> Direction.BACKWARD;
                default -> throw new IllegalStateException("Unexpected value: " + offset);
            };

            result.put(dir, movementDirections.get(index));
        }

        return result;
    }

    public static boolean isStartingPosition(Position pos, Piece.Color color, Board board) {
        return distanceFromFinalRank(pos, color, board) == board.getBoardDiameter() - 5;
    }

    public static boolean isInPromotionPosition(Position pos, Piece.Color color, Board board) {
        // Note: two options here
        // - distanceFromFinalRank == 0                               (must be final rank)
        // - distanceFromFinalRank == distanceFromStartingPos - 6     (same distance as 2 player)
        //                         == boardDiameter - 5 - 6
        return distanceFromFinalRank(pos, color, board) == 0;
    }

    public static boolean isStartingPawnMove(Move move,  Piece.Color playerColor, Board board) {
        Position forwardPos = Pawn.getDirections(playerColor).get(Direction.FORWARD).apply(move.fromPos, board.getBoardDiameter());
        Position doubleForwardPos = Pawn.getDirections(playerColor).get(Direction.FORWARD).apply(forwardPos, board.getBoardDiameter());
        return move.toPos.equals(doubleForwardPos);
    }

    public static Position getPassantedPawnIfExists(Move move, Piece.Color playerColor, Board board) {
        Position leftCapturePos = Pawn.getDirections(playerColor).get(Direction.CAPTURE_LEFT).apply(move.fromPos, board.getBoardDiameter());
        Position rightCapturePos = Pawn.getDirections(playerColor).get(Direction.CAPTURE_RIGHT).apply(move.fromPos, board.getBoardDiameter());
        for (Piece.Color passantableColor : board.getPassantablePawns().keySet()) {
            if (passantableColor == playerColor) continue;

            Position passantablePawn = board.getPassantablePawns().get(passantableColor);

            // the passanted pawn had to have made 2 steps in the last turn; get the position
            // it would be at if it only made one step (and hence could be captured)
            Position singleStepForPassantablePawn = Pawn.getDirections(passantableColor).get(Direction.BACKWARD)
                    .apply(passantablePawn, board.getBoardDiameter());
            
            if (leftCapturePos.equals(singleStepForPassantablePawn)) {
                return passantablePawn;
            } else if (rightCapturePos.equals(singleStepForPassantablePawn)) {
                return passantablePawn;
            }
        }

        return null;
    }

    // distance from the final rank for this pawn
    public static int distanceFromFinalRank(Position fromPos, Piece.Color color, Board board) {
        BiFunction<Position, Integer, Position> forwardDir = getDirections(color).get(Direction.FORWARD);

        int result = 0;
        fromPos = forwardDir.apply(fromPos, board.getBoardDiameter());
        while (board.isInBounds(fromPos)) {
            fromPos = forwardDir.apply(fromPos, board.getBoardDiameter());
            result++;
        }

        return result;
    }

    @Override
    public char getCharBase() {
        return 'p';
    }

    @Override
    public Set<Move> getMovesFromPos(Board board, Position fromPos) {
        Set<Move> moves = new HashSet<>();

        Piece pawn = board.getPos(fromPos);

        Map<Direction, BiFunction<Position, Integer, Position>> directions = getDirections(pawn.color);

        Position forwardPos, doubleForwardPos, leftCapturePos, rightCapturePos;
        forwardPos = directions.get(Direction.FORWARD).apply(fromPos, board.getBoardDiameter());
        doubleForwardPos = directions.get(Direction.FORWARD).apply(forwardPos, board.getBoardDiameter());
        leftCapturePos = directions.get(Direction.CAPTURE_LEFT).apply(fromPos, board.getBoardDiameter());
        rightCapturePos = directions.get(Direction.CAPTURE_RIGHT).apply(fromPos, board.getBoardDiameter());

        // Pawn basic logic: able to move one space ahead into an empty square
        if (board.isInBounds(forwardPos) && board.getPos(forwardPos) == null)
            moves.add(new Move(fromPos, forwardPos));

        // Pawn basic logic: able to capture to the side and forward
        if (board.isInBounds(leftCapturePos)
                && board.getPos(leftCapturePos) != null
                && board.getPos(leftCapturePos).color != this.color)
            moves.add(new Move(fromPos, leftCapturePos));

        if (board.isInBounds(rightCapturePos)
                && board.getPos(rightCapturePos) != null
                && board.getPos(rightCapturePos).color != this.color)
            moves.add(new Move(fromPos, rightCapturePos));

        // Pawn logic: if the piece is on a starting position, it can jump two steps forward
        // Note this is true also if the pawn moves into the starting position of another pawn
        if (isStartingPosition(fromPos, pawn.color, board)) {
            if (board.isInBounds(doubleForwardPos) && board.getPos(forwardPos) == null && board.getPos(doubleForwardPos) == null)
                moves.add(new Move(fromPos, doubleForwardPos));
        }

        // Pawn logic: en passant
        // If the enemy pawn has moved 2 spaces in the immediate last turn, and you are attacking the space that it
        // would have moved to if it moved 1 space, then you can still capture it
        for (Color passantableColor : board.getPassantablePawns().keySet()) {
            if (passantableColor == pawn.color) continue;

            Position passantablePawn = board.getPassantablePawns().get(passantableColor);

            Position singleStepForPassantablePawn = Pawn.getDirections(passantableColor).get(Direction.BACKWARD)
                                                        .apply(passantablePawn, board.getBoardDiameter());

            if (leftCapturePos.equals(singleStepForPassantablePawn)) {
                moves.add(new Move(fromPos, leftCapturePos));
            } else if (rightCapturePos.equals(singleStepForPassantablePawn)) {
                moves.add(new Move(fromPos, rightCapturePos));
            }
        }
        
        return moves;
    }

    @Override
    public Set<Move> getPotentialCapturingMovesFromPos(Board board, Position fromPos) {
        Set<Move> moves = new HashSet<>();

        Piece pawn = board.getPos(fromPos);
        Map<Direction, BiFunction<Position, Integer, Position>> directions = getDirections(pawn.color);

        Position leftCapturePos, rightCapturePos;
        leftCapturePos = directions.get(Direction.CAPTURE_LEFT).apply(fromPos, board.getBoardDiameter());
        rightCapturePos = directions.get(Direction.CAPTURE_RIGHT).apply(fromPos, board.getBoardDiameter());

        if (board.isInBounds(leftCapturePos))
            moves.add(new Move(fromPos, leftCapturePos));

        if (board.isInBounds(rightCapturePos))
            moves.add(new Move(fromPos, rightCapturePos));

        return moves;
    }

    @Override
    public PieceType getPieceType() {
        return PieceType.PAWN;
    }
}
