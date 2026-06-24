package gui;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Stage;
import model.*;
import model.piece.Pawn;
import model.piece.Piece;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.UnaryOperator;

import static java.lang.Thread.sleep;

public class Main extends Application {
    Stage primaryStage;

    // list of GUI elements and variables associated with game settings on the main menu
    boolean timerEnabled;
    VBox timerSettings;
    CheckBox enableTimer;
    ComboBox<String> timerPresetDropdown;
    HBox timerSelect;
    List<HBox> customTimerFieldsWithText;
    TextField customTimerStartingTimeWhite;
    TextField customTimerIncrementWhite;
    TextField customTimerStartingTimeBlack;
    TextField customTimerIncrementBlack;
    boolean customTimerSettingsEnabled;
    ComboBox<String> gameModeDropdown;

    // list of GUI elements that are printed on the board side
    BoardTile[][] boardTilesAsArray;
    PieceView[][] pieceViewsAsArray;
    StackPane[][] tileStacksAsArray;
    Group boardAndCoordinatesView;

    // list of GUI elements printed on the sidebar
    VBox sidebar;
    Text moveNumber;
    Text currentPlayer;
    Text gameStatus;
    Map<Piece.Color, TextFlow> capturedPiecesTexts = new HashMap<>();
    Text gameWins;
    Text timerSettingsDesc;
    Text timeRemainingWhite;
    Text timeRemainingBlack;
    Text temporaryMessage;

    // timer
    AnimationTimer animationTimer;

    boolean showingTempMessage = false;
    boolean handlingPromotion = false;

    // list of backend objects
    Game game;
    GameTimer gameTimer;

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
        VBox root = new VBox(16);
        root.setPrefSize(WINDOWWIDTH, WINDOWHEIGHT);
        root.setAlignment(Pos.CENTER);

        Text titleText = new Text("Hex Chess");
        titleText.setFont(new Font(72));

        VBox settings = new VBox(5);
        settings.setAlignment(Pos.CENTER);
        settings.setPrefWidth(200);

        Text settingsHeader = new Text("Settings:");
        settingsHeader.setFont(Font.font(24));
        settingsHeader.setTextAlignment(TextAlignment.CENTER);
        settings.getChildren().add(settingsHeader);

        timerSettings = new VBox(5);
        timerSettings.setAlignment(Pos.CENTER);
        enableTimer = new CheckBox("Enable timer");
        enableTimer.setFont(Font.font(16));
        timerSettings.getChildren().add(enableTimer);
        enableTimer.setOnAction(e -> { expandTimerOptions(); });
        settings.getChildren().add(timerSettings);

        HBox gameModeSettings = new HBox(5);
        gameModeSettings.setAlignment(Pos.CENTER);
        Text gameModeText = new Text("Game mode:");
        gameModeText.setFont(Font.font(16));
        gameModeDropdown = new ComboBox<>();
        gameModeDropdown.getItems().addAll("2 player", "3 player", "6 player");
        gameModeDropdown.setValue("2 player");
        gameModeSettings.getChildren().addAll(gameModeText, gameModeDropdown);
        settings.getChildren().add(gameModeSettings);

        Button playButton = new Button("Play");
        playButton.setFont(new Font(48));
        playButton.setOnAction(e -> {
            // once play button is pressed, change the scene to the game screen scene
            primaryStage.getScene().setRoot(createGameScreen());
        });

        root.getChildren().add(titleText);
        root.getChildren().add(settings);
        root.getChildren().add(playButton);

