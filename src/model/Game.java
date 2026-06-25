package model;

import model.piece.Pawn;
import model.piece.Piece;

import java.util.*;

public class Game {
    // points for each player, tracked across several matches
    Map<Piece.Color, Double> points = new HashMap<>();
    int movesSinceCaptureOrPawnMovement = 0;
    int moveNumberForCurrentSide = 1;

    public enum Mode {
        TWO_PLAYER,
        THREE_PLAYER,
        SIX_PLAYER
    }

    Board board;
    Mode mode;
    List<Piece.Color> activeColors = new ArrayList<>();
    Map<Piece.Color, PlayerStatus> eliminatedColors = new HashMap<>();

    public List<Piece.Color> getActiveColors() {
        return activeColors;
    }

    public Map<Piece.Color, PlayerStatus> getEliminatedColors() {
        return eliminatedColors;
    }

    // for checking repetition
    Deque<Board> previousBoards = new ArrayDeque<>();
    static final int repetitionCheckMaxWindow = 20;

    Piece.Color currentPlayer = Piece.Color.WHITE;
    GameResult currentGameState;

    // for handling timing logic
    GameTimer gameTimer;

    public GameTimer getGameTimer() {
        return gameTimer;
    }

    public void setGameTimer(GameTimer gameTimer) {
        this.gameTimer = gameTimer;
    }

    public enum GameResult {
        CONTINUING,
        DRAW,
        FINISHED
    }

    public enum PlayerStatus {
        ACTIVE,
        STALEMATE,
        CHECKMATE,
        FLAGGED,
        RESIGNED
    }

    public Game() {
        board = new Board();
        mode = Mode.TWO_PLAYER;
        currentGameState = GameResult.CONTINUING;
        activeColors.addAll(List.of(Piece.Color.WHITE, Piece.Color.BLACK));
        for (Piece.Color color : activeColors) {
            points.put(color, 0.0);
        }
    }

    public Game(Mode mode) {
        this.mode = mode;
        board = new Board(mode);
        currentGameState = GameResult.CONTINUING;
        activeColors.addAll(switch(mode) {
            case TWO_PLAYER -> List.of(Piece.Color.WHITE, Piece.Color.BLACK);
            case THREE_PLAYER -> List.of(Piece.Color.WHITE, Piece.Color.RED, Piece.Color.BLUE);
            case SIX_PLAYER -> List.of(Piece.Color.WHITE, Piece.Color.GREEN, Piece.Color.RED,
                                       Piece.Color.YELLOW, Piece.Color.BLUE, Piece.Color.PURPLE);
        });
        for (Piece.Color color : activeColors) {
            points.put(color, 0.0);
        }
    }

    // testing constructors
    public Game(List<String> pieces) {
        this(pieces, 6, List.of(Piece.Color.WHITE, Piece.Color.BLACK));
    }

    public Game(List<String> pieces, int boarddim, List<Piece.Color> activeColors) {
        board = new Board(pieces, boarddim);
        currentGameState = GameResult.CONTINUING;
        this.activeColors.addAll(activeColors);
        for (Piece.Color color : activeColors) {
            points.put(color, 0.0);
        }
        switch (activeColors.size()) {
            case 2 -> { mode = Mode.TWO_PLAYER; }
            case 3 -> { mode = Mode.THREE_PLAYER; }
            case 6 -> { mode = Mode.SIX_PLAYER; }
        }
    }

    public int getMoveNumberForCurrentSide() {
        return moveNumberForCurrentSide;
    }

    @Override
    public String toString() {
        StringBuilder string = new StringBuilder();

        string.append("Current board:\n");
        string.append(board);
        string.append("Current player: " + (currentPlayer.toStringCapitalised()));
        //string.append("Points are white: " + whitePoints + " and Black: " + blackPoints);
        if (this.board.isKingInCheck(currentPlayer)) {
            string.append("You are in check!\n");
        }

        return string.toString();
    }

    public Set<Move> getLegalMovesFromPos(Position pos) {
        return board.getLegalMovesFromPos(pos);
    }

