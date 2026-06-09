package model;

import model.piece.*;

public class PieceFactory {
    public static Piece createPiece(String type, String color) {
        Piece piece;
        Piece.Color colorAsObj;

        switch(color.toLowerCase()) {
            case "w":
            case "white":
                colorAsObj = Piece.Color.WHITE;
                break;
            case "b":
            case "black":
                colorAsObj = Piece.Color.BLACK;
                break;
            default:
                throw new RuntimeException("Invalid piece color");
        }

        switch(type.toLowerCase()) {
            case "p":
            case "pawn":
                piece = new Pawn(colorAsObj);
                break;
            case "r":
            case "rook":
                piece = new Rook(colorAsObj);
                break;
            case "n":
            case "knight":
                piece = new Knight(colorAsObj);
                break;
            case "b":
            case "bishop":
                piece = new Bishop(colorAsObj);
                break;
            case "q":
            case "queen":
                piece = new Queen(colorAsObj);
                break;
            case "k":
            case "king":
                piece = new King(colorAsObj);
                break;
            default:
                throw new RuntimeException("Invalid piece type");
        }

        return piece;
    }
}
