package model;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import model.piece.King;
import model.piece.Pawn;
import model.piece.Piece;

import java.lang.Math;
import java.util.*;
import java.util.stream.Stream;

/**
 * Class for tracking the board and the pieces on the board
 */
public class Board {
    public int boardDim; // side length of the board in tiles
    public int boardDiameter; // most importantly, determines the max height and width of the board

    // for 91 space board; there are 11 files going vertically and 11 ranks going skew-horizontally.
    // for files on the outer edges of the board, some of the ranks don't exist, e.g. only a1-7 exist
    // file f, or file 6, is the middle of the board
    Piece[][] board;

    // references to all pieces in the game (for check/checkmate detection)
    Set<Position> whitePiecePositions = new HashSet<>();
    Set<Position> blackPiecePositions = new HashSet<>();

    // types of captured pieces, for use in displaying captured piece lists on GUI
    List<PieceType> whiteCapturedPieces = new ArrayList<>(18);
    List<PieceType> blackCapturedPieces = new ArrayList<>(18);

    Position whiteKingPos;
    Position blackKingPos;

    // for tracking en passant
    public Position passantablePawn;

    public static final int centerFile = 5;

    public Piece getPos(Position pos) {
        return this.board[pos.file][pos.rank];
    }

    void setPos(Position pos, Piece piece) {
        this.board[pos.file][pos.rank] = piece;
    }

    public Position getWhiteKingPos() {
        return whiteKingPos;
    }

    public Position getBlackKingPos() {
        return blackKingPos;
    }

    public List<PieceType> getWhiteCapturedPieces() {
        return whiteCapturedPieces;
    }

    public List<PieceType> getBlackCapturedPieces() {
        return blackCapturedPieces;
    }

    public Board() {
        this(Game.Mode.TWO_PLAYER);
    }

    // default constructor; initialise board in starting position
    public Board(Game.Mode mode) {
        if (mode == Game.Mode.TWO_PLAYER) {
            boardDim = 6;
            boardDiameter = 2*boardDim - 1;

            board = new Piece[boardDiameter][boardDiameter];

            for (String pos : List.of("b1", "c2", "d3", "e4", "f5", "g4", "h3", "i2", "j1")) {
                Piece pawn = PieceFactory.createPiece("pawn", "white");
                Position posAsObj = new Position(pos);
                whitePiecePositions.add(posAsObj);
                this.setPos(posAsObj, pawn);
            }

            for (String pos : List.of("b7", "c7", "d7", "e7", "f7", "g7", "h7", "i7", "j7")) {
                Piece pawn = PieceFactory.createPiece("pawn", "black");
                Position posAsObj = new Position(pos);
                blackPiecePositions.add(posAsObj);
                this.setPos(new Position(pos), pawn);
            }

            Piece lrook = PieceFactory.createPiece("rook", "white");
            this.setPos(new Position("c1"), lrook);
            Piece lknight = PieceFactory.createPiece("knight", "white");
            this.setPos(new Position("d1"), lknight);
            Piece queen = PieceFactory.createPiece("queen", "white");
            this.setPos(new Position("e1"), queen);
            Piece bishop1 = PieceFactory.createPiece("bishop", "white");
            this.setPos(new Position("f1"), bishop1);
            Piece bishop2 = PieceFactory.createPiece("bishop", "white");
            this.setPos(new Position("f2"), bishop2);
            Piece bishop3 = PieceFactory.createPiece("bishop", "white");
            this.setPos(new Position("f3"), bishop3);
            Piece king = PieceFactory.createPiece("king", "white");
            this.setPos(new Position("g1"), king);
            Piece rknight = PieceFactory.createPiece("knight", "white");
            this.setPos(new Position("h1"), rknight);
            Piece rrook = PieceFactory.createPiece("rook", "white");
            this.setPos(new Position("i1"), rrook);
            whitePiecePositions.addAll(Stream.of("c1", "d1", "e1", "f1", "f2", "f3", "g1", "h1", "i1").map(Position::new).toList());

            Piece blrook = PieceFactory.createPiece("rook", "black");
            this.setPos(new Position("c8"), blrook);
            Piece blknight = PieceFactory.createPiece("knight", "black");
            this.setPos(new Position("d9"), blknight);
            Piece bqueen = PieceFactory.createPiece("queen", "black");
            this.setPos(new Position("e10"), bqueen);
            Piece bbishop1 = PieceFactory.createPiece("bishop", "black");
            this.setPos(new Position("f11"), bbishop1);
            Piece bbishop2 = PieceFactory.createPiece("bishop", "black");
            this.setPos(new Position("f10"), bbishop2);
            Piece bbishop3 = PieceFactory.createPiece("bishop", "black");
            this.setPos(new Position("f9"), bbishop3);
            Piece bking = PieceFactory.createPiece("king", "black");
            this.setPos(new Position("g10"), bking);
            Piece brknight = PieceFactory.createPiece("knight", "black");
            this.setPos(new Position("h9"), brknight);
            Piece brrook = PieceFactory.createPiece("rook", "black");
            this.setPos(new Position("i8"), brrook);
            blackPiecePositions.addAll(Stream.of("c8", "d9", "e10", "f11", "f10", "f9", "g10", "h9", "i8").map(Position::new).toList());

            whiteKingPos = new Position("g1");
            blackKingPos = new Position("g10");
        } else if (mode == Game.Mode.THREE_PLAYER) {
            // TODO
        }
    }

