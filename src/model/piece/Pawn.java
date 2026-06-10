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
            forwardPos = Position.oneStepForward(fromPos, board.boarddim);
            doubleForwardPos = Position.oneStepForward(forwardPos, board.boarddim);
            leftCapturePos = Position.oneStepLeftAndForward(fromPos, board.boarddim);
            rightCapturePos = Position.oneStepRightAndForward(fromPos, board.boarddim);
        } else {
            forwardPos = Position.oneStepBackward(fromPos, board.boarddim);
            doubleForwardPos = Position.oneStepBackward(forwardPos, board.boarddim);
            leftCapturePos = Position.oneStepLeftAndBackward(fromPos, board.boarddim);
            rightCapturePos = Position.oneStepRightAndBackward(fromPos, board.boarddim);
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
            if (fromPos.rank == 4 - Position.distanceFromCenter(fromPos, board.boarddim)) {
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
        // If the enemy pawn has moved 2 spaces in the last turn, and you could have captured it if it
        // had only moved one space, you still can
        // TODO: add
        
        return moves;
    }

    @Override
    public Set<Move> getPotentialCapturingMovesFromPos(Board board, Position fromPos) {
        Set<Move> moves = new HashSet<>();

        Position leftCapturePos, rightCapturePos;
        if (this.color == Color.WHITE) {
            leftCapturePos = Position.oneStepLeftAndForward(fromPos, board.boarddim);
            rightCapturePos = Position.oneStepRightAndForward(fromPos, board.boarddim);
        } else {
            leftCapturePos = Position.oneStepLeftAndBackward(fromPos, board.boarddim);
            rightCapturePos = Position.oneStepRightAndBackward(fromPos, board.boarddim);
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
