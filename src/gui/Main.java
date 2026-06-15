package gui;

import javafx.application.Application;
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
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import model.*;
import model.piece.Piece;

import java.util.List;

import static java.lang.Thread.sleep;

public class Main extends Application {
    Stage primaryStage;

    // list of GUI elements that are printed on the board side
    BoardTile[][] boardTilesAsArray = new BoardTile[11][11];
    PieceView[][] pieceViewsAsArray = new PieceView[11][11];
    StackPane[][] tileStacksAsArray = new StackPane[11][11];
    Group boardAndCoordinatesView;

    // list of GUI elements printed on the sidebar
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
        boardAndCoordinatesView = new Group();
        root.getChildren().add(boardAndCoordinatesView);
        double h = BOARD_SIZE/11;                       // tile height
        double s = BoardTile.fullHeightToSideLength(h); // tile side length

        // create board tile stacks, pieces and boards
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

                    // create hexagon tile stack
                    StackPane tileStack = new StackPane();
                    tileStack.setLayoutX(x);
                    tileStack.setLayoutY(y);
                    tileStacksAsArray[xBoard][yBoard] = tileStack;
                    tileStack.setAlignment(Pos.CENTER);
                    boardAndCoordinatesView.getChildren().add(tileStack);

                    // create hexagon tile object
                    BoardTile tile = new BoardTile(s, color);
                    tile.setxBoard(xBoard);
                    tile.setyBoard(yBoard);
                    boardTilesAsArray[xBoard][yBoard] = tile;
                    tileStack.getChildren().add(tile);
                    tile.setOnMouseClicked(e -> this.handleClickOnTile(tile.xBoard, tile.yBoard) );