    // testing constructor; intiialises a board with the pieces given as strings, e.g.:
    // Nh6 is a white knight at position h6
    // pc7 is a black pawn at position c7
    public Board(List<String> pieces) {
        boardDim = 6;
        boardDiameter = 2*boardDim - 1;

        board = new Piece[boardDiameter][boardDiameter];

        for (String piece : pieces) {
            boolean isWhite = Character.isUpperCase(piece.charAt(0));
            String pieceType = Character.toString(piece.charAt(0));
            String piecePos = piece.substring(1);

            if (isWhite) {
                Piece pieceAsObj = PieceFactory.createPiece(pieceType, "w");
                Position posAsObj = new Position(piecePos);
                this.setPos(posAsObj, pieceAsObj);
                whitePiecePositions.add(posAsObj);
                if (pieceAsObj instanceof King)
                    whiteKingPos = posAsObj;
            } else {
                Piece pieceAsObj = PieceFactory.createPiece(pieceType, "b");
                Position posAsObj = new Position(piecePos);
                this.setPos(posAsObj, pieceAsObj);
                blackPiecePositions.add(posAsObj);
                if (pieceAsObj instanceof King)
                    blackKingPos = posAsObj;
            }
        }
    }

    public Board(List<String> pieces, int boardDim) {
        boardDiameter = 2*boardDim - 1;

        board = new Piece[boardDiameter][boardDiameter];

        for (String piece : pieces) {
            boolean isWhite = Character.isUpperCase(piece.charAt(0));
            String pieceType = Character.toString(piece.charAt(0));
            String piecePos = piece.substring(1);

            if (isWhite) {
                Piece pieceAsObj = PieceFactory.createPiece(pieceType, "w");
                Position posAsObj = new Position(piecePos);
                this.setPos(posAsObj, pieceAsObj);
                whitePiecePositions.add(posAsObj);
                if (pieceAsObj instanceof King)
                    whiteKingPos = posAsObj;
            } else {
                Piece pieceAsObj = PieceFactory.createPiece(pieceType, "b");
                Position posAsObj = new Position(piecePos);
                this.setPos(posAsObj, pieceAsObj);
                blackPiecePositions.add(posAsObj);
                if (pieceAsObj instanceof King)
                    blackKingPos = posAsObj;
            }
        }
    }

    // deep copy constructor; given a board, create a copy
    // explicitly create the fields with new/createPiece to avoid just having a reference to the object referred to
    // by the original fields
    public Board(Board boardOriginal) {
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

        for (Position pos : boardOriginal.whitePiecePositions) {
            this.whitePiecePositions.add(new Position(pos.file, pos.rank));
        }
        for (Position pos : boardOriginal.blackPiecePositions) {
            this.blackPiecePositions.add(new Position(pos.file, pos.rank));
        }

        this.whiteCapturedPieces.addAll(boardOriginal.whiteCapturedPieces);
        this.blackCapturedPieces.addAll(boardOriginal.blackCapturedPieces);

        if (boardOriginal.whiteKingPos == null)
            this.whiteKingPos = null;
        else
            this.whiteKingPos = new Position(boardOriginal.whiteKingPos.file, boardOriginal.whiteKingPos.rank);

        if (boardOriginal.blackKingPos == null)
            this.blackKingPos = null;
        else
            this.blackKingPos = new Position(boardOriginal.blackKingPos.file, boardOriginal.blackKingPos.rank);
    }

