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
            MoveResult moveResult;

            while (true) {
                System.out.print("Input a piece coordinate to move: ");
                String pieceCoord = scanner.nextLine();
                try {
                    fromPos = new Position(pieceCoord);
                } catch (Exception e) {
                    System.out.println("Illegal position coordinate; try again");
                    continue;
                }

                if (board.getPos(fromPos) == null) {
                    System.out.println("No piece at location");
                    continue;
                }

                if (board.getPos(fromPos).color != currentPlayer) {
                    System.out.println("Cannot move opponent's pieces");
                    continue;
                }

                System.out.print("Valid moves: ");
                List<String> moves = new ArrayList<>(board.getLegalMovesFromPos(fromPos)).stream().map(m -> m.toPos.toString()).sorted().toList();
                for (int i = 0; i < moves.size(); i++) {
                    if (i != 0) System.out.print(", ");
                    System.out.print(moves.get(i));
                }

                if (moves.isEmpty()) {
                    System.out.println("No possible moves; choose another piece");
                    continue;
                }

                System.out.println();
                System.out.print("Choose a position to move to: ");

                try {
                    String move = scanner.nextLine();
                    toPos = new Position(move);
                } catch (Exception e) {
                    System.out.println("Illegal position coordinate; try again");
                    continue;
                }

                moveResult = board.applyMove(new Move(fromPos, toPos), currentPlayer);

                if (!moveResult.validMove) {
                    System.out.println("Move was not valid; try again");
                    continue;
                }

                if (moveResult.promoteablePawn != null) {
                    while (true) {
                        System.out.println("Choose type to promote pawn at " + moveResult.promoteablePawn + " to (n/b/r/q): ");
                        String promotedType = scanner.nextLine();
                        PromotionChoices promotionChoice;
                        switch (promotedType.strip().toLowerCase()) {
                            case "n", "knight" -> promotionChoice = PromotionChoices.KNIGHT;
                            case "b", "bishop" -> promotionChoice = PromotionChoices.BISHOP;
                            case "r", "rook" -> promotionChoice = PromotionChoices.ROOK;
                            case "q", "queen" -> promotionChoice = PromotionChoices.QUEEN;
                            default -> {
                                System.out.println("Illegal promotion choice; try again");
                                continue;
                            }
                        }

                        board.handlePromotion(moveResult.promoteablePawn, promotionChoice);
                    }
                }

                break;
            }

            currentPlayer = currentPlayer == Piece.Color.WHITE ? Piece.Color.BLACK : Piece.Color.WHITE;
        }
    }
}
