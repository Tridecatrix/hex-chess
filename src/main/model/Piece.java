public class Piece {
    public enum Type {
        PAWN,
        ROOK,
        KNIGHT,
        BISHOP,
        QUEEN,
        KING
    }

    public enum Color {
        WHITE,
        BLACK
    }

    Type type;
    Color color;

    public Piece() {
        this.type = null;
        this.color = null;
    }

    public Piece(Type type, Color color) {
        this.type = type;
        this.color = color;
    }

    public Piece(String type, String color) {
        switch(type.toLowerCase()) {
            case "p":
            case "pawn":
                this.type = Type.PAWN;
                break;
            case "r":
            case "rook":
                this.type = Type.ROOK;
                break;
            case "n":
            case "knight":
                this.type = Type.KNIGHT;
                break;
            case "b":
            case "bishop":
                this.type = Type.BISHOP;
                break;
            case "q":
            case "queen":
                this.type = Type.QUEEN;
                break;
            case "king":
                this.type = Type.KING;
                break;
            default:
                throw new RuntimeException("Invalid piece type");
        }

        switch(color.toLowerCase()) {
            case "w":
            case "white":
                this.color = Color.WHITE;
                break;
            case "b":
            case "black":
                this.color = Color.BLACK;
                break;
            default:
                throw new RuntimeException("Invalid piece color");
        }
    }
}