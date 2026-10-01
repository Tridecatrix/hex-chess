package gui;

import javafx.scene.image.ImageView;
import model.PieceType;
import model.Position;
import model.piece.Piece;
import java.net.URL;

import java.io.File;

public class PieceView extends ImageView {
    int xBoard;
    int yBoard;
    PieceType type;

    public PieceView(PieceType type, Piece.Color color, int xBoard, int yBoard) {
        this("/assets/Chess_" + type.getChar() + switch (color) {
            case WHITE -> 'l';
            case BLACK -> 'd';
            case RED -> 'r';
            case BLUE -> 'b';
            case YELLOW -> 'y';
            case GREEN -> 'g';
            case PURPLE -> 'p';
            case DISABLED -> 'm';
        } + "t45.png", xBoard, yBoard);
        this.type = type;
    }

    public PieceView(PieceType type, Piece.Color color, Position pos) {
        this(type, color, pos.file, pos.rank);
    }

    public PieceView(String path, int xBoard, int yBoard) {
        super(getImageUrl(path));
        this.xBoard = xBoard;
        this.yBoard = yBoard;
    }

    private static String getImageUrl(String path) {
        URL resource = PieceView.class.getResource(path);
        if (resource == null) {
            throw new IllegalArgumentException("Resource not found at path: " + path);
        }
        return resource.toExternalForm();
    }
}
