package model;

import model.piece.King;
import model.piece.Pawn;
import model.piece.Piece;

import java.lang.Math;
import java.util.*;
import java.util.function.BiFunction;

/**
 * Class for tracking the board and the pieces on the board
 */
public class Board {
    int boardDim; // side length of the board in tiles
    int boardDiameter; // most importantly, determines the max height and width of the board

    Piece[][] board;

    // references to all pieces in the game (for check/checkmate detection)
    Map<Piece.Color, Set<Position>> piecePositions = new HashMap<>();
    Map<Piece.Color, List<Piece>> capturedPieces = new HashMap<>();
    Map<Piece.Color, Position> kingPositions = new HashMap<>();

    // for tracking en passant
    public Map<Piece.Color, Position> passantablePawns = new HashMap<>();

    public Map<Piece.Color, Position> getPassantablePawns() {
        return passantablePawns;
    }

    public int getBoardDim() {
        return boardDim;
    }

    public int getBoardDiameter() {
        return boardDiameter;
    }

    public Piece getPos(Position pos) {
        return this.board[pos.file][pos.rank];
    }

    void setPos(Position pos, Piece piece) {
        this.board[pos.file][pos.rank] = piece;
    }

    public Map<Piece.Color, Position> getKingPositions() {
        return kingPositions;
    }

    public Map<Piece.Color, List<Piece>> getCapturedPieces() {
        return capturedPieces;
    }

    public Board() {
        this(Game.Mode.TWO_PLAYER);
    }

