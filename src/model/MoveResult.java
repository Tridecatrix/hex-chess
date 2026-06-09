package model;

import model.piece.Pawn;

public class MoveResult {
    public enum CheckStatus {
        NONE,
        CHECK,
        CHECKMATE,
        STALEMATE
    }

    public boolean validMove;
    public CheckStatus checkStatus;
    public Position promoteablePawn; // null means no pawn to promote

    public MoveResult(boolean validMove, CheckStatus checkStatus, Position promoteablePawn) {
        this.validMove = validMove;
        this.checkStatus = checkStatus;
        this.promoteablePawn = promoteablePawn;
    }

    // simplified constructor for "move isn't really special"
    public MoveResult(boolean validMove) {
        this.validMove = validMove;
        this.checkStatus = CheckStatus.NONE;
        this.promoteablePawn = null;
    }
}