    @Override
    public String toString() {
        StringBuilder string = new StringBuilder();
        int xScale = 6;

        string.append("   a     b     c     d     e     f     g     h     i     j     k\n");

        for (int y = 21; y >= 0; y--) {
            for (int x = 0; x < 11*xScale; x++) {
                int xBoard = x/xScale;
                float yBoard = ((float) (y - Math.abs(xBoard - centerFile)))/2;

                // if yBoard is off the bounds of the board, print blank
                if (yBoard < 0 || yBoard >= 11 - Math.abs(xBoard - centerFile)) {
                    string.append(' ');
                    continue;
                }

                // NOTE THAT THE BELOW ONLY WORKS FOR xScale == 6
                // get the coordinate within each 2x6 size hexagon cell. how y % 2 corresponds to the bottom/top
                // of the cell alternates with each cell going horizontally, hence why both y % 2 and xBoard % 2
                // are involved in the calculation.
                int xCell = x % xScale;
                boolean isBottomOfCell = (y % 2 == 1) ^ (xBoard % 2 == 1);

                // for x coordinates in the cell that aren't the left/right walls, print an underscore or overline
                // to indicate the tops/bottoms of cells
                if (xCell == 1 || xCell == 2 || xCell == 4 || xCell == 5) {
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
                    continue;
                }

                Piece piece = board[xBoard][(int) yBoard];

                if (piece == null) {
                    string.append(' ');
                    continue;
                }

                char charToAppend = piece.getChar();
                charToAppend = piece.color == Piece.Color.WHITE ? Character.toUpperCase(charToAppend) : charToAppend;
                string.append(charToAppend);
            }

            string.append('\n');
        }

        // go back through the string and add in the rightmost walls
        //
        // (the first for loop does the \ wall on the rightmost cell in the top 5 rows,
        // the second for loop does the / wall on the rightmost cell in the bottom 5 rows,
        // and the final for loop does alternating \ and / to make the two rightmost walls
        // of the middle 6 rows of hexagons. the offset calculations are somewhat bespoke
        // and are the result of experimentation)
        for (int y = 1; y < 6; y++) {
            int x = 6*6 + (y-1)*8 - 1;
            string.insert(y*11*6 + x, '\\');
        }
        for (int y = 23; y > 18; y--) {
            int x = 4*6 - (y-17)*5 + 1;
            string.insert(y*11*6 + x, '/');
        }
        for (int y = 0; y < 12; y++) {
            string.insert(6*11+3 + (6*11+1)*6 + y*(6*11+2), y % 2 == 1 ? '/' : '\\');
        }

        return string.toString();
    }

    public boolean isInBounds(Position pos) {
        boolean isFileInBounds = pos.file >= 0 && pos.file < 11;
        boolean isRankInBounds = pos.rank >= 0 && pos.rank < 11 - Math.abs(pos.file - centerFile);
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

        Position playerKingPos = piece.color == Piece.Color.WHITE ? this.whiteKingPos : this.blackKingPos;

        // if there is no king, do not need the below
        if (playerKingPos == null) return potentialMoves;

        // need one more condition: a move is not allowed if it causes the king to be in check,
        Set<Move> moves = new HashSet<>();
        for (Move move : potentialMoves) {
            Board boardcopy = new Board(this);
            boardcopy.applyMoveTentatively(move);

            // apply the move tentatively and check if the king is in check afterwards, then revert the move
            if (!boardcopy.isKingInCheck(piece.color)) {
                moves.add(move);
            }
        }

        return moves;
    }

    // helper for getLegalMoves, used to check that the king is not in check after a move is done
    // returns captured piece, so that it isn't lost
    Piece applyMoveTentatively(Move move) {
        Piece movedPiece = this.getPos(move.fromPos);
        Piece.Color playerColor = movedPiece.color;

        Set<Position> playerPositionsList = playerColor == Piece.Color.WHITE ? whitePiecePositions : blackPiecePositions;
        Set<Position> enemyPositionsList = playerColor == Piece.Color.WHITE ?  blackPiecePositions : whitePiecePositions;

        this.setPos(move.fromPos, null);
        playerPositionsList.remove(move.fromPos);

        Piece capturedPiece = this.getPos(move.toPos);
        this.setPos(move.toPos, movedPiece);
        playerPositionsList.add(move.toPos);

        if (movedPiece instanceof King) {
            if (movedPiece.color == Piece.Color.WHITE) {
                this.whiteKingPos = move.toPos;
            } else {
                this.blackKingPos = move.toPos;
            }
        }

        if (capturedPiece != null)
            enemyPositionsList.remove(move.toPos);

        return capturedPiece;
    }

