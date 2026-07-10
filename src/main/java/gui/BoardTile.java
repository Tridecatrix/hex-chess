package gui;

import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

import static java.lang.Math.sqrt;

public class BoardTile extends Polygon {
    enum TileColor {
        WHITE,
        BLACK,
        GREY,
    }

    enum Highlight {
        NONE,
        NORMAL,
        CAPTURE,
        CHECK,
        PROMOTION
    }

    static final String whiteTileColor = "0xFFCE9E";
    static final String blackTileColor = "0xE8AB6F";
    static final String greyTileColor = "0xD18B47";

    // for coloring tiles which the player can legally move to
    static final String highlightedWhiteTileColor = "0x9AB7C4";
    static final String highlightedGreyTileColor = "0x8CA1A8";
    static final String highlightedBlackTileColor = "0x7E8E8F";

    // for coloring tiles which the player can move to and capture
    static final String highlightedCaptureWhiteTileColor = "0xFFD257";
    static final String highlightedCaptureGreyTileColor = "0xF2BE3D";
    static final String highlightedCaptureBlackTileColor = "0xE5AD27";

    static final String checkWhiteTileColor = "0xFF5A45";
    static final String checkBlackTileColor = "0xEA3D1F";
    static final String checkGreyTileColor = "0xF44B30";

    static final String promotionTileColor = "0xB875C7";

    // for coloring king tile when it is in check

    public void setHighlight(Highlight highlight) {
        Color newFill = Color.web(switch (highlight) {
            case NONE -> switch (this.color) {
                case WHITE -> whiteTileColor;
                case BLACK -> blackTileColor;
                case GREY -> greyTileColor;
            };
            case NORMAL -> switch (this.color) {
                case WHITE -> highlightedWhiteTileColor;
                case BLACK -> highlightedBlackTileColor;
                case GREY -> highlightedGreyTileColor;
            };
            case CAPTURE -> switch (this.color) {
                case WHITE -> highlightedCaptureWhiteTileColor;
                case BLACK -> highlightedCaptureBlackTileColor;
                case GREY -> highlightedCaptureGreyTileColor;
            };
            case CHECK -> switch (this.color) {
                case WHITE -> checkWhiteTileColor;
                case BLACK -> checkBlackTileColor;
                case GREY -> checkGreyTileColor;
            };
            case PROMOTION -> promotionTileColor;
        });
        this.setFill(newFill);
        this.highlight = highlight;
    }

    public Highlight getHighlight() {
        return highlight;
    }

    TileColor color;
    Highlight highlight = Highlight.NONE;

    public int getxBoard() {
        return xBoard;
    }

    public void setxBoard(int xBoard) {
        this.xBoard = xBoard;
    }

    public int getyBoard() {
        return yBoard;
    }

    public void setyBoard(int yBoard) {
        this.yBoard = yBoard;
    }

    int xBoard;
    int yBoard;

    // constructs a board tile, i.e. hexagon, with side length s at (0,0) relative to parent
    public BoardTile(double s, TileColor c) {
        super();
        double h = sideLengthToHeight(s);
        this.getPoints().addAll(s, 0.0,
                s/2, h,
                -s/2, h,
                -s, 0.0,
                -s/2, -h,
                s/2, -h);
        this.color = c;

        this.setFill(Color.web(
                this.color == TileColor.WHITE ? whiteTileColor :
                this.color == TileColor.GREY ? greyTileColor : blackTileColor
        ));
    }

    public static double sideLengthToHeight(double s) {
        return s*sqrt(3)/2;
    }

    public static double sideLengthToDiameter(double s) {
        return 2*s;
    }

    public static double sideLengthToFullHeight(double s) {
        return s*sqrt(3);
    }

    public static double fullHeightToSideLength(double h) {
        return h/sqrt(3);
    }
}
