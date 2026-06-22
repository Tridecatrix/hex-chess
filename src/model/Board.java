package model;

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
    Map<Piece.Color, Set<Position>> piecePositions = new HashMap<>();

    Map<Piece.Color, List<Piece>> capturedPieces = new HashMap<>();

    Map<Piece.Color, Position> kingPositions = new HashMap<>();

    // for tracking en passant
    public Position passantablePawn;

    public static final int centerFile = 5;

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
        if (mode == Game.Mode.TWO_PLAYER) {
            boardDim = 6;
            boardDiameter = 2*boardDim - 1;

            board = new Piece[boardDiameter][boardDiameter];

            piecePositions.put(Piece.Color.WHITE, new HashSet<>());
            piecePositions.put(Piece.Color.BLACK, new HashSet<>());

            capturedPieces.put(Piece.Color.WHITE, new ArrayList<>());
            capturedPieces.put(Piece.Color.BLACK, new ArrayList<>());

            for (String pos : List.of("b1", "c2", "d3", "e4", "f5", "g4", "h3", "i2", "j1")) {
                Piece pawn = PieceFactory.createPiece("pawn", "white");
                Position posAsObj = new Position(pos);
                piecePositions.get(Piece.Color.WHITE).add(posAsObj);
                this.setPos(posAsObj, pawn);
            }

            for (String pos : List.of("b7", "c7", "d7", "e7", "f7", "g7", "h7", "i7", "j7")) {
                Piece pawn = PieceFactory.createPiece("pawn", "black");
                Position posAsObj = new Position(pos);
                piecePositions.get(Piece.Color.BLACK).add(posAsObj);
                this.setPos(new Position(pos), pawn);
            }

            this.setPos(new Position("c1"), PieceFactory.createPiece("rook", "white"));
            this.setPos(new Position("d1"), PieceFactory.createPiece("knight", "white"));
            this.setPos(new Position("e1"), PieceFactory.createPiece("queen", "white"));
            this.setPos(new Position("f1"), PieceFactory.createPiece("bishop", "white"));
            this.setPos(new Position("f2"), PieceFactory.createPiece("bishop", "white"));
            this.setPos(new Position("f3"), PieceFactory.createPiece("bishop", "white"));
            this.setPos(new Position("g1"), PieceFactory.createPiece("king", "white"));
            this.setPos(new Position("h1"), PieceFactory.createPiece("knight", "white"));
            this.setPos(new Position("i1"), PieceFactory.createPiece("rook", "white"));
            piecePositions.get(Piece.Color.WHITE).addAll(Stream.of("c1", "d1", "e1", "f1", "f2", "f3", "g1", "h1", "i1").map(Position::new).toList());

            this.setPos(new Position("c8"), PieceFactory.createPiece("rook", "black"));
            this.setPos(new Position("d9"), PieceFactory.createPiece("knight", "black"));
            this.setPos(new Position("e10"), PieceFactory.createPiece("queen", "black"));
            this.setPos(new Position("f11"), PieceFactory.createPiece("bishop", "black"));
            this.setPos(new Position("f10"), PieceFactory.createPiece("bishop", "black"));
            this.setPos(new Position("f9"), PieceFactory.createPiece("bishop", "black"));
            this.setPos(new Position("g10"), PieceFactory.createPiece("king", "black"));
            this.setPos(new Position("h9"), PieceFactory.createPiece("knight", "black"));
            this.setPos(new Position("i8"), PieceFactory.createPiece("rook", "black"));
            piecePositions.get(Piece.Color.BLACK).addAll(Stream.of("c8", "d9", "e10", "f11", "f10", "f9", "g10", "h9", "i8").map(Position::new).toList());

            kingPositions.put(Piece.Color.WHITE, new Position("g1"));
            kingPositions.put(Piece.Color.BLACK, new Position("g10"));
        } else if (mode == Game.Mode.THREE_PLAYER) {
            boardDim = 7;
            boardDiameter = 2*boardDim - 1;

            board = new Piece[boardDiameter][boardDiameter];

            piecePositions.put(Piece.Color.WHITE, new HashSet<>());
            piecePositions.put(Piece.Color.RED, new HashSet<>());
            piecePositions.put(Piece.Color.BLUE, new HashSet<>());

            capturedPieces.put(Piece.Color.WHITE, new ArrayList<>());
            capturedPieces.put(Piece.Color.RED, new ArrayList<>());
            capturedPieces.put(Piece.Color.BLUE, new ArrayList<>());

            for (String pos : List.of("c1", "d2", "e3", "f4", "g5", "h4", "i3", "j2", "k1")) {
                Piece pawn = PieceFactory.createPiece("pawn", "white");
                Position posAsObj = new Position(pos);
                piecePositions.get(Piece.Color.WHITE).add(posAsObj);
                this.setPos(posAsObj, pawn);
            }

            for (String pos : List.of("a3", "b4", "c5", "d6", "e7", "e8", "e9", "e10", "e11")) {
                Piece pawn = PieceFactory.createPiece("pawn", "black");
                Position posAsObj = new Position(pos);
                piecePositions.get(Piece.Color.RED).add(posAsObj);
                this.setPos(new Position(pos), pawn);
            }

            for (String pos : List.of("m3", "l4", "k5", "j6", "i7", "i8", "i9", "i10", "i11")) {
                Piece pawn = PieceFactory.createPiece("pawn", "blue");
                Position posAsObj = new Position(pos);
                piecePositions.get(Piece.Color.BLUE).add(posAsObj);
                this.setPos(new Position(pos), pawn);
            }

            this.setPos(new Position("d1"), PieceFactory.createPiece("rook", "white"));
            this.setPos(new Position("e1"), PieceFactory.createPiece("knight", "white"));
            this.setPos(new Position("f1"), PieceFactory.createPiece("queen", "white"));
            this.setPos(new Position("f3"), PieceFactory.createPiece("knight", "white"));
            this.setPos(new Position("g1"), PieceFactory.createPiece("bishop", "white"));
            this.setPos(new Position("g2"), PieceFactory.createPiece("bishop", "white"));
            this.setPos(new Position("g3"), PieceFactory.createPiece("bishop", "white"));
            this.setPos(new Position("h1"), PieceFactory.createPiece("king", "white"));
            this.setPos(new Position("h3"), PieceFactory.createPiece("knight", "white"));
            this.setPos(new Position("i1"), PieceFactory.createPiece("knight", "white"));
            this.setPos(new Position("j1"), PieceFactory.createPiece("rook", "white"));
            piecePositions.get(Piece.Color.WHITE).addAll(Stream.of(
                    "d1", "e1", "f1", "f3", "g1", "g2", "g3", "h1", "h3", "i1", "j1").map(Position::new).toList());

            this.setPos(new Position("d10"), PieceFactory.createPiece("rook", "red"));
            this.setPos(new Position("c9"), PieceFactory.createPiece("knight", "red"));
            this.setPos(new Position("b8"), PieceFactory.createPiece("queen", "red"));
            this.setPos(new Position("d8"), PieceFactory.createPiece("knight", "red"));
            this.setPos(new Position("a7"), PieceFactory.createPiece("bishop", "red"));
            this.setPos(new Position("b7"), PieceFactory.createPiece("bishop", "red"));
            this.setPos(new Position("c7"), PieceFactory.createPiece("bishop", "red"));
            this.setPos(new Position("a6"), PieceFactory.createPiece("king", "red"));
            this.setPos(new Position("c6"), PieceFactory.createPiece("knight", "red"));
            this.setPos(new Position("a5"), PieceFactory.createPiece("knight", "red"));
            this.setPos(new Position("a4"), PieceFactory.createPiece("rook", "red"));
            piecePositions.get(Piece.Color.RED).addAll(Stream.of(
                    "d10", "c9", "b8", "d8", "a7", "b7", "c7", "a6", "c6", "a5", "a4").map(Position::new).toList());

            this.setPos(new Position("m4"), PieceFactory.createPiece("rook", "blue"));
            this.setPos(new Position("m5"), PieceFactory.createPiece("knight", "blue"));
            this.setPos(new Position("m6"), PieceFactory.createPiece("queen", "blue"));
            this.setPos(new Position("k6"), PieceFactory.createPiece("knight", "blue"));
            this.setPos(new Position("m7"), PieceFactory.createPiece("bishop", "blue"));
            this.setPos(new Position("l7"), PieceFactory.createPiece("bishop", "blue"));
            this.setPos(new Position("k7"), PieceFactory.createPiece("bishop", "blue"));
            this.setPos(new Position("l8"), PieceFactory.createPiece("king", "blue"));
            this.setPos(new Position("j8"), PieceFactory.createPiece("knight", "blue"));
            this.setPos(new Position("k9"), PieceFactory.createPiece("knight", "blue"));
            this.setPos(new Position("j10"), PieceFactory.createPiece("rook", "blue"));
            piecePositions.get(Piece.Color.BLUE).addAll(Stream.of(
                    "m4", "m5", "m6", "k6", "m7", "l7", "k7", "l8", "j8", "k9", "j10").map(Position::new).toList());

            kingPositions.put(Piece.Color.WHITE, new Position("h1"));
            kingPositions.put(Piece.Color.RED, new Position("a6"));
            kingPositions.put(Piece.Color.BLUE, new Position("l8"));
        } else if (mode == Game.Mode.SIX_PLAYER) {
            boardDim = 11;
            boardDiameter = 2*boardDim - 1;

            board = new Piece[boardDiameter][boardDiameter];

            piecePositions.put(Piece.Color.WHITE, new HashSet<>());
            piecePositions.put(Piece.Color.GREEN, new HashSet<>());
            piecePositions.put(Piece.Color.RED, new HashSet<>());
            piecePositions.put(Piece.Color.YELLOW, new HashSet<>());
            piecePositions.put(Piece.Color.BLUE, new HashSet<>());
            piecePositions.put(Piece.Color.PURPLE, new HashSet<>());

            capturedPieces.put(Piece.Color.WHITE, new ArrayList<>());
            capturedPieces.put(Piece.Color.GREEN, new ArrayList<>());
            capturedPieces.put(Piece.Color.RED, new ArrayList<>());
            capturedPieces.put(Piece.Color.YELLOW, new ArrayList<>());
            capturedPieces.put(Piece.Color.BLUE, new ArrayList<>());
            capturedPieces.put(Piece.Color.PURPLE, new ArrayList<>());

            for (String pos : List.of("g1", "h2", "i3", "j4", "k5", "m4", "n3", "o2", "p1")) {
                Piece pawn = PieceFactory.createPiece("pawn", "white");
                Position posAsObj = new Position(pos);
                piecePositions.get(Piece.Color.WHITE).add(posAsObj);
                this.setPos(posAsObj, pawn);
            }

            this.setPos(new Position("h1"), PieceFactory.createPiece("rook", "white"));
            this.setPos(new Position("i1"), PieceFactory.createPiece("knight", "white"));
            this.setPos(new Position("j1"), PieceFactory.createPiece("queen", "white"));
            this.setPos(new Position("j3"), PieceFactory.createPiece("knight", "white"));
            this.setPos(new Position("k1"), PieceFactory.createPiece("bishop", "white"));
            this.setPos(new Position("k2"), PieceFactory.createPiece("bishop", "white"));
            this.setPos(new Position("k3"), PieceFactory.createPiece("bishop", "white"));
            this.setPos(new Position("l1"), PieceFactory.createPiece("king", "white"));
            this.setPos(new Position("m1"), PieceFactory.createPiece("knight", "white"));
            this.setPos(new Position("n1"), PieceFactory.createPiece("knight", "white"));
            this.setPos(new Position("o1"), PieceFactory.createPiece("rook", "white"));
            piecePositions.get(Piece.Color.WHITE).addAll(Stream.of(
                    "h1", "i1", "j1", "j3", "k1", "k2", "k3", "l1", "m3", "n1", "o1").map(Position::new).toList());

            for (String pos : List.of("a5", "b5", "c5", "d5", "e5", "e4", "e3", "e2", "e1")) {
                Piece pawn = PieceFactory.createPiece("pawn", "green");
                Position posAsObj = new Position(pos);
                piecePositions.get(Piece.Color.GREEN).add(posAsObj);
                this.setPos(posAsObj, pawn);
            }

            this.setPos(new Position("a4"), PieceFactory.createPiece("rook", "green"));
            this.setPos(new Position("a3"), PieceFactory.createPiece("knight", "green"));
            this.setPos(new Position("a2"), PieceFactory.createPiece("queen", "green"));
            this.setPos(new Position("c4"), PieceFactory.createPiece("knight", "green"));
            this.setPos(new Position("a1"), PieceFactory.createPiece("bishop", "green"));
            this.setPos(new Position("b2"), PieceFactory.createPiece("bishop", "green"));
            this.setPos(new Position("c3"), PieceFactory.createPiece("bishop", "green"));
            this.setPos(new Position("b1"), PieceFactory.createPiece("king", "green"));
            this.setPos(new Position("d3"), PieceFactory.createPiece("knight", "green"));
            this.setPos(new Position("c1"), PieceFactory.createPiece("knight", "green"));
            this.setPos(new Position("d1"), PieceFactory.createPiece("rook", "green"));
            piecePositions.get(Piece.Color.GREEN).addAll(Stream.of(
                    "a4", "a3", "a2", "c4", "a1", "b2", "c3", "b1", "d3", "c1", "d1").map(Position::new).toList());

            for (String pos : List.of("e15", "e14", "e13", "e12", "e11", "d10", "c9", "b8", "a7")) {
                Piece pawn = PieceFactory.createPiece("pawn", "red");
                Position posAsObj = new Position(pos);
                piecePositions.get(Piece.Color.RED).add(posAsObj);
                this.setPos(posAsObj, pawn);
            }

            this.setPos(new Position("d14"), PieceFactory.createPiece("rook", "red"));
            this.setPos(new Position("c13"), PieceFactory.createPiece("knight", "red"));
            this.setPos(new Position("b12"), PieceFactory.createPiece("queen", "red"));
            this.setPos(new Position("d12"), PieceFactory.createPiece("knight", "red"));
            this.setPos(new Position("a11"), PieceFactory.createPiece("bishop", "red"));
            this.setPos(new Position("b11"), PieceFactory.createPiece("bishop", "red"));
            this.setPos(new Position("c11"), PieceFactory.createPiece("bishop", "red"));
            this.setPos(new Position("a10"), PieceFactory.createPiece("king", "red"));
            this.setPos(new Position("c10"), PieceFactory.createPiece("knight", "red"));
            this.setPos(new Position("a9"), PieceFactory.createPiece("knight", "red"));
            this.setPos(new Position("a8"), PieceFactory.createPiece("rook", "red"));
            piecePositions.get(Piece.Color.RED).addAll(Stream.of(
                    "d14", "c13", "b12", "d12", "a11", "b11", "c11", "a10", "c10", "a9", "a8").map(Position::new).toList());

            for (String pos : List.of("h17", "i17", "j17", "k17", "l17", "m17", "n17", "o17", "p17")) {
                Piece pawn = PieceFactory.createPiece("pawn", "yellow");
                Position posAsObj = new Position(pos);
                piecePositions.get(Piece.Color.YELLOW).add(posAsObj);
                this.setPos(posAsObj, pawn);
            }

            this.setPos(new Position("o18"), PieceFactory.createPiece("rook", "yellow"));
            this.setPos(new Position("n19"), PieceFactory.createPiece("knight", "yellow"));
            this.setPos(new Position("m20"), PieceFactory.createPiece("queen", "yellow"));
            this.setPos(new Position("m18"), PieceFactory.createPiece("knight", "yellow"));
            this.setPos(new Position("l21"), PieceFactory.createPiece("bishop", "yellow"));
            this.setPos(new Position("l20"), PieceFactory.createPiece("bishop", "yellow"));
            this.setPos(new Position("l19"), PieceFactory.createPiece("bishop", "yellow"));
            this.setPos(new Position("k20"), PieceFactory.createPiece("king", "yellow"));
            this.setPos(new Position("k18"), PieceFactory.createPiece("knight", "yellow"));
            this.setPos(new Position("j19"), PieceFactory.createPiece("knight", "yellow"));
            this.setPos(new Position("i18"), PieceFactory.createPiece("rook", "yellow"));
            piecePositions.get(Piece.Color.YELLOW).addAll(Stream.of(
                    "o18", "n19", "m20", "m18", "l21", "l20", "l19", "k20", "k18", "j19", "i18").map(Position::new).toList());

            for (String pos : List.of("u7", "t8", "s9", "r10", "q11", "q12", "q13", "q14", "q15")) {
                Piece pawn = PieceFactory.createPiece("pawn", "blue");
                Position posAsObj = new Position(pos);
                piecePositions.get(Piece.Color.BLUE).add(posAsObj);
                this.setPos(posAsObj, pawn);
            }

            this.setPos(new Position("u8"), PieceFactory.createPiece("rook", "blue"));
            this.setPos(new Position("u9"), PieceFactory.createPiece("knight", "blue"));
            this.setPos(new Position("u10"), PieceFactory.createPiece("queen", "blue"));
            this.setPos(new Position("s10"), PieceFactory.createPiece("knight", "blue"));
            this.setPos(new Position("u11"), PieceFactory.createPiece("bishop", "blue"));
            this.setPos(new Position("t11"), PieceFactory.createPiece("bishop", "blue"));
            this.setPos(new Position("s11"), PieceFactory.createPiece("bishop", "blue"));
            this.setPos(new Position("t12"), PieceFactory.createPiece("king", "blue"));
            this.setPos(new Position("r12"), PieceFactory.createPiece("knight", "blue"));
            this.setPos(new Position("s13"), PieceFactory.createPiece("knight", "blue"));
            this.setPos(new Position("r14"), PieceFactory.createPiece("rook", "blue"));
            piecePositions.get(Piece.Color.BLUE).addAll(Stream.of(
                    "u8", "u9", "u10", "s10", "u11", "t11", "s11", "t12", "r12", "s13", "r14").map(Position::new).toList());

            for (String pos : List.of("q1", "q2", "q3", "q4", "q5", "r5", "s5", "t5", "u5")) {
                Piece pawn = PieceFactory.createPiece("pawn", "purple");
                Position posAsObj = new Position(pos);
                piecePositions.get(Piece.Color.PURPLE).add(posAsObj);
                this.setPos(posAsObj, pawn);
            }

            this.setPos(new Position("r1"), PieceFactory.createPiece("rook", "purple"));
            this.setPos(new Position("s1"), PieceFactory.createPiece("knight", "purple"));
            this.setPos(new Position("r3"), PieceFactory.createPiece("queen", "purple"));
            this.setPos(new Position("t1"), PieceFactory.createPiece("knight", "purple"));
            this.setPos(new Position("u1"), PieceFactory.createPiece("bishop", "purple"));
            this.setPos(new Position("t2"), PieceFactory.createPiece("bishop", "purple"));
            this.setPos(new Position("s3"), PieceFactory.createPiece("bishop", "purple"));
            this.setPos(new Position("u2"), PieceFactory.createPiece("king", "purple"));
            this.setPos(new Position("s4"), PieceFactory.createPiece("knight", "purple"));
            this.setPos(new Position("u3"), PieceFactory.createPiece("knight", "purple"));
            this.setPos(new Position("u4"), PieceFactory.createPiece("rook", "purple"));
            piecePositions.get(Piece.Color.PURPLE).addAll(Stream.of(
                    "r1", "s1", "r3", "t1", "u1", "t2", "s3", "u2", "s4", "u3", "u4").map(Position::new).toList());
        }
    }


    public Board(List<String> pieces) {
        this(pieces, 6);
    }

    // testing constructor; intiialises a board with the pieces given as strings, e.g.:
    // Nh6 is a white knight at position h6
    // pc7 is a black pawn at position c7
    public Board(List<String> pieces, int boardDim) {
        boardDiameter = 2*boardDim - 1;

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

        Position playerKingPos = kingPositions.get(piece.color);

        // if there is no king, do not need the below
        if (playerKingPos == null) return potentialMoves;

        // need one more condition: a move is not allowed if it causes the king to be in check,
        Set<Move> moves = new HashSet<>();
        for (Move move : potentialMoves) {
            Board boardcopy = new Board(this);
            boardcopy.applyMove(move);

            // apply the move tentatively and check if the king is in check afterwards, then revert the move
            if (!boardcopy.isKingInCheck(piece.color)) {
                moves.add(move);
            }
        }

        return moves;
    }

    // helper for getLegalMoves, used to check that the king is not in check after a move is done
    // need to skip the legality check for this to avoid infinite recursion in getLegalMoves
    Piece applyMove(Move move) {
        Piece movedPiece = this.getPos(move.fromPos);

        this.setPos(move.fromPos, null);
        piecePositions.get(movedPiece.color).remove(move.fromPos);

        Piece capturedPiece = this.getPos(move.toPos);
        this.setPos(move.toPos, movedPiece);
        piecePositions.get(movedPiece.color).add(move.toPos);

        if (movedPiece instanceof King) {
            kingPositions.put(movedPiece.color, move.toPos);
        }

        if (capturedPiece != null) {
            piecePositions.get(capturedPiece.color).remove(move.toPos);
        }

        return capturedPiece;
    }

    public MoveResult applyMoveWithLegalityCheck(Move move, Piece.Color playerColor) {
        if (!isLegalMove(move, playerColor))
            return new MoveResult(false);

        Piece movedPiece = this.getPos(move.fromPos);
        this.setPos(move.fromPos, null);
        piecePositions.get(movedPiece.color).remove(move.fromPos); // note: need to do this because the piece's position changed

        Piece capturedPiece = this.getPos(move.toPos);
        if (capturedPiece != null) {
            piecePositions.get(capturedPiece.color).remove(move.toPos);
            capturedPieces.get(playerColor).add(capturedPiece);
            capturedPieces.get(playerColor).sort(Piece::compareTo);
        }
        this.setPos(move.toPos, movedPiece);
        piecePositions.get(movedPiece.color).add(move.toPos);

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
            kingPositions.put(movedPiece.color, move.toPos);
        }

        // If this is a pawn capturing another pawn with en passant, remove the passantable pawn
        int forwardStep = movedPiece.color == Piece.Color.WHITE ? 1 : -1;
        if (passantablePawn != null && movedPiece instanceof Pawn
                && move.fromPos.file != move.toPos.file // this is a capturing move
                && move.toPos.file == passantablePawn.file && move.toPos.rank == passantablePawn.rank + forwardStep) {
                // the passantable pawn is in the capture position
            Piece passantedPawn = this.getPos(passantablePawn);

            this.capturedPieces.get(movedPiece.color).add(passantedPawn);
            this.piecePositions.get(passantedPawn.color).remove(passantablePawn);
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
