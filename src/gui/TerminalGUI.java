package gui;

import model.*;
import model.piece.Piece;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.List;
import java.util.Set;

public class TerminalGUI {
    public static void main(String[] args) {
        Board board = new Board();
        Piece.Color currentPlayer = Piece.Color.WHITE;

        while (true) {
            Scanner scanner = new Scanner(System.in);

            System.out.println("Current board:");
            System.out.println(board);
            System.out.println("Current player: " + (currentPlayer.equals(Piece.Color.WHITE) ? "White" : "Black"));

            Position fromPos;
            Position toPos;
            while (true) {
                System.out.print("Input a piece coordinate to move: ");
                String pieceCoord = scanner.nextLine();
                fromPos = new Position(pieceCoord);

                if (board.getPos(fromPos).color != currentPlayer) {
                    System.out.println("Cannot move opponent's pieces");
                }

                if (board.getLegalMovesFromPos(fromPos).isEmpty()) {
                    System.out.println("No legal moves; try again");
                    continue;
                }

                System.out.print("Possible moves: ");
                List<Move> moves = new ArrayList<>(board.getLegalMovesFromPos(fromPos));
                for (int i = 0; i < moves.size(); i++) {
                    if (i != 0) System.out.print(", ");
                    System.out.print(moves.get(i).toPos);
                }

                System.out.println();
                System.out.print("Choose a move: ");

                String move = scanner.nextLine();
                toPos = new Position(move);
                break;
            }

            board.applyMove(new Move(fromPos, toPos), currentPlayer);

            currentPlayer = currentPlayer == Piece.Color.WHITE ? Piece.Color.BLACK : Piece.Color.WHITE;
        }
    }
}
