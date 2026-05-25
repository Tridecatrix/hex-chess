public class Game {
    Piece.Color currentPlayer;
    Board board;

    public Game() {
        this.board = new Board();
        this.currentPlayer = Piece.Color.WHITE;
    }
}
