package model;

public class Move {
    public Position fromPos;
    public Position toPos;

    public Move(Position fromPos, Position toPos) {
        this.fromPos = fromPos;
        this.toPos = toPos;
    }

    public Move(String fromPos, String toPos) {
        this.fromPos = new Position(fromPos);
        this.toPos = new Position(toPos);
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Move) {
            return this.fromPos.equals(((Move) other).fromPos) && this.toPos.equals(((Move) other).toPos);
        } else return false;
    }

    public String toString() {
        return fromPos.toString() + "->" + toPos.toString();
    }
}
