import java.lang.Math;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

public class Board {
    // for 91 space board; there are 11 files going vertically and 11 ranks going skew-horizontally.
    // for files on the outer edges of the board, some of the ranks don't exist, e.g. only a1-6 exist
    // f, or file 6, is the middle of the board
    Piece[][] board = new Piece[11][11];

    // references to all pieces in the game (for check/checkmate detection)
    List<Piece> whitePieces;
    List<Piece> blackPieces;
    List<Piece> whiteCapturedPieces;
    List<Piece> blackCapturedPieces;

    public static final int centerFile = 5;

    public static class Position {
        // these are values from 0 to 10 inclusive
        public int file;
        public int rank;

        public Position(int file, int rank) {
            this.file = file;
            this.rank = rank;
        }

        public Position(String pos) {
            if (pos.isEmpty()) {
                throw new RuntimeException("Position cannot be empty");
            }

            this.file = ((pos.toLowerCase().charAt(0) - 'a'));
            this.rank = Integer.parseInt(pos.substring(1)) - 1;
        }

        @Override
        public boolean equals(Object other) {
            if (other instanceof Position) {
                return this.file == ((Position) other).file && this.rank == ((Position) other).rank;
            } else return false;
        }
    }

    public static class Move {
        public Position fromPos;
        public Position toPos;

        public Move(Position fromPos, Position toPos) {
            this.fromPos = fromPos;
            this.toPos = toPos;
        }

        @Override
        public boolean equals(Object other) {
            if (other instanceof Move) {
                return this.fromPos.equals(((Move) other).fromPos) && this.toPos.equals(((Move) other).toPos);
            } else return false;
        }
    }

    public Piece getPos(Position pos) {
        return this.board[pos.file][pos.rank];
    }

    public void setPos(Position pos, Piece piece) {
        this.board[pos.file][pos.rank] = piece;
    }

    // default constructor; initialise board in starting position
    public Board() {
        for (String pos : List.of("b1", "c2", "d3", "e4", "f5", "g4", "h3", "i2", "j1")) {
            Piece pawn = new Piece("pawn", "white");
            whitePieces.add(pawn);
            this.setPos(new Position(pos), pawn);
        }

        for (String pos : List.of("b7", "c7", "d7", "e7", "f7", "g7", "h7", "i7", "j7")) {
            Piece pawn = new Piece("pawn", "black");
            blackPieces.add(pawn);
            this.setPos(new Position(pos), pawn);
        }

        Piece lrook = new Piece("rook", "white"); whitePieces.add(lrook); this.setPos(new Position("c1"), lrook);
        Piece lknight = new Piece("knight", "white"); whitePieces.add(lknight); this.setPos(new Position("d1"), lknight);
        Piece queen = new Piece("queen", "white"); whitePieces.add(queen); this.setPos(new Position("e1"), queen);
        Piece bishop1 = new Piece("bishop", "white"); whitePieces.add(bishop1); this.setPos(new Position("f1"), bishop1);
        Piece bishop2 = new Piece("bishop", "white"); whitePieces.add(bishop2); this.setPos(new Position("f2"), bishop2);
        Piece bishop3 = new Piece("bishop", "white"); whitePieces.add(bishop3); this.setPos(new Position("f3"), bishop3);
        Piece king = new Piece("king", "white"); whitePieces.add(king); this.setPos(new Position("g1"), king);
        Piece rknight = new Piece("knight", "white"); whitePieces.add(rknight); this.setPos(new Position("h1"), rknight);
        Piece rrook = new Piece("rook", "white"); whitePieces.add(rrook); this.setPos(new Position("i1"), rrook);

        Piece blrook = new Piece("rook", "black"); blackPieces.add(blrook); this.setPos(new Position("c8"), blrook);
        Piece blknight = new Piece("knight", "black"); blackPieces.add(blknight); this.setPos(new Position("d9"), blknight);
        Piece bqueen = new Piece("queen", "black"); blackPieces.add(bqueen); this.setPos(new Position("e10"), bqueen);
        Piece bbishop1 = new Piece("bishop", "black"); blackPieces.add(bbishop1); this.setPos(new Position("f11"), bbishop1);
        Piece bbishop2 = new Piece("bishop", "black"); blackPieces.add(bbishop2); this.setPos(new Position("f10"), bbishop2);
        Piece bbishop3 = new Piece("bishop", "black"); blackPieces.add(bbishop3); this.setPos(new Position("f9"), bbishop3);
        Piece bking = new Piece("king", "black"); blackPieces.add(bking); this.setPos(new Position("g10"), bking);
        Piece brknight = new Piece("knight", "black"); blackPieces.add(brknight); this.setPos(new Position("h9"), brknight);
        Piece brrook = new Piece("rook", "black"); blackPieces.add(brrook); this.setPos(new Position("i8"), brrook);
    }

