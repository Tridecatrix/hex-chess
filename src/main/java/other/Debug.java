package other;

import model.Board;
import model.Game;

public class Debug {
    public static void main(String[] args) {
        Board board = new Board(Game.Mode.SIX_PLAYER);

        System.out.println(board);
    }
}
