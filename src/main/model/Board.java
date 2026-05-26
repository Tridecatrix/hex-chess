import java.lang.Math;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

public class Board {
    // for 91 space board; there are 11 files going vertically and 11 ranks going skew-horizontally.
    // for files on the outer edges of the board, some of the ranks don't exist, e.g. only a1-6 exist
    // f, or file 6, is the middle of the board
    Piece[][] board = new Piece[11][11];

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

            this.file = (int) ((pos.toLowerCase().charAt(0) - 'a'));
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
        this.setPos(new Position("b1"), new Piece("pawn", "white"));
        this.setPos(new Position("c2"), new Piece("pawn", "white"));
        this.setPos(new Position("d3"), new Piece("pawn", "white"));
        this.setPos(new Position("e4"), new Piece("pawn", "white"));
        this.setPos(new Position("f5"), new Piece("pawn", "white"));
        this.setPos(new Position("g4"), new Piece("pawn", "white"));
        this.setPos(new Position("h3"), new Piece("pawn", "white"));
        this.setPos(new Position("i2"), new Piece("pawn", "white"));
        this.setPos(new Position("j1"), new Piece("pawn", "white"));
        this.setPos(new Position("c1"), new Piece("rook", "white"));
        this.setPos(new Position("d1"), new Piece("knight", "white"));
        this.setPos(new Position("e1"), new Piece("queen", "white"));
        this.setPos(new Position("f1"), new Piece("bishop", "white"));
        this.setPos(new Position("f2"), new Piece("bishop", "white"));
        this.setPos(new Position("f3"), new Piece("bishop", "white"));
        this.setPos(new Position("g1"), new Piece("king", "white"));
        this.setPos(new Position("h1"), new Piece("knight", "white"));
        this.setPos(new Position("i1"), new Piece("rook", "white"));

        this.setPos(new Position("b7"), new Piece("pawn", "black"));
        this.setPos(new Position("c7"), new Piece("pawn", "black"));
        this.setPos(new Position("d7"), new Piece("pawn", "black"));
        this.setPos(new Position("e7"), new Piece("pawn", "black"));
        this.setPos(new Position("f7"), new Piece("pawn", "black"));
        this.setPos(new Position("g7"), new Piece("pawn", "black"));
        this.setPos(new Position("h7"), new Piece("pawn", "black"));
        this.setPos(new Position("i7"), new Piece("pawn", "black"));
        this.setPos(new Position("j7"), new Piece("pawn", "black"));
        this.setPos(new Position("c8"), new Piece("rook", "black"));
        this.setPos(new Position("d9"), new Piece("knight", "black"));
        this.setPos(new Position("e10"), new Piece("queen", "black"));
        this.setPos(new Position("f11"), new Piece("bishop", "black"));
        this.setPos(new Position("f10"), new Piece("bishop", "black"));
        this.setPos(new Position("f9"), new Piece("bishop", "black"));
        this.setPos(new Position("g10"), new Piece("king", "black"));
        this.setPos(new Position("h9"), new Piece("knight", "black"));
        this.setPos(new Position("i8"), new Piece("rook", "black"));
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
                ArrayList<BiFunction<Position, Piece.Color, Position>> movementDirs = new ArrayList<>(List.of(
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
                possibleMoves = new ArrayList<>(12);

                possibleMoves.add(new Move(fromPos, oneStepForward(fromPos, playerColor)));
                possibleMoves.add(new Move(fromPos, oneStepBackward(fromPos, playerColor)));
                possibleMoves.add(new Move(fromPos, oneStepLeftAndForward(fromPos, playerColor)));
                possibleMoves.add(new Move(fromPos, oneStepLeftAndBackward(fromPos, playerColor)));
                possibleMoves.add(new Move(fromPos, oneStepRightAndForward(fromPos, playerColor)));
                possibleMoves.add(new Move(fromPos, oneStepRightAndBackward(fromPos, playerColor)));
                possibleMoves.add(new Move(fromPos, bishopStepLeft(fromPos, playerColor)));
                possibleMoves.add(new Move(fromPos, bishopStepRight(fromPos, playerColor)));
                possibleMoves.add(new Move(fromPos, bishopStepBackwardLeft(fromPos, playerColor)));
                possibleMoves.add(new Move(fromPos, bishopStepForwardLeft(fromPos, playerColor)));
                possibleMoves.add(new Move(fromPos, bishopStepBackwardRight(fromPos, playerColor)));
                possibleMoves.add(new Move(fromPos, bishopStepForwardRight(fromPos, playerColor)));

                return new ArrayList<>(possibleMoves.stream().filter(move -> isInBounds(move.toPos)
                                                                     && (this.getPos(move.toPos) == null
                                                                     || this.getPos(move.toPos).color != playerColor)).toList());
        }

        return moves;
    }
}
