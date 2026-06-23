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

    enum Direction {
        FORWARD,
        CAPTURE_LEFT,
        CAPTURE_RIGHT,
        PASSANT_LEFT,
        PASSANT_RIGHT
    }

    // helper for getLegalMoves; returns a map of functions corresponding to each direction relevant to the pawn
    Map<Direction, BiFunction<Position, Integer, Position>> getDirections(Color color) {
        int forwardDirIndex = switch(color) {
            case WHITE -> 0;
            case YELLOW -> 1;
            case RED -> 2;
            case BLACK, GREEN -> 3;
            case BLUE -> 4;
            case PURPLE -> 5;
        };

        Map<Direction, BiFunction<Position, Integer, Position>> result = new HashMap<>();

        for (int offset = -2; offset <= 2; offset++) {
            int index = (forwardDirIndex + offset + 6) % 6;

            Direction dir = switch(offset) {
                case -2 -> Direction.PASSANT_LEFT;
                case -1 -> Direction.CAPTURE_LEFT;
                case 0 -> Direction.FORWARD;
                case 1 -> Direction.CAPTURE_RIGHT;
                case 2 -> Direction.PASSANT_RIGHT;
                default -> throw new IllegalStateException("Unexpected value: " + offset);
            };

            result.put(dir, movementDirections.get(index));
        }

        return result;
    }

    public boolean isStartingPosition(Position pos, Piece.Color color, int boarddim) {
        if (color == Color.WHITE && boarddim == 6) {
            return pos.rank == (4 - Position.distanceFromCenter(pos, 2*boarddim-1));
        } else if (color == Color.BLACK && boarddim == 6) {
            return pos.rank == 6;
        }
        // TODO
        return false;
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
        if (isStartingPosition(fromPos, pawn.color, board.getBoardDim())) {
            if (board.isInBounds(doubleForwardPos) && board.getPos(forwardPos) == null && board.getPos(doubleForwardPos) == null)
                moves.add(new Move(fromPos, doubleForwardPos));
        }

        // Pawn logic: en passant
        // If the enemy pawn has moved 2 spaces in the immediate last turn, and you are attacking the space that it
        // would have moved to if it moved 1 space, then you can still capture it
        // TODO: fix for 3 and 6 player
        if (board.passantablePawn != null) {
            Position leftPassantPosition, rightPassantPosition;
            if (this.color == Color.WHITE) {
                leftPassantPosition = directions.get(Direction.PASSANT_LEFT).apply(fromPos, board.getBoardDiameter());
                rightPassantPosition = directions.get(Direction.PASSANT_RIGHT).apply(fromPos, board.getBoardDiameter());
            } else {
                leftPassantPosition = Position.oneStepLeftAndForward(fromPos, board.getBoardDiameter());
                rightPassantPosition = Position.oneStepRightAndForward(fromPos, board.getBoardDiameter());
            }

            if (board.passantablePawn.equals(leftPassantPosition)) {
                moves.add(new Move(fromPos, leftCapturePos));
            } else if (board.passantablePawn.equals(rightPassantPosition)) {
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