    @Override
    public String toString() {
        StringBuilder string = new StringBuilder();
        int xScale = 6;

        for (int y = 0; y < 22; y++) {
            for (int x = 0; x < 11*xScale; x++) {
                if (x % xScale != xScale/2) {
                    string.append(' ');
                    continue;
                }

                int xBoard = x/xScale;
                float yBoard = ((float) (y - Math.abs(xBoard - centerFile)))/2;
                if (yBoard < 0 || yBoard >= 11 - Math.abs(xBoard - centerFile)) {
                    string.append(' ');
                    continue;
                }
                Piece piece = board[xBoard][(int) yBoard];

                if (piece == null) {
                    string.append(' ');
                    continue;
                }

                switch(piece.type) {
                    case PAWN:
                        string.append('p');
                        break;
                    case ROOK:
                        string.append('r');
                        break;
                    case BISHOP:
                        string.append('b');
                        break;
                    case KNIGHT:
                        string.append('n');
                        break;
                    case QUEEN:
                        string.append('q');
                        break;
                    case KING:
                        string.append('k');
                        break;
                }
            }
            string.append('\n');
        }

        return string.toString();
    }

    public boolean isInBounds(Position pos) {
        boolean isFileInBounds = pos.file >= 0 && pos.file < 11;
        boolean isRankInBounds = pos.rank >= 0 && pos.rank < 11 - Math.abs(pos.file - centerFile);
        return isFileInBounds && isRankInBounds;
    }

    public static int distanceFromCenter(Position pos) {
        return Math.abs(pos.file - centerFile);
    }

    public static Position oneStepForward(Position pos, Piece.Color playerColor) {
        int forwardStep = playerColor == Piece.Color.WHITE ? 1 : -1;

        return new Position(pos.file, pos.rank + forwardStep);
    }

    public static Position oneStepBackward(Position pos, Piece.Color playerColor) {
        int forwardStep = playerColor == Piece.Color.WHITE ? 1 : -1;

        return new Position(pos.file, pos.rank - forwardStep);
    }

    public static Position oneStepLeftAndForward(Position pos, Piece.Color playerColor) {
        int forwardStep = playerColor == Piece.Color.WHITE ? 1 : -1;

        if (pos.file < centerFile) {
            return new Position(pos.file - 1, pos.rank);
        } else {
            return new Position(pos.file - 1, pos.rank + forwardStep);
        }
    }

    public static Position oneStepRightAndForward(Position pos, Piece.Color playerColor) {
        int forwardStep = playerColor == Piece.Color.WHITE ? 1 : -1;

        if (pos.file < centerFile) {
            return new Position(pos.file + 1, pos.rank + forwardStep);
        } else {
            return new Position(pos.file + 1, pos.rank);
        }
    }

    public static Position oneStepLeftAndBackward(Position pos, Piece.Color playerColor) {
        int forwardStep = playerColor == Piece.Color.WHITE ? 1 : -1;

        if (pos.file < centerFile) {
            return new Position(pos.file - 1, pos.rank - forwardStep);
        } else {
            return new Position(pos.file - 1, pos.rank);
        }
    }
    public static Position oneStepRightAndBackward(Position pos, Piece.Color playerColor) {
        int forwardStep = playerColor == Piece.Color.WHITE ? 1 : -1;

        if (pos.file < centerFile) {
            return new Position(pos.file - 1, pos.rank);
        } else {
            return new Position(pos.file - 1, pos.rank - forwardStep);
        }
    }

    public static Position bishopStepLeft(Position pos, Piece.Color playerColor) {
        int forwardStep = playerColor == Piece.Color.WHITE ? 1 : -1;

        if (pos.file < centerFile) {
            return new Position(pos.file - 2, pos.rank - forwardStep);
        } else {
            return new Position(pos.file - 2, pos.rank + forwardStep);
        }
    }

