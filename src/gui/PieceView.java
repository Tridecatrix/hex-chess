package gui;

import javafx.scene.image.ImageView;
import model.PieceType;
import model.Position;
import model.piece.Piece;

public class PieceView extends ImageView {
    int xBoard;
    int yBoard;
    PieceType type;

    public PieceView(PieceType type, Piece.Color color, int xBoard, int yBoard) {
        char colorChar = color == Piece.Color.WHITE ? 'l' : 'd';
        this("gui/assets/Chess_" + type.getChar() + colorChar + "t45.png", xBoard, yBoard);
        this.type = type;
    }

    public PieceView(PieceType type, Piece.Color color, Position pos) {
        this(type, color, pos.file, pos.rank);
    }

    public PieceView(String path, int xBoard, int yBoard) {
        super(path);
        this.xBoard = xBoard;
        this.yBoard = yBoard;
    }
}