    // default constructor; initialise board in starting position
    public Board(Game.Mode mode) {
        List<Piece.Color> colors;
        boolean extraPieces = false;

        if (mode == Game.Mode.TWO_PLAYER) {
            boardDim = 6;
            boardDiameter = 2*boardDim - 1;

            board = new Piece[boardDiameter][boardDiameter];

            colors = List.of(Piece.Color.WHITE, Piece.Color.BLACK);

        } else if (mode == Game.Mode.THREE_PLAYER) {
            boardDim = 8;
            boardDiameter = 2*boardDim - 1;

            board = new Piece[boardDiameter][boardDiameter];

            colors = List.of(Piece.Color.WHITE, Piece.Color.RED, Piece.Color.BLUE);
            extraPieces = true;

        } else { // (mode == Game.Mode.SIX_PLAYER)
            boardDim = 11;
            boardDiameter = 2*boardDim - 1;

            board = new Piece[boardDiameter][boardDiameter];

            colors = List.of(Piece.Color.WHITE, Piece.Color.GREEN, Piece.Color.RED,
                    Piece.Color.YELLOW, Piece.Color.BLUE, Piece.Color.PURPLE);
            extraPieces = true;
        }

        // generate the pieces for each color
        for (Piece.Color color : colors) {
            Position boardCenter = new Position(this.boardDim-1, this.boardDim-1);

            Map<Pawn.Direction, BiFunction<Position, Integer, Position>> directions = Pawn.getDirections(color);

            // get the position which is the bottom corner of the formation
            Position cornerPos = boardCenter;
            Position nextPos = directions.get(Pawn.Direction.BACKWARD).apply(cornerPos, boardDiameter);
            while (nextPos.isInBounds(boardDiameter)) {
                cornerPos = nextPos;
                nextPos = directions.get(Pawn.Direction.BACKWARD).apply(cornerPos, boardDiameter);
            }

            // derive all other piece positions as offsets from the bottom corner
            Position bishop1Pos = cornerPos;
            Position bishop2Pos = directions.get(Pawn.Direction.FORWARD).apply(bishop1Pos, boardDiameter);
            Position bishop3Pos = directions.get(Pawn.Direction.FORWARD).apply(bishop2Pos, boardDiameter);
            Position queenPos = directions.get(Pawn.Direction.FORWARD_LEFT).apply(bishop1Pos, boardDiameter);
            Position lknightPos = directions.get(Pawn.Direction.FORWARD_LEFT).apply(queenPos, boardDiameter);
            Position lrookPos = directions.get(Pawn.Direction.FORWARD_LEFT).apply(lknightPos, boardDiameter);
            Position kingPos = directions.get(Pawn.Direction.FORWARD_RIGHT).apply(bishop1Pos, boardDiameter);
            Position rknightPos = directions.get(Pawn.Direction.FORWARD_RIGHT).apply(kingPos, boardDiameter);
            Position rrookPos = directions.get(Pawn.Direction.FORWARD_RIGHT).apply(rknightPos, boardDiameter);

            Position extraLKnightPos = directions.get(Pawn.Direction.FORWARD_LEFT).apply(bishop3Pos, boardDiameter);
            Position extraLRookPos = directions.get(Pawn.Direction.FORWARD_RIGHT).apply(lrookPos, boardDiameter);
            Position extraRKnightPos = directions.get(Pawn.Direction.FORWARD_RIGHT).apply(bishop3Pos, boardDiameter);
            Position extraRRookPos = directions.get(Pawn.Direction.FORWARD_LEFT).apply(rrookPos, boardDiameter);

            // get pawn positions, starting from leftmost pawn and working inwards
            Position leftPawn = directions.get(Pawn.Direction.FORWARD_LEFT).apply(lrookPos, boardDiameter);
            Position rightPawn = directions.get(Pawn.Direction.FORWARD_RIGHT).apply(rrookPos, boardDiameter);
            List<Position> pawnPositions = new ArrayList<>(List.of(leftPawn, rightPawn));
            for (int i = 0; i < 4; i++) {
                leftPawn = directions.get(Pawn.Direction.FORWARD_RIGHT).apply(leftPawn, boardDiameter);
                rightPawn = directions.get(Pawn.Direction.FORWARD_LEFT).apply(rightPawn, boardDiameter);
                pawnPositions.addAll(List.of(leftPawn, rightPawn));
            }

            this.setPos(bishop1Pos, PieceFactory.createPiece(PieceType.BISHOP, color));
            this.setPos(bishop2Pos, PieceFactory.createPiece(PieceType.BISHOP, color));
            this.setPos(bishop3Pos, PieceFactory.createPiece(PieceType.BISHOP, color));
            this.setPos(queenPos, PieceFactory.createPiece(PieceType.QUEEN, color));
            this.setPos(lknightPos, PieceFactory.createPiece(PieceType.KNIGHT, color));
            this.setPos(lrookPos, PieceFactory.createPiece(PieceType.ROOK, color));
            this.setPos(kingPos, PieceFactory.createPiece(PieceType.KING, color));
            this.setPos(rknightPos, PieceFactory.createPiece(PieceType.KNIGHT, color));
            this.setPos(rrookPos, PieceFactory.createPiece(PieceType.ROOK, color));

            if (extraPieces) {
                this.setPos(extraLKnightPos, PieceFactory.createPiece(PieceType.KNIGHT, color));
                this.setPos(extraLRookPos, PieceFactory.createPiece(PieceType.ROOK, color));
                this.setPos(extraRKnightPos, PieceFactory.createPiece(PieceType.KNIGHT, color));
                this.setPos(extraRRookPos, PieceFactory.createPiece(PieceType.ROOK, color));
            }

            for (Position pawnPos : pawnPositions) {
                this.setPos(pawnPos, PieceFactory.createPiece(PieceType.PAWN, color));
            }

            Set<Position> piecePositionsForThisColor = new HashSet<>(List.of(bishop1Pos, bishop2Pos, bishop3Pos,
                    queenPos, lknightPos, lrookPos, kingPos, rknightPos, rrookPos));
            piecePositionsForThisColor.addAll(pawnPositions);
            if (extraPieces) piecePositionsForThisColor.addAll(List.of(extraLKnightPos, extraLRookPos, extraRKnightPos, extraRRookPos));

            this.piecePositions.put(color, piecePositionsForThisColor);

            capturedPieces.put(color, new ArrayList<>());

            kingPositions.put(color, kingPos);
        }
    }


