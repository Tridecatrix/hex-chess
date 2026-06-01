package gui;

import model.*;
import java.util.Scanner;

public class TerminalGUI {
    public static void main(String[] args) {
        Board board = new Board();
        Piece.Color currentPlayer = Piece.Color.WHITE;

        while (true) {
            Scanner scanner = new Scanner(System.in);

            System.out.println("Current board:");
            System.out.println(board);
            System.out.println("Current player: " + (currentPlayer.equals(Piece.Color.WHITE) ? "White" : "Black"));

            Board.Position fromPos;
            Board.Position toPos;
            while (true) {
                System.out.print("Input a piece coordinate to move: ");
                String pieceCoord = scanner.nextLine();
                fromPos = new Board.Position(pieceCoord);

                if (board.getPos(fromPos).color != currentPlayer) {
                    System.out.println("Cannot move opponent's pieces");
                }

                if (board.getLegalMovesFromPos(fromPos).isEmpty()) {
                    System.out.println("No legal moves; try again");
                    continue;
                }

                System.out.print("Possible moves: ");
                for (Board.Move move : board.getLegalMovesFromPos(fromPos)) {
                    System.out.print(move.toPos);
                    System.out.print(", ");
                }

                System.out.println();
                System.out.print("Choose a move:");

                String move = scanner.nextLine();
                toPos = new Board.Position(move);
                break;
            }

            board.applyMove(new Board.Move(fromPos, toPos), currentPlayer);

            currentPlayer = currentPlayer == Piece.Color.WHITE ? Piece.Color.BLACK : Piece.Color.WHITE;
        }
    }
}