    public MoveResult applyMove(Move move) {
        boolean pawnMovementOrCapture = board.getPos(move.fromPos) instanceof Pawn || board.getPos(move.toPos) != null;

        // before applying the move, save the board state
        Board boardStateCopy = new Board(this.board);

        MoveResult result = board.applyMove(move, currentPlayer);

        if (result.validMove) {
            // update time since last pawn movement/capture
            if (pawnMovementOrCapture) movesSinceCaptureOrPawnMovement = 0;
            else movesSinceCaptureOrPawnMovement++;

            // update move number; only update once the last player in the list of active players has had its turn
            moveNumberForCurrentSide += currentPlayer == activeColors.getLast() ? 1 : 0;

            // update current player (cycle to next player)
            Piece.Color previousPlayer = currentPlayer;
            currentPlayer = activeColors.get((activeColors.indexOf(previousPlayer) + 1) % activeColors.size());

            // update timer
            if (gameTimer != null) {
                gameTimer.updateTimersPlayerChange(previousPlayer, currentPlayer);

                // detect loss due to flagging (running out of time)
                if (gameTimer.isOutOfTime(previousPlayer)) {
                    activeColors.remove(previousPlayer);
                    eliminatedColors.put(previousPlayer, PlayerStatus.FLAGGED);
                    if (activeColors.size() != 1) board.eliminatePlayer(previousPlayer);
                }
            }

            // update list of previous boards
            previousBoards.add(boardStateCopy);
            if (previousBoards.size() > repetitionCheckMaxWindow) {
                previousBoards.remove(); // throw away any boards which are older than 40 moves to limit memory usage
            }
        }

        return result;
    }

    public void handlePromotion(Position promotetablePawn, PieceType promotionChoice) {
        this.board.handlePromotion(promotetablePawn, promotionChoice);
    }

    // check for player elimination
    public PlayerStatus checkIfCurrentPlayerEliminated() {
        PlayerStatus result;

        if (board.isInCheckmate(currentPlayer)) {
            result = PlayerStatus.CHECKMATE;
        } else if (board.isInStalemate(currentPlayer)) {
            result = PlayerStatus.STALEMATE;
        } else {
            return PlayerStatus.ACTIVE;
        }

        // fall through means that the current player is eliminated
        activeColors.remove(currentPlayer);
        eliminatedColors.put(currentPlayer, result);
        if (activeColors.size() != 1) board.eliminatePlayer(currentPlayer);
        return result;
    }

    // check if game end
    public GameResult checkIfGameEnd() {
        GameResult result;
        Piece.Color winningPlayer;

        if (activeColors.size() == 1) {
            result = GameResult.FINISHED;
            winningPlayer = activeColors.getFirst();

            // if timer enabled, stop the timing
            if (this.gameTimer != null) {
                this.gameTimer.stopTimer();
            }

            points.put(winningPlayer, points.get(winningPlayer)+1);

            currentGameState = result;
            return result;
        } else if (checkIfForcedDraw()) {
            result = GameResult.DRAW;
            handleDraw();
        } else {
            result = GameResult.CONTINUING;
            return result;
        }

        // if timer enabled, stop the timing
        if (this.gameTimer != null) {
            this.gameTimer.stopTimer();
        }

        currentGameState = result;
        return result;
    }

    public void updateTimer(long now) {
        if (gameTimer != null) {
            gameTimer.updateTimers(now, this.currentPlayer);

            // detect loss due to flagging (running out of time)
            if (gameTimer.isOutOfTime(this.currentPlayer)) {
                activeColors.remove(currentPlayer);
                eliminatedColors.put(currentPlayer, PlayerStatus.FLAGGED);
                if (activeColors.size() != 1) board.eliminatePlayer(currentPlayer);
            }
        }
    }

    public void resetPoints() {
        for (Piece.Color color : points.keySet()) {
            points.put(color, 0.0);
        }
    }

