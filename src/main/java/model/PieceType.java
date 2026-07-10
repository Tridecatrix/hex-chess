package model;

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
}