        return root;
    }

    private void expandTimerOptions() {
        this.timerEnabled = enableTimer.isSelected();
        if (timerEnabled) {
            timerSelect = new HBox(10);
            timerSelect.getChildren().add(new Text("Timing: "));
            timerSelect.setAlignment(Pos.CENTER);

            timerPresetDropdown = new ComboBox<>();
            timerPresetDropdown.getItems().addAll("Bullet", "Blitz", "Rapid", "Classical", "Custom");
            timerPresetDropdown.setValue("Rapid");
            gameTimer = new GameTimer(GameTimer.TimingSetting.RAPID);
            timerPresetDropdown.setOnAction(e2 -> {
                if (Objects.equals(timerPresetDropdown.getValue(), "Custom")) {
                    customTimerSettingsEnabled = true;

                    customTimerStartingTimeWhite = new TextField();
                    customTimerIncrementWhite = new TextField();
                    customTimerStartingTimeBlack = new TextField();
                    customTimerIncrementBlack = new TextField();

                    HBox hbox1 = new HBox(5, new Text("Start time for White (sec):"), customTimerStartingTimeWhite);
                    HBox hbox2 = new HBox(5, new Text("Increment for White (sec):"), customTimerIncrementWhite);
                    HBox hbox3 = new HBox(5, new Text("Start time for Black (sec):"), customTimerStartingTimeBlack);
                    HBox hbox4 = new HBox(5, new Text("Increment for Black (sec):"), customTimerIncrementBlack);
                    customTimerFieldsWithText = List.of(hbox1, hbox2, hbox3, hbox4);

                    // set numeric filter on the textfields
                    UnaryOperator<TextFormatter.Change> filter = change -> {
                        String text = change.getControlNewText();
                        if (text.matches("\\d*")) {
                            return change; // Accept the change
                        }
                        return null; // Reject the change
                    };


                    for (TextField field : List.of(customTimerIncrementWhite, customTimerStartingTimeWhite,
                            customTimerIncrementBlack, customTimerIncrementBlack)) {
                        field.setTextFormatter(new TextFormatter<>(filter));
                    }

                    for (HBox box : customTimerFieldsWithText) {
                        box.setAlignment(Pos.CENTER);
                    }

                    timerSettings.getChildren().addAll(customTimerFieldsWithText);
                } else if (customTimerSettingsEnabled) {
                    customTimerSettingsEnabled = false;
                    timerSettings.getChildren().removeAll(customTimerFieldsWithText);
                }
            });
            timerSelect.getChildren().add(timerPresetDropdown);

            timerSettings.getChildren().add(timerSelect);
        }
        else {
            // get rid of the timer settings
            timerSettings.getChildren().remove(timerSelect);
        }
    }

    private Parent createGameScreen() {
        HBox root = new HBox(16);
        root.setPrefSize(1920, 1080);
        root.setAlignment(Pos.CENTER);
        root.setSpacing(100);

        // initialise the backend (game object)
        Game.Mode gamemode = switch (gameModeDropdown.getValue()) {
            case "2 player" -> Game.Mode.TWO_PLAYER;
            case "3 player" -> Game.Mode.THREE_PLAYER;
            case "6 player" -> Game.Mode.SIX_PLAYER;
            default -> throw new IllegalArgumentException("Illegal game mode");
        };
        game = new Game(gamemode);

        // initialise the backend (game timer)
        if (timerEnabled) {
            GameTimer timer;

            switch (timerPresetDropdown.getValue()) {
                case "Bullet" -> { timer = new GameTimer(GameTimer.TimingSetting.BULLET); }
                case "Blitz" -> { timer = new GameTimer(GameTimer.TimingSetting.BLITZ); }
                case "Rapid" -> { timer = new GameTimer(GameTimer.TimingSetting.RAPID); }
                case "Classical" -> { timer = new GameTimer(GameTimer.TimingSetting.CLASSICAL); }
                case "Custom" -> {
                    timer = new GameTimer(
                            Integer.parseInt(customTimerStartingTimeWhite.getText()),
                            Integer.parseInt(customTimerStartingTimeBlack.getText()),
                            Integer.parseInt(customTimerIncrementWhite.getText()),
                            Integer.parseInt(customTimerIncrementBlack.getText()));
                }
                default -> {
                    throw new IllegalArgumentException("Illegal timing option");
                }
            }

            timer.startOrRestartTimer();
            game.setGameTimer(timer);

            // set up an animation timer to update the timer
            animationTimer = new AnimationTimer() {
                @Override
                public void handle(long l) {
                    game.updateTimer(l);
                    renderGameInfo();
                }
            };
            animationTimer.start();
        }

        // begin setting up the board view
        boardAndCoordinatesView = new Group();
        root.getChildren().add(boardAndCoordinatesView);
        int d = game.getBoard().getBoardDiameter();
        double h = BOARD_SIZE/d;                       // tile height
        double s = BoardTile.fullHeightToSideLength(h); // tile side length

        tileStacksAsArray = new StackPane[d][d];
        boardTilesAsArray = new BoardTile[d][d];
        pieceViewsAsArray = new PieceView[d][d];

        // create board tile stacks, pieces and boards
        for (int xBoard = 0; xBoard < d; xBoard++) {
            for (int yBoard = 0; yBoard < d; yBoard++) {
                if (new Position(xBoard, yBoard).isInBounds(d)) {
                    // calculate the x and y pixel position of the hexagon center from hexagon side length s, hexagon height h,
                    // xBoard and yBoard (i.e. position on the dxd board array)
                    double x = 1.5 * s * xBoard;
                    double y = -(-h / 2 * Position.distanceFromEdge(new Position(xBoard, yBoard), d) + h * yBoard);

                    // get the hexagon color based on its position on the board
                    int colorIndex = (yBoard + Position.distanceFromEdge(new Position(xBoard, yBoard), d)) % 3;
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
        for (int xBoard = 0; xBoard < d; xBoard++) {
            char file = (char) ((int) 'a' + xBoard);
            Text coordinateMarking = new Text(Character.toString(file));
            coordinateMarking.setFont(Font.font(24));
            coordinateMarking.setX(s + 1.5 * s * xBoard - 6);
            coordinateMarking.setY(h * 1.5 + h / 2 * Position.distanceFromEdge(new Position(xBoard, 0), d));
            coordinateMarking.setTextAlignment(TextAlignment.CENTER);
            boardAndCoordinatesView.getChildren().add(coordinateMarking);
        }

        // create coordinate markings: y markings
        for (int yBoard = 0; yBoard < d; yBoard++) {
            Text leftCoordinateMarking = new Text(Integer.toString(yBoard + 1));
            Text rightCoordinateMarking = new Text(Integer.toString(yBoard + 1));
            leftCoordinateMarking.setFont(Font.font(24));
            rightCoordinateMarking.setFont(Font.font(24));
            leftCoordinateMarking.setTextAlignment(TextAlignment.RIGHT); // for some reason this doesn't work
            rightCoordinateMarking.setTextAlignment(TextAlignment.LEFT);

            int dim = game.getBoard().getBoardDim();

            if (yBoard <= dim-1) {
                if (yBoard <= 8)
                    leftCoordinateMarking.setX(-12);
                else
                    leftCoordinateMarking.setX(-24);
                leftCoordinateMarking.setY(h * 0.25 - h * yBoard);

                rightCoordinateMarking.setX(0.4 * s + 1.5 * s * d);
                rightCoordinateMarking.setY(h * 0.25 - h * yBoard);
            } else {
                if (yBoard <= 8)
                    leftCoordinateMarking.setX(-12 + (yBoard - (dim-1)) * (s * 1.5));
                else
                    leftCoordinateMarking.setX(-24 + (yBoard - (dim-1)) * (s * 1.5));
                leftCoordinateMarking.setY(h * 0.25 - h * (dim-1) - h/2 * (yBoard - (dim-1)));

                rightCoordinateMarking.setX(0.4 * s + 1.5 * s * d - (yBoard - (dim-1)) * (s * 1.5));
                rightCoordinateMarking.setY(h * 0.25 - h * (dim-1) - h/2 * (yBoard - (dim-1)));
            }

            boardAndCoordinatesView.getChildren().add(leftCoordinateMarking);
            boardAndCoordinatesView.getChildren().add(rightCoordinateMarking);
        }

        // render initial pieces
        renderPieces();

        // build sidebar with background
        sidebar = new VBox(16);
        sidebar.setAlignment(Pos.CENTER_LEFT);
        sidebar.setMaxHeight(200);
        sidebar.setBackground(new Background(new BackgroundFill(Color.web("#e3e3e3"), new CornerRadii(20), new Insets(-20))));
        root.getChildren().add(sidebar);

        // add game info
        moveNumber = new Text();
        moveNumber.setFont(Font.font(22));

        currentPlayer = new Text();
        currentPlayer.setFont(Font.font(22));

        gameStatus = new Text();
        gameStatus.setFont(Font.font(22));

        sidebar.getChildren().addAll(moveNumber, currentPlayer, gameStatus);

        Text capturesHeading = new Text("Captures:");
        capturesHeading.setFont(Font.font(22));
        sidebar.getChildren().add(capturesHeading);

        for (Piece.Color color : game.getActiveColors()) {
            Text capturedPiecesColor = new Text(color.toStringCapitalised() + ":");
            capturedPiecesColor.setFont(Font.font(16));

            TextFlow capturedPiecesText = new TextFlow(capturedPiecesColor);
            capturedPiecesText.setTextAlignment(TextAlignment.LEFT);
            capturedPiecesText.setMaxWidth(250);
            capturedPiecesTexts.put(color, capturedPiecesText);
            sidebar.getChildren().add(capturedPiecesText);
        }

        if (timerEnabled) {
            if (game.getGameTimer().getIncrementSecWhite() == game.getGameTimer().getIncrementSecBlack() &&
                game.getGameTimer().getStartingTimeSecWhite() == game.getGameTimer().getStartingTimeSecBlack())
                timerSettingsDesc = new Text("Time setting: " + game.getGameTimer().getStartingTimeSecWhite()/60 + "m+" + game.getGameTimer().getIncrementSecWhite() + "s");
            else {
                timerSettingsDesc = new Text("Time setting: " + game.getGameTimer().getStartingTimeSecWhite()/60 + "m+" + game.getGameTimer().getIncrementSecWhite() + "s"
                    + "/" + game.getGameTimer().getStartingTimeSecBlack()/60 + "m+" + game.getGameTimer().getIncrementSecBlack() + "s");
            }
            timerSettingsDesc.setFont(Font.font(22));
            timeRemainingWhite = new Text();
            timeRemainingWhite.setFont(Font.font(16));
            timeRemainingBlack = new Text();
            timeRemainingBlack.setFont(Font.font(16));
        }

//        VBox wins = new VBox(5);
//        Text heading1 = new Text("Games won by:");
//        Text heading2 = new Text("White   Black");
//        gameWins = new Text();
//        heading1.setFont(Font.font(22));
//        heading2.setFont(Font.font(16));
//        gameWins.setFont(Font.font(16));
//        wins.getChildren().addAll(heading1, heading2, gameWins);
//
//        sidebar.getChildren().add(wins);

        renderGameInfo();

        if (timerEnabled) {
            sidebar.getChildren().add(timerSettingsDesc);
            sidebar.getChildren().add(timeRemainingWhite);
            sidebar.getChildren().add(timeRemainingBlack);
        }

        // add game buttons
        VBox gameButtons = new VBox(5);
        HBox gameButtonsRow1 = new HBox(5);
        HBox gameButtonsRow2 = new HBox(5);
        Button resign = new Button("Resign");
        Button restart = new Button("Restart");
        Button restartScores = new Button("Restart scores");
        Button claimDraw = new Button("Claim draw");
        Button changeSettings = new Button("Change settings");
        Button undo = new Button("Undo");
        gameButtonsRow1.getChildren().addAll(resign, restart, restartScores);
        gameButtonsRow2.getChildren().addAll(claimDraw, changeSettings, undo);
        gameButtons.getChildren().addAll(gameButtonsRow1, gameButtonsRow2);
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

        restartScores.setOnAction(e -> {
            game.restartScores();
            renderGameInfo();
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

        changeSettings.setOnAction(e -> {
            timerEnabled = false;
            this.primaryStage.setScene(new Scene(createTitleScreen()));
        });

        undo.setOnAction(e -> {
            if (!game.undo()) {
                if (showingTempMessage)
                    sidebar.getChildren().remove(temporaryMessage);

                temporaryMessage = new Text("Cannot undo");
                temporaryMessage.setFont(Font.font(22));
                temporaryMessage.setFill(Color.RED);
                sidebar.getChildren().add(temporaryMessage);
                showingTempMessage = true;
            }

            renderPieces();
            renderGameInfo();
            clearHighlightingAndRehighlightCheck();
        });

        return root;
    }

    // After an update to the underlying Game, i.e. piece position update, render pieces
    private void renderPieces() {
        int d = game.getBoard().getBoardDiameter();
        double h = BOARD_SIZE/d;                       // tile height
        double s = BoardTile.fullHeightToSideLength(h); // tile side length

        // Remove all current piece images
        for (int xBoard = 0; xBoard < d; xBoard++) {
            for (int yBoard = 0; yBoard < d; yBoard++) {
                if (pieceViewsAsArray[xBoard][yBoard] != null)  {
                    tileStacksAsArray[xBoard][yBoard].getChildren().remove(pieceViewsAsArray[xBoard][yBoard]);
                    pieceViewsAsArray[xBoard][yBoard] = null;
                }
            }
        }

        // go back over the board and rebuild pieces array
        for (int xBoard = 0; xBoard < d; xBoard++) {
            for (int yBoard = 0; yBoard < d; yBoard++) {
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
        currentPlayer.setText("Current player: " + game.getCurrentPlayer().toStringCapitalised());
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
            case FLAGGED -> {
                gameStatus.setText("Game status: " + (game.getCurrentPlayer() == Piece.Color.WHITE ? "Black" : "White") + "\nwins by timeout!");
            }
            case CONTINUING -> {
                gameStatus.setText("Game status: continuing");
            }
        }

        for (Piece.Color color : game.getActiveColors()) {
            TextFlow capturedPieceText = capturedPiecesTexts.get(color);

            while (!capturedPieceText.getChildren().isEmpty())
                capturedPieceText.getChildren().removeFirst();

            Text capturedPiecesColor = new Text(color.toStringCapitalised() + ": ");
            capturedPiecesColor.setFont(Font.font(16));
            capturedPieceText.getChildren().add(capturedPiecesColor);

            List<Piece> capturedPieces = game.getBoard().getCapturedPieces().get(color);
            for (int i = 0; i < capturedPieces.size(); i++) {
                Piece piece = capturedPieces.get(i);
                Text capturedPieceIcon = new Text(Character.toString(piece.getPieceIcon()));
                capturedPieceIcon.setFont(Font.font(16));
                capturedPieceIcon.setFill(Color.web(piece.getColor().getPieceColorAsHex()));
                capturedPieceText.getChildren().add(capturedPieceIcon);

                if (i != capturedPieces.size()-1) {
                    Text comma = new Text(", ");
                    comma.setFont(Font.font(16));
                    capturedPieceText.getChildren().add(comma);
                }
            }

            capturedPieceText.getChildren().add(new Text(" "));
        }

//        gameWins.setText(game.getWhitePoints() + "        " + game.getBlackPoints());

        if (timerEnabled) {
            timeRemainingWhite.setText("White time remaining: " + game.getGameTimer().getTimeRemainingAsString(Piece.Color.WHITE));
            timeRemainingBlack.setText("Black time remaining: " + game.getGameTimer().getTimeRemainingAsString(Piece.Color.BLACK));
        }
    }

    private void renderTimers() {

    }

    private void renderPromotionMenu(Position promoteablePawn) {
        handlingPromotion = true;

        Piece.Color playerColor = this.game.getBoard().getPos(promoteablePawn).color;
        int d = game.getBoard().getBoardDiameter();

        Position promotionQueenPos, promotionBishopPos, promotionKnightPos, promotionRookPos;
        promotionQueenPos = promoteablePawn;
        promotionBishopPos = Pawn.getDirections(playerColor).get(Pawn.Direction.BACK_LEFT).apply(promoteablePawn, d);
        promotionKnightPos = Pawn.getDirections(playerColor).get(Pawn.Direction.BACK_RIGHT).apply(promoteablePawn, d);
        promotionRookPos = Pawn.getDirections(playerColor).get(Pawn.Direction.BACKWARD).apply(promoteablePawn, d);

        if (!promotionBishopPos.isInBounds(d)) {
            promotionBishopPos = Pawn.getDirections(playerColor).get(Pawn.Direction.CAPTURE_RIGHT).apply(promoteablePawn, d);
        }
        if (!promotionKnightPos.isInBounds(d)) {
            promotionKnightPos = Pawn.getDirections(playerColor).get(Pawn.Direction.CAPTURE_LEFT).apply(promoteablePawn, d);
        }

        PieceView promotionQueen = new PieceView(PieceType.QUEEN, playerColor, promotionQueenPos);
        PieceView promotionBishop = new PieceView(PieceType.BISHOP, playerColor, promotionBishopPos);
        PieceView promotionKnight = new PieceView(PieceType.KNIGHT, playerColor, promotionKnightPos);
        PieceView promotionRook = new PieceView(PieceType.ROOK, playerColor, promotionRookPos);

        // highlight the promotion tiles and put the promotion objects on them
        for (PieceView piece : List.of(promotionQueen, promotionBishop, promotionKnight, promotionRook)) {
            double h = BOARD_SIZE/d;
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
        int d = this.game.getBoard().getBoardDiameter();

        Position promotionQueenPos, promotionBishopPos, promotionKnightPos, promotionRookPos;
        promotionQueenPos = promoteablePawn;
        promotionBishopPos = Pawn.getDirections(playerColor).get(Pawn.Direction.BACK_LEFT).apply(promoteablePawn, d);
        promotionKnightPos = Pawn.getDirections(playerColor).get(Pawn.Direction.BACK_RIGHT).apply(promoteablePawn, d);
        promotionRookPos = Pawn.getDirections(playerColor).get(Pawn.Direction.BACKWARD).apply(promoteablePawn, d);

        if (!promotionBishopPos.isInBounds(d)) {
            promotionBishopPos = Pawn.getDirections(playerColor).get(Pawn.Direction.CAPTURE_RIGHT).apply(promoteablePawn, d);
        }
        if (!promotionKnightPos.isInBounds(d)) {
            promotionKnightPos = Pawn.getDirections(playerColor).get(Pawn.Direction.CAPTURE_LEFT).apply(promoteablePawn, d);
        }

        for (Position pos : List.of(promotionQueenPos, promotionBishopPos, promotionKnightPos, promotionRookPos)) {
            // set the handlers for the board tiles back to normal
            boardTilesAsArray[pos.file][pos.rank].setOnMouseClicked(e -> { handleClickOnTile(pos.file, pos.rank); });
        }

        renderPieces();
        renderGameInfo();

        // remove promotion highlighting and then rehighlight the king if it's in check
        clearHighlightingAndRehighlightCheck();
    }

    private void closePromotionMenu() {
        // re-render pieces
        renderPieces();

        // remove promotion highlighting and then rehighlight the king if it's in check
        clearHighlightingAndRehighlightCheck();
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

            clearHighlightingAndRehighlightCheck();
        }
        // if the player is clicking on a position that was highlighted after a previous selection, move the selected
        // piece to that position
        else if (boardTilesAsArray[x][y].getHighlight() == BoardTile.Highlight.NORMAL ||
                boardTilesAsArray[x][y].getHighlight() == BoardTile.Highlight.CAPTURE) {
            Position fromPos = selectedPos;
            Position toPos = clickedTilePos;

            // clear selection and highlighting (including king)
            this.selectedPos = null;

            clearHighlightingAll();

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
                Position kingPos = game.getBoard().getKingPositions().get(game.getCurrentPlayer());

                boardTilesAsArray[kingPos.file][kingPos.rank].setHighlight(BoardTile.Highlight.CHECK);
            }
        }
        else if (game.getBoard().getPos(clickedTilePos) != null && game.getBoard().getPos(clickedTilePos).color == game.getCurrentPlayer()) {
            this.selectedPos = clickedTilePos;

            clearHighlightingAndRehighlightCheck();

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
        int d = game.getBoard().getBoardDiameter();

        for (int x2 = 0; x2 < d; x2++) {
            for (int y2 = 0; y2 < d; y2++) {
                if (boardTilesAsArray[x2][y2] != null)
                    boardTilesAsArray[x2][y2].setHighlight(BoardTile.Highlight.NONE);
            }
        }
    }

    public void clearHighlightingAndRehighlightCheck() {
        clearHighlightingAll();
        if (game.getBoard().isKingInCheck(game.getCurrentPlayer())) {
            Position kingPos = game.getBoard().getKingPositions().get(game.getCurrentPlayer());

            boardTilesAsArray[kingPos.file][kingPos.rank].setHighlight(BoardTile.Highlight.CHECK);
        }
    }



    public static void main(String[] args) {
        launch(args);
    }

}
