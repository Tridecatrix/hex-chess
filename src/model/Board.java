package model;

import model.piece.Piece;

import java.lang.Math;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;

public class Board {
    public final int boarddim = 11;

    // for 91 space board; there are 11 files going vertically and 11 ranks going skew-horizontally.
    // for files on the outer edges of the board, some of the ranks don't exist, e.g. only a1-6 exist
    // f, or file 6, is the middle of the board
    Piece[][] board = new Piece[boarddim][boarddim];

    // references to all pieces in the game (for check/checkmate detection)
    List<Piece> whitePieces = new ArrayList<>();
    List<Piece> blackPieces = new ArrayList<>();

    public static final int centerFile = 5;

    public Piece getPos(Position pos) {
        return this.board[pos.file][pos.rank];
    }

    public void setPos(Position pos, Piece piece) {
        this.board[pos.file][pos.rank] = piece;
    }

    // default constructor; initialise board in starting position
    public Board() {
        whitePieces = new ArrayList<>();
        blackPieces = new ArrayList<>();

        for (String pos : List.of("b1", "c2", "d3", "e4", "f5", "g4", "h3", "i2", "j1")) {
            Piece pawn = PieceFactory.createPiece("pawn", "white");
            whitePieces.add(pawn);
            this.setPos(new Position(pos), pawn);
        }

        for (String pos : List.of("b7", "c7", "d7", "e7", "f7", "g7", "h7", "i7", "j7")) {
            Piece pawn = PieceFactory.createPiece("pawn", "black");
            blackPieces.add(pawn);
            this.setPos(new Position(pos), pawn);
        }

        Piece lrook = PieceFactory.createPiece("rook", "white"); this.setPos(new Position("c1"), lrook);
        Piece lknight = PieceFactory.createPiece("knight", "white"); this.setPos(new Position("d1"), lknight);
        Piece queen = PieceFactory.createPiece("queen", "white"); this.setPos(new Position("e1"), queen);
        Piece bishop1 = PieceFactory.createPiece("bishop", "white"); this.setPos(new Position("f1"), bishop1);
        Piece bishop2 = PieceFactory.createPiece("bishop", "white"); this.setPos(new Position("f2"), bishop2);
        Piece bishop3 = PieceFactory.createPiece("bishop", "white"); this.setPos(new Position("f3"), bishop3);
        Piece king = PieceFactory.createPiece("king", "white"); this.setPos(new Position("g1"), king);
        Piece rknight = PieceFactory.createPiece("knight", "white"); this.setPos(new Position("h1"), rknight);
        Piece rrook = PieceFactory.createPiece("rook", "white"); this.setPos(new Position("i1"), rrook);
        whitePieces.addAll(List.of(lrook, lknight, queen, bishop1, bishop2, bishop3, king, rknight, rrook));

        Piece blrook = PieceFactory.createPiece("rook", "black"); this.setPos(new Position("c8"), blrook);
        Piece blknight = PieceFactory.createPiece("knight", "black"); this.setPos(new Position("d9"), blknight);
        Piece bqueen = PieceFactory.createPiece("queen", "black"); this.setPos(new Position("e10"), bqueen);
        Piece bbishop1 = PieceFactory.createPiece("bishop", "black"); this.setPos(new Position("f11"), bbishop1);
        Piece bbishop2 = PieceFactory.createPiece("bishop", "black"); this.setPos(new Position("f10"), bbishop2);
        Piece bbishop3 = PieceFactory.createPiece("bishop", "black"); this.setPos(new Position("f9"), bbishop3);
        Piece bking = PieceFactory.createPiece("king", "black"); this.setPos(new Position("g10"), bking);
        Piece brknight = PieceFactory.createPiece("knight", "black"); this.setPos(new Position("h9"), brknight);
        Piece brrook = PieceFactory.createPiece("rook", "black"); this.setPos(new Position("i8"), brrook);
        blackPieces.addAll(List.of(blrook, blknight, bqueen, bbishop1, bbishop2, bbishop3, bking, brknight, brrook));
    }

    // testing constructor; intiialises a board with the pieces given as strings, e.g.:
    // Nh6 is a white knight at position h6
    // pc7 is a black pawn at position c7
    public Board(List<String> pieces) {
        for (String piece : pieces) {
            boolean isWhite = Character.isUpperCase(piece.charAt(0));
            String pieceType = Character.toString(piece.charAt(0));
            String piecePos = piece.substring(1);

            if (isWhite) {
                Piece pieceAsObj = PieceFactory.createPiece(pieceType, "w");
                this.setPos(new Position(piecePos), pieceAsObj);
                whitePieces.add(pieceAsObj);
            } else {
                Piece pieceAsObj = PieceFactory.createPiece(pieceType, "b");
                this.setPos(new Position(piecePos), pieceAsObj);
                blackPieces.add(pieceAsObj);
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder string = new StringBuilder();
        int xScale = 6;

        string.append("   a     b     c     d     e     f     g     h     i     j     k\n");

        // TODO:
        // - make the cell walls logic work for any xScale
        // - add vertical coordinates to the left and right side
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

        return getLegalMovesFromPos(move.fromPos).contains(move);
    }

    /**
     * Main function: get all legal moves for player playerColor for the piece at fromPos
     *
     * @param fromPos position of piece
     * @return list of legal moves
     */
    public Set<Move> getLegalMovesFromPos(Position fromPos) {
        Piece piece = this.getPos(fromPos);

        Set<Move> moves = new HashSet<>();

        if (piece == null) {
            // there is no piece there OR the piece is not the player's color
            return moves;
        }

        return piece.getMovesFromPos(this, fromPos);
    }


    // TODO: have this method return a result object (basically a struct), with options:
    // - does the move result in a check, checkmate or stalemate (RESULT_CHECK, RESULT_CHECKMATE, etc.)
    // - does the move result in a pawn promotion
    // - does the move result in a draw due to repetition and/or insufficient material
    public void applyMove(Move move, Piece.Color playerColor) {
        if (!isLegalMove(move, playerColor)) return; // TODO: maybe change this to an assert

        List<Piece> enemyPiecesList = playerColor == Piece.Color.BLACK ? whitePieces : blackPieces;

        Piece movedPiece = this.getPos(move.fromPos);
        this.setPos(move.fromPos, null);

        Piece capturedPiece = this.getPos(move.toPos);
        if (capturedPiece != null) {
            assert capturedPiece.color != playerColor;
            enemyPiecesList.remove(capturedPiece);
        }
        this.setPos(move.toPos, movedPiece);
    }
}
