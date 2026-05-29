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

    public BoardTile(double s, TileColor c) {
        super();
        this.getPoints().addAll(new Double[]{
                s, 0.0,
                s/2, s*sqrt(3)/2,
                -s/2, s*sqrt(3)/2,
                -s, 0.0,
                -s/2, -s*sqrt(3)/2,
                s/2, -s*sqrt(3)/2
        });
        this.color = c;

        this.setFill(Color.web(
                this.color == TileColor.WHITE ? whiteTileColor :
                this.color == TileColor.GREY ? greyTileColor : blackTileColor
        ));
    }
}