    public static Position bishopStepRight(Position pos, Piece.Color playerColor) {
        int forwardStep = playerColor == Piece.Color.WHITE ? 1 : -1;

        if (pos.file < centerFile) {
            return new Position(pos.file + 2, pos.rank + forwardStep);
        } else {
            return new Position(pos.file + 2, pos.rank - forwardStep);
        }
    }

    public static Position bishopStepForwardLeft(Position pos, Piece.Color playerColor) {
        int forwardStep = playerColor == Piece.Color.WHITE ? 1 : -1;

        if (pos.file < centerFile) {
            return new Position(pos.file - 1, pos.rank + forwardStep);
        } else {
            return new Position(pos.file - 1, pos.rank + 2 * forwardStep);
        }
    }

    public static Position bishopStepForwardRight(Position pos, Piece.Color playerColor) {
        int forwardStep = playerColor == Piece.Color.WHITE ? 1 : -1;

        if (pos.file < centerFile) {
            return new Position(pos.file + 1, pos.rank + 2 * forwardStep);
        } else {
            return new Position(pos.file + 1, pos.rank + forwardStep);
        }
    }

    public static Position bishopStepBackwardLeft(Position pos, Piece.Color playerColor) {
        int forwardStep = playerColor == Piece.Color.WHITE ? 1 : -1;

        if (pos.file < centerFile) {
            return new Position(pos.file - 1, pos.rank - 2 * forwardStep);
        } else {
            return new Position(pos.file - 1, pos.rank - forwardStep);
        }
    }
    public static Position bishopStepBackwardRight(Position pos, Piece.Color playerColor) {
        int forwardStep = playerColor == Piece.Color.WHITE ? 1 : -1;

        if (pos.file < centerFile) {
            return new Position(pos.file + 1, pos.rank - forwardStep);
        } else {
            return new Position(pos.file + 1, pos.rank - 2 * forwardStep);
        }
    }


    public boolean isLegalMove(Move move, Piece.Color playerColor) {
        if (!isInBounds(move.fromPos) || !isInBounds(move.toPos)) {
            return false;
        }

        return getLegalMoves(move.fromPos, playerColor).contains(move);
    }

