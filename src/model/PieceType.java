package model;

public enum PieceType {
    KNIGHT,
    BISHOP,
    ROOK,
    QUEEN,
    PAWN,
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
            default -> {
                return ' ';
            }
        }
    }
}
