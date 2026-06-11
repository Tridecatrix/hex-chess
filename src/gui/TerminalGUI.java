package gui;

import model.*;
import model.piece.Piece;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.List;

public class TerminalGUI {
    public static void main(String[] args) {
        GameState game = new GameState();

        // Game loop
        while (true) {
            Scanner scanner = new Scanner(System.in);

            System.out.println(game);

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

                if (game.getBoard().getPos(fromPos) == null) {
                    System.out.println("No piece at location");
                    continue;
                }

                if (game.getBoard().getPos(fromPos).color != game.getCurrentPlayer()) {
                    System.out.println("Cannot move opponent's pieces");
                    continue;
                }

                System.out.print("Valid moves: ");
                List<String> moves = new ArrayList<>(game.getLegalMovesFromPos(fromPos)).stream().map(m -> m.toPos.toString()).sorted().toList();
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

                moveResult = game.applyMove(new Move(fromPos, toPos));

                if (!moveResult.validMove) {
                    System.out.println("Move was not valid; try again");
                    continue;
                }

                if (moveResult.promoteablePawn != null) {
                    while (true) {
                        System.out.println("Choose type to promote pawn at " + moveResult.promoteablePawn + " to (n/b/r/q): ");
                        String promotedType = scanner.nextLine();
                        PieceType promotionChoice;
                        switch (promotedType.strip().toLowerCase()) {
                            case "n", "knight" -> promotionChoice = PieceType.KNIGHT;
                            case "b", "bishop" -> promotionChoice = PieceType.BISHOP;
                            case "r", "rook" -> promotionChoice = PieceType.ROOK;
                            case "q", "queen" -> promotionChoice = PieceType.QUEEN;
                            default -> {
                                System.out.println("Illegal promotion choice; try again");
                                continue;
                            }
                        }

                        game.handlePromotion(moveResult.promoteablePawn, promotionChoice);
                    }
                }

                System.out.println();
                break;
            }
        }
    }
}