    public void restartGame() {
        currentPlayer = Piece.Color.WHITE;
        activeColors = new ArrayList<>();
        activeColors.addAll(switch(mode) {
            case TWO_PLAYER -> List.of(Piece.Color.WHITE, Piece.Color.BLACK);
            case THREE_PLAYER -> List.of(Piece.Color.WHITE, Piece.Color.RED, Piece.Color.BLUE);
            case SIX_PLAYER -> List.of(Piece.Color.WHITE, Piece.Color.GREEN, Piece.Color.RED,
                    Piece.Color.YELLOW, Piece.Color.BLUE, Piece.Color.PURPLE);
        });
        eliminatedColors = new HashMap<>();
        this.board = new Board(this.mode);

        movesSinceCaptureOrPawnMovement = 0;
        while (!previousBoards.isEmpty()) {
            previousBoards.remove();
        }
        moveNumberForCurrentSide = 1;
        currentGameState = GameResult.CONTINUING;
        if (this.gameTimer != null) {
            this.gameTimer.startOrRestartTimer();
        }
    }

    public void resign() {
        int index = activeColors.indexOf(currentPlayer);
        boolean lastPlayerInSequence = index == activeColors.size()-1;
        activeColors.remove(currentPlayer);
        eliminatedColors.put(currentPlayer, PlayerStatus.RESIGNED);
        if (activeColors.size() != 1) board.eliminatePlayer(currentPlayer);

        // change player to next player
        currentPlayer = activeColors.get(lastPlayerInSequence ? 0 : index);

        // note: checkIfGameEnd and restartGame have to be called seperately
    }

    // forced draws include:
    // - draw by repetition (repeat 5 times)
    // - draw by 75 moves without captures or pawn movements
    // - draw by insufficient material
    public boolean checkIfForcedDraw() {
        if (movesSinceCaptureOrPawnMovement >= 2*75) {
            return true;
        }

        // check for fivefold repetition
        int repTimes = 0;
        for (Board previousBoard : previousBoards) {
            if (this.board.equals(previousBoard)) repTimes++;
        }

        if (repTimes >= 4) return true;

        return false;
    }

    // method to be called when a player wants to claim draw (as opposed to forced draw)
    // returns true if claim is successful
    // claimable draws include:
    // - draw by repetition (repeat 3 times)
    // - draw by 50 moves without captures or pawn movements
    public boolean claimDraw() {
        if (movesSinceCaptureOrPawnMovement >= 2*50) {
            handleDraw();
            return true;
        }

        // check for threefold repetition of the current position
        int repTimes = 0;
        for (Board previousBoard : previousBoards) {
            if (this.board.equals(previousBoard)) repTimes++;
        }

        if (repTimes >= 2) {
            handleDraw();
            return true;
        }

        return false;
    }

    public void handleDraw() {
        this.currentGameState = GameResult.DRAW;
        for (Piece.Color remainingColor : activeColors) {
            points.put(remainingColor, points.get(remainingColor)+0.5);
        }
        if (this.gameTimer != null) {
            this.gameTimer.stopTimer();
        }
    }

    public Map<Piece.Color, Double> getPoints() {
        return points;
    }

    public Board getBoard() {
        return board;
    }

    public Piece.Color getCurrentPlayer() {
        return currentPlayer;
    }

    public GameResult getCurrentGameState() {
        return currentGameState;
    }

    // returns true if undo is successful
    public boolean undo() {
        if (previousBoards.isEmpty()) { return false; }

        moveNumberForCurrentSide += currentPlayer == activeColors.getLast() ? 1 : 0;

        this.board = previousBoards.removeLast();

        // update current player (cycle to next player)
        Piece.Color previousPlayer = currentPlayer;
        currentPlayer = activeColors.get((activeColors.indexOf(previousPlayer) - 1 + activeColors.size()) % activeColors.size());

        // update move number
        moveNumberForCurrentSide -= currentPlayer == Piece.Color.WHITE ? 0 : 1;

        // update game status
        if (currentGameState != GameResult.CONTINUING) {
            currentGameState = GameResult.CONTINUING;
        }

        return true;
    }

    public void restartScores() {
        for (Piece.Color color : points.keySet()) {
            points.put(color, 0.0);
        }
    }
}