    public Board(List<String> pieces) {
        this(pieces, 6);
    }

    // testing constructor; intiialises a board with the pieces given as strings, e.g.:
    // Nh6 is a white knight at position h6
    // pc7 is a black pawn at position c7
    public Board(List<String> pieces, int boardDim) {
        this.boardDim = boardDim;
        this.boardDiameter = 2*boardDim - 1;

        board = new Piece[boardDiameter][boardDiameter];

        for (Piece.Color color : Piece.allColors) {
            piecePositions.put(color, new HashSet<>());

            capturedPieces.put(color, new ArrayList<>());
        }

        for (String piece : pieces) {
            String color, pieceType, piecePos;
            String pieceWithoutNumber = piece.replaceAll("[0-9]", "");
            if (pieceWithoutNumber.length() == 2) {
                color = Character.isUpperCase(piece.charAt(0)) ? "w" : "b";
                pieceType = Character.toString(piece.charAt(0));
                piecePos = piece.substring(1);
            } else {
                color = Character.toString(piece.charAt(0));
                pieceType = Character.toString(piece.charAt(1));
                piecePos = piece.substring(2);
            }

            Piece pieceAsObj = PieceFactory.createPiece(pieceType, color);
            Position posAsObj = new Position(piecePos);
            this.setPos(posAsObj, pieceAsObj);
            piecePositions.get(pieceAsObj.color).add(posAsObj);
            if (pieceAsObj instanceof King)
                kingPositions.put(pieceAsObj.color, posAsObj);
        }
    }

