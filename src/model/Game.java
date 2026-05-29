package model;

public class Game {
    int moveNumber;
    Piece.Color currentPlayer;
    Board board;

    public int getMoveNumber() {
        return moveNumber;
    }

    public String getCurrentPlayer() {
        return currentPlayer == Piece.Color.WHITE ? "White" : "Black";
    }

    public Game() {
        this.moveNumber = 1;
        this.currentPlayer = Piece.Color.WHITE;
        this.board = new Board();
    }
}
