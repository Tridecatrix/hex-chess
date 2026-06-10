import model.Board;
import model.Move;
import model.Position;
import model.piece.Piece;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class ChecksAndCheckmateTest {
    Set<String> movesToString(Set<Move> moves) {
        Set<String> strs = new HashSet<>(moves.size());
        for (Move move : moves) {
            strs.add(move.toPos.toString());
        }
        return strs;
    }

    void assertMovesEqual(Set<String> expected, Board board, String fromPos) {
        Set<String> actual = movesToString(board.getLegalMovesFromPos(new Position(fromPos)));

        Set<String> diffExtra = new HashSet<>(actual);
        diffExtra.removeAll(expected);

        Set<String> diffMissing = new HashSet<>(expected);
        diffMissing.removeAll(actual);

        assertEquals(expected, actual, "\n" + board
                + "Moves from position " + fromPos + " don't match; actual is missing moves "
                + diffMissing + " and has extra moves " + diffExtra);
    }

    @Test
    void check1() {
        Board board = new Board(List.of("kd9", "pc8", "pd8", "Ri6"));
        System.out.println(board);
        assertTrue(board.isKingInCheck(Piece.Color.BLACK));
    }

    @Test
    void check2() {
        Board board = new Board(List.of("Ka1", "Pa2", "Pb1", "Pb2", "nb4"));
        System.out.println(board);
        assertTrue(board.isKingInCheck(Piece.Color.WHITE));
    }

    @Test
    void check3() {
        Board board = new Board(List.of("Kf6", "Pf5", "Pf7", "Pe5", "Pe6", "Pg5", "Pg6", "bh2"));
        System.out.println(board);
        assertTrue(board.isKingInCheck(Piece.Color.WHITE));
    }

    @Test
    void check4() {
        Board board = new Board(List.of("kf11", "Ka1", "Pg10", "pb2"));
        System.out.println(board);
        assertTrue(board.isKingInCheck(Piece.Color.WHITE));
        assertTrue(board.isKingInCheck(Piece.Color.BLACK));
    }

    @Test
    void noncheck() {
        Board board = new Board(List.of("ka3", "pb3", "pc4", "pa4", "Rf3", "Bg5", "Qa6"));
        System.out.println(board);
        assertFalse(board.isKingInCheck(Piece.Color.BLACK));
    }

    @Test
    void checkmate1() {
        Board board = new Board(List.of("kf11", "pe10", "Rd8", "Ne7", "Rk6"));
        System.out.println(board);
        assertTrue(board.isInCheckmate(Piece.Color.BLACK));
    }

    @Test
    void checkmate2() {
        Board board = new Board(List.of("Kf6", "Bd5", "re3", "qh4", "ph7", "ni6"));
        System.out.println(board);
        assertTrue(board.isInCheckmate(Piece.Color.WHITE));
    }

    @Test
    void nonCheckmate1() {
        Board board = new Board(List.of("Kf6", "Bd5", "re3", "qh4", "ph7"));
        System.out.println(board);
        assertFalse(board.isInCheckmate(Piece.Color.WHITE));
    }

    @Test
    void nonCheckmate2() {
        Board board = new Board(List.of("Kf6", "Bd5", "re3", "qh4", "ni6"));
        System.out.println(board);
        assertFalse(board.isInCheckmate(Piece.Color.WHITE));
    }
}
