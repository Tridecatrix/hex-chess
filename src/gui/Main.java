package gui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.*;
import model.piece.Piece;

import java.util.List;
import java.util.Set;

import static java.lang.Thread.sleep;

public class Main extends Application {
    // list of GUI elements
    Stage primaryStage;
    BoardTile[][] boardTilesAsArray = new BoardTile[11][11];
    Pane boardAndPieces = new StackPane();
    Group pieces;
    VBox sidebar;
    Text moveNumber;
    Text currentPlayer;
    Text gameStatus;
    Text gameWins;
    Text temporaryMessage;
    boolean showingTempMessage = false;
    boolean handlingPromotion = false;

    // list of backend objects
    Game game;

    // other objects involved in logic
    Position selectedPos; // for tracking selections after a piece is selected

    // constants
    final double WINDOWWIDTH = 1920;
    final double WINDOWHEIGHT = 1080;
    final double BOARD_SIZE = 800;

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
        pieces = new Group();
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
                    tile.setxBoard(xBoard);
                    tile.setyBoard(yBoard);

                    boardTilesAsArray[xBoard][yBoard] = tile;
                    boardTiles.getChildren().add(tile);

                    tile.setOnMouseClicked(e -> this.handleClickOnTile(tile.xBoard, tile.yBoard) );
                }
            }
        }

        boardAndPieces.getChildren().add(boardTiles);

        Group coordinateMarkings = new Group();

        // create coordinate markings: x markings
        for (int xBoard = 0; xBoard < 11; xBoard++) {
            char file = (char) ((int) 'a' + xBoard);
            Text coordinateMarking = new Text(Character.toString(file));
            coordinateMarking.setFont(Font.font(24));
            coordinateMarking.setX(1.5 * s * xBoard - 5);
            coordinateMarking.setY(h / 2 * Position.distanceFromEdge(new Position(xBoard, 0), 11) + h);
            boardTiles.getChildren().add(coordinateMarking);
        }

        // create coordinate markings: y markings
        for (int yBoard = 0; yBoard < 11; yBoard++) {
            Text leftCoordinateMarking = new Text(Integer.toString(yBoard + 1));
            Text rightCoordinateMarking = new Text(Integer.toString(yBoard + 1));
            leftCoordinateMarking.setFont(Font.font(24));
            rightCoordinateMarking.setFont(Font.font(24));

            if (yBoard <= 5) {
                leftCoordinateMarking.setX(-1.25 * s);
                leftCoordinateMarking.setY(-h * 0.25 - h * yBoard);
                rightCoordinateMarking.setX(0.9 * s + 1.5 * s * 10);
                rightCoordinateMarking.setY(-h * 0.25 - h * yBoard);
            } else {
                if (yBoard <= 8) {
                    leftCoordinateMarking.setX(-1.25 * s + (yBoard - 5) * (s * 1.5));
                    rightCoordinateMarking.setX(0.9 * s + 1.5 * s * 10 - (yBoard - 5) * (s * 1.5));
                }
                else {
                    leftCoordinateMarking.setX(-1.5 * s + (yBoard - 5) * (s * 1.5));
                    rightCoordinateMarking.setX(0.9 * s + 1.5 * s * 10 - (yBoard - 5) * (s * 1.5));
                }
                leftCoordinateMarking.setY(-h * 0.25 - h * 2.5 - h/2 * yBoard);
                rightCoordinateMarking.setY(-h * 0.25 - h * 2.5 - h/2 * yBoard);
            }

            boardTiles.getChildren().add(leftCoordinateMarking);
            boardTiles.getChildren().add(rightCoordinateMarking);
        }

        // render initial pieces
        renderPieces();

        root.getChildren().add(boardAndPieces);

        // build sidebar
        sidebar = new VBox(16);
        sidebar.setAlignment(Pos.CENTER_LEFT);

        // add game info
        moveNumber = new Text();
        moveNumber.setFont(Font.font(22));

        currentPlayer = new Text();
        currentPlayer.setFont(Font.font(22));

        gameStatus = new Text();
        gameStatus.setFont(Font.font(22));

        VBox wins = new VBox(5);
        Text heading1 = new Text("Games won by:");
        Text heading2 = new Text("White   Black");
        gameWins = new Text();
        heading1.setFont(Font.font(16));
        heading2.setFont(Font.font(16));
        gameWins.setFont(Font.font(16));
        wins.getChildren().addAll(heading1, heading2, gameWins);

        renderGameInfo();

        sidebar.getChildren().addAll(moveNumber, currentPlayer, gameStatus, wins);

        // add game buttons
        HBox gameButtons = new HBox(5);
        Button resign = new Button("Resign");
        Button restart = new Button("Restart");
        Button claimDraw = new Button("Claim draw");
        gameButtons.getChildren().addAll(resign, restart, claimDraw);
        sidebar.getChildren().add(gameButtons);

        resign.setOnAction(e -> {
            if (game.getCurrentGameState() == Game.GameResult.CONTINUING) {
                game.resign();
                renderGameInfo();
            }
        });

        restart.setOnAction(e -> {
            game.restartGame();
            renderPieces();
            renderGameInfo();
            clearHighlightingAll();
        });

        claimDraw.setOnAction(e -> {
            if (game.claimDraw()) {
                renderGameInfo();
            } else {
                if (showingTempMessage)
                    sidebar.getChildren().remove(temporaryMessage);

                temporaryMessage = new Text("No draw; continue");
                temporaryMessage.setFont(Font.font(22));
                temporaryMessage.setFill(Color.RED);
                sidebar.getChildren().add(temporaryMessage);
                showingTempMessage = true;
            }
        });

        root.getChildren().add(sidebar);

        return root;
    }

    // After an update to the underlying Game, i.e. piece position update, render pieces
    private void renderPieces() {
        // Remove all current piece images
        boardAndPieces.getChildren().remove(pieces);

        Group newPieces = new Group();

        double h = BOARD_SIZE/11;                       // tile height
        double s = BoardTile.fullHeightToSideLength(h); // tile side length

        // go back over the board and rebuild pieces array
        for (int xBoard = 0; xBoard < 11; xBoard++) {
            for (int yBoard = 0; yBoard < 11; yBoard++) {
                Position pos = new Position(xBoard, yBoard);

                Piece piece = game.getBoard().getPos(pos);

                String imagePath;
                if (piece != null) {
                    imagePath = "gui/assets/Chess_"
                            + piece.getChar()
                            + (piece.color == Piece.Color.WHITE ? 'l' : 'd')
                            + "t45.png";
                } else if (pos.isInBounds(11))
                    // for empty but in bounds positions, put a blank transparent box; this ensures that piece
                    // images are aligned properly to the tiles that they are on
                    imagePath = "gui/assets/blank.png";
                else {
                    continue;
                }

                double x = 1.5 * s * xBoard;
                double y = -(-h / 2 * Position.distanceFromEdge(new Position(xBoard, yBoard), 11) + h * yBoard);

                PieceView pieceView = new PieceView(imagePath, xBoard, yBoard);
                pieceView.setX(x);
                pieceView.setY(y);
                pieceView.setFitHeight(h * 0.8);
                pieceView.setFitWidth(2 * s * 0.8);

                pieceView.setOnMouseClicked(e -> this.handleClickOnTile(pieceView.xBoard, pieceView.yBoard));

                newPieces.getChildren().add(pieceView);
            }
        }

        // add additional blanks to adjust the piece positions given that the board also has coordinate markings
        ImageView coordinateMarkingBlank = new ImageView("gui/assets/blank.png");
        coordinateMarkingBlank.setX(1.5 * s * 5);
        coordinateMarkingBlank.setY(h / 2 * Position.distanceFromEdge(new Position(5, 0), 11) + h*1.2);
        newPieces.getChildren().add(coordinateMarkingBlank);

        ImageView coordinateMarkingBlank3 = new ImageView("gui/assets/blank.png");
        coordinateMarkingBlank3.setX(s*0.25);
        coordinateMarkingBlank3.setY(-h * 0.125 - h * 2.5 - h/2 * 10);
        newPieces.getChildren().add(coordinateMarkingBlank3);


        this.pieces = newPieces;
        boardAndPieces.getChildren().add(newPieces);
    }

    // After an update to the underlying game, update the rendered game info
    private void renderGameInfo() {
        currentPlayer.setText("Current player: " + (game.getCurrentPlayer() == Piece.Color.WHITE ? "White" : "Black"));
        moveNumber.setText((String.format("%-28s", "Move number: " + game.getMoveNumberForCurrentSide())));

        switch (game.getCurrentGameState()) {
            case STALEMATE -> {
                gameStatus.setText("Game status: " + (game.getCurrentPlayer() == Piece.Color.WHITE ? "White" : "Black") + "\nwins by stalemate!");
            }
            case CHECKMATE -> {
                gameStatus.setText("Game status: " + (game.getCurrentPlayer() == Piece.Color.WHITE ? "White" : "Black") + "\nwins by checkmate!");
            }
            case DRAW -> {
                gameStatus.setText("Game status: draw");
            }
            case RESIGNED -> {
                gameStatus.setText("Game status: " + (game.getCurrentPlayer() == Piece.Color.WHITE ? "Black" : "White") + "\nwins by resignation!");
            }
            case CONTINUING -> {
                gameStatus.setText("Game status: continuing");
            }
        }

        gameWins.setText(game.getWhitePoints() + "        " + game.getBlackPoints());
    }

    private void renderPromotionMenu(Position promoteablePawn) {
        handlingPromotion = true;

        Group promotionMenu = new Group();

        double h = BOARD_SIZE/11;                       // tile height
        double s = BoardTile.fullHeightToSideLength(h); // tile side length

        // put in blanks at all locations on the board to ensure coordinate alignment
        for (int xBoard = 0; xBoard < 11; xBoard++) {
            for (int yBoard = 0; yBoard < 11; yBoard++) {
                Position pos = new Position(xBoard, yBoard);

                String imagePath;
                if (pos.isInBounds(11) || pos.equals(promoteablePawn))
                    imagePath = "gui/assets/blank.png";
                else {
                    continue;
                }

                double x = 1.5 * s * xBoard;
                double y = -(-h / 2 * Position.distanceFromEdge(new Position(xBoard, yBoard), 11) + h * yBoard);

                PieceView blank = new PieceView(imagePath, xBoard, yBoard);
                blank.setX(x);
                blank.setY(y);
                blank.setFitHeight(h * 0.8);
                blank.setFitWidth(2 * s * 0.8);

                promotionMenu.getChildren().add(blank);
            }
        }

        // add additional blanks to adjust the piece positions given that the board also has coordinate markings
        ImageView coordinateMarkingBlank = new ImageView("gui/assets/blank.png");
        coordinateMarkingBlank.setX(1.5 * s * 5);
        coordinateMarkingBlank.setY(h / 2 * Position.distanceFromEdge(new Position(5, 0), 11) + h*1.2);
        promotionMenu.getChildren().add(coordinateMarkingBlank);

        ImageView coordinateMarkingBlank3 = new ImageView("gui/assets/blank.png");
        coordinateMarkingBlank3.setX(s*0.25);
        coordinateMarkingBlank3.setY(-h * 0.125 - h * 2.5 - h/2 * 10);
        promotionMenu.getChildren().add(coordinateMarkingBlank3);

        // start building promotion tiles and icons

        char pieceColor = game.getBoard().getPos(promoteablePawn).color == Piece.Color.WHITE ? 'l' : 'd';

        int multiplier = game.getBoard().getPos(promoteablePawn).color == Piece.Color.WHITE ? 1 : -1;
        double promotionX = s/2 + 13 + 1.5 * s * promoteablePawn.file;
        double promotionY = h/2 - 4
                - (-h / 2 * Position.distanceFromEdge(new Position(promoteablePawn.file, promoteablePawn.rank), 11)
                + h * promoteablePawn.rank);

        BoardTile promotionTileQueen = new BoardTile(promotionX, promotionY, s, BoardTile.TileColor.WHITE);
        PieceView promotionQueen = new PieceView("gui/assets/Chess_q" + pieceColor + "t45.png");
        promotionQueen.setX(promotionX-s+10);
        promotionQueen.setY(promotionY-h/2+10);
        promotionQueen.setFitHeight(h * 0.8);
        promotionQueen.setFitWidth(2 * s * 0.8);

        BoardTile promotionTileBishop;
        PieceView promotionBishop = new PieceView("gui/assets/Chess_b" + pieceColor + "t45.png");
        if (promoteablePawn.file == 0 || promoteablePawn.file == 1) {
            promotionTileBishop = new BoardTile(promotionX + s * 1.5 + s / 2, promotionY - multiplier * (h / 2 + s / 2), s, BoardTile.TileColor.WHITE);
            promotionBishop.setX(promotionX-s+10 + s * 1.5 + s / 2);
            promotionBishop.setY(promotionY-h/2+10 - multiplier * (h / 2 + s / 2));
            promotionBishop.setFitHeight(h * 0.8);
            promotionBishop.setFitWidth(2 * s * 0.8);
        } else {
            promotionTileBishop = new BoardTile(promotionX - s * 1.5 - s / 2, promotionY + multiplier * (h / 2 + s / 2), s, BoardTile.TileColor.WHITE);
            promotionBishop.setX(promotionX-s+10 - s * 1.5 - s / 2);
            promotionBishop.setY(promotionY-h/2+10 + multiplier * (h / 2 + s / 2));
            promotionBishop.setFitHeight(h * 0.8);
            promotionBishop.setFitWidth(2 * s * 0.8);
        }

        BoardTile promotionTileKnight;
        PieceView promotionKnight = new PieceView("gui/assets/Chess_n" + pieceColor + "t45.png");
        if (promoteablePawn.file == 9 || promoteablePawn.file == 10) {
            promotionTileKnight = new BoardTile(promotionX - s * 1.5 - s / 2, promotionY - multiplier * (h / 2 + s / 2), s, BoardTile.TileColor.WHITE);
            promotionKnight.setX(promotionX-s+10 - s * 1.5 - s / 2);
            promotionKnight.setY(promotionY-h/2+10 - multiplier * (h / 2 + s / 2));
            promotionKnight.setFitHeight(h * 0.8);
            promotionKnight.setFitWidth(2 * s * 0.8);
        } else {
            promotionTileKnight = new BoardTile(promotionX + s * 1.5 + s / 2, promotionY + multiplier * (h / 2 + s / 2), s, BoardTile.TileColor.WHITE);
            promotionKnight.setX(promotionX-s+10 + s * 1.5 + s / 2);
            promotionKnight.setY(promotionY-h/2+10 + multiplier * (h / 2 + s / 2));
            promotionKnight.setFitHeight(h * 0.8);
            promotionKnight.setFitWidth(2 * s * 0.8);
        }

        BoardTile promotionTileRook = new BoardTile(promotionX, promotionY + multiplier * (h + s), s, BoardTile.TileColor.WHITE);
        PieceView promotionRook = new PieceView("gui/assets/Chess_r" + pieceColor + "t45.png");
        promotionRook.setX(promotionX-s+10);
        promotionRook.setY(promotionY-h/2+10 + multiplier * (h + s));
        promotionRook.setFitHeight(h * 0.8);
        promotionRook.setFitWidth(2 * s * 0.8);

        promotionTileQueen.setHighlight(BoardTile.Highlight.PROMOTION);
        promotionTileBishop.setHighlight(BoardTile.Highlight.PROMOTION);
        promotionTileKnight.setHighlight(BoardTile.Highlight.PROMOTION);
        promotionTileRook.setHighlight(BoardTile.Highlight.PROMOTION);
        promotionMenu.getChildren().addAll(List.of(promotionTileQueen, promotionTileBishop, promotionTileKnight, promotionTileRook,
                promotionQueen, promotionBishop, promotionKnight, promotionRook));

        boardAndPieces.getChildren().add(promotionMenu);

        // add handlers for each button and piece object
        promotionTileQueen.setOnMouseClicked(e -> {
            handlePromotionAndRender(promoteablePawn, PieceType.QUEEN, promotionMenu);
        });
        promotionQueen.setOnMouseClicked(e -> {
            handlePromotionAndRender(promoteablePawn, PieceType.QUEEN, promotionMenu);
        });

        promotionTileBishop.setOnMouseClicked(e -> {
            handlePromotionAndRender(promoteablePawn, PieceType.BISHOP, promotionMenu);
        });
        promotionBishop.setOnMouseClicked(e -> {
            handlePromotionAndRender(promoteablePawn, PieceType.BISHOP, promotionMenu);
        });

        promotionTileKnight.setOnMouseClicked(e -> {
            handlePromotionAndRender(promoteablePawn, PieceType.KNIGHT, promotionMenu);
        });
        promotionKnight.setOnMouseClicked(e -> {
            handlePromotionAndRender(promoteablePawn, PieceType.KNIGHT, promotionMenu);
        });

        promotionTileRook.setOnMouseClicked(e -> {
            handlePromotionAndRender(promoteablePawn, PieceType.ROOK, promotionMenu);
        });
        promotionRook.setOnMouseClicked(e -> {
            handlePromotionAndRender(promoteablePawn, PieceType.ROOK, promotionMenu);
        });
    }

    private void handlePromotionAndRender(Position promoteablePawn, PieceType type, Group promotionMenu) {
        game.handlePromotion(promoteablePawn, type);

        boardAndPieces.getChildren().remove(promotionMenu);

        renderPieces();
        renderGameInfo();

        // highlight the king if it is in check after the move
        if (game.getBoard().isKingInCheck(game.getCurrentPlayer())) {
            Position kingPos = game.getCurrentPlayer() == Piece.Color.WHITE ? game.getBoard().getWhiteKingPos()
                    : game.getBoard().getBlackKingPos();

            boardTilesAsArray[kingPos.file][kingPos.rank].setHighlight(BoardTile.Highlight.CHECK);
        }
    }


    // handler for clicking on a tile
    private void handleClickOnTile(int x, int y) {
        // clear any temporary messages
        if (showingTempMessage || handlingPromotion) {
            sidebar.getChildren().remove(temporaryMessage);
        }

        Position clickedTilePos = new Position(x, y);

        // if the game is already over, do nothing
        if (game.getCurrentGameState() != Game.GameResult.CONTINUING) {
            return;
        }

        // if the player is clicking again on the selected position
        if (this.selectedPos != null && this.selectedPos.equals(clickedTilePos)) {
            // deselect
            this.selectedPos = null;

            // clear highlighting (except for king position)
            for (int x2 = 0; x2 < 11; x2++) {
                for (int y2 = 0; y2 < 11; y2++) {
                    if (boardTilesAsArray[x2][y2] != null && !(new Position(x2, y2)).equals(game.getBoard().getWhiteKingPos()) &&
                            !(new Position(x2, y2)).equals(game.getBoard().getBlackKingPos()))
                        boardTilesAsArray[x2][y2].setHighlight(BoardTile.Highlight.NONE);
                }
            }

            // rehighlight the king if it is the king being selected
            if (game.getBoard().isKingInCheck(game.getCurrentPlayer())) {
                Position kingPos = game.getCurrentPlayer() == Piece.Color.WHITE ? game.getBoard().getWhiteKingPos()
                        : game.getBoard().getBlackKingPos();

                boardTilesAsArray[kingPos.file][kingPos.rank].setHighlight(BoardTile.Highlight.CHECK);
            }
        }
        // if the player is clicking on a position that was highlighted after a previous selection, move the selected
        // piece to that position
        else if (boardTilesAsArray[x][y].getHighlight() == BoardTile.Highlight.NORMAL ||
                boardTilesAsArray[x][y].getHighlight() == BoardTile.Highlight.CAPTURE) {
            Position fromPos = selectedPos;
            Position toPos = clickedTilePos;

            // clear selection and highlighting (including king)
            this.selectedPos = null;

            for (int x2 = 0; x2 < 11; x2++) {
                for (int y2 = 0; y2 < 11; y2++) {
                    if (boardTilesAsArray[x2][y2] != null)
                        boardTilesAsArray[x2][y2].setHighlight(BoardTile.Highlight.NONE);
                }
            }

            // apply the move
            Move move = new Move(fromPos, toPos);
            MoveResult result = game.applyMove(move);

            if (result.promoteablePawn != null) {
                // the handler for the tiles in the promotion menu will handle deleting the promotion menu
                // and completing the render operation
                renderPromotionMenu(result.promoteablePawn);
                return;
            }

            // additional actions: check if game end
            game.checkIfGameEnd();

            renderPieces();
            renderGameInfo();

            // highlight the king if it is in check after the move
            if (game.getBoard().isKingInCheck(game.getCurrentPlayer())) {
                Position kingPos = game.getCurrentPlayer() == Piece.Color.WHITE ? game.getBoard().getWhiteKingPos()
                        : game.getBoard().getBlackKingPos();

                boardTilesAsArray[kingPos.file][kingPos.rank].setHighlight(BoardTile.Highlight.CHECK);
            }
        }
        else if (game.getBoard().getPos(clickedTilePos) != null && game.getBoard().getPos(clickedTilePos).color == game.getCurrentPlayer()) {
            this.selectedPos = clickedTilePos;

            // clear existing highlighting
            for (int x2 = 0; x2 < 11; x2++) {
                for (int y2 = 0; y2 < 11; y2++) {
                    if (boardTilesAsArray[x2][y2] != null && !(new Position(x2, y2)).equals(game.getBoard().getWhiteKingPos()) &&
                            !(new Position(x2, y2)).equals(game.getBoard().getBlackKingPos()))
                        boardTilesAsArray[x2][y2].setHighlight(BoardTile.Highlight.NONE);
                }
            }

            // rehighlight the king if it is the king being selected
            if (game.getBoard().isKingInCheck(game.getCurrentPlayer())) {
                Position kingPos = game.getCurrentPlayer() == Piece.Color.WHITE ? game.getBoard().getWhiteKingPos()
                        : game.getBoard().getBlackKingPos();

                boardTilesAsArray[kingPos.file][kingPos.rank].setHighlight(BoardTile.Highlight.CHECK);
            }

            // add new highlighting
            boardTilesAsArray[x][y].setHighlight(BoardTile.Highlight.NORMAL);
            for (Move move : game.getLegalMovesFromPos(selectedPos)) {
                Position toPos = move.toPos;
                if (game.getBoard().getPos(toPos) != null) {
                    // this is a capturing move
                    boardTilesAsArray[toPos.file][toPos.rank].setHighlight(BoardTile.Highlight.CAPTURE);
                } else {
                    // this is a normal move
                    boardTilesAsArray[toPos.file][toPos.rank].setHighlight(BoardTile.Highlight.NORMAL);
                }
            }
        }
    }

    public void clearHighlightingAll() {
        for (int x2 = 0; x2 < 11; x2++) {
            for (int y2 = 0; y2 < 11; y2++) {
                if (boardTilesAsArray[x2][y2] != null)
                    boardTilesAsArray[x2][y2].setHighlight(BoardTile.Highlight.NONE);
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

}