                    // piece objects will be created later in renderPieces()
                }
            }
        }


        // create coordinate markings: x markings
        for (int xBoard = 0; xBoard < 11; xBoard++) {
            char file = (char) ((int) 'a' + xBoard);
            Text coordinateMarking = new Text(Character.toString(file));
            coordinateMarking.setFont(Font.font(24));
            coordinateMarking.setX(s + 1.5 * s * xBoard);
            coordinateMarking.setY(h * 1.5 + h / 2 * Position.distanceFromEdge(new Position(xBoard, 0), 11));
            boardAndCoordinatesView.getChildren().add(coordinateMarking);
            coordinateMarking.setTextAlignment(TextAlignment.CENTER);
        }

        // create coordinate markings: y markings
        for (int yBoard = 0; yBoard < 11; yBoard++) {
            Text leftCoordinateMarking = new Text(Integer.toString(yBoard + 1));
            Text rightCoordinateMarking = new Text(Integer.toString(yBoard + 1));
            leftCoordinateMarking.setFont(Font.font(24));
            rightCoordinateMarking.setFont(Font.font(24));
            leftCoordinateMarking.setTextAlignment(TextAlignment.RIGHT); // for some reason this doesn't work
            rightCoordinateMarking.setTextAlignment(TextAlignment.LEFT);

            if (yBoard <= 5) {
                leftCoordinateMarking.setX(s - 1.25 * s);
                leftCoordinateMarking.setY(h * 0.25 - h * yBoard);
                rightCoordinateMarking.setX(1.9 * s + 1.5 * s * 10);
                rightCoordinateMarking.setY(h * 0.25 - h * yBoard);
            } else {
                if (yBoard <= 8)
                    leftCoordinateMarking.setX(-0.25 * s + (yBoard - 5) * (s * 1.5));
                else
                    leftCoordinateMarking.setX(-12 - 0.25 * s + (yBoard - 5) * (s * 1.5));
                leftCoordinateMarking.setY(h * 0.25 - h * 2.5 - h/2 * yBoard);
                rightCoordinateMarking.setX(1.9 * s + 1.5 * s * 10   - (yBoard - 5) * (s * 1.5));
                rightCoordinateMarking.setY(h * 0.25 - h * 2.5 - h/2 * yBoard);
            }

            boardAndCoordinatesView.getChildren().add(leftCoordinateMarking);
            boardAndCoordinatesView.getChildren().add(rightCoordinateMarking);
        }

        // render initial pieces
        renderPieces();

        // build sidebar
        sidebar = new VBox(16);
        sidebar.setAlignment(Pos.CENTER_LEFT);
        root.getChildren().add(sidebar);

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
            if (handlingPromotion) {
                handlingPromotion = false;
                closePromotionMenu();
            }
            if (showingTempMessage) {
                sidebar.getChildren().remove(temporaryMessage);
            }
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

        return root;
    }

    // After an update to the underlying Game, i.e. piece position update, render pieces
    private void renderPieces() {
        double h = BOARD_SIZE/11;                       // tile height
        double s = BoardTile.fullHeightToSideLength(h); // tile side length

        // Remove all current piece images
        for (int xBoard = 0; xBoard < 11; xBoard++) {
            for (int yBoard = 0; yBoard < 11; yBoard++) {
                if (pieceViewsAsArray[xBoard][yBoard] != null)  {
                    tileStacksAsArray[xBoard][yBoard].getChildren().remove(pieceViewsAsArray[xBoard][yBoard]);
                    pieceViewsAsArray[xBoard][yBoard] = null;
                }
            }
        }

        // go back over the board and rebuild pieces array
        for (int xBoard = 0; xBoard < 11; xBoard++) {
            for (int yBoard = 0; yBoard < 11; yBoard++) {
                Position pos = new Position(xBoard, yBoard);

                Piece piece = game.getBoard().getPos(pos);

                if (piece == null) {
                    continue;
                }

                PieceView pieceView = new PieceView(piece.getPieceType(), piece.color, xBoard, yBoard);
                pieceView.setFitWidth(s * 1.5);
                pieceView.setFitHeight(s * 1.5);

                pieceView.setOnMouseClicked(e -> this.handleClickOnTile(pieceView.xBoard, pieceView.yBoard));

                tileStacksAsArray[xBoard][yBoard].getChildren().add(pieceView);
                pieceViewsAsArray[xBoard][yBoard] = pieceView;
            }
        }
    }

    // After an update to the underlying game, update the rendered game info
    private void renderGameInfo() {
        currentPlayer.setText("Current player: " + (game.getCurrentPlayer() == Piece.Color.WHITE ? "White" : "Black"));
        moveNumber.setText((String.format("%-28s", "Move number: " + game.getMoveNumberForCurrentSide())));

        switch (game.getCurrentGameState()) {
            case STALEMATE -> {
                gameStatus.setText("Game status: " + (game.getCurrentPlayer() == Piece.Color.WHITE ? "Black" : "White") + "\nwins by stalemate!");
            }
            case CHECKMATE -> {
                gameStatus.setText("Game status: " + (game.getCurrentPlayer() == Piece.Color.WHITE ? "Black" : "White") + "\nwins by checkmate!");
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

        Piece.Color playerColor = this.game.getBoard().getPos(promoteablePawn).color;

        Position promotionQueenPos, promotionBishopPos, promotionKnightPos, promotionRookPos;
        if (playerColor == Piece.Color.WHITE) {
            promotionQueenPos = promoteablePawn;
            promotionBishopPos = Position.oneStepLeftAndBackward(promoteablePawn, 11);
            promotionKnightPos = Position.oneStepRightAndBackward(promoteablePawn, 11);
            promotionRookPos = Position.oneStepBackward(promoteablePawn, 11);

            if (!promotionBishopPos.isInBounds(11)) {
                promotionBishopPos = Position.oneStepRightAndForward(promoteablePawn, 11);
            }
            if (!promotionKnightPos.isInBounds(11)) {
                promotionKnightPos = Position.oneStepLeftAndForward(promoteablePawn, 11);
            }
        } else {
            promotionQueenPos = promoteablePawn;
            promotionBishopPos = Position.oneStepLeftAndForward(promoteablePawn, 11);
            promotionKnightPos = Position.oneStepRightAndForward(promoteablePawn, 11);
            promotionRookPos = Position.oneStepForward(promoteablePawn, 11);

            if (!promotionBishopPos.isInBounds(11)) {
                promotionBishopPos = Position.oneStepRightAndBackward(promoteablePawn, 11);
            }
            if (!promotionKnightPos.isInBounds(11)) {
                promotionKnightPos = Position.oneStepLeftAndBackward(promoteablePawn, 11);
            }
        }


        PieceView promotionQueen = new PieceView(PieceType.QUEEN, playerColor, promotionQueenPos);
        PieceView promotionBishop = new PieceView(PieceType.BISHOP, playerColor, promotionBishopPos);
        PieceView promotionKnight = new PieceView(PieceType.KNIGHT, playerColor, promotionKnightPos);
        PieceView promotionRook = new PieceView(PieceType.ROOK, playerColor, promotionRookPos);

        // highlight the promotion tiles and put the promotion objects on them
        for (PieceView piece : List.of(promotionQueen, promotionBishop, promotionKnight, promotionRook)) {
            double h = BOARD_SIZE/11;
            double s = BoardTile.fullHeightToSideLength(h);

            piece.setFitHeight(s * 1.5);
            piece.setFitWidth(s * 1.5);

            int xBoard = piece.xBoard;
            int yBoard = piece.yBoard;
            boardTilesAsArray[xBoard][yBoard].setHighlight(BoardTile.Highlight.PROMOTION);
            tileStacksAsArray[xBoard][yBoard].getChildren().remove(pieceViewsAsArray[xBoard][yBoard]);
            tileStacksAsArray[xBoard][yBoard].getChildren().add(piece);
            pieceViewsAsArray[xBoard][yBoard] = piece;

            boardTilesAsArray[xBoard][yBoard].setOnMouseClicked(e -> { handlePromotionAndRender(promoteablePawn, piece.type); });
            piece.setOnMouseClicked(e -> {handlePromotionAndRender(promoteablePawn, piece.type); });
        }
    }

    private void handlePromotionAndRender(Position promoteablePawn, PieceType type) {
        game.handlePromotion(promoteablePawn, type);

        Piece.Color playerColor = this.game.getBoard().getPos(promoteablePawn).color;

        Position promotionQueenPos, promotionBishopPos, promotionKnightPos, promotionRookPos;
        if (playerColor == Piece.Color.WHITE) {
            promotionQueenPos = promoteablePawn;
            promotionBishopPos = Position.oneStepLeftAndBackward(promoteablePawn, 11);
            promotionKnightPos = Position.oneStepRightAndBackward(promoteablePawn, 11);
            promotionRookPos = Position.oneStepBackward(promoteablePawn, 11);

            if (!promotionBishopPos.isInBounds(11)) {
                promotionBishopPos = Position.oneStepRightAndForward(promoteablePawn, 11);
            }
            if (!promotionKnightPos.isInBounds(11)) {
                promotionKnightPos = Position.oneStepLeftAndForward(promoteablePawn, 11);
            }
        } else {
            promotionQueenPos = promoteablePawn;
            promotionBishopPos = Position.oneStepLeftAndForward(promoteablePawn, 11);
            promotionKnightPos = Position.oneStepRightAndForward(promoteablePawn, 11);
            promotionRookPos = Position.oneStepForward(promoteablePawn, 11);

            if (!promotionBishopPos.isInBounds(11)) {
                promotionBishopPos = Position.oneStepRightAndBackward(promoteablePawn, 11);
            }
            if (!promotionKnightPos.isInBounds(11)) {
                promotionKnightPos = Position.oneStepLeftAndBackward(promoteablePawn, 11);
            }
        }

        for (Position pos : List.of(promotionQueenPos, promotionBishopPos, promotionKnightPos, promotionRookPos)) {
            // set the handlers for the board tiles back to normal
            boardTilesAsArray[pos.file][pos.rank].setOnMouseClicked(e -> { handleClickOnTile(pos.file, pos.rank); });
        }

        renderPieces();
        renderGameInfo();

        // remove promotion highlighting and then rehighlight the king if it's in check
        clearHighlightingAll();
        if (game.getBoard().isKingInCheck(game.getCurrentPlayer())) {
            Position kingPos = game.getCurrentPlayer() == Piece.Color.WHITE ? game.getBoard().getWhiteKingPos()
                    : game.getBoard().getBlackKingPos();

            boardTilesAsArray[kingPos.file][kingPos.rank].setHighlight(BoardTile.Highlight.CHECK);
        }
    }

    private void closePromotionMenu() {
        // re-render pieces
        renderPieces();

        // remove promotion highlighting and then rehighlight the king if it's in check
        clearHighlightingAll();
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
                    if (boardTilesAsArray[x2][y2] != null)
                        boardTilesAsArray[x2][y2].setHighlight(BoardTile.Highlight.NONE);
                }
            }

            // rehighlight the king if it is in check
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
