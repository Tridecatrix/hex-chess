package gui;

import com.sun.javafx.scene.shape.PolygonHelper;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.Board;
import model.Game;
import model.Position;
import model.piece.Piece;

import java.util.List;

import static java.lang.Math.sqrt;

public class Viewer extends Application {
    // list of GUI elements
    Stage primaryStage;
    BoardTile[][] boardTilesAsArray = new BoardTile[11][11];
    ImageView[][] boardPiecesAsArray = new ImageView[11][11];
    Text moveNumber;
    Text currentPlayer;

    // list of backend objects
    Game game;

    // constants
    final double WINDOWWIDTH = 1920;
    final double WINDOWHEIGHT = 1080;
    final double BOARD_SIZE = 900;

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

        // initialise the backend (game object)
        game = new Game();

        // begin setting up the board view
        Group boardTiles = new Group();
        Group pieces = new Group();
        double h = BOARD_SIZE/11;                       // tile height
        double s = BoardTile.fullHeightToSideLength(h); // tile side length

        // create board tiles
        for (int xBoard = 0; xBoard < 11; xBoard++) {
            for (int yBoard = 0; yBoard < 11; yBoard++) {
                if (new Position(xBoard, yBoard).isInBounds(11)) {
                    // calculate the x and y pixel position of the hexagon center from hexagon side length s, hexagon height h,
                    // xBoard and yBoard (i.e. position on the 11x11 board array)
                    double x = 1.5 * s * xBoard;
                    double y = -(-h / 2 * Position.distanceFromEdge(new Position(xBoard, yBoard), 11) + h * yBoard);

                    // get the hexagon color based on its position on the board
                    int colorIndex = (yBoard + Position.distanceFromEdge(new Position(xBoard, yBoard), 11)) % 3;
                    BoardTile.TileColor color = List.of(BoardTile.TileColor.BLACK, BoardTile.TileColor.GREY, BoardTile.TileColor.WHITE).get(colorIndex);

                    // create hexagon tile object
                    BoardTile tile = new BoardTile(x, y, s, color);

                    boardTilesAsArray[xBoard][yBoard] = tile;
                    boardTiles.getChildren().add(tile);
                }
            }
        }

        // create pieces
        Board boardModel = new Board();
        for (int xBoard = 0; xBoard < 11; xBoard++) {
            for (int yBoard = 0; yBoard < 11; yBoard++) {
                Position pos = new Position(xBoard, yBoard);

                Piece piece = boardModel.getPos(pos);

                if (piece != null) {
                    String imagePath = "gui/assets/Chess_"
                            + piece.getChar()
                            + (piece.color == Piece.Color.WHITE ? 'l' : 'd')
                            + "t45.png";

                    double x = 1.5 * s * xBoard;
                    double y = -(-h / 2 * Position.distanceFromEdge(new Position(xBoard, yBoard), 11) + h * yBoard);

                    ImageView pieceView = new ImageView(imagePath);
                    pieceView.setX(x);
                    pieceView.setY(y);
                    pieceView.setFitHeight(h * 0.8);
                    pieceView.setFitWidth(2*s * 0.8);

                    boardPiecesAsArray[xBoard][yBoard] = pieceView;
                    pieces.getChildren().add(pieceView);
                }
            }
        }

        // use a stackpane to ensure the pieces appear on top of the board tiles
        Pane boardAndPieces = new StackPane();
        boardAndPieces.getChildren().addAll(boardTiles, pieces);

        root.getChildren().add(boardAndPieces);

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

    private void renderBoard() {

    }

    private void renderGameInfo() {

    }

    public static void main(String[] args) {
        launch(args);
    }

}
