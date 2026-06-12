package gui;

import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

import static java.lang.Math.sqrt;

public class BoardTile extends Polygon {
    enum TileColor {
        WHITE,
        BLACK,
        GREY
    }

    final String whiteTileColor = "0xFFCE9E";
    final String blackTileColor = "0xE8AB6F";
    final String greyTileColor = "0xD18B47";

    TileColor color;

    public BoardTile(double x, double y, double s, TileColor c) {
        super();
        this.getPoints().addAll(new Double[]{
                x+s, y,
                x+s/2, y+s*sqrt(3)/2,
                x-s/2, y+s*sqrt(3)/2,
                x-s, y,
                x-s/2, y-s*sqrt(3)/2,
                x+s/2, y-s*sqrt(3)/2
        });
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
