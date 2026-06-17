package model.piece;

import model.Board;
import model.Move;
import model.PieceType;
import model.Position;

import java.util.HashSet;
import java.util.Set;
import java.util.Set;

public class Pawn extends Piece {
    public Pawn(Color color) {
        super(color);
    }

    @Override
    public char getChar() {
        return 'p';
    }

    @Override
    public Set<Move> getMovesFromPos(Board board, Position fromPos) {
        Set<Move> moves = new HashSet<>();

        Position forwardPos, doubleForwardPos, leftCapturePos, rightCapturePos;
        if (this.color == Color.WHITE) {
            forwardPos = Position.oneStepForward(fromPos, board.boardDiameter);
            doubleForwardPos = Position.oneStepForward(forwardPos, board.boardDiameter);
            leftCapturePos = Position.oneStepLeftAndForward(fromPos, board.boardDiameter);
            rightCapturePos = Position.oneStepRightAndForward(fromPos, board.boardDiameter);
        } else {
            forwardPos = Position.oneStepBackward(fromPos, board.boardDiameter);
            doubleForwardPos = Position.oneStepBackward(forwardPos, board.boardDiameter);
            leftCapturePos = Position.oneStepLeftAndBackward(fromPos, board.boardDiameter);
            rightCapturePos = Position.oneStepRightAndBackward(fromPos, board.boardDiameter);
        }

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
        if (this.color == Color.WHITE) {
            if (fromPos.rank == 4 - Position.distanceFromCenter(fromPos, board.boardDiameter)) {
                if (board.isInBounds(doubleForwardPos) && board.getPos(forwardPos) == null && board.getPos(doubleForwardPos) == null)
                    moves.add(new Move(fromPos, doubleForwardPos));
            }
        } else { // piece is black
            if (fromPos.rank == 6) {
                if (board.isInBounds(doubleForwardPos) && board.getPos(forwardPos) == null && board.getPos(doubleForwardPos) == null)
                    moves.add(new Move(fromPos, doubleForwardPos));
            }
        }

        // Pawn logic: en passant
        // If the enemy pawn has moved 2 spaces in the immediate last turn, and you are attacking the space that it
        // would have moved to if it moved 1 space, then you can still capture it
        if (board.passantablePawn != null) {
            Position leftPassantPosition, rightPassantPosition;
            if (this.color == Color.WHITE) {
                leftPassantPosition = Position.oneStepLeftAndBackward(fromPos, board.boardDiameter);
                rightPassantPosition = Position.oneStepRightAndBackward(fromPos, board.boardDiameter);
            } else {
                leftPassantPosition = Position.oneStepLeftAndForward(fromPos, board.boardDiameter);
                rightPassantPosition = Position.oneStepRightAndForward(fromPos, board.boardDiameter);
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

        Position leftCapturePos, rightCapturePos;
        if (this.color == Color.WHITE) {
            leftCapturePos = Position.oneStepLeftAndForward(fromPos, board.boardDiameter);
            rightCapturePos = Position.oneStepRightAndForward(fromPos, board.boardDiameter);
        } else {
            leftCapturePos = Position.oneStepLeftAndBackward(fromPos, board.boardDiameter);
            rightCapturePos = Position.oneStepRightAndBackward(fromPos, board.boardDiameter);
        }

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