    // helper for getLegalMoves; reverts a tentative move
    void revertTentativeMove(Move move, Piece capturedPiece) {
        Piece movedPiece = this.getPos(move.toPos);
        Piece.Color playerColor = movedPiece.color;

        Set<Position> playerPositionsList = playerColor == Piece.Color.WHITE ? whitePiecePositions : blackPiecePositions;
        Set<Position> enemyPositionsList = playerColor == Piece.Color.WHITE ?  blackPiecePositions : whitePiecePositions;

        this.setPos(move.toPos, capturedPiece);
        if (capturedPiece != null)
            enemyPositionsList.add(move.toPos);
        playerPositionsList.remove(move.toPos);

        this.setPos(move.fromPos, movedPiece);
        playerPositionsList.add(move.fromPos);

        if (movedPiece instanceof King) {
            if (movedPiece.color == Piece.Color.WHITE) {
                this.whiteKingPos = move.fromPos;
            } else {
                this.blackKingPos = move.fromPos;
            }
        }
    }

    public MoveResult applyMoveWithLegalityCheck(Move move, Piece.Color playerColor) {
        if (!isLegalMove(move, playerColor))
            return new MoveResult(false);

        Set<Position> playerPositionsList = playerColor == Piece.Color.WHITE ? whitePiecePositions : blackPiecePositions;
        Set<Position> enemyPositionsList = playerColor == Piece.Color.WHITE ?  blackPiecePositions : whitePiecePositions;
        List<PieceType> enemyCapturedList = playerColor == Piece.Color.WHITE ? blackCapturedPieces : whiteCapturedPieces;

        Piece movedPiece = this.getPos(move.fromPos);
        this.setPos(move.fromPos, null);
        playerPositionsList.remove(move.fromPos); // note: need to do this because the piece's position changed

        Piece capturedPiece = this.getPos(move.toPos);
        if (capturedPiece != null) {
            enemyPositionsList.remove(move.toPos);
            enemyCapturedList.add(capturedPiece.getPieceType());
            enemyCapturedList.sort(PieceType::compareTo);
        }
        this.setPos(move.toPos, movedPiece);
        playerPositionsList.add(move.toPos);

        Position promotedPawn = null;

        // Promotion check; the promotion action is handled in handlePromotion
        if (movedPiece instanceof Pawn) {
            if (movedPiece.color == Piece.Color.WHITE
                    && move.toPos.rank == boardDiameter - Position.distanceFromCenter(move.toPos, boardDiameter) - 1
               || movedPiece.color == Piece.Color.BLACK && move.toPos.rank == 0) {
                promotedPawn = move.toPos;
            }
        }

        // Update the king position if the moved piece is a king
        if (movedPiece instanceof King) {
            if (playerColor == Piece.Color.WHITE)
                whiteKingPos = move.toPos;
            else {
                blackKingPos = move.toPos;
            }
        }

        // If this is a pawn capturing another pawn with en passant, remove the passantable pawn
        int forwardStep = movedPiece.color == Piece.Color.WHITE ? 1 : -1;
        if (passantablePawn != null && movedPiece instanceof Pawn
                && move.fromPos.file != move.toPos.file // this is a capturing move
                && move.toPos.file == passantablePawn.file && move.toPos.rank == passantablePawn.rank + forwardStep) {
                // the passantable pawn is in the capture position
            enemyCapturedList.addFirst(PieceType.PAWN);
            enemyPositionsList.remove(passantablePawn);
            this.setPos(passantablePawn, null);
        }

        // If this is a pawn making its starting move, update passantable pawn; else, clear passantable pawn
        if (movedPiece instanceof Pawn && Math.abs(move.toPos.rank - move.fromPos.rank) == 2) {
            passantablePawn = move.toPos;
        } else {
            passantablePawn = null;
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
        Set<Position> enemyPositionsList = playerColor == Piece.Color.WHITE ?  blackPiecePositions : whitePiecePositions;
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
        Set<Position> playerPiecePositions = playerColor == Piece.Color.WHITE ? whitePiecePositions : blackPiecePositions;
        for (Position pos : playerPiecePositions) {
            if (!this.getLegalMovesFromPos(pos).isEmpty()) return false;
        }
        return true;
    }

    /**
     * Checks if the king is in check
     */
    public boolean isKingInCheck(Piece.Color playerColor) {
        Position kingPos = playerColor == Piece.Color.WHITE ? whiteKingPos : blackKingPos;
        return isSpaceUnderThreat(playerColor, kingPos);
    }

    /**
     * Checks for checkmate
     */
    public boolean isInCheckmate(Piece.Color playerColor) {
        return isInStalemate(playerColor) && isKingInCheck(playerColor);
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
