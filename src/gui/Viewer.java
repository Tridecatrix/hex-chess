package gui;

import com.sun.javafx.scene.shape.PolygonHelper;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import static java.lang.Math.sqrt;

public class Viewer extends Application {
    Stage primaryStage;
    Text moveNumber;
    Text currentPlayer;

    final double WINDOWWIDTH = 1920;
    final double WINDOWHEIGHT = 1080;


    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        stage.setScene(new Scene(createTitleScreen()));
        stage.show();
    }

    private Parent createTitleScreen() {
        VBox root = new VBox(8);
        root.setPrefSize(WINDOWWIDTH, WINDOWHEIGHT);
        root.setAlignment(Pos.CENTER);

        Text titleText = new Text("Hex Chess");
        titleText.setFont(new Font(72));

        Button playButton = new Button("Play");
        playButton.setFont(new Font(48));
        playButton.setOnAction(e -> {
            // once play button is pressed, change the scene to the game screen scene
            primaryStage.getScene().setRoot(createGameScreen());
        });

        root.getChildren().add(titleText);
        root.getChildren().add(playButton);

        return root;
    }

    private Parent createGameScreen() {
        HBox root = new HBox(16);
        root.setPrefSize(1920, 1080);
        root.setAlignment(Pos.CENTER);

        AnchorPane boardSpace = new AnchorPane();
        int BOARD_SIZE = 900;
        boardSpace.setPrefSize(BOARD_SIZE, BOARD_SIZE);

        BoardTile tile;
        int TILE_HEIGHT = BOARD_SIZE / (2*6 - 1);
        double TILE_SIDELENGTH = TILE_HEIGHT / sqrt(3);
        for (int y = 0; y < 11; y++) {
            for (int x = 0; x < 11; x++) {
                if (y >= 11 - Math.abs(x - 5)) continue;

                double tileX = x * BOARD_SIZE / 11;
                double tileY = y * BOARD_SIZE / 11;

                tile = new BoardTile(TILE_SIDELENGTH, BoardTile.TileColor.WHITE);

                AnchorPane.setTopAnchor(tile, tileY);
                AnchorPane.setLeftAnchor(tile, tileX);
                boardSpace.getChildren().add(tile);
            }
        }

        root.getChildren().add(boardSpace);

        VBox gameInfo = new VBox(16);
        gameInfo.setAlignment(Pos.CENTER_LEFT);
        moveNumber = new Text("Move number: 1");
        moveNumber.setFont(Font.font(22));
        currentPlayer = new Text("Current player: White");
        currentPlayer.setFont(Font.font(22));
        gameInfo.getChildren().addAll(moveNumber, currentPlayer);

        HBox gameButtons = new HBox(5);
        Button resign = new Button("Resign");
        Button restart = new Button("Restart");
        gameButtons.getChildren().addAll(resign, restart);
        gameInfo.getChildren().add(gameButtons);

        root.getChildren().add(gameInfo);

        return root;
    }



    public static void main(String[] args) {
        launch(args);
    }

}
