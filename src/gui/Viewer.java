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
import model.Position;

import java.util.List;

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
        root.setSpacing(100);

        Group boardView = new Group();
        double BOARD_SIZE = 900;

        for (int xBoard = 0; xBoard < 11; xBoard++) {
            for (int yBoard = 0; yBoard < 11; yBoard++) {
                double h = BOARD_SIZE/11;
                double s = BoardTile.fullHeightToSideLength(h);
                if (new Position(xBoard, yBoard).isInBounds(11)) {
                    // below is based on paper and pen working out of the pixel position of the hexagon centers given
                    // xBoard and yBoard
                    double x = 1.5 * s * xBoard;
                    double y = -h / 2 * Position.distanceFromEdge(new Position(xBoard, yBoard), 11) + h * yBoard;

                    int colorIndex = (yBoard + Position.distanceFromEdge(new Position(xBoard, yBoard), 11)) % 3;
                    BoardTile.TileColor color = List.of(BoardTile.TileColor.BLACK, BoardTile.TileColor.GREY, BoardTile.TileColor.WHITE).get(colorIndex);

                    boardView.getChildren().add(new BoardTile(x, y, s, color));
                }
            }
        }

        root.getChildren().add(boardView);

        // add game info
        VBox gameInfo = new VBox(16);
        gameInfo.setAlignment(Pos.CENTER_LEFT);
        moveNumber = new Text("Move number: 1");
        moveNumber.setFont(Font.font(22));
        currentPlayer = new Text("Current player: White");
        currentPlayer.setFont(Font.font(22));
        gameInfo.getChildren().addAll(moveNumber, currentPlayer);

        // add game buttons
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
