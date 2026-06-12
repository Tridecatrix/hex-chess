package gui;

import javafx.scene.image.ImageView;

public class PieceView extends ImageView {
    int xBoard;
    int yBoard;

    public PieceView(String path) {
        super(path);
    }

    public PieceView(String path, int xBoard, int yBoard) {
        super(path);
        this.xBoard = xBoard;
        this.yBoard = yBoard;
    }
}
