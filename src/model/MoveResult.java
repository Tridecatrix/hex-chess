package model;

import model.piece.Pawn;

public class MoveResult {

    public boolean validMove;
    public Position promoteablePawn; // null means no pawn to promote

    public MoveResult(boolean validMove, Position promoteablePawn) {
        this.validMove = validMove;
        this.promoteablePawn = promoteablePawn;
    }

    // simplified constructor for "move isn't really special"
    public MoveResult(boolean validMove) {
        this.validMove = validMove;
        this.promoteablePawn = null;
    }
}
