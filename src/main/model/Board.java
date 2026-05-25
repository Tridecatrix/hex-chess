import java.lang.Math;

public class Board {
    // for 91 space board; there are 11 files going vertically and 11 ranks going skew-horizontally.
    // for files on the outer edges of the board, some of the ranks don't exist, e.g. only a1-6 exist
    // f, or file 6, is the middle of the board
    Piece[][] board = new Piece[11][11];

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
    }

    public static class Move {
        public Position fromPos;
        public Position toPos;

        public Move(Position fromPos, Position toPos) {
            this.fromPos = fromPos;
            this.toPos = toPos;
        }
    }

    // Note: g
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
                float yBoard = ((float) (y - Math.abs(xBoard - 5)))/2;
                if (yBoard < 0 || yBoard >= 11 - Math.abs(xBoard - 5)) {
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
        boolean isRankInBounds = pos.rank >= 0 && pos.rank < 11 - Math.abs(pos.file - 5);
        return isFileInBounds && isRankInBounds;
    }

    public static int distanceFromCenter(Position pos) {
        return Math.abs(pos.file - 5);
    }

    public boolean isLegalMove(Move move, Piece.Color playerColor) {
        if (!isInBounds(move.fromPos) || !isInBounds(move.toPos)) {
            return false;
        }

        Piece piece = this.getPos(move.fromPos);

        if (piece == null || piece.color != playerColor) {
            // there is no piece there OR the piece is not the player's color
            return false;
        }

        Piece destinationPiece = this.getPos(move.toPos);

        if (destinationPiece != null && destinationPiece.color == playerColor) {
            // cannot move a piece onto one of your own pieces
            return false;
        }

        Position fromPos = move.fromPos;
        Position toPos = move.toPos;

        switch (piece.type) {
            case PAWN:
                int forwardStep = playerColor == Piece.Color.WHITE ? 1 : -1;

                // Pawn basic logic: able to move one space ahead into an empty square
                if (fromPos.file == toPos.file && fromPos.rank + forwardStep == toPos.rank && destinationPiece == null) {
                    return true;
                }

                // Pawn basic logic: able to capture to the side
                // Note: on hex chess, capturing inwards moves the piece up a rank
                // and capturing outwards keeps the piece in the same rank
                if ((fromPos.file == toPos.file - 1 || fromPos.file == toPos.file + 1)      // check for sideways: in neighbouring files
                     && (distanceFromCenter(toPos) > distanceFromCenter(fromPos)
                         ? toPos.rank == fromPos.rank
                         : toPos.rank == fromPos.rank + 1)                                  // check that rank change is correct
                     && destinationPiece != null) { // check that it's a capturing move
                        return true;
                     }

                // Pawn logic: if the piece is on a starting position, it can jump two steps forward
                // Note this is true also if the pawn moves into the starting position of another pawn
                if (((playerColor == Piece.Color.WHITE && fromPos.rank == 4 - distanceFromCenter(fromPos))
                    || (playerColor == Piece.Color.BLACK && fromPos.rank == 6)) &&
                    toPos.file == fromPos.file && toPos.rank == fromPos.rank + 2 * forwardStep) {
                    return true;
                }

                // Pawn logic: en passant
                // If the enemy pawn has moved 2 spaces in the last turn, and you could have captured it if it
                // had only moved one space, you still can
                // TODO: add
                break;
            case ROOK:
                break;
            case BISHOP:
                break;
            case KNIGHT:
                break;
            case QUEEN:
                break;
            case KING:
                break;
        }

        // TODO: REMOVE
        return false;
    }
}
