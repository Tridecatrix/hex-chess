package model;

import model.piece.Piece;

import java.util.Objects;

public class Position {
    // these are values from 0 to 10 inclusive
    public int file;
    public int rank;

    public Position(int file, int rank) {
        this.file = file;
        this.rank = rank;
    }

    public Position(String pos) {
        if (pos.isEmpty()) {
            throw new GameException("Position cannot be empty");
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

    @Override
    public int hashCode() {
        return Objects.hash(file, rank);
    }

    @Override
    public String toString() {
        StringBuilder string = new StringBuilder();
        string.append((char)('a' + this.file));
        string.append(this.rank + 1);
        return string.toString();
    }

    public static int distanceFromCenter(Position pos, int boardDiameter) {
        return Math.abs(pos.file - boardDiameter/2);
    }

    public static int distanceFromEdge(Position pos, int boardDiameter) {
        return boardDiameter/2 - Math.abs(pos.file - boardDiameter/2);
    }

    // note in the following that movement directions need to be treated differently depending on if
    // the original position/destination position is on the left, middle or right of the board's center.
    // this is because the ranks (horizontal coordinates) go DOWN AND RIGHT on the left side of the board,
    // but DOWN AND LEFT on the right side of the board.

    public static Position oneStepForward(Position pos, int boardDiameter) {
        return new Position(pos.file, pos.rank + 1);
    }

    public static Position oneStepBackward(Position pos, int boardDiameter) {
        return new Position(pos.file, pos.rank - 1);
    }

    public static Position oneStepLeftAndForward(Position pos, int boardDiameter) {
        if (pos.file <= boardDiameter/2) {
            return new Position(pos.file - 1, pos.rank);
        } else {
            return new Position(pos.file - 1, pos.rank + 1);
        }
    }

    public static Position oneStepRightAndForward(Position pos, int boardDiameter) {
        if (pos.file >= boardDiameter/2) {
            return new Position(pos.file + 1, pos.rank);
        } else {
            return new Position(pos.file + 1, pos.rank + 1);
        }
    }

    public static Position oneStepLeftAndBackward(Position pos, int boardDiameter) {
        if (pos.file <= boardDiameter/2) {
            return new Position(pos.file - 1, pos.rank - 1);
        } else {
            return new Position(pos.file - 1, pos.rank);
        }
    }
    public static Position oneStepRightAndBackward(Position pos, int boardDiameter) {
        if (pos.file >= boardDiameter/2) {
            return new Position(pos.file + 1, pos.rank - 1);
        } else {
            return new Position(pos.file + 1, pos.rank);
        }
    }

    public static Position bishopStepLeft(Position pos, int boardDiameter) {
        if (pos.file < boardDiameter/2 + 1) {
            return new Position(pos.file - 2, pos.rank - 1);
        } else if (pos.file == boardDiameter/2 + 1) {
            return new Position(pos.file - 2, pos.rank);
        } else {
            return new Position(pos.file - 2, pos.rank + 1);
        }
    }

    public static Position bishopStepRight(Position pos, int boardDiameter) {
        if (pos.file < boardDiameter/2 - 1) {
            return new Position(pos.file + 2, pos.rank + 1);
        } else if (pos.file == boardDiameter/2 - 1) {
            return new Position(pos.file + 2, pos.rank);
        } else {
            return new Position(pos.file + 2, pos.rank - 1);
        }
    }

    public static Position bishopStepForwardLeft(Position pos, int boardDiameter) {
        if (pos.file <= boardDiameter/2) {
            return new Position(pos.file - 1, pos.rank + 1);
        } else {
            return new Position(pos.file - 1, pos.rank + 2);
        }
    }

    public static Position bishopStepForwardRight(Position pos, int boardDiameter) {
        if (pos.file >= boardDiameter/2) {
            return new Position(pos.file + 1, pos.rank + 1);
        } else {
            return new Position(pos.file + 1, pos.rank + 2);
        }
    }

    public static Position bishopStepBackwardLeft(Position pos, int boardDiameter) {
        if (pos.file <= boardDiameter/2) {
            return new Position(pos.file - 1, pos.rank - 2);
        } else {
            return new Position(pos.file - 1, pos.rank - 1);
        }
    }
    public static Position bishopStepBackwardRight(Position pos, int boardDiameter) {
        if (pos.file >= boardDiameter/2) {
            return new Position(pos.file + 1, pos.rank - 2);
        } else {
            return new Position(pos.file + 1, pos.rank - 1);
        }
    }

    public boolean isInBounds(int boardDiameter) {
        boolean isFileInBounds = this.file >= 0 && this.file < boardDiameter;
        boolean isRankInBounds = this.rank >= 0 && this.rank < boardDiameter - distanceFromCenter(this, boardDiameter);
        return isFileInBounds && isRankInBounds;
    }
}