    // deep copy constructor; given a board, create a copy
    // explicitly create the fields with new/createPiece to avoid just having a reference to the object referred to
    // by the original fields
    public Board(Board boardOriginal) {
        boardDim = boardOriginal.boardDim;
        boardDiameter = boardOriginal.boardDiameter;
        board = new Piece[boardOriginal.boardDiameter][boardOriginal.boardDiameter];

        for (int x = 0; x < this.boardDiameter; x++) {
            for (int y = 0; y < this.boardDiameter; y++) {
                Position pos = new Position(x, y);
                Piece originalPiece = boardOriginal.getPos(pos);

                if (originalPiece != null) {
                    Piece copyPiece = PieceFactory.createPiece(originalPiece.getPieceType(), originalPiece.color);
                    this.setPos(pos, copyPiece);
                }
            }
        }

        for (Piece.Color color : boardOriginal.piecePositions.keySet()) {
            Set<Position> piecePositionsForColor = boardOriginal.piecePositions.get(color);
            List<Piece> capturedPiecesForColor = boardOriginal.capturedPieces.get(color);

            this.piecePositions.put(color, new HashSet<>());
            this.capturedPieces.put(color, new ArrayList<>());

            for (Position pos : piecePositionsForColor) {
                this.piecePositions.get(color).add(new Position(pos));
            }
            for (Piece piece : capturedPiecesForColor) {
                this.capturedPieces.get(color).add(PieceFactory.createPiece(piece.getPieceType(), piece.color));
            }

            if (boardOriginal.kingPositions.containsKey(color)) {
                this.kingPositions.put(color, new Position(boardOriginal.kingPositions.get(color)));
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder string = new StringBuilder();
        int xScale = 6;

        // print coordinates at top of board
        for (int c = 0; c < boardDiameter; c++) {
            if (c == 0) string.repeat(" ", xScale/2);
            else string.repeat(" ", xScale-1);

            string.append((char) ('a' + c));

            if (c == boardDiameter - 1) string.repeat(" ", xScale/2);
        }
        string.append('\n');

        // main loop
        for (int y = boardDiameter*2 - 1; y >= 0; y--) { // go in reverse so that white is at bottom of board
            for (int x = 0; x < boardDiameter * xScale; x++) {
                int xBoard = x/xScale;
                float yBoard = ((float) (y - Math.abs(xBoard - (boardDim-1))))/2; // if this isn't a float,
                                                                                  // there ends up being half-cells
                                                                                  // at the bottom of the board for some reason

                // if yBoard is off the bounds of the board, print blank
                if (yBoard < 0 || yBoard >= boardDiameter - Math.abs(xBoard - (boardDim-1))) {
                    string.append(' ');
                    continue;
                }

                // NOTE THAT THE BELOW ONLY WORKS FOR xScale == 6
                // get the coordinate within each 2x6 size hexagon cell. how y % 2 corresponds to the bottom/top
                // of the cell alternates with each cell going horizontally, hence why both y % 2 and xBoard % 2
                // are involved in the calculation. the parity also depends on if boardDim is odd or even.
                int xCell = x % xScale;
                boolean isBottomOfCell = ((y % 2 == 1) ^ (xBoard % 2 == 1)) ^ (boardDim % 2 == 1);

                // for x coordinates in the cell that aren't the left/right walls, print an underscore or overline
                // to indicate the tops/bottoms of cells
                if (xCell != 0 && xCell != xScale/2) {
                    if (isBottomOfCell) string.append('_');
                    else string.append('‾');
                    continue;
                }

                // for coordinates in the cell that are the left wall, print a / or an \ to indicate the left wall of
                // the cell. note that non-rightmost cells share their right wall with the left wall of the neighbouring
                // cell, so this also covers most right walls.
                if (xCell == 0) {
                    if (isBottomOfCell) string.append('\\');
                    else string.append('/');
                } else {
                    // else xCell = xScale/2, i.e. the middle of the cell

                    Piece piece = board[xBoard][(int) yBoard];

                    if (piece == null || piece.color == Piece.Color.DISABLED) {
                        string.append(' ');
                        continue;
                    }

                    char charToAppend = piece.getChar();
                    string.append(charToAppend);
                }
            }

            string.append('\n');
        }

        // go back through and add the rightmost wall to each row of cells
        // first loop does the top boardDim-1 rows (which all end in \), second loop does alternating \ and /
        // on the middle 2*boardDim rows and the third loop does the bottom boardDim-1 rows (which all end in /)

        // the row length is boardDiameter*xScale+2 to account for the extra \ added to previous lines as well
        // as the \n newline character
        int rowLen = boardDiameter*xScale+2;

        for (int y = 1; y < boardDim; y++) {
            int x = (boardDim + y-1) * xScale;
            string.insert(y * rowLen + x, '\\');
        }

        for (int y = boardDim; y < boardDim*3; y++) {
            int yRelative = y - boardDim;
            int x = boardDiameter * xScale;
            string.insert(y * rowLen + x, yRelative % 2 == 0 ? '\\' : '/');
        }

        for (int y = boardDim*3; y < boardDiameter*2+1; y++) {
            int yRelative = y - boardDim*3;
            int x = (boardDiameter - yRelative-1) * xScale;
            string.insert(y * rowLen + x, '/');
        }

        return string.toString();
    }

    public boolean isInBounds(Position pos) {
        boolean isFileInBounds = pos.file >= 0 && pos.file < boardDiameter;
        boolean isRankInBounds = pos.rank >= 0 && pos.rank < boardDiameter - Math.abs(pos.file - (boardDim-1));
        return isFileInBounds && isRankInBounds;
    }


    public boolean isLegalMove(Move move, Piece.Color playerColor) {
        if (!isInBounds(move.fromPos) || !isInBounds(move.toPos)) {
            return false;
        }
        if (this.getPos(move.fromPos) == null || this.getPos(move.fromPos).color != playerColor) {
            return false; // no piece there OR piece is not player's color
        }

        Set<Move> legalMoves = getLegalMovesFromPos(move.fromPos);
        return legalMoves.contains(move);
    }

    public Set<Move> getLegalMovesFromPos(Position fromPos) {
        Piece piece = this.getPos(fromPos);

        if (piece == null) {
            // there is no piece there, so no moves
            return new HashSet<>();
        }

        // delegate calculation of legal moves to the specific class which the Piece belongs to
        Set<Move> potentialMoves = piece.getMovesFromPos(this, fromPos);

        Position playerKingPos = kingPositions.get(piece.color);

        // if there is no king, do not need the below
        if (playerKingPos == null) return potentialMoves;

        // need one more condition: a move is not allowed if it causes the king to be in check,
        Set<Move> moves = new HashSet<>();
        for (Move move : potentialMoves) {
            Board boardcopy = new Board(this);
            boardcopy.applyMoveMinimal(move);

            // apply the move tentatively and check if the king is in check afterwards, then revert the move
            if (!boardcopy.isKingInCheck(piece.color)) {
                moves.add(move);
            }
        }

        return moves;
    }

    // helper for getLegalMoves, used to check that the king is not in check after a move is done
    // need to skip the legality check for this to avoid infinite recursion in getLegalMoves
    void applyMoveMinimal(Move move) {
        Piece movedPiece = this.getPos(move.fromPos);

        this.setPos(move.fromPos, null);
        piecePositions.get(movedPiece.color).remove(move.fromPos);

        Piece capturedPiece = this.getPos(move.toPos);
        this.setPos(move.toPos, movedPiece);
        piecePositions.get(movedPiece.color).add(move.toPos);

        if (movedPiece instanceof King) {
            kingPositions.put(movedPiece.color, move.toPos);
        }

        if (capturedPiece != null && capturedPiece.color != Piece.Color.DISABLED) {
            piecePositions.get(capturedPiece.color).remove(move.toPos);
        }

    }

    public MoveResult applyMove(Move move, Piece.Color playerColor) {
        if (!isLegalMove(move, playerColor))
            return new MoveResult(false);

        Piece movedPiece = this.getPos(move.fromPos);
        this.setPos(move.fromPos, null);
        piecePositions.get(movedPiece.color).remove(move.fromPos); // note: need to do this because the piece's position changed

        Piece capturedPiece = this.getPos(move.toPos);
        if (capturedPiece != null && capturedPiece.color != Piece.Color.DISABLED) {
            piecePositions.get(capturedPiece.color).remove(move.toPos);
            capturedPieces.get(playerColor).add(capturedPiece);
            capturedPieces.get(playerColor).sort(Piece::compareTo);
        }
        this.setPos(move.toPos, movedPiece);
        piecePositions.get(movedPiece.color).add(move.toPos);

        // Promotion check; the promotion action is handled in handlePromotion
        Position promotedPawn = null;
        if (movedPiece instanceof Pawn) {
            if (Pawn.isInPromotionPosition(move.toPos, movedPiece.color, this)) {
                promotedPawn = move.toPos;
            }
        }

        // Update the king position if the moved piece is a king
        if (movedPiece instanceof King) {
            kingPositions.put(movedPiece.color, move.toPos);
        }

        // If this is a pawn move that delivers en passant, handle capturing the passanted pawn
        if (movedPiece instanceof Pawn &&
                Pawn.getPassantedPawnIfExists(move, playerColor, this) != null) {
            Position passantedPawnPos = Pawn.getPassantedPawnIfExists(move, playerColor, this);
            Piece passantedPawn = this.getPos(passantedPawnPos);
            piecePositions.get(passantedPawn.color).remove(passantedPawnPos);
            capturedPieces.get(playerColor).addFirst(passantedPawn);
            this.setPos(passantedPawnPos, null);
        }

        // If this is a pawn making its starting move, update passantable pawn; else, clear this color's passantable pawn
        if (movedPiece instanceof Pawn && Pawn.isStartingPawnMove(move, playerColor, this)) {
            passantablePawns.put(playerColor, move.toPos);
        } else {
            passantablePawns.remove(playerColor);
        }


        return new MoveResult(true, promotedPawn);
    }

    public void handlePromotion(Position promotionPos, PieceType promotionType) {
        assert promotionType != PieceType.KING && promotionType != PieceType.PAWN;

        Piece originalPiece = this.getPos(promotionPos);
        assert originalPiece instanceof Pawn;

        Piece promotedPiece = PieceFactory.createPiece(promotionType, originalPiece.color);
        this.setPos(promotionPos, promotedPiece);
    }

    /**
     * Helper for checking checks/checkmates: calculate the set of positions which are under attack
     */
    public boolean[][] getSpacesUnderThreat(Piece.Color playerColor) {
        Set<Position> enemyPositionsList = new HashSet<>();
        for (Piece.Color color : piecePositions.keySet()) {
            if (color != playerColor)
                enemyPositionsList.addAll(piecePositions.get(color));
        }


        boolean[][] underAttack = new boolean[boardDiameter][boardDiameter];
        // note: the above could easily be a bit vector instead, but will avoid premature optimisation

        // initially set all entries to false
        for (int x = 0; x < boardDiameter; x++) {
            for (int y = 0; y < boardDiameter; y++) {
                underAttack[x][y] = false;
            }
        }

        for (Position pos : enemyPositionsList) {
            assert this.getPos(pos) != null && this.getPos(pos).color != playerColor;

            for (Move move : this.getPos(pos).getPotentialCapturingMovesFromPos(this, pos)) {
                // any space which the opponent can move to is under attack
                underAttack[move.toPos.file][move.toPos.rank] = true;
            }
        }

        return underAttack;
    }

    /**
     * Helper for checks: given a position, return true if it is under threat
     */
    public boolean isSpaceUnderThreat(Piece.Color playerColor, Position pos) {
        return this.getSpacesUnderThreat(playerColor)[pos.file][pos.rank];
    }

    /**
     * Helper for checking checkmates: given a list of positions, returns a list where index i
     * is true if position i is under threat. More efficient than calling isSpaceUnderThreat n times
     * since the call to getSpacesUnderThreat is amortised.
     */
    public List<Boolean> areSpacesUnderThreat(Piece.Color playerColor, List<Position> poss) {
        boolean[][] spacesUnderThreat = this.getSpacesUnderThreat(playerColor);
        List<Boolean> res = new ArrayList<>(poss.size());
        for (Position pos : poss) {
            res.add(spacesUnderThreat[pos.file][pos.rank]);
        }
        return res;
    }

    /**
     * Checks stalemate (player has no legal moves)
     */
    public boolean isInStalemate(Piece.Color playerColor) {
        Set<Position> playerPiecePositions = piecePositions.get(playerColor);
        for (Position pos : playerPiecePositions) {
            if (!this.getLegalMovesFromPos(pos).isEmpty()) return false;
        }
        return true;
    }

    /**
     * Checks if the king is in check
     */
    public boolean isKingInCheck(Piece.Color playerColor) {
        Position kingPos = kingPositions.get(playerColor);
        return isSpaceUnderThreat(playerColor, kingPos);
    }

    /**
     * Checks for checkmate
     */
    public boolean isInCheckmate(Piece.Color playerColor) {
        return isInStalemate(playerColor) && isKingInCheck(playerColor);
    }

    /**
     * Handles eliminating a player (setting their pieces to dead)
     */
    public void eliminatePlayer(Piece.Color player) {
        for (Position pos : piecePositions.get(player)) {
            this.getPos(pos).color = Piece.Color.DISABLED;
        }
        piecePositions.remove(player);
        kingPositions.remove(player);
        passantablePawns.remove(player);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Board board1)) return false;
        return boardDiameter == board1.boardDiameter && Objects.deepEquals(board, board1.board);
    }

    @Override
    public int hashCode() {
        return Objects.hash(boardDim, Arrays.deepHashCode(board));
    }
}
