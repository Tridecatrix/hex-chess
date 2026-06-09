package model.piece;

import model.Board;
import model.Move;
import model.Position;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static model.Position.*;

public class Knight extends Piece {
    public Knight(Color color) {
        super(color);
    }

    @Override
    public Set<Move> getMovesFromPos(Board board, Position fromPos) {
        List<Move> possibleMoves = new ArrayList<>(12);

        possibleMoves.add(new Move(fromPos, oneStepLeftAndForward(
                        oneStepForward(
                        oneStepForward(fromPos, board.boarddim), board.boarddim), board.boarddim)));
        possibleMoves.add(new Move(fromPos, oneStepRightAndForward(
                        oneStepForward(
                        oneStepForward(fromPos, board.boarddim), board.boarddim), board.boarddim)));
        possibleMoves.add(new Move(fromPos, oneStepLeftAndBackward(
                        oneStepLeftAndForward(
                        oneStepLeftAndForward(fromPos, board.boarddim), board.boarddim), board.boarddim)));
        possibleMoves.add(new Move(fromPos, oneStepForward(
                        oneStepLeftAndForward(
                        oneStepLeftAndForward(fromPos, board.boarddim), board.boarddim), board.boarddim)));
        possibleMoves.add(new Move(fromPos, oneStepRightAndBackward(
                        oneStepRightAndForward(
                        oneStepRightAndForward(fromPos, board.boarddim), board.boarddim), board.boarddim)));
        possibleMoves.add(new Move(fromPos, oneStepForward(
                        oneStepRightAndForward(
                        oneStepRightAndForward(fromPos, board.boarddim), board.boarddim), board.boarddim)));
        possibleMoves.add(new Move(fromPos, oneStepLeftAndForward(
                        oneStepLeftAndBackward(
                        oneStepLeftAndBackward(fromPos, board.boarddim), board.boarddim), board.boarddim)));
        possibleMoves.add(new Move(fromPos, oneStepBackward(
                        oneStepLeftAndBackward(
                        oneStepLeftAndBackward(fromPos, board.boarddim), board.boarddim), board.boarddim)));
        possibleMoves.add(new Move(fromPos, oneStepRightAndForward(
                        oneStepRightAndBackward(
                        oneStepRightAndBackward(fromPos, board.boarddim), board.boarddim), board.boarddim)));
        possibleMoves.add(new Move(fromPos, oneStepBackward(
                        oneStepRightAndBackward(
                        oneStepRightAndBackward(fromPos, board.boarddim), board.boarddim), board.boarddim)));
        possibleMoves.add(new Move(fromPos, oneStepLeftAndBackward(
                        oneStepBackward(
                        oneStepBackward(fromPos, board.boarddim), board.boarddim), board.boarddim)));
        possibleMoves.add(new Move(fromPos, oneStepRightAndBackward(
                        oneStepBackward(
                        oneStepBackward(fromPos, board.boarddim), board.boarddim), board.boarddim)));

        Set<Move> moves = new HashSet<>();
        moves.addAll(possibleMoves.stream().filter(move -> board.isInBounds(move.toPos)
                && (board.getPos(move.toPos) == null
                || board.getPos(move.toPos).color != this.color)).toList());
        return moves;
    }

    @Override
    public char getChar() {
        return 'n';
    }
}