    /**
     * Main function: get all legal moves for player playerColor for the piece at fromPos
     *
     * @param fromPos position of piece
     * @param playerColor color that is playing
     * @return list of legal moves
     */
    public ArrayList<Move> getLegalMoves(Position fromPos, Piece.Color playerColor) {
        Piece piece = this.getPos(fromPos);

        ArrayList<Move> moves = new ArrayList<>();

        if (piece == null || piece.color != playerColor) {
            // there is no piece there OR the piece is not the player's color
            return moves;
        }

        ArrayList<Move> possibleMoves;
        ArrayList<BiFunction<Position, Piece.Color, Position>> movementDirs;
        switch (piece.type) {
            case PAWN:
                int forwardStep = playerColor == Piece.Color.WHITE ? 1 : -1;

                // Pawn basic logic: able to move one space ahead into an empty square
                Position forwardPos = oneStepForward(fromPos, playerColor);
                if (isInBounds(forwardPos) && this.getPos(forwardPos) == null)
                    moves.add(new Move(fromPos, forwardPos));

                // Pawn basic logic: able to capture to the side
                // Note: on hex chess, capturing inwards moves the piece up a rank
                // and capturing outwards keeps the piece in the same rank
                Position leftCapturePos = oneStepLeftAndForward(fromPos, playerColor);
                if (isInBounds(leftCapturePos)
                        && this.getPos(leftCapturePos) != null
                        && this.getPos(leftCapturePos).color != playerColor)
                    moves.add(new Move(fromPos, leftCapturePos));

                Position rightCapturePos = oneStepRightAndForward(fromPos, playerColor);
                if (isInBounds(rightCapturePos)
                        && this.getPos(rightCapturePos) != null
                        && this.getPos(rightCapturePos).color != playerColor)
                    moves.add(new Move(fromPos, rightCapturePos));


                // Pawn logic: if the piece is on a starting position, it can jump two steps forward
                // Note this is true also if the pawn moves into the starting position of another pawn
                if (((playerColor == Piece.Color.WHITE && fromPos.rank == 4 - distanceFromCenter(fromPos))
                        || (playerColor == Piece.Color.BLACK && fromPos.rank == 6))) {
                    Position doubleForwardPos = new Position(fromPos.file, fromPos.rank + 2 * forwardStep);
                    if (isInBounds(doubleForwardPos) && this.getPos(doubleForwardPos) == null)
                        moves.add(new Move(fromPos, doubleForwardPos));
                }

                // Pawn logic: en passant
                // If the enemy pawn has moved 2 spaces in the last turn, and you could have captured it if it
                // had only moved one space, you still can
                // TODO: add
                break;

            case ROOK:
                movementDirs = new ArrayList<>(List.of(
                        Board::oneStepBackward,
                        Board::oneStepForward,
                        Board::oneStepLeftAndBackward,
                        Board::oneStepLeftAndForward,
                        Board::oneStepRightAndBackward,
                        Board::oneStepRightAndForward));

                for (BiFunction<Position, Piece.Color, Position> stepInDir : movementDirs) {
                    Position nextPos = stepInDir.apply(fromPos, playerColor);

                    while (isInBounds(nextPos) && this.getPos(nextPos) == null) {
                        moves.add(new Move(fromPos, nextPos));
                        nextPos = stepInDir.apply(nextPos, playerColor); // continue moving in direction until we
                                                                         // hit a piece or edge of board
                    }

                    if (isInBounds(nextPos) && this.getPos(nextPos) != null && this.getPos(nextPos).color != playerColor) {
                        moves.add(new Move(fromPos, nextPos)); // add the capturing move for opponent piece
                    }
                }
                break;

            case BISHOP:
                movementDirs = new ArrayList<>(List.of(
                        Board::bishopStepLeft,
                        Board::bishopStepRight,
                        Board::bishopStepBackwardLeft,
                        Board::bishopStepBackwardRight,
                        Board::bishopStepForwardLeft,
                        Board::bishopStepForwardRight));

                for (BiFunction<Position, Piece.Color, Position> stepInDir : movementDirs) {
                    Position nextPos = stepInDir.apply(fromPos, playerColor);

                    while (isInBounds(nextPos) && this.getPos(nextPos) == null) {
                        moves.add(new Move(fromPos, nextPos));
                        nextPos = stepInDir.apply(nextPos, playerColor); // continue moving in direction until we
                                                                         // hit a piece or edge of board
                    }

                    if (isInBounds(nextPos) && this.getPos(nextPos) != null && this.getPos(nextPos).color != playerColor) {
                        moves.add(new Move(fromPos, nextPos)); // add the capturing move for opponent piece
                    }
                }

            case QUEEN:
                movementDirs = new ArrayList<>(List.of(
                        Board::oneStepBackward,
                        Board::oneStepForward,
                        Board::oneStepLeftAndBackward,
                        Board::oneStepLeftAndForward,
                        Board::oneStepRightAndBackward,
                        Board::oneStepRightAndForward,
                        Board::bishopStepLeft,
                        Board::bishopStepRight,
                        Board::bishopStepBackwardLeft,
                        Board::bishopStepBackwardRight,
                        Board::bishopStepForwardLeft,
                        Board::bishopStepForwardRight));

                for (BiFunction<Position, Piece.Color, Position> stepInDir : movementDirs) {
                    Position nextPos = stepInDir.apply(fromPos, playerColor);

                    while (isInBounds(nextPos) && this.getPos(nextPos) == null) {
                        moves.add(new Move(fromPos, nextPos));
                        nextPos = stepInDir.apply(nextPos, playerColor); // continue moving in direction until we
                                                                         // hit a piece or edge of board
                    }

                    if (isInBounds(nextPos) && this.getPos(nextPos) != null && this.getPos(nextPos).color != playerColor) {
                        moves.add(new Move(fromPos, nextPos)); // add the capturing move for opponent piece
                    }
                }
                break;


            case KNIGHT:
                possibleMoves = new ArrayList<>(12);

                possibleMoves.add(new Move(fromPos, oneStepLeftAndForward(
                                                    oneStepForward(
                                                    oneStepForward(fromPos, playerColor), playerColor), playerColor)));
                possibleMoves.add(new Move(fromPos, oneStepRightAndForward(
                                                    oneStepForward(
                                                    oneStepForward(fromPos, playerColor), playerColor), playerColor)));
                possibleMoves.add(new Move(fromPos, oneStepLeftAndBackward(
                                                    oneStepLeftAndForward(
                                                    oneStepLeftAndForward(fromPos, playerColor), playerColor), playerColor)));
                possibleMoves.add(new Move(fromPos, oneStepForward(
                                                    oneStepLeftAndForward(
                                                    oneStepLeftAndForward(fromPos, playerColor), playerColor), playerColor)));
                possibleMoves.add(new Move(fromPos, oneStepRightAndBackward(
                                                    oneStepRightAndForward(
                                                    oneStepRightAndForward(fromPos, playerColor), playerColor), playerColor)));
                possibleMoves.add(new Move(fromPos, oneStepForward(
                                                    oneStepRightAndForward(
                                                    oneStepRightAndForward(fromPos, playerColor), playerColor), playerColor)));
                possibleMoves.add(new Move(fromPos, oneStepLeftAndForward(
                                                    oneStepLeftAndBackward(
                                                    oneStepLeftAndBackward(fromPos, playerColor), playerColor), playerColor)));
                possibleMoves.add(new Move(fromPos, oneStepBackward(
                                                    oneStepLeftAndBackward(
                                                    oneStepLeftAndBackward(fromPos, playerColor), playerColor), playerColor)));
                possibleMoves.add(new Move(fromPos, oneStepRightAndForward(
                                                    oneStepRightAndBackward(
                                                    oneStepRightAndBackward(fromPos, playerColor), playerColor), playerColor)));
                possibleMoves.add(new Move(fromPos, oneStepBackward(
                                                    oneStepRightAndBackward(
                                                    oneStepRightAndBackward(fromPos, playerColor), playerColor), playerColor)));
                possibleMoves.add(new Move(fromPos, oneStepLeftAndBackward(
                                                    oneStepBackward(
                                                    oneStepBackward(fromPos, playerColor), playerColor), playerColor)));
                possibleMoves.add(new Move(fromPos, oneStepRightAndBackward(
                                                    oneStepBackward(
                                                    oneStepBackward(fromPos, playerColor), playerColor), playerColor)));


                moves.addAll(possibleMoves.stream().filter(move -> isInBounds(move.toPos)
                        && (this.getPos(move.toPos) == null
                        || this.getPos(move.toPos).color != playerColor)).toList());
                return moves;

            case KING:
                movementDirs = new ArrayList<>(List.of(
                        Board::oneStepBackward,
                        Board::oneStepForward,
                        Board::oneStepLeftAndBackward,
                        Board::oneStepLeftAndForward,
                        Board::oneStepRightAndBackward,
                        Board::oneStepRightAndForward,
                        Board::bishopStepLeft,
                        Board::bishopStepRight,
                        Board::bishopStepBackwardLeft,
                        Board::bishopStepBackwardRight,
                        Board::bishopStepForwardLeft,
                        Board::bishopStepForwardRight));

                possibleMoves = new ArrayList<>(12);
                for (BiFunction<Position, Piece.Color, Position> stepInDir : movementDirs) {
                    possibleMoves.add(new Move(fromPos, stepInDir.apply(fromPos, playerColor)));
                }

                return new ArrayList<>(possibleMoves.stream().filter(move -> isInBounds(move.toPos)
                                                                     && (this.getPos(move.toPos) == null
                                                                     || this.getPos(move.toPos).color != playerColor)).toList());
        }

        return moves;
    }

    public void applyMove(Move move, Piece.Color playerColor) {
        if (!isLegalMove(move, playerColor)) return; // TODO: maybe change this to an assert

        List<Piece> enemyPiecesList = playerColor == Piece.Color.BLACK ? whitePieces : blackPieces;
        List<Piece> enemyCapturedPiecesList = playerColor == Piece.Color.BLACK ? whiteCapturedPieces : blackCapturedPieces;

        Piece movedPiece = this.getPos(move.fromPos);
        this.setPos(move.fromPos, null);

        Piece capturedPiece = this.getPos(move.toPos);
        if (capturedPiece != null) {
            assert capturedPiece.color != playerColor;
            enemyPiecesList.remove(capturedPiece);
            enemyCapturedPiecesList.add(capturedPiece);
        }
    }
}
