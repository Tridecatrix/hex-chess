package model;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import model.piece.Pawn;
import model.piece.Piece;

import java.util.*;

public class Game {
    // points for each player, tracked across several matches
    double whitePoints = 0;
    double blackPoints = 0;
    int movesSinceCaptureOrPawnMovement = 0;
    int moveNumberForCurrentSide = 1;

    Board board;

    // for checking repetition
    Queue<Board> previousBoards = new ArrayDeque<>();
    static final int repetitionCheckMaxWindow = 20;

    Piece.Color currentPlayer = Piece.Color.WHITE;
    GameResult currentGameState;

    public int getMoveNumberForCurrentSide() {
        return moveNumberForCurrentSide;
    }

    public enum GameResult {
        STALEMATE,
        CHECKMATE,
        DRAW,
        RESIGNED,
        CONTINUING
    }

    public Game() {
        board = new Board();
        currentGameState = GameResult.CONTINUING;
    }

    // testing constructor
    public Game(List<String> pieces) {
        board = new Board(pieces);
        currentGameState = GameResult.CONTINUING;
    }

    @Override
    public String toString() {
        StringBuilder string = new StringBuilder();

        string.append("Current board:\n");
        string.append(board);
        string.append("Current player: " + (currentPlayer.equals(Piece.Color.WHITE) ? "White" : "Black\n"));
        //string.append("Points are white: " + whitePoints + " and Black: " + blackPoints);
        if (this.board.isKingInCheck(currentPlayer)) {
            string.append("You are in check!\n");
        }

        return string.toString();
    }

    public Set<Move> getLegalMovesFromPos(Position pos) {
        return board.getLegalMovesFromPos(pos);
    }

    public enum MoveStatus {
        ILLEGAL_OPPONENT_PIECE,
        ILLEGAL_NO_PIECE,
        ILLEGAL_INVALID_PIECE_MOVEMENT,
        LEGAL;
    }

    public MoveStatus isLegalMove(Move move) {
        if (board.getPos(move.fromPos) == null) {
            return MoveStatus.ILLEGAL_NO_PIECE;
        } else if (board.getPos(move.fromPos).color != currentPlayer) {
            return MoveStatus.ILLEGAL_OPPONENT_PIECE;
        } else if (!board.isLegalMove(move, currentPlayer)) {
            return MoveStatus.ILLEGAL_INVALID_PIECE_MOVEMENT;
        } else {
            return MoveStatus.LEGAL;
        }
    }

    public MoveResult applyMove(Move move) {
        boolean pawnMovementOrCapture = board.getPos(move.fromPos) instanceof Pawn || board.getPos(move.toPos) != null;

        // before applying the move, save the board state
        Board boardStateCopy = new Board(this.board);

        MoveResult result = board.applyMoveWithLegalityCheck(move, currentPlayer);

        if (result.validMove) {
            // update time since last pawn movement/capture
            if (pawnMovementOrCapture) movesSinceCaptureOrPawnMovement = 0;
            else movesSinceCaptureOrPawnMovement++;

            // update move number
            moveNumberForCurrentSide += currentPlayer == Piece.Color.WHITE ? 0 : 1;

            // update current player
            currentPlayer = currentPlayer == Piece.Color.WHITE ? Piece.Color.BLACK : Piece.Color.WHITE;

            // update list of previous boards
            previousBoards.add(boardStateCopy);
            if (previousBoards.size() > repetitionCheckMaxWindow) {
                previousBoards.remove(); // throw away any boards which are older than 40 moves to limit memory usage
            }
        }

        return result;
    }

    public void handlePromotion(Position promotetablePawn, PieceType promotionChoice) {
        this.board.handlePromotion(promotetablePawn, promotionChoice);
    }

    public GameResult checkIfGameEnd() {
        double winPoints;
        GameResult result;

        if (board.isInCheckmate(currentPlayer)) {
            winPoints = 1;
            result = GameResult.CHECKMATE;
        } else if (board.isInStalemate(currentPlayer)) {
            winPoints = 0.75;
            result = GameResult.STALEMATE;
        } else if (this.checkIfForcedDraw()) {
            winPoints = 0.5;
            result = GameResult.DRAW;
        } else {
            return GameResult.CONTINUING;
        }

        if (currentPlayer == Piece.Color.WHITE) {
            whitePoints += winPoints;
            blackPoints += 1 - winPoints;
        } else if (currentPlayer == Piece.Color.BLACK) {
            blackPoints += winPoints;
            whitePoints += 1 - winPoints;
        }

        currentGameState = result;
        return result;
    }

