package model;

import model.piece.Piece;

import java.util.Set;

public class Game {
    double whitePoints = 0;
    double blackPoints = 0;

    Board board;

    Piece.Color currentPlayer = Piece.Color.WHITE;
    GameResult currentGameState;

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

    @Override
    public String toString() {
        StringBuilder string = new StringBuilder();

        string.append("Current board:\n");
        string.append(board);
        string.append("Current player: " + (currentPlayer.equals(Piece.Color.WHITE) ? "White" : "Black"));
        //string.append("Points are white: " + whitePoints + " and Black: " + blackPoints);

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
        MoveResult result = board.applyMoveWithLegalityCheck(move, currentPlayer);
        if (result.promoteablePawn == null && result.validMove) // if pawn needs to be promoted, player change is deferred
            currentPlayer = currentPlayer == Piece.Color.WHITE ? Piece.Color.BLACK : Piece.Color.WHITE;
        return result;
    }

    public void handlePromotion(Position promotetablePawn, PieceType promotionChoice) {
        this.board.handlePromotion(promotetablePawn, promotionChoice);
        currentPlayer = currentPlayer == Piece.Color.WHITE ? Piece.Color.BLACK : Piece.Color.WHITE;
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
        } else if (this.checkIfDraw()) {
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

    // TODO
    public boolean checkIfDraw() {
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
