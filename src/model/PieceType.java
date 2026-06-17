package model;

import model.piece.Piece;

public enum PieceType {
    PAWN,
    KNIGHT,
    NIGHTRIDER,
    BISHOP,
    ROOK,
    QUEEN,
    KING;

    public char getChar() {
        switch(this) {
            case KNIGHT -> {
                return 'n';
            }
            case BISHOP -> {
                return 'b';
            }
            case ROOK -> {
                return 'r';
            }
            case QUEEN -> {
                return 'q';
            }
            case PAWN -> {
                return 'p';
            }
            case KING -> {
                return 'k';
            }
            case NIGHTRIDER -> {
                return 'm';
            }
            default -> {
                return ' ';
            }
        }
    }

    public char getPieceIcon(Piece.Color color) {
        if (color == Piece.Color.WHITE || color == Piece.Color.GREY) {
            switch (this) {
                case PAWN -> {
                    return '♙';
                }
                case KNIGHT -> {
                    return '♘';
                }
                case BISHOP -> {
                    return '♗';
                }
                case ROOK -> {
                    return '♖';
                }
                case QUEEN -> {
                    return '♕';
                }
                case KING -> {
                    return '♔';
                }
                case NIGHTRIDER -> {
                    return 'm';
                }
            }
        } else {
            switch (this) {
                case PAWN -> {
                    return '♟';
                }
                case KNIGHT -> {
                    return '♞';
                }
                case BISHOP -> {
                    return '♝';
                }
                case ROOK -> {
                    return '♜';
                }
                case QUEEN -> {
                    return '♛';
                }
                case KING -> {
                    return '♚';
                }
                case NIGHTRIDER -> {
                    return 'm';
                }
            }
        }

        return ' ';
    }
}