    public void resetPoints() {
        whitePoints = 0;
        blackPoints = 0;
    }

    public void restartGame() {
        currentPlayer = Piece.Color.WHITE;
        board = new Board();
        movesSinceCaptureOrPawnMovement = 0;
        while (!previousBoards.isEmpty()) {
            previousBoards.remove();
        }
        moveNumberForCurrentSide = 1;
        currentGameState = GameResult.CONTINUING;
    }

    public void resign() {
        if (currentPlayer == Piece.Color.WHITE) {
            blackPoints += 1;
        } else {
            whitePoints += 1;
        }

        currentGameState = GameResult.RESIGNED;

        // note: restartGame has to be called seperately
    }

    // forced draws include:
    // - draw by repetition (repeat 5 times)
    // - draw by 75 moves without captures or pawn movements
    // - draw by insufficient material
    public boolean checkIfForcedDraw() {
        if (movesSinceCaptureOrPawnMovement >= 2*75) {
            return true;
        }

        // check for fivefold repetition
        int repTimes = 0;
        for (Board previousBoard : previousBoards) {
            if (this.board.equals(previousBoard)) repTimes++;
        }

        if (repTimes >= 4) return true;

        // insufficient material: build sets of all types of pieces for white and black
        Multiset<PieceType> whitePieceTypes = HashMultiset.create();
        Multiset<PieceType> blackPieceTypes = HashMultiset.create();
        for (Position pos : this.board.whitePiecePositions) {
            whitePieceTypes.add(this.board.getPos(pos).getPieceType());
        }
        for (Position pos : this.board.blackPiecePositions) {
            blackPieceTypes.add(this.board.getPos(pos).getPieceType());
        }

        // insufficient material: king vs king
        if (whitePieceTypes.equals(HashMultiset.create(List.of(PieceType.KING)))
                && blackPieceTypes.equals(HashMultiset.create(List.of(PieceType.KING)))) {
            return true;
        }

        // insufficient material: king & knight vs king
        if (whitePieceTypes.equals(HashMultiset.create(List.of(PieceType.KING, PieceType.KNIGHT)))
                && blackPieceTypes.equals(HashMultiset.create(List.of(PieceType.KING)))
           || whitePieceTypes.equals(HashMultiset.create(List.of(PieceType.KING)))
                && blackPieceTypes.equals(HashMultiset.create(List.of(PieceType.KING, PieceType.KNIGHT)))) {
            return true;
        }

        // insufficient material: king and bishop v king
        if (whitePieceTypes.equals(HashMultiset.create(List.of(PieceType.KING, PieceType.BISHOP)))
                && blackPieceTypes.equals(HashMultiset.create(List.of(PieceType.KING)))
                || whitePieceTypes.equals(HashMultiset.create(List.of(PieceType.KING)))
                && blackPieceTypes.equals(HashMultiset.create(List.of(PieceType.KING, PieceType.BISHOP)))) {
            return true;
        }

        return false;
    }

    // method to be called when a player wants to claim draw (as opposed to forced draw)
    // returns true if claim is successful
    // claimable draws include:
    // - draw by repetition (repeat 3 times)
    // - draw by 50 moves without captures or pawn movements
    public boolean claimDraw() {
        if (movesSinceCaptureOrPawnMovement >= 2*50) {
            this.currentGameState = GameResult.DRAW;
            whitePoints += 0.5;
            blackPoints += 0.5;
            return true;
        }

        // check for threefold repetition of the current position
        int repTimes = 0;
        for (Board previousBoard : previousBoards) {
            if (this.board.equals(previousBoard)) repTimes++;
        }

        if (repTimes >= 2) {
            this.currentGameState = GameResult.DRAW;
            whitePoints += 0.5;
            blackPoints += 0.5;
            return true;
        }

        return false;
    }

    public double getWhitePoints() {
        return whitePoints;
    }

    public double getBlackPoints() {
        return blackPoints;
    }

    public Board getBoard() {
        return board;
    }

    public Piece.Color getCurrentPlayer() {
        return currentPlayer;
    }

    public GameResult getCurrentGameState() {
        return currentGameState;
    }
}